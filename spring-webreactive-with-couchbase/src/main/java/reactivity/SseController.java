/*
 * The MIT License (MIT) Copyright (c) 2016 The reactivity authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package reactivity;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.couchbase.client.java.document.JsonDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import reactivity.couchbase.ReactiveCouchbaseArtifactRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ReplayProcessor;
import rx.Observable;
import rx.RxReactiveStreams;

/**
 * This controller serves a SSE stream and exposes an endpoint that allows to generate random {@link Artifact} pushed to that stream.
 */
@RestController
public class SseController {

    /**
     * Messages are cached for one hour.
     */
    private static final Duration CACHE_AGE = Duration.ofMinutes(1);

    /**
     * Uses a {@code ReplayProcessor} to keep messages in a cache.
     */
	private ReplayProcessor<ServerSentEvent<Artifact>> replayProcessor = ReplayProcessor.createTimeout(CACHE_AGE);

    /**
     * The couchbase repository.
     */
    @Autowired
    private ReactiveCouchbaseArtifactRepository repository;

    /**
     * Generates a random artifact.
     *
     * @return the timestamp associated to that artifact
     */
    @PostMapping("/sse/artifacts")
    long artifact() {
        final Map<String, String> categories = new HashMap<>();
        final BiConsumer<String, String[]> populator = (key, values) -> {
            final String value = values[ThreadLocalRandom.current().nextInt(0, values.length)];

            if (value != null) {
                categories.put(key, value);
            }
        };

        populator.accept("assignee", new String[]{"ndamie", "gdrouet", "asanchez", "qlevaslot", "cazelart", "fclety", "hazarian", null});
        populator.accept("category", new String[]{"bug", "feature", "question", null});
        populator.accept("description", new String[]{"foo bar baz", "some description", "reactivity is awesome", null});
        populator.accept("color", new String[]{"blue", "green", "red", "yellow", null});
        populator.accept("priority", new String[]{"high", "low", "medium", null});

        final Group group;

        if (Math.random() < 0.5) {
            group = new Group("personal", "gdrouet");
        } else {
            group = new Group("organization", "org" + (char) ThreadLocalRandom.current().nextInt(65, 70));
        }

        final Artifact a = new Artifact("default", group, categories);
        final Observable<JsonDocument> o = repository.add(a);
        o.subscribe(d -> replayProcessor.onNext(sse(a)));
        o.subscribe(d -> timeseries(a));

        return a.getTimestamp();
    }

    /**
     * Returns a SSE stream of artifacts.
     *
     * @param ts the timestamp used as event ID in case of reconnection
     * @return the SSE stream
     */
	@GetMapping("/sse/artifacts")
	Flux<ServerSentEvent<Artifact>> artifact(@RequestHeader(name = "Last-Event-ID", required = false) final Long ts) {
        final long timestamp = ts == null ? 0L : ts;
        final TimestampFilter defaultFilter = new TimestampFilter("default", timestamp);
        final TimestampFilter timeseriesFilter = new TimestampFilter("timeseries", timestamp);
        final Consumer<ServerSentEvent<Artifact>> updater = e -> {
            final Artifact a = e.data().orElseThrow(IllegalStateException::new);

            if ("default".equals(a.getViewType())) {
                defaultFilter.setTimestamp(a.getTimestamp());
            } else {
                timeseriesFilter.setTimestamp(a.getTimestamp());
            }
        };

        // Excludes messages already sent
        return replayProcessor
                .log("Reactivity")

                // Make sure we don't replay messages already persisted
                .filter(defaultFilter).filter(timeseriesFilter)

                // Retrieve all time series created since the given timestamp
                .startWith(RxReactiveStreams.toPublisher(repository.color()
                        // we can't filter a view's value at query level
                        .filter((a) -> timestamp < a.getTimestamp())
                        .map(a -> sse(a))))

                // Retrieve all artifacts created since the given timestamp
                .startWith(RxReactiveStreams.toPublisher(repository.list(timestamp).map(a -> sse(a))))

                // Updates to the latest timestamp
                .doOnNext(updater);
    }

    /**
     * <p>
     * Retrieves and pushes the timeseries associated to the given artifact.
     * </p>
     *
     * @param a the artifact associated to timeseries
     */
    private void timeseries(final Artifact a) {

        // Compute the key
        final LocalDateTime i = LocalDateTime.ofInstant(Instant.ofEpochMilli(a.getTimestamp()), ZoneId.systemDefault());
        final Object[] key = new Object[] {
                a.getGroup().getType() + "/" + a.getGroup().getName(),
                i.getYear(),
                i.getMonthValue(),
                i.getDayOfMonth()
        };

        repository.color(key).subscribe((item) -> replayProcessor.onNext(sse(Artifact.class.cast(item))));
    }

    /**
     * Builds a new SSE containing the given {@link Artifact}.
     *
     * @param artifact the artifact
     * @return the SSE
     */
    private ServerSentEvent<Artifact> sse(final Artifact artifact) {
        return ServerSentEvent.<Artifact>builder(artifact)
                .retry(Duration.ofSeconds(5))
                .id(String.valueOf(artifact.getTimestamp()))
                .build();
    }

    /**
     * A {@code Predicate} that filters artifacts already sent.
     */
    static class TimestampFilter implements Predicate<ServerSentEvent<Artifact>> {

        /**
         * The timestamp of the latest sent artifact.
         */
        private long timestamp;

        /**
         * The artifact view type.
         */
        private String viewType;

        /**
         * Builds a new instance.
         *
         * @param viewType artifact view type
         * @param timestamp latest artifact timestamp
         */
        TimestampFilter(final String viewType, final long timestamp) {
            this.timestamp = timestamp;
            this.viewType = viewType;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean test(final ServerSentEvent<Artifact> e) {
            final Artifact a = e.data().orElseThrow(IllegalStateException::new);
            return !viewType.equals(a.getViewType()) || timestamp < a.getTimestamp();
        }

        /**
         * <p>
         * Updates the timestamp of the latest artifact.
         * </p>
         *
         * @param timestamp the new timestamp
         */
        void setTimestamp(final long timestamp) {
            this.timestamp = timestamp;
        }
    }
}

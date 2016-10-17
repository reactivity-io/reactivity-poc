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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import reactivity.couchbase.ReactiveCouchbaseArtifactRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ReplayProcessor;
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
    @PostMapping("/sse/artifacts") long artifact() {
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

        final Artifact a = new Artifact(group, categories);
        repository.add(a);
        replayProcessor.onNext(sse(a));
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
        final AtomicLong timestamp = new AtomicLong(ts == null ? 0L : ts);

        // Excludes messages already sent
        return replayProcessor
                .log("Reactivity")

                // Make sure we don't replay messages already persisted
                .filter((e) -> timestamp.get() < e.data().orElseThrow(IllegalStateException::new).getTimestamp())

                // Retrieve all artifacts created since the given timestamp
                .startWith(RxReactiveStreams.toPublisher(repository.list(timestamp.get()).map(a -> {

                    // Update the timestamp with the newer document
                    timestamp.set(a.getTimestamp());
                    return sse(a);
                })));
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
}

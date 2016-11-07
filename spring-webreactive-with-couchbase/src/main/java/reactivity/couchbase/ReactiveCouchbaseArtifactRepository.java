/*
 * The MIT License (MIT) Copyright (c) 2016 The reactivity authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package reactivity.couchbase;

import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.view.AsyncViewResult;
import com.couchbase.client.java.view.DefaultView;
import com.couchbase.client.java.view.DesignDocument;
import com.couchbase.client.java.view.Stale;
import com.couchbase.client.java.view.View;
import com.couchbase.client.java.view.ViewQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import org.springframework.util.FileCopyUtils;
import reactivity.Artifact;
import reactivity.Group;
import rx.Observable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import java.util.Arrays;
import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * <p>
 * This repository performs operations on {@link Artifact} documents with {@code RxJava} support.
 * </p>
 */
@Repository
public class ReactiveCouchbaseArtifactRepository {

    /**
     * The bucket.
     */
    private final AsyncBucket bucket;

    /**
     * <p>
     * Builds a new repository.
     * </p>
     *
     * @param bucket the bucket
     * @throws IOException if the repository is not able to initialize the views
     */
    @Autowired
    public ReactiveCouchbaseArtifactRepository(final Bucket bucket) throws IOException {
        this.bucket = bucket.async();

        String map;
        String reduce;

        try (final Reader in = new InputStreamReader(new ClassPathResource("couchbase/views/color/map.js").getInputStream())) {
            map = FileCopyUtils.copyToString(in);
        }

        try (final Reader in = new InputStreamReader(new ClassPathResource("couchbase/views/color/reduce.js").getInputStream())) {
            reduce = FileCopyUtils.copyToString(in);
        }

        View color = DefaultView.create("color", map, reduce);

        try (final Reader in = new InputStreamReader(new ClassPathResource("couchbase/views/default/map.js").getInputStream())) {
            map = FileCopyUtils.copyToString(in);
        }

        final List<View> views = Arrays.asList(DefaultView.create("default", map), color);
        bucket.bucketManager().upsertDesignDocument(DesignDocument.create("artifact", views));
    }

    /**
     * <p>
     * Inserts the given artifact.
     * </p>
     *
     * @param artifact the artifact
     * @return the artifact
     */
    public Observable<JsonDocument> add(final Artifact artifact) {
        final JsonObject object = JsonObject.create()
                .put("group", JsonObject.create().put("type", artifact.getGroup().getType()).put("name", artifact.getGroup().getName()))
                .put("timestamp", artifact.getTimestamp())
                .put("categories", JsonObject.from(artifact.getCategories()));
        return bucket.upsert(JsonDocument.create(UUID.randomUUID().toString(), object));
    }

    /**
     * <p>
     * List color timeseries.
     * </p>
     *
     * @param key a particular key to retrieve (optional)
     * @return a reduced {@code Artifact} counting the colors by day
     */
    public Observable<Artifact> color(final Object ... key) {

        // stale=false will wait that a document has been updated in the view before getting the result
        ViewQuery viewQuery = ViewQuery.from("artifact", "color").stale(Stale.FALSE);

        if (key.length > 0) {
            viewQuery = viewQuery.key(JsonArray.from(key));
        }

        return bucket.query(viewQuery.groupLevel(4))
                .flatMap(result -> {

                    // Statement executed successfully
                    if (result.success()) {

                        // Map each internal JSON object to an Artifact
                        return result.rows().map(row -> {
                            final Map<String, Object> count = new HashMap<>();
                            final JsonObject countObject = JsonObject.class.cast(row.value()).getObject("count");

                            for (final String name : JsonObject.class.cast(countObject).getNames()) {
                                count.put(name, countObject.getInt(name));
                            }

                            final Artifact retval = new Artifact("timeseries", new Group(row.key().toString(), "color"), count);
                            retval.setTimestamp(JsonObject.class.cast(row.value()).getLong("lastTimestamp"));
                            return retval;
                        });
                    } else {
                        // Statement failed
                        return error(result);
                    }
                });
    }

    /**
     * <p>
     * Lists all the artifacts newer than the given timestamp.
     * </p>
     *
     * @param timestamp the timestamp
     * @return the artifacts
     */
    public Observable<Artifact> list(final Long timestamp) {

        // stale=false will wait that a document has been updated in the view before getting the result
        final ViewQuery viewQuery = ViewQuery.from("artifact", "default")
                .startKey(JsonArray.from(timestamp + 1)) // timestamp + 1 means "greater than"
                .stale(Stale.FALSE);

        return this.bucket
                .query(viewQuery)
                .flatMap(result -> {

                    // Statement executed successfully
                    if (result.success()) {

                        // Map each internal JSON object to an Artifact
                        return result.rows().map(row -> {
                            final JsonObject o = JsonObject.class.cast(row.value());
                            final JsonObject g = o.getObject("group");
                            return new Artifact("default", new Group(g.getString("type"), g.getString("name")), o.getObject("categories").toMap());
                        });
                    } else {
                        // Statement failed
                        return error(result);
                    }
                });
    }

    /**
     * <p>
     * Generates an error for the given result.
     * </p>
     *
     * @param asyncViewResult the result
     * @return the observable error
     */
    private Observable<Artifact> error(final AsyncViewResult asyncViewResult) {
        return asyncViewResult.error().flatMap(
                jsonErrors -> Observable.<Artifact>error(
                        new IllegalStateException(
                                jsonErrors.getInt("code") + ": " + jsonErrors.getString("msg"))));
    }
}

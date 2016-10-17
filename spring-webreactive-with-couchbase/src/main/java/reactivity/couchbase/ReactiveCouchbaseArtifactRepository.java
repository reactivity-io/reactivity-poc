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
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.Select;
import com.couchbase.client.java.query.Statement;
import com.couchbase.client.java.query.dsl.Expression;
import com.couchbase.client.java.query.dsl.Sort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactivity.Artifact;
import rx.Observable;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
     * Synchronous bucket.
     */
    private Bucket syncBucket;

    /**
     * The object mapper.
     */
    @Autowired
    private ObjectMapper mapper;

    /**
     * <p>
     * Builds a new repository.
     * </p>
     *
     * @param bucket the bucket
     */
    @Autowired
    public ReactiveCouchbaseArtifactRepository(final Bucket bucket) {
        this.bucket = bucket.async();
        this.syncBucket = bucket;
    }

    /**
     * <p>
     * Inserts the given artifact.
     * </p>
     *
     * @param artifact the artifact
     * @return the artifact
     */
    public JsonDocument add(final Artifact artifact) {
        final JsonObject object = JsonObject.create()
                .put("group", JsonObject.create().put("type", artifact.getGroup().getType()).put("name", artifact.getGroup().getName()))
                .put("timestamp", artifact.getTimestamp())
                .put("categories", JsonObject.from(artifact.getCategories()));
        return syncBucket.upsert(JsonDocument.create(UUID.randomUUID().toString(), object));
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

        // Selects the artifacts by checking their timestamp
        final Statement statement = Select.select("categories,`group`,timestamp")
                .from("artifacts")
                .where(Expression.x("timestamp").gt(timestamp))
                .orderBy(Sort.asc("timestamp"));

        return this.bucket
                .query(statement)
                .flatMap(result -> {

                    // Statement executed successfully
                    if (result.parseSuccess()) {

                        // Map each internal JSON object to an Artifact
                        return result.rows().map(row -> {
                            try {
                                return mapper.readValue(row.byteValue(), Artifact.class);
                            } catch (IOException e) {
                                throw new IllegalStateException(e);
                            }
                        });
                    } else {
                        // Statement failed
                        return result.errors().flatMap(
                                jsonErrors -> Observable.<Artifact>error(
                                        new IllegalStateException(
                                                jsonErrors.getInt("code") + ": " + jsonErrors.getString("msg"))));
                    }
                });
    }
}

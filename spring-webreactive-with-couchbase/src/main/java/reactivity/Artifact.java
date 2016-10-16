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

import java.util.Map;

/**
 * An artifact belongs to a group and has a random set of categories.
 */
public class Artifact {

    /**
     * Artifact age.
     */
    private long timestamp;

    /**
     * The group.
     */
    private Group group;

    /**
     * The categories.
     */
    private Map<String, String> categories;

    /**
     * Builds a new instance.
     *
     * @param group the group
     * @param categories the categories
     */
    public Artifact(final Group group, final Map<String, String> categories) {
        this.timestamp = System.currentTimeMillis();
        this.group = group;
        this.categories = categories;
    }

    /**
     * <p>
     * Gets the timestamp.
     * </p>
     *
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * <p>
     * Sets the timestamp.
     * </p>
     *
     * @param timestamp the new timestamp
     */
    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * <p>
     * Gets the group.
     * </p>
     *
     * @return the group
     */
    public Group getGroup() {
        return group;
    }

    /**
     * <p>
     * Sets the group.
     * </p>
     *
     * @param group the new group
     */
    public void setGroup(final Group group) {
        this.group = group;
    }

    /**
     * <p>
     * Gets the categories.
     * </p>
     *
     * @return the categories
     */
    public Map<String, String> getCategories() {
        return categories;
    }

    /**
     * <p>
     * Sets the categories.
     * </p>
     *
     * @param categories the new categories
     */
    public void setCategories(final Map<String, String> categories) {
        this.categories = categories;
    }
}

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
     * View type.
     */
    private String viewType;

    /**
     * The group.
     */
    private Group group;

    /**
     * The categories.
     */
    private Map<String, Object> categories;

    /**
     * Builds a default instance.
     */
    public Artifact() {
        viewType = "default";
    }

    /**
     * Builds a new instance.
     *
     * @param viewType the view type
     * @param group the group
     * @param categories the categories
     */
    public Artifact(final String viewType, final Group group, final Map<String, Object> categories) {
        this.timestamp = System.currentTimeMillis();
        this.group = group;
        this.categories = categories;
        this.viewType = viewType;
    }

    /**
     * <p>
     * The view type.
     * </p>
     *
     * @return view type
     */
    public String getViewType() {
        return viewType;
    }

    /**
     * <p>
     * Sets the view type.
     * </p>
     *
     * @param viewType the new view type
     */
    public void setViewType(final String viewType) {
        this.viewType = viewType;
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
    public Map<String, Object> getCategories() {
        return categories;
    }

    /**
     * <p>
     * Sets the categories.
     * </p>
     *
     * @param categories the new categories
     */
    public void setCategories(final Map<String, Object> categories) {
        this.categories = categories;
    }
}

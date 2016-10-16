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

/**
 * A group as a type and a name.
 */
public class Group {

    /**
     * Type.
     */
    private String type;

    /**
     * Name.
     */
    private String name;

    /**
     * Builds a new instance.
     *
     * @param type the type
     * @param name the name
     */
    public Group(final String type, final String name) {
        this.type = type;
        this.name = name;
    }

    /**
     * <p>
     * Gets the type.
     * </p>
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * <p>
     * Sets the type.
     * </p>
     *
     * @param type the new type
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * <p>
     * Gets the name.
     * </p>
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * <p>
     * Sets the name
     * </p>
     *
     * @param name the new name
     */
    public void setName(final String name) {
        this.name = name;
    }
}

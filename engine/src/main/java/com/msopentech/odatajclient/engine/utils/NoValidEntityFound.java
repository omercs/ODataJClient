/*
 * Copyright 2013 MS OpenTech.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.msopentech.odatajclient.engine.utils;

public class NoValidEntityFound extends Exception {

    private static final long serialVersionUID = -3078954221364213688L;

    /**
     * Creates a new instance of
     * <code>NoSuchEntityFound</code> without detail message.
     */
    public NoValidEntityFound() {
        super();
    }

    /**
     * Constructs an instance of
     * <code>NoSuchEntityFound</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public NoValidEntityFound(String msg) {
        super(msg);
    }

    public NoValidEntityFound(final Throwable t) {
        this.initCause(t);
    }
}
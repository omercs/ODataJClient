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
package com.msopentech.odatajclient.engine.communication.request;

import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import java.util.concurrent.Future;

/**
 * Basic OData request.
 *
 * @param <V> OData response type corresponding to the request implementation.
 * @param <T> Accepted content-type formats by the request in object.
 */
public interface ODataBasicRequest<V extends ODataResponse, T extends Enum<T>> extends ODataRequest {

    /**
     * Request execute.
     *
     * @return return an OData response.
     */
    V execute();

    /**
     * Async request execute.
     *
     * @return <code>Future&lt;ODataResponse&gt;</code> about the executed request.
     */
    Future<V> asyncExecute();

    /**
     * Returns resource representation format as string.
     *
     * @return the configured format (or default if not specified).
     * @see com.msopentech.odatajclient.engine.utils.Configuration#getDefaultPubFormat()
     * @see com.msopentech.odatajclient.engine.utils.Configuration#getDefaultFormat()
     */
    T getFormat();

    /**
     * Override configured request format.
     *
     * @param format request format.
     * @see com.msopentech.odatajclient.engine.format.ODataFormat
     * @see com.msopentech.odatajclient.engine.format.ODataPubFormat
     */
    void setFormat(T format);
}

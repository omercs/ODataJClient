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
package com.msopentech.odatajclient.engine.communication.response;

import com.msopentech.odatajclient.engine.communication.header.ODataHeaders;
import com.msopentech.odatajclient.engine.communication.request.batch.ODataBatchUtilities;
import com.msopentech.odatajclient.engine.communication.request.batch.ODataBatchController;
import com.msopentech.odatajclient.engine.communication.request.batch.ODataBatchLineIterator;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.slf4j.LoggerFactory;

/**
 * Abstract representation of an OData response.
 */
public abstract class ODataResponseImpl implements ODataResponse {

    /**
     * Logger.
     */
    protected static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ODataResponse.class);

    protected final HttpClient client;

    protected final HttpResponse res;

    protected final Map<String, Collection<String>> headers =
            new TreeMap<String, Collection<String>>(String.CASE_INSENSITIVE_ORDER);

    private int statusCode = -1;

    private String statusMessage = null;

    private InputStream payload = null;

    private boolean hasBeenInitialized = false;

    private ODataBatchController batchInfo = null;

    public ODataResponseImpl() {
        this.client = null;
        this.res = null;
    }

    public ODataResponseImpl(final HttpClient client, final HttpResponse res) {
        this.client = client;
        this.res = res;

        try {
            this.payload = this.res.getEntity() == null ? null : this.res.getEntity().getContent();
        } catch (Exception e) {
            LOG.error("Error retrieving payload", e);
            throw new IllegalStateException(e);
        }

        this.hasBeenInitialized = true;

        for (Header header : res.getAllHeaders()) {
            final Collection<String> headerValues;
            if (headers.containsKey(header.getName())) {
                headerValues = headers.get(header.getName());
            } else {
                headerValues = new HashSet<String>();
                headers.put(header.getName(), headerValues);
            }

            headerValues.add(header.getValue());
        }

        statusCode = res.getStatusLine().getStatusCode();
        statusMessage = res.getStatusLine().getReasonPhrase();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getHeaderNames() {
        return headers.keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getHeader(final String name) {
        return headers.get(name);
    }

    @Override
    public Collection<String> getHeader(final ODataHeaders.HeaderName name) {
        return headers.get(name.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEtag() {
        final Collection<String> etag = getHeader(ODataHeaders.HeaderName.etag);
        return etag == null || etag.isEmpty()
                ? null
                : etag.iterator().next();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContentType() {
        final Collection<String> contentTypes = getHeader(ODataHeaders.HeaderName.contentType);
        return contentTypes == null || contentTypes.isEmpty()
                ? null
                : contentTypes.iterator().next();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStatusMessage() {
        return statusMessage;
    }

    @Override
    public ODataResponse initFromBatch(
            final Map.Entry<Integer, String> responseLine,
            final Map<String, Collection<String>> headers,
            final ODataBatchLineIterator batchLineIterator,
            final String boundary) {

        if (hasBeenInitialized) {
            throw new IllegalStateException("Request already initialized");
        }

        this.hasBeenInitialized = true;

        this.batchInfo = new ODataBatchController(batchLineIterator, boundary);

        this.statusCode = responseLine.getKey();
        this.statusMessage = responseLine.getValue();
        this.headers.putAll(headers);

        return this;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() {
        if (client == null) {
            IOUtils.closeQuietly(payload);
        } else {
            this.client.getConnectionManager().shutdown();
        }

        if (batchInfo != null) {
            batchInfo.setValidBatch(false);
        }
    }

    @Override
    public InputStream getRawResponse() {
        if (payload == null && batchInfo.isValidBatch()) {
            // get input stream till the end of item
            payload = new PipedInputStream();

            try {
                final PipedOutputStream os = new PipedOutputStream((PipedInputStream) payload);

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            ODataBatchUtilities.readBatchPart(batchInfo, os, true);
                        } catch (Exception e) {
                            LOG.error("Error streaming batch item payload", e);
                        } finally {
                            IOUtils.closeQuietly(os);
                        }
                    }
                }).start();

            } catch (IOException e) {
                LOG.error("Error streaming payload response", e);
                throw new IllegalStateException(e);
            }
        }

        return payload;
    }
}
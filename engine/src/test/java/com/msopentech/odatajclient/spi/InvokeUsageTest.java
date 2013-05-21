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
package com.msopentech.odatajclient.spi;

import com.msopentech.odatajclient.engine.client.ODataClient;
import com.msopentech.odatajclient.engine.client.ODataRestClient;
import com.msopentech.odatajclient.engine.data.ODataURI;
import com.msopentech.odatajclient.engine.communication.request.ODataRequest;
import com.msopentech.odatajclient.engine.communication.request.ODataRequestFactory;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import java.util.Collections;
import java.util.Map;

public class InvokeUsageTest {

    private final ODataClient client = new ODataRestClient();

    public void invokeFunction() {
        // provide the target URI
        final ODataURI targetURI = new ODataURI("http://services.odata.org/OData/Odata.svc");
        targetURI.append("GetProductsByRating?rating=4");

        // create your request
        final ODataRequest request = ODataRequestFactory.getInvokeFunctionRequest(targetURI);

        // execute the request
        ODataResponse res = client.execute(request);

        // .....
    }

    public void invokeBindableAction() {
        // provide the target URI
        final ODataURI targetURI = new ODataURI("http://services.odata.org/OData/Odata.svc");
        targetURI.append("Product(0)");

        Map<String, Object> parameters = Collections.<String, Object>singletonMap("rating", 2);

        // create your request
        final ODataRequest request = ODataRequestFactory.getInvokeActionRequest(targetURI, parameters);

        // execute the request
        ODataResponse res = client.execute(request);

        // .....
    }
}

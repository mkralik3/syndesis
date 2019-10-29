/*
 * Copyright (C) 2016 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.syndesis.connector.mongo;

import java.util.Arrays;
import java.util.Map;

import io.syndesis.connector.support.util.ConnectorOptions;
import io.syndesis.integration.component.proxy.ComponentProxyComponent;

public class MongoProducerCustomizer extends MongoAbstractCustomizer {
    @Override
    public void customize(ComponentProxyComponent component, Map<String, Object> options) {
        String operation = ConnectorOptions.extractOption(options, "operation");
        if (operation != null) {
            if (!MongoProducerOperation.isValid(operation)) {
                throw new IllegalArgumentException(String.format("Operation %s is not supported. " +
                    "Supported operations are %s", operation, Arrays.toString(MongoProducerOperation.values())));
            }
        } else {
            throw new IllegalArgumentException(String.format("You must provide a text `operation` option. " +
                "Supported operations are %s", Arrays.toString(MongoProducerOperation.values())));
        }
    }
}


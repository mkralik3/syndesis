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

import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;
import io.syndesis.common.model.integration.Step;
import org.bson.Document;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class MongoDBConnectorSaveTest extends MongoDBConnectorTestSupport {

    @Override
    protected List<Step> createSteps() {
        return fromDirectToMongo("start", "io.syndesis.connector:connector-mongodb-upsert", DATABASE, COLLECTION);
    }

    @Test
    public void mongoSaveNewTest() {
        // When
        // Given
        String saveArguments = "{\"_id\":11,\"test\":\"new\"}";
        UpdateResult result = template.requestBody("direct:start", saveArguments, UpdateResult.class);
        // Then
        assertThat(result.getUpsertedId().asNumber().longValue(), equalTo(11L));
        List<Document> docsFound = collection.find(Filters.eq("_id", 11)).into(new ArrayList<>());
        assertThat(docsFound, hasSize(1));
        assertThat(docsFound.get(0).getString("test"), equalTo("new"));
    }

    @Test
    public void mongoSaveNewAutogeneratedIDTest() {
        // When
        // Given
        String saveArguments = "{\"test\":\"new\"}";
        UpdateResult result = template.requestBody("direct:start", saveArguments, UpdateResult.class);
        // Then
        List<Document> docsFound = collection.find(Filters.eq("_id",
            result.getUpsertedId().asObjectId())).into(new ArrayList<>());
        assertThat(docsFound, hasSize(1));
        assertThat(docsFound.get(0).getString("test"), equalTo("new"));
        assertThat(result.getUpsertedId().asObjectId().getValue(), equalTo(docsFound.get(0).getObjectId("_id")));
    }

    @Test
    public void mongoSaveExistingTest() {
        // When
        String saveArguments = "{\"_id\":32,\"test\":\"new\"}";
        template().sendBody("direct:start", saveArguments);
        // Given
        String saveArguments2 = "{\"_id\":32,\"test\":\"save\",\"newField\":true}";
        UpdateResult result = template.requestBody("direct:start", saveArguments2, UpdateResult.class);
        // Then
        assertThat(result.getModifiedCount(), equalTo(1L));
        List<Document> docsFound = collection.find(Filters.eq("_id", 32)).into(new ArrayList<>());
        assertThat(docsFound, hasSize(1));
        assertThat(docsFound.get(0).getString("test"), equalTo("save"));
        assertThat(docsFound.get(0).getBoolean("newField"), equalTo(true));
    }

}

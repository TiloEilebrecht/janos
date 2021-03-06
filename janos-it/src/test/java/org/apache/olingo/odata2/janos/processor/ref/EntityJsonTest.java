/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 ******************************************************************************/
package org.apache.olingo.odata2.janos.processor.ref;

import com.google.gson.internal.LinkedTreeMap;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.olingo.odata2.api.commons.HttpContentType;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 *  
 */
public class EntityJsonTest extends AbstractRefJsonTest {

  public EntityJsonTest(String modelPackage) {
    super(modelPackage);
  }

  @Test
  @SuppressWarnings(value = "unchecked")
  public void readEntityRoom() throws Exception {
    String roomId = requestRoomId();
    HttpResponse response =
        callUri("Rooms('" + roomId + "')", HttpHeaders.ACCEPT, HttpContentType.APPLICATION_JSON, HttpStatusCodes.OK);
    checkMediaType(response, HttpContentType.APPLICATION_JSON);

    String body = getBody(response);
    LinkedTreeMap<?, ?> firstRoom = getLinkedTreeMap(body);

    assertEquals("Small green room", firstRoom.get("Name"));
    assertEquals(20.0, firstRoom.get("Seats"));
    assertEquals(42.0, firstRoom.get("Version"));
  }

  @Test
  public void createEntryRoom() throws Exception {
    String id = UUID.randomUUID().toString();
    String content = "{\"d\":{\"__metadata\":{\"id\":\"" + getEndpoint() + "Rooms('1')\","
        + "\"uri\":\"" + getEndpoint() + "Rooms('1')\",\"type\":\"RefScenario.Room\","
        + "\"etag\":\"W/\\\"3\\\"\"},"
        + "\"Id\":\"" + id + "\",\"Name\":\"Room 104\",\"Seats\":4,\"Version\":2,"
        + "\"nr_Employees\":{\"__deferred\":{\"uri\":\"" + getEndpoint() + "Rooms('1')/nr_Employees\"}},"
        + "\"nr_Building\":{\"__deferred\":{\"uri\":\"" + getEndpoint() + "Rooms('1')/nr_Building\"}}}}";
    assertNotNull(content);
    HttpResponse response =
        postUri("Rooms", content, HttpContentType.APPLICATION_JSON, HttpHeaders.ACCEPT,
            HttpContentType.APPLICATION_JSON, HttpStatusCodes.CREATED);
    checkMediaType(response, HttpContentType.APPLICATION_JSON);

    String body = getBody(response);
    LinkedTreeMap<?, ?> map = getLinkedTreeMap(body);
    assertEquals(id, map.get("Id"));
    assertEquals("Room 104", map.get("Name"));
    @SuppressWarnings("unchecked")
    LinkedTreeMap<String, String> metadataMap = (LinkedTreeMap<String, String>) map.get("__metadata");
    assertNotNull(metadataMap);
    String expectedRoomId = getEndpoint() + "Rooms('" + id + "')";
    assertEquals(expectedRoomId, metadataMap.get("id"));
    assertEquals("RefScenario.Room", metadataMap.get("type"));
    assertEquals(expectedRoomId, metadataMap.get("uri"));

    response = callUri("Rooms('" + id + "')/Seats/$value");
    body = getBody(response);
    assertEquals("4", body);
  }

  @Test
  public void createEntryRoomWithLink() throws Exception {
    final String id = UUID.randomUUID().toString();
    String content = "{\"d\":{\"__metadata\":{\"id\":\"" + getEndpoint() + "Rooms('1')\","
        + "\"uri\":\"" + getEndpoint() + "Rooms('1')\",\"type\":\"RefScenario.Room\","
        + "\"etag\":\"W/\\\"3\\\"\"},"
        + "\"Id\":\"" + id + "\",\"Name\":\"Room 104\","
        + "\"nr_Employees\":{\"__deferred\":{\"uri\":\"" + getEndpoint() + "Rooms('1')/nr_Employees\"}},"
        + "\"nr_Building\":{\"__deferred\":{\"uri\":\"" + getEndpoint() + "Rooms('1')/nr_Building\"}}}}";
    assertNotNull(content);
    HttpResponse response =
        postUri("Rooms", content, HttpContentType.APPLICATION_JSON, HttpHeaders.ACCEPT,
            HttpContentType.APPLICATION_JSON, HttpStatusCodes.CREATED);
    checkMediaType(response, HttpContentType.APPLICATION_JSON);

    String body = getBody(response);
    LinkedTreeMap<?, ?> map = getLinkedTreeMap(body);
    assertEquals(id, map.get("Id"));
    assertEquals("Room 104", map.get("Name"));
    @SuppressWarnings("unchecked")
    LinkedTreeMap<Object, Object> employeesMap = (LinkedTreeMap<Object, Object>) map.get("nr_Employees");
    assertNotNull(employeesMap);
    @SuppressWarnings("unchecked")
    LinkedTreeMap<String, String> deferredMap = (LinkedTreeMap<String, String>) employeesMap.get("__deferred");
    assertNotNull(deferredMap);
    assertEquals(getEndpoint() + "Rooms('" + id + "')/nr_Employees", deferredMap.get("uri"));
  }

  @Test
  public void createAndModifyEntryEmployee() throws Exception {
    String content = "{iVBORw0KGgoAAAANSUhEUgAAAB4AAAAwCAIAAACJ9F2zAAAAA}";

    assertNotNull(content);
    HttpResponse createResponse =
        postUri("Employees", content, HttpContentType.TEXT_PLAIN, HttpHeaders.ACCEPT, HttpContentType.APPLICATION_JSON,
            HttpStatusCodes.CREATED);
    checkMediaType(createResponse, HttpContentType.APPLICATION_JSON);

    String body = getBody(createResponse);
    LinkedTreeMap<?, ?> map = getLinkedTreeMap(body);
    String id = (String) map.get("EmployeeId");
    assertNull(map.get("EmployeeName"));

    putUri("Employees('" + id + "')", JSON_EMPLOYEE, HttpContentType.APPLICATION_JSON, HttpStatusCodes.NO_CONTENT);

    HttpResponse updateResponse = callUri("Employees('" + id + "')", "Accept", HttpContentType.APPLICATION_JSON);
    checkMediaType(updateResponse, HttpContentType.APPLICATION_JSON);
    String updatedBody = getBody(updateResponse);
    LinkedTreeMap<?, ?> updatedMap = getLinkedTreeMap(updatedBody);
    assertNotNull(updatedMap.get("EmployeeId"));
    assertEquals("Douglas", updatedMap.get("EmployeeName"));
    assertNull(updatedMap.get("EntryData"));

    LinkedTreeMap<?, ?> location = (LinkedTreeMap<?, ?>) updatedMap.get("Location");
    assertEquals("Britian", location.get("Country"));

    LinkedTreeMap<?, ?> city = (LinkedTreeMap<?, ?>) location.get("City");
    assertEquals("12345", city.get("PostalCode"));
    assertEquals("Sample", city.get("CityName"));
  }

  private static final String JSON_EMPLOYEE = "{" +
      "    \"d\": {" +
      // "        \"__metadata\": {" +
      // "            \"id\": \"http://localhost:19000/abc/EntryJsonCreateTest/Employees('1')\"," +
      // "            \"uri\": \"http://localhost:19000/abc/EntryJsonCreateTest/Employees('1')\"," +
      // "            \"type\": \"RefScenario.Employee\"," +
      // "            \"content_type\": \"application/octet-stream\"," +
      // "            \"media_src\": \"Employees('1')/$value\"," +
      // "            \"edit_media\": \"http://localhost:19000/abc/EntryJsonCreateTest/Employees('1')/$value\"" +
      // "        }," +
      "        \"EmployeeId\": \"1\"," +
      "        \"EmployeeName\": \"Douglas\"," +
      "        \"Age\": 42," +
      "        \"Location\": {" +
      "            \"__metadata\": {" +
      "                \"type\": \"RefScenario.c_Location\"" +
      "            }," +
      "            \"Country\": \"Britian\"," +
      "            \"City\": {" +
      "                \"__metadata\": {" +
      "                    \"type\": \"RefScenario.c_City\"" +
      "                }," +
      "                \"PostalCode\": \"12345\"," +
      "                \"CityName\": \"Sample\"" +
      "            }" +
      "        }" +
      "    }" +
      "}";

  @SuppressWarnings(value = "unchecked")
  private String requestRoomId() throws Exception {
    HttpResponse response =
        callUri("Rooms", HttpHeaders.ACCEPT, HttpContentType.APPLICATION_JSON, HttpStatusCodes.OK);
    checkMediaType(response, HttpContentType.APPLICATION_JSON);

    String body = getBody(response);
    LinkedTreeMap<?, ?> map = getLinkedTreeMap(body);

    List<LinkedTreeMap<String, String>> results = (List) map.get("results");

    for (LinkedTreeMap<String, String> result : results) {
      if(result.get("Name").equals("Small green room")) {
        return result.get("Id");
      }
    }
    return null;
  }
}

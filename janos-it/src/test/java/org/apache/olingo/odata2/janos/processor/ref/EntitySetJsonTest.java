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

import static org.junit.Assert.*;

/**
 *  
 */
public class EntitySetJsonTest extends AbstractRefJsonTest {

  public EntitySetJsonTest(String modelPackage) {
    super(modelPackage);
  }

  @Test
  @SuppressWarnings(value = "unchecked")
  public void entitySetRooms() throws Exception {
    HttpResponse response =
        callUri("Rooms", HttpHeaders.ACCEPT, HttpContentType.APPLICATION_JSON, HttpStatusCodes.OK);
    checkMediaType(response, HttpContentType.APPLICATION_JSON);

    String body = getBody(response);
    LinkedTreeMap<?, ?> map = getLinkedTreeMap(body);

    List<LinkedTreeMap<String, String>> results = (List) map.get("results");
    assertFalse(results.isEmpty());

    LinkedTreeMap<String, String> firstRoom = null;
    for (LinkedTreeMap<String, String> result: results) {
      if(result.get("Name").equals("Small green room")) {
        firstRoom = result;
      }
    }
    assertNotNull(firstRoom);
    assertEquals("Small green room", firstRoom.get("Name"));
    assertEquals(20.0, firstRoom.get("Seats"));
    assertEquals(42.0, firstRoom.get("Version"));
  }

  @Test
  @SuppressWarnings(value = "unchecked")
  public void entitySetRoomsTop() throws Exception {
    HttpResponse response =
        callUri("Rooms?$top=1", HttpHeaders.ACCEPT, HttpContentType.APPLICATION_JSON, HttpStatusCodes.OK);
    checkMediaType(response, HttpContentType.APPLICATION_JSON);

    String body = getBody(response);
    LinkedTreeMap<?, ?> map = getLinkedTreeMap(body);

    List<LinkedTreeMap<String, String>> results = (List) map.get("results");
    assertFalse(results.isEmpty());
    assertEquals(1, results.size());
    //
    assertTrue(roomsCount() != results.size());

    // TODO: 150917_mibo: Improve this test,
    // currently it can not determined which room is returned as 'top' room
//    LinkedTreeMap<String, String> firstRoom = null;
//    for (LinkedTreeMap<String, String> result: results) {
//      if(result.get("Name").equals("Small green room")) {
//        firstRoom = result;
//      }
//    }
//    assertNotNull("Found only room " + results.get(0), firstRoom);
//    assertEquals("Small green room", firstRoom.get("Name"));
//    assertEquals(20.0, firstRoom.get("Seats"));
//    assertEquals(42.0, firstRoom.get("Version"));
  }

  @Test
  @SuppressWarnings(value = "unchecked")
  public void entitySetRoomsSkip() throws Exception {
    HttpResponse response =
        callUri("Rooms?$skip=1", HttpHeaders.ACCEPT, HttpContentType.APPLICATION_JSON, HttpStatusCodes.OK);
    checkMediaType(response, HttpContentType.APPLICATION_JSON);

    String body = getBody(response);
    LinkedTreeMap<?, ?> map = getLinkedTreeMap(body);

    List<LinkedTreeMap<String, String>> results = (List) map.get("results");
    assertFalse(results.isEmpty());
    //
    assertTrue(roomsCount() != results.size());

    LinkedTreeMap<String, String> roomToCheck = null;
    for (LinkedTreeMap<String, String> result: results) {
      if(result.get("Name").equals("Small dark green room")) {
        roomToCheck = result;
      }
    }
    assertNotNull(roomToCheck);
    assertEquals("Small dark green room", roomToCheck.get("Name"));
    assertEquals(30.0, roomToCheck.get("Seats"));
    assertEquals(2.0, roomToCheck.get("Version"));
  }

  private int roomsCount() throws Exception {
    HttpResponse response =
        callUri("Rooms/$count", HttpHeaders.ACCEPT, HttpContentType.APPLICATION_JSON, HttpStatusCodes.OK);

    String body = getBody(response);
    return Integer.valueOf(body);
  }
}

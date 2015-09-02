/**
 * *****************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ***************************************************************************** 
 */
package org.apache.olingo.odata2.janos.processor.ref;

import org.apache.olingo.odata2.api.ODataCallback;
import org.apache.olingo.odata2.api.ODataDebugCallback;
import org.apache.olingo.odata2.api.ODataService;
import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataErrorCallback;
import org.apache.olingo.odata2.api.processor.ODataErrorContext;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.janos.processor.api.JanosService;
import org.apache.olingo.odata2.janos.processor.api.datasource.DataStoreException;
import org.apache.olingo.odata2.janos.processor.core.datasource.InMemoryDataStore;
import org.apache.olingo.odata2.janos.processor.ref.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * ODataServiceFactory implementation based on ListProcessor
 * in combination with Annotation-Support-Classes for EdmProvider, DataSource and ValueAccess.
 */
public class JanosRefServiceFactory extends ODataServiceFactory {

  /**
   * Instance holder for all annotation relevant instances which should be used as singleton
   * instances within the ODataApplication (ODataService)
   */
  private static class AnnotationInstances {
//    final static String MODEL_PACKAGE = "org.apache.olingo.odata2.janos.processor.ref.model";
    final static Set<Class<?>> ANNOTATED_MODEL_CLASSES = new HashSet<Class<?>>();
    static {
      ANNOTATED_MODEL_CLASSES.add(Building.class);
      ANNOTATED_MODEL_CLASSES.add(City.class);
      ANNOTATED_MODEL_CLASSES.add(Employee.class);
      ANNOTATED_MODEL_CLASSES.add(Location.class);
      ANNOTATED_MODEL_CLASSES.add(Manager.class);
      ANNOTATED_MODEL_CLASSES.add(Photo.class);
      ANNOTATED_MODEL_CLASSES.add(RefBase.class);
      ANNOTATED_MODEL_CLASSES.add(Room.class);
      ANNOTATED_MODEL_CLASSES.add(Team.class);
      ANNOTATED_MODEL_CLASSES.add(RefFunctions.class);
    }
    final static ODataService ANNOTATION_ODATA_SERVICE;

    static {
      try {
//        ANNOTATION_ODATA_SERVICE = AnnotationServiceFactory.createFor(MODEL_PACKAGE).build();
        ANNOTATION_ODATA_SERVICE = JanosService.createFor(ANNOTATED_MODEL_CLASSES).build();
        initializeSampleData();
      } catch (ODataApplicationException ex) {
        throw new RuntimeException("Exception during sample data generation.", ex);
      } catch (ODataException ex) {
        throw new RuntimeException("Exception during data source initialization generation.", ex);
      }
    }
  }

  @Override
  public ODataService createService(final ODataContext context) throws ODataException {
    // Edm via Annotations and ListProcessor via AnnotationDS with AnnotationsValueAccess
    return AnnotationInstances.ANNOTATION_ODATA_SERVICE;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends ODataCallback> T getCallback(final Class<? extends ODataCallback> callbackInterface) {
    return (T) (callbackInterface.isAssignableFrom(ScenarioErrorCallback.class)
        ? new ScenarioErrorCallback() : callbackInterface.isAssignableFrom(ODataDebugCallback.class)
            ? new ScenarioDebugCallback() : null);
  }

  /*
   * Helper classes and methods
   */

  /**
   * Callback class to enable debugging.
   */
  private final class ScenarioDebugCallback implements ODataDebugCallback {

    @Override
    public boolean isDebugEnabled() {
      return true;
    }
  }

  /**
   * Callback class for error handling.
   */
  private class ScenarioErrorCallback implements ODataErrorCallback {

    private final Logger LOG = LoggerFactory.getLogger(ScenarioErrorCallback.class);

    @Override
    public ODataResponse handleError(final ODataErrorContext context) throws ODataApplicationException {
      if (context.getHttpStatus() == HttpStatusCodes.INTERNAL_SERVER_ERROR) {
        LOG.error("Internal Server Error", context.getException());
      }

      return EntityProvider.writeErrorDocument(context);
    }

  }

  private static <T> InMemoryDataStore<T> getDataStore(Class<T> clz) throws DataStoreException {
    return InMemoryDataStore.createInMemory(clz, true);
  }

  private static void initializeSampleData() throws ODataApplicationException {
    InMemoryDataStore<Team> teamDs = getDataStore(Team.class);
    teamDs.create(createTeam("Team Alpha", true));
    teamDs.create(createTeam("Team Beta", false));
    teamDs.create(createTeam("Team Gamma", false));
    teamDs.create(createTeam("Team Omega", true));
    Team subTeam = createTeam("SubTeamOne", false);
    teamDs.create(subTeam);
    teamDs.create(createTeam("Team Zeta", true, subTeam));

    InMemoryDataStore<Building> buildingsDs = getDataStore(Building.class);
    Building redBuilding = createBuilding("Red Building");
    buildingsDs.create(redBuilding);
    Building greenBuilding = createBuilding("Green Building");
    buildingsDs.create(greenBuilding);
    Building blueBuilding = createBuilding("Blue Building");
    buildingsDs.create(blueBuilding);
    Building yellowBuilding = createBuilding("Yellow Building");
    buildingsDs.create(yellowBuilding);

    InMemoryDataStore<Photo> photoDs = getDataStore(Photo.class);
    photoDs.create(createPhoto("Small picture", ResourceHelper.Format.GIF));
    photoDs.create(createPhoto("Medium picture", ResourceHelper.Format.PNG));
    photoDs.create(createPhoto("Big picture", ResourceHelper.Format.JPEG));
    photoDs.create(createPhoto("Huge picture", ResourceHelper.Format.BMP));

    InMemoryDataStore<Room> roomDs = getDataStore(Room.class);
    roomDs.create(createRoom("Tiny red room", 5, 1, redBuilding));
    roomDs.create(createRoom("Small red room", 20, 1, redBuilding));
    roomDs.create(createRoom("Small green room", 20, 1, greenBuilding));
    roomDs.create(createRoom("Big blue room", 40, 1, blueBuilding));
    roomDs.create(createRoom("Huge blue room", 120, 1, blueBuilding));
    roomDs.create(createRoom("Huge yellow room", 120, 1, yellowBuilding));

    InMemoryDataStore<Employee> employeeDataStore = getDataStore(Employee.class);
    Iterator<Photo> iterator = photoDs.read().iterator();
    Photo photo = iterator.next();
    employeeDataStore.create(createEmployee("first Employee",
        new Location("Nörge", "8392", "Northpole"), 42, null,
        photo.getImage(), photo.getImageType(),
        "http://localhost/image/first.png",
        null, teamDs.read().iterator().next(), roomDs.read().iterator().next()));
    photo = iterator.next();
    employeeDataStore.create(createEmployee("Second Employee",
        new Location("Nörge", "8392", "Northpole"), 34, null,
        photo.getImage(), photo.getImageType(),
        "http://localhost/image/first.png",
        null, teamDs.read().iterator().next(), roomDs.read().iterator().next()));
  }

  private static Employee createEmployee(final String name,
      final Location location, final int age, final Calendar date,
      final byte[] image, final String imageType, final String imageUrl,
      final Manager manager, final Team team, final Room room) {
    Employee employee = new Employee();
    employee.setEmployeeName(name);
    employee.setLocation(location);
    employee.setAge(age);
    employee.setEntryDate(date);
    employee.setImage(image);
    employee.setImageType(imageType);
    employee.setImageUri(imageUrl);
    employee.setManager(manager);
    employee.setTeam(team);
    employee.setRoom(room);
    return employee;
  }

  private static Team createTeam(final String teamName, final boolean isScrumTeam) {
    return createTeam(teamName, isScrumTeam, null);
  }

  private static Team createTeam(final String teamName, final boolean isScrumTeam, Team subTeam) {
    Team team = new Team();
    team.setName(teamName);
    team.setScrumTeam(isScrumTeam);
    team.setSubTeam(subTeam);
    return team;
  }

  private static Building createBuilding(final String buildingName) {
    Building b = new Building();
    b.setName(buildingName);
    return b;
  }

  private static Photo createPhoto(final String name, final ResourceHelper.Format format) {
    Photo p = new Photo();
    p.setName(name);
    p.setImageUri("http://localhost/image/" + name);
    p.setType(format.name());
    p.setImageType("image/" + format.name().toLowerCase());
    p.setImage(ResourceHelper.generateImage(format));
    return p;
  }

  private static Room createRoom(final String name, final int seats, final int version, final Building building) {
    Room r = new Room();
    r.setName(name);
    r.setSeats(seats);
    r.setVersion(version);
    r.setBuilding(building);

    building.addRoom(r);

    return r;
  }
}

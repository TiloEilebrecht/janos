/*
 * Copyright 2013 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.olingo.odata2.janos.processor.core;

import junit.framework.Assert;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.janos.processor.api.JanosServiceFactory;
import org.apache.olingo.odata2.janos.processor.core.model.*;
import org.apache.olingo.odata2.janos.processor.core.rt.JanosServiceFactoryBuilderImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 */
public class JanosServiceFactoryFactoryImplTest {

  @Test
  public void createFromPackage() throws ODataException {
    JanosServiceFactoryBuilderImpl factory = new JanosServiceFactoryBuilderImpl();
    JanosServiceFactory service = factory.createFor(Building.class.getPackage().getName()).build();

    Assert.assertNotNull(service);
  }

  @Test
  public void createFromAnnotatedClasses() throws ODataException {
    JanosServiceFactoryBuilderImpl factory = new JanosServiceFactoryBuilderImpl();
    final Collection<Class<?>> annotatedClasses = new ArrayList<>();
    annotatedClasses.add(RefBase.class);
    annotatedClasses.add(Building.class);
    annotatedClasses.add(Employee.class);
    annotatedClasses.add(Manager.class);
    annotatedClasses.add(Photo.class);
    annotatedClasses.add(Room.class);
    annotatedClasses.add(Team.class);
    JanosServiceFactory service = factory.createFor(annotatedClasses).build();

    Assert.assertNotNull(service);
  }

  @Test(expected = ODataException.class)
  public void createFromClasses() throws ODataException {
    JanosServiceFactoryBuilderImpl factory = new JanosServiceFactoryBuilderImpl();

    final Collection<Class<?>> notAnnotatedClasses = new ArrayList<>();
    notAnnotatedClasses.add(String.class);
    notAnnotatedClasses.add(Long.class);
    JanosServiceFactory service = factory.createFor(notAnnotatedClasses).build();

    Assert.assertNotNull(service);
  }
}

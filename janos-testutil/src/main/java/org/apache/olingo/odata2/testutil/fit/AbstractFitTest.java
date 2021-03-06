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
package org.apache.olingo.odata2.testutil.fit;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.janos.processor.api.JanosServiceFactory;
import org.apache.olingo.odata2.testutil.TestUtilRuntimeException;
import org.apache.olingo.odata2.testutil.data.JanosSampleDataGenerator;
import org.apache.olingo.odata2.testutil.server.ServerRuntimeException;
import org.apache.olingo.odata2.testutil.server.TestServer;
import org.junit.After;
import org.junit.Before;

import java.net.URI;

/**
 *  
 */
public abstract class AbstractFitTest extends BaseTest {

  private final TestServer server;

  private JanosServiceFactory service;

  private final HttpClient httpClient = new DefaultHttpClient();

  public AbstractFitTest() {
    server = new TestServer(this.getClass().getSimpleName());
  }

  public AbstractFitTest(String prefix) {
    server = new TestServer(prefix + this.getClass().getSimpleName());
  }

  protected URI getEndpoint() {
    return server.getEndpoint();
  }

  protected HttpClient getHttpClient() {
    return httpClient;
  }

  protected JanosServiceFactory getService() {
    return service;
  }

  protected void startCustomServer(Class<? extends FitStaticServiceFactory> factoryClass){
    try {
      service = createService();
      server.startServer(service, factoryClass);
    } catch (final ODataException e) {
      throw new TestUtilRuntimeException(e);
    }
  }
  
  protected void stopCustomServer(){
    try {
      server.stopServer();
    } catch (final ServerRuntimeException e) {
      throw new TestUtilRuntimeException(e);
    }
  }
  
  protected abstract JanosServiceFactory createService() throws ODataException;

  @Before
  public void before() {
    try {
      service = createService();
      server.startServer(service);

      JanosSampleDataGenerator.generateData(getEndpoint().toASCIIString());

    } catch (final ODataException e) {
      throw new TestUtilRuntimeException(e);
    }
  }

  @After
  public void after() {
    try {
      server.stopServer();
    } catch (final ServerRuntimeException e) {
      throw new TestUtilRuntimeException(e);
    }
  }
}

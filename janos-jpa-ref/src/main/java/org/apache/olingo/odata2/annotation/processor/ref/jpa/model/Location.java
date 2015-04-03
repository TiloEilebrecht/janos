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
package org.apache.olingo.odata2.annotation.processor.ref.jpa.model;

import org.apache.olingo.odata2.api.annotation.edm.EdmComplexType;
import org.apache.olingo.odata2.api.annotation.edm.EdmProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 *  
 */
@Entity
@EdmComplexType(name = "c_Location", namespace = ModelSharedConstants.NAMESPACE_1)
public class Location {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private String id;
  @EdmProperty
  private String country;
  @OneToOne
  @EdmProperty
  private City city;

  public Location() {}

  public Location(final String country, final String postalCode, final String cityName) {
    this.country = country;
    city = new City(postalCode, cityName);
  }

  public void setCountry(final String country) {
    this.country = country;
  }

  public String getCountry() {
    return country;
  }

  public void setCity(final City city) {
    this.city = city;
  }

  public City getCity() {
    return city;
  }

  @Override
  public String toString() {
    return String.format("%s, %s", country, city.toString());
  }


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}

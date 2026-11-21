/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.solr.handler.dataimport;

import org.apache.solr.common.SolrException;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Checks that the dataConfig request parameter is refused unless
 * the system property {@link DataImportHandler#ENABLE_DIH_DATA_CONFIG_PARAM} is set.
 */
public class TestDataConfigParamDisabled extends AbstractDataImportHandlerTestCase {

  private static final String DATA_CONFIG = "<dataConfig><document></document></dataConfig>";

  @BeforeClass
  public static void beforeClass() throws Exception {
    // the base class enables the param; the handler reads the property when the core is created
    System.clearProperty(DataImportHandler.ENABLE_DIH_DATA_CONFIG_PARAM);
    initCore("dataimport-solrconfig.xml", "dataimport-schema.xml");
  }

  @Test
  public void testDataConfigParamRefused() throws Exception {
    LocalSolrQueryRequest request = lrf.makeRequest("command", "full-import",
        "debug", "on", "clean", "true", "commit", "true", "dataConfig", DATA_CONFIG);
    ignoreException("dataConfig param");
    try {
      h.query("/dataimport", request);
      fail("dataConfig param should be refused without " + DataImportHandler.ENABLE_DIH_DATA_CONFIG_PARAM);
    } catch (SolrException e) {
      assertEquals(SolrException.ErrorCode.FORBIDDEN.code, e.code());
    } finally {
      unIgnoreException("dataConfig param");
    }
  }

  @Test
  public void testRequestWithoutDataConfigParamAllowed() throws Exception {
    LocalSolrQueryRequest request = lrf.makeRequest("command", "status");
    String response = h.query("/dataimport", request);
    assertTrue(response, response.contains("idle"));
  }
}

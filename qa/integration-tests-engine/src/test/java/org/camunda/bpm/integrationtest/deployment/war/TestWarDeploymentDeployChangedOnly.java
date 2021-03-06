/**
 * Copyright (C) 2011, 2012 camunda services GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.integrationtest.deployment.war;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.integrationtest.util.AbstractFoxPlatformIntegrationTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;



@RunWith(Arquillian.class)
public class TestWarDeploymentDeployChangedOnly extends AbstractFoxPlatformIntegrationTest {

  private static final String PA1 = "PA1";
  private static final String PA2 = "PA2";

  @Deployment(order=1, name=PA1)
  public static WebArchive processArchive1() {
    return initWebArchiveDeployment("pa1.war")
      .addAsResource("org/camunda/bpm/integrationtest/deployment/war/testDeployProcessArchiveV1.bpmn20.xml")
      .addAsResource("org/camunda/bpm/integrationtest/deployment/war/testDeployProcessArchiveUnchanged.bpmn20.xml");
  }

  @Deployment(order=2, name=PA2)
  public static WebArchive processArchive2() {
    return initWebArchiveDeployment("pa2.war", "org/camunda/bpm/integrationtest/deployment/war/deployChangedOnly_processes.xml")
      .addAsResource("org/camunda/bpm/integrationtest/deployment/war/testDeployProcessArchiveV2.bpmn20.xml")
      .addAsResource("org/camunda/bpm/integrationtest/deployment/war/testDeployProcessArchiveUnchanged.bpmn20.xml");

  }

  @Test
  @OperateOnDeployment(value=PA2)
  public void testDeployProcessArchive() {
    Assert.assertNotNull(processEngine);
    RepositoryService repositoryService = processEngine.getRepositoryService();
    long count = repositoryService.createProcessDefinitionQuery()
      .processDefinitionKey("testDeployProcessArchive")
      .count();

    Assert.assertEquals(2, count);

    count = repositoryService.createProcessDefinitionQuery()
        .processDefinitionKey("testDeployProcessArchiveUnchanged")
        .count();

    Assert.assertEquals(1, count);
  }


}

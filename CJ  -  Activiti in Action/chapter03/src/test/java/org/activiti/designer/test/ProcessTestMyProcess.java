package org.activiti.designer.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessTestMyProcess
{

	private String filename = "/simple.bpmn";

	private Logger logger = LoggerFactory.getLogger(ProcessTestMyProcess.class);

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule();

	@Test
	public void startProcess() throws Exception
	{
		try (InputStream lIS = this.getClass().getResourceAsStream(this.filename))
		{
			RepositoryService lRepositoryService = this.activitiRule.getRepositoryService();
			lRepositoryService
					.createDeployment()
					.addInputStream("myProcess.bpmn20.xml", lIS)
					.deploy();
			RuntimeService lRuntimeService = this.activitiRule.getRuntimeService();
			Map<String, Object> lVariables = new HashMap<String, Object>();
			lVariables.put("name", "Activiti");
			ProcessInstance lProcessInstance = lRuntimeService.startProcessInstanceByKey(
					"myProcess",
					lVariables);
			assertThat(lProcessInstance.getId()).as("process id").isNotNull();
			this.logger.info(
					String.format(
							"%s: process definition id is %s, process key is %s",
							lProcessInstance.getId(),
							lProcessInstance.getProcessDefinitionId(),
							lProcessInstance.getProcessDefinitionKey()));
		}
	}
}
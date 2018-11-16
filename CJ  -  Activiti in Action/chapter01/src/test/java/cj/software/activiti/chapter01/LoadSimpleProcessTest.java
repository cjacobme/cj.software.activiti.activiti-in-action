package cj.software.activiti.chapter01;

import static org.assertj.core.api.Assertions.assertThat;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadSimpleProcessTest
{
	private Logger logger = LoggerFactory.getLogger(LoadSimpleProcessTest.class);

	@Test
	public void startBookOrder()
	{
		ProcessEngine lProcessEngine = ProcessEngineConfiguration
				.createStandaloneInMemProcessEngineConfiguration()
				.buildProcessEngine();
		RepositoryService lRepoService = lProcessEngine.getRepositoryService();
		lRepoService
				.createDeployment()
				.addClasspathResource("bookorder.simple.bpmn20.xml")
				.deploy();

		RuntimeService lRuntimeService = lProcessEngine.getRuntimeService();
		ProcessInstance lProcessInstance = lRuntimeService.startProcessInstanceByKey(
				"simple-book-order");
		assertThat(lProcessInstance).as("loaded process instance").isNotNull();
		assertThat(lProcessInstance.getId()).as("id").isNotNull();
		this.logger.info(
				String.format(
						"process instance with id %s and definition-id %s",
						lProcessInstance.getId(),
						lProcessInstance.getProcessDefinitionId()));
	}
}

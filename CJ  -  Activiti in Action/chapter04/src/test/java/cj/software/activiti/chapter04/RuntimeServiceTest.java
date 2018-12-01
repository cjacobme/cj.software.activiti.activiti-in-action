package cj.software.activiti.chapter04;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

public class RuntimeServiceTest
{
	private static RuntimeService runtimeService;

	private Logger logger = LogManager.getLogger(RuntimeServiceTest.class);

	@BeforeClass
	public static void initRuntimeService()
	{
		ProcessEngine lProcessEngine = ProcessEngineConfiguration
				.createStandaloneInMemProcessEngineConfiguration()
				.setDatabaseSchemaUpdate("drop-create")
				.buildProcessEngine();
		RepositoryService lRepositoryService = lProcessEngine.getRepositoryService();
		lRepositoryService.createDeployment().addClasspathResource("bookorder.bpmn20.xml").deploy();

		runtimeService = lProcessEngine.getRuntimeService();
	}

	@Test
	public void startProcessInstance()
	{
		Map<String, Object> lVariables = new HashMap<>();
		lVariables.put("isbn", "654321");
		ProcessInstance lProcessInstance = runtimeService.startProcessInstanceByKey(
				"bookorder",
				lVariables);
		assertThat(lProcessInstance).as("process instance").isNotNull();
		this.logger.info(
				String.format("Process instantiated with id %s", lProcessInstance.getId()));
	}

	@Test
	public void queryProcessInstance()
	{
		Map<String, Object> lVariables = new HashMap<>();
		lVariables.put("isbn", "13579");
		runtimeService.startProcessInstanceByKey("bookorder", lVariables);
		List<ProcessInstance> lProcessInstances = runtimeService
				.createProcessInstanceQuery()
				.processDefinitionKey("bookorder")
				.list();
		for (ProcessInstance bProcessInstance : lProcessInstances)
		{
			assertThat(bProcessInstance.isEnded()).isFalse();
			this.logger.info(
					String.format(
							"process instance with id %s is ended: %s",
							bProcessInstance.getId(),
							String.valueOf(bProcessInstance.isEnded())));
		}
	}
}

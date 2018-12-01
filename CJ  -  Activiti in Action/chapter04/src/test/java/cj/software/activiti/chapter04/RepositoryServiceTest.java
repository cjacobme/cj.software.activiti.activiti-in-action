package cj.software.activiti.chapter04;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;

public class RepositoryServiceTest
{
	@Rule
	public ActivitiRule activitiRule = new ActivitiRule("activiti.cfg-mem.xml");

	private Logger logger = LogManager.getLogger(RepositoryServiceTest.class);

	@Test
	public void deleteDeployment()
	{
		RepositoryService lRepositoryService = this.activitiRule.getRepositoryService();
		Deployment lDeployment = lRepositoryService
				.createDeployment()
				.addClasspathResource("bookorder.bpmn20.xml")
				.deploy();
		String lDeploymentId = lDeployment.getId();

		Deployment lFoundDeployment = lRepositoryService
				.createDeploymentQuery()
				.deploymentId(lDeploymentId)
				.singleResult();
		assertThat(lFoundDeployment).as("found deployment").isNotNull();
		this.logger.info(
				String.format(
						"found deployment with id %s, deployed at %s",
						lFoundDeployment.getId(),
						lFoundDeployment.getDeploymentTime()));

		ProcessDefinition lProcDef = lRepositoryService
				.createProcessDefinitionQuery()
				.latestVersion()
				.singleResult();
		assertThat(lProcDef).as("found process definition").isNotNull();
		assertThat(lProcDef.getKey()).isEqualTo("bookorder");
		this.logger.info(String.format("Found process definition %s", lProcDef.getId()));

		RuntimeService lRuntimeService = this.activitiRule.getRuntimeService();
		Map<String, Object> lVariables = new HashMap<>();
		lVariables.put("isbn", "1324981234");
		lRuntimeService.startProcessInstanceByKey("bookorder", lVariables);

		ProcessInstance lFoundProcInst = lRuntimeService
				.createProcessInstanceQuery()
				.processDefinitionId(lProcDef.getId())
				.singleResult();
		assertThat(lFoundProcInst).as("found process instance").isNotNull();

		lRepositoryService.deleteDeployment(lDeploymentId, true);

		lFoundDeployment = lRepositoryService
				.createDeploymentQuery()
				.deploymentId(lDeploymentId)
				.singleResult();
		assertThat(lFoundDeployment).as("found deployment").isNull();

		lFoundProcInst = lRuntimeService
				.createProcessInstanceQuery()
				.processDefinitionId(lProcDef.getId())
				.singleResult();
		assertThat(lFoundProcInst).as("found process instance").isNull();
	}

}

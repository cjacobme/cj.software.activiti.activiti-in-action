package cj.software.activiti.chapter04;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;

public class IdentityServiceTest
{
	@Rule
	public ActivitiRule activitiRule = new ActivitiRule("activiti.cfg-mem.xml");

	private Logger logger = LogManager.getLogger(IdentityServiceTest.class);

	@Test
	@Deployment(resources =
	{ "bookorder.bpmn20.xml"
	})
	public void membership()
	{
		IdentityService lIdentityService = this.activitiRule.getIdentityService();

		User lNewUser = lIdentityService.newUser("John Doe");
		lIdentityService.saveUser(lNewUser);
		this.logger.info(String.format("new User with id %s created", lNewUser.getId()));

		User lFoundUser = lIdentityService.createUserQuery().singleResult();
		assertThat(lFoundUser).as("found user").isNotNull();
		assertThat(lFoundUser.getId()).as("user id").isEqualTo("John Doe");

		Group lNewGroup = lIdentityService.newGroup("sales");
		lNewGroup.setName("Sales");
		lIdentityService.saveGroup(lNewGroup);
		this.logger.info(
				String.format(
						"new Group with id %s and name %s created",
						lNewGroup.getId(),
						lNewGroup.getName()));

		Group lFoundGroup = lIdentityService.createGroupQuery().singleResult();
		assertThat(lFoundGroup).isNotNull();
		assertThat(lFoundGroup.getId()).as("group id").isEqualTo("sales");

		lIdentityService.createMembership("John Doe", "sales");

		lIdentityService.setAuthenticatedUserId("John Doe");

		RuntimeService lRuntimeService = this.activitiRule.getRuntimeService();
		Map<String, Object> lVariables = new HashMap<>();
		lVariables.put("isbn", "öosudbhfg");
		ProcessInstance lProcInstance = lRuntimeService.startProcessInstanceByKey(
				"bookorder",
				lVariables);
		String lProcInstanceId = lProcInstance.getId();
		this.logger.info(String.format("started process instance with id %s", lProcInstanceId));

		TaskService lTaskService = this.activitiRule.getTaskService();
		Task lTask = lTaskService.createTaskQuery().taskCandidateUser("John Doe").singleResult();
		assertThat(lTask).as("found task").isNotNull();
		assertThat(lTask.getName()).as("task name").isEqualTo("Complete order");
		this.logger.info(String.format("assigned to John Doe: \"%s\"", lTask.getName()));
	}
}

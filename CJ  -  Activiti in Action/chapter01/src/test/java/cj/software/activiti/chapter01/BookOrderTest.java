package cj.software.activiti.chapter01;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BookOrderTest
{
	private Logger logger = LoggerFactory.getLogger(BookOrderTest.class);

	@Test
	public void startBookOrder()
	{
		ProcessEngine lProcessEngine = ProcessEngineConfiguration
				.createStandaloneInMemProcessEngineConfiguration()
				.buildProcessEngine();
		try
		{

			RepositoryService lRepoService = lProcessEngine.getRepositoryService();
			RuntimeService lRuntimeService = lProcessEngine.getRuntimeService();
			IdentityService lIdentityService = lProcessEngine.getIdentityService();
			TaskService lTaskService = lProcessEngine.getTaskService();

			lRepoService.createDeployment().addClasspathResource("bookorder.bpmn20.xml").deploy();

			// remove tasks already present
			List<Task> availableTaskList = lTaskService
					.createTaskQuery()
					.taskName("Work on order")
					.list();
			for (Task task : availableTaskList)
			{
				lTaskService.complete(task.getId());
			}

			Map<String, Object> lVariables = new HashMap<String, Object>();
			lVariables.put("isbn", "123456");
			lIdentityService.setAuthenticatedUserId("kermit");
			ProcessInstance lProcessInstance = lRuntimeService.startProcessInstanceByKey(
					"bookorder",
					lVariables);
			assertThat(lProcessInstance).as("process instance").isNotNull();
			assertThat(lProcessInstance.getId()).as("process instance id").isNotNull();
			List<Task> lTaskList = lTaskService.createTaskQuery().taskName("Work on order").list();
			assertThat(lTaskList).as("list of tasks for kermit").hasSize(1);
			Task lTask = lTaskList.get(0);
			this.logger.info(String.format("%s: found task %s", lTask.getId(), lTask.getName()));

			lTaskService.complete(lTask.getId());

			lTaskList = lTaskService.createTaskQuery().taskName("Work on order").list();
			assertThat(lTaskList).as("list of tasks after finishing the first one").isEmpty();
		}
		finally
		{
			lProcessEngine.close();
		}
	}
}

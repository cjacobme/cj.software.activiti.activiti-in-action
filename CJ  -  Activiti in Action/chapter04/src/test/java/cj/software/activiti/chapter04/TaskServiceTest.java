package cj.software.activiti.chapter04;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;

public class TaskServiceTest
{
	@Rule
	public ActivitiRule activitiRule = new ActivitiRule("activiti.cfg-mem.xml");

	private Logger logger = LogManager.getLogger(TaskServiceTest.class);

	@Test
	@Deployment(resources = "bookorder.bpmn20.xml")
	public void queryTask()
	{
		this.startProcessInstance();

		TaskService lTaskService = this.activitiRule.getTaskService();
		Task lTask = lTaskService.createTaskQuery().taskCandidateGroup("sales").singleResult();
		assertThat(lTask).isNotNull();
		assertThat(lTask.getName()).as("task name").isEqualTo("Complete order");
		this.logger.info(
				String.format(
						"found task has name \"%s\", id \"%s\", definition-key \"%s\"",
						lTask.getName(),
						lTask.getId(),
						lTask.getTaskDefinitionKey()));
	}

	private void startProcessInstance()
	{
		RuntimeService lRuntimeService = this.activitiRule.getRuntimeService();
		Map<String, Object> lVariables = new HashMap<>();
		lVariables.put("isbn", "123456");
		lRuntimeService.startProcessInstanceByKey("bookorder", lVariables);
	}
}

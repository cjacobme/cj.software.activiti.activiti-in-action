package cj.software.activiti.chapter04;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-test-application-context.xml")
public class SpringTest
{
	private Logger logger = LogManager.getLogger(SpringTest.class);

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;

	@Before
	public void cleanActiviti()
	{
		List<Task> lTasks = this.taskService.createTaskQuery().list();
		for (Task bTask : lTasks)
		{
			this.taskService.complete(bTask.getId());
			this.taskService.deleteTask(bTask.getId());
			this.logger.info(String.format("deleted task %s %s", bTask.getId(), bTask.getName()));
		}
	}

	@Test
	public void simpleSpringExecution()
	{
		Map<String, Object> lVariables = new HashMap<>();
		lVariables.put("isbn", 123456L);
		this.runtimeService.startProcessInstanceByKey("bookorder", lVariables);
		Task lTask = this.taskService.createTaskQuery().singleResult();
		assertThat(lTask).isNotNull();
		assertThat(lTask.getName()).isEqualTo("Complete order");
		this.taskService.complete(lTask.getId());
		long lNumProcesses = this.runtimeService.createProcessInstanceQuery().count();
		assertThat(lNumProcesses).isEqualTo(0L);
	}
}

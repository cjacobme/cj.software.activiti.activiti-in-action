package cj.software.activiti.chapter04;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;

public class HistoryServiceTest
{
	@Rule
	public ActivitiRule activitiRule = new ActivitiRule("activiti.cfg-mem-fullhistory.xml");

	private Logger logger = Logger.getLogger(HistoryServiceTest.class);

	private String startAndComplete()
	{
		RuntimeService lRuntimeService = this.activitiRule.getRuntimeService();
		Map<String, Object> lVariables = new HashMap<>();
		lVariables.put("isbn", "123456");
		String lProcessInstanceId = lRuntimeService
				.startProcessInstanceByKey("bookorder", lVariables)
				.getId();

		TaskService lTaskService = this.activitiRule.getTaskService();
		Task lTask = lTaskService.createTaskQuery().taskCandidateGroup("sales").singleResult();
		lVariables = new HashMap<>();
		lVariables.put("extraInfo", "Extra Information");
		lVariables.put("isbn", "654321");
		lTaskService.complete(lTask.getId(), lVariables);

		return lProcessInstanceId;
	}

	@Test
	@Deployment(resources =
	{ "bookorder.bpmn20.xml"
	})
	public void queryHistoricInstances()
	{
		String lProcessInstanceID = this.startAndComplete();
		this.logger.info(String.format("Process completed with id %s", lProcessInstanceID));

		HistoryService lHistoryService = this.activitiRule.getHistoryService();
		HistoricProcessInstance lHistoricProcessInstance = lHistoryService
				.createHistoricProcessInstanceQuery()
				.processInstanceId(lProcessInstanceID)
				.singleResult();
		assertThat(lHistoricProcessInstance).isNotNull();
		assertThat(lHistoricProcessInstance.getId())
				.as("id of historic process instance")
				.isEqualTo(lProcessInstanceID);
		this.logger.info(
				String.format(
						"history process with definition id %s started at %s, "
								+ "ended at %s, duration %d ms",
						lHistoricProcessInstance.getProcessDefinitionId(),
						lHistoricProcessInstance.getStartTime(),
						lHistoricProcessInstance.getEndTime(),
						lHistoricProcessInstance.getDurationInMillis()));
	}

	@Test
	@Deployment(resources =
	{ "bookorder.bpmn20.xml"
	})
	public void queryHistoricActivities()
	{
		startAndComplete();

		HistoryService lHistoryService = this.activitiRule.getHistoryService();
		List<HistoricActivityInstance> lActivities = lHistoryService
				.createHistoricActivityInstanceQuery()
				.list();
		assertThat(lActivities).as("list of historic activities").hasSize(4);

		for (HistoricActivityInstance bHistActivity : lActivities)
		{
			assertThat(bHistActivity.getActivityId()).isNotNull();
			this.logger.info(
					String.format(
							"history activity %-15s, type %-12s, duration %03d ms",
							bHistActivity.getActivityName(),
							bHistActivity.getActivityType(),
							bHistActivity.getDurationInMillis()));
		}
	}
}

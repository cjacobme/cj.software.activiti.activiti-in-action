package cj.software.activiti.chapter04;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.ProcessEngineImpl;
import org.activiti.engine.impl.jobexecutor.JobExecutor;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;

public class JavaBpmnTest
{
	@Rule
	public ActivitiRule activitiRule = new ActivitiRule("activiti.cfg-mem.xml");

	private Logger logger = Logger.getLogger(JavaBpmnTest.class);

	private ProcessInstance startProcessInstance()
	{
		RuntimeService lRuntimeService = this.activitiRule.getRuntimeService();
		Map<String, Object> lVariables = new HashMap<>();
		lVariables.put("isbn", 123456l);
		ProcessInstance lResult = lRuntimeService.startProcessInstanceByKey(
				"bookorder",
				lVariables);
		return lResult;
	}

	@Test
	@Deployment(resources =
	{ "bookorder.java.bpmn20.xml"
	})
	public void withJavaService()
	{
		ProcessInstance lProcessInstance = this.startProcessInstance();
		RuntimeService lRuntimeService = this.activitiRule.getRuntimeService();
		OffsetDateTime lValidationTimestamp = (OffsetDateTime) lRuntimeService.getVariable(
				lProcessInstance.getId(),
				ValidateService.KEY_VALIDATION_TIMESTAMP);
		assertThat(lValidationTimestamp).isNotNull();

		this.logger.info(String.format("validation timestamp is %s", lValidationTimestamp));
	}

	@Test
	@Deployment(resources =
	{ "bookorder.java.async.bpmn20.xml"
	})
	public void asyncInvocation()
	{
		JobExecutor lJobExecutor = ((ProcessEngineImpl) this.activitiRule.getProcessEngine())
				.getProcessEngineConfiguration()
				.getJobExecutor();
		lJobExecutor.start();
		try
		{
			ProcessInstance lProcessInstance = this.startProcessInstance();
			boolean lWaitFor = waitForJobExecutorToProcessAllJobs(7000, 1000);
			assertThat(lWaitFor).as("wait for").isFalse();
			RuntimeService lRuntimeService = this.activitiRule.getRuntimeService();
			OffsetDateTime lValidationTimestamp = (OffsetDateTime) lRuntimeService.getVariable(
					lProcessInstance.getId(),
					ValidateService.KEY_VALIDATION_TIMESTAMP);
			assertThat(lValidationTimestamp).isNotNull();

			this.logger.info(
					String.format("validation timestamp (async) is %s", lValidationTimestamp));
		}
		finally
		{
			lJobExecutor.shutdown();
		}
	}

	private boolean waitForJobExecutorToProcessAllJobs(long maxMillisToWait, long intervalMillis)
	{
		Timer lTimer = new Timer();
		InteruptTask lTask = new InteruptTask(Thread.currentThread());
		lTimer.schedule(lTask, maxMillisToWait);
		boolean lAreJobsAvailable = true;
		try
		{
			while (lAreJobsAvailable && !lTask.isTimeLimitExceeded())
			{
				Thread.sleep(intervalMillis);
				lAreJobsAvailable = areJobsAvailable();
				this.logger.info(String.format("woke up: %s", String.valueOf(lAreJobsAvailable)));
			}
		}
		catch (InterruptedException e)
		{
		}
		finally
		{
			lTimer.cancel();
		}
		return lAreJobsAvailable;
	}

	public boolean areJobsAvailable()
	{
		return !this.activitiRule
				.getManagementService()
				.createJobQuery()
				.executable()
				.list()
				.isEmpty();
	}

	private static class InteruptTask
			extends TimerTask
	{
		protected boolean timeLimitExceeded = false;
		protected Thread thread;

		public InteruptTask(Thread thread)
		{
			this.thread = thread;
		}

		public boolean isTimeLimitExceeded()
		{
			return this.timeLimitExceeded;
		}

		@Override
		public void run()
		{
			this.timeLimitExceeded = true;
			this.thread.interrupt();
		}
	}
}

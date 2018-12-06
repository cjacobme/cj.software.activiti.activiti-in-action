package cj.software.activiti.chapter04;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.RuntimeService;
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
}

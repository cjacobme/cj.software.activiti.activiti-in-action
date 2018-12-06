package cj.software.activiti.chapter04;

import java.time.OffsetDateTime;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.log4j.Logger;

/**
 * a Java Server Task that validates a book order.
 */
public class ValidateService
		implements
		JavaDelegate
{
	private Logger logger = Logger.getLogger(ValidateService.class);

	public static final String KEY_VALIDATION_TIMESTAMP = "validatione-timestamp";

	@Override
	public void execute(DelegateExecution pExecution) throws Exception
	{
		this.logger.info(String.format("Execution id \"%s\"", pExecution.getId()));
		Long lISBN = (Long) pExecution.getVariable("isbn");
		this.logger.info(String.format("Received isbn %d", lISBN));
		OffsetDateTime lNow = OffsetDateTime.now();
		this.logger.info(String.format("Validation timestamp is %s", lNow));
		pExecution.setVariable(KEY_VALIDATION_TIMESTAMP, lNow);
	}

}

package cj.software.activiti.chapter04;

import java.time.OffsetDateTime;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.el.Expression;
import org.apache.log4j.Logger;

/**
 * a Java Server Task that validates a book order.
 */
public class ValidateServiceWithExpressions
		implements
		JavaDelegate
{
	private Expression validateText;

	private Expression isbn;

	private Logger logger = Logger.getLogger(ValidateServiceWithExpressions.class);

	@Override
	public void execute(DelegateExecution pExecution) throws Exception
	{
		this.logger.info(String.format("Execution id \"%s\"", pExecution.getId()));
		Long lISBN = (Long) this.isbn.getValue(pExecution);
		this.logger.info(String.format("Received isbn %d", lISBN));
		OffsetDateTime lNow = OffsetDateTime.now();
		pExecution.setVariable(ValidateService.KEY_VALIDATION_TIMESTAMP, lNow);
		String lValidateText = this.validateText.getValue(pExecution).toString();
		this.logger.info(String.format("%s %s", lValidateText, lNow));
	}

}

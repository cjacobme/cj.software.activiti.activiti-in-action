package cj.software.activiti.chapter04.spring;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class OrderService
{
	private Logger logger = LogManager.getLogger(OrderService.class);

	public void validate(DelegateExecution pExecution)
	{
		this.logger.info(String.format("validating for isbn %s", pExecution.getVariable("isbn")));
	}

}

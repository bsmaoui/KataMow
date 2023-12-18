package com.kata.mow.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.file.transform.FlatFileFormatException;
import org.springframework.stereotype.Component;

import com.kata.mow.CommandLineJobRunner;


@Component
public class BatchStepSkipper implements SkipPolicy {
	
	protected static final Log logger = LogFactory.getLog(BatchStepSkipper.class);
	
	@Override
	public boolean shouldSkip(Throwable t, long skipCount) throws SkipLimitExceededException {
		if(t instanceof FlatFileFormatException && !"bloking".equals(((FlatFileFormatException) t).getInput())) {
			return true;
		}
		logger.error(t.getMessage());
		return false;
	}
}

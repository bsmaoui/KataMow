package com.kata.mow;


import java.time.LocalDateTime;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.ExitCodeMapper;
import org.springframework.batch.core.launch.support.JvmSystemExiter;
import org.springframework.batch.core.launch.support.SimpleJvmExitCodeMapper;
import org.springframework.batch.core.launch.support.SystemExiter;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class CommandLineJobRunner {

	protected static final Log logger = LogFactory.getLog(CommandLineJobRunner.class);

	private ExitCodeMapper exitCodeMapper = new SimpleJvmExitCodeMapper();

	private JobLauncher launcher;

	private SystemExiter systemExiter = new JvmSystemExiter();
	
	public void setLauncher(JobLauncher launcher) {
		this.launcher = launcher;
	}

	public void setExitCodeMapper(ExitCodeMapper exitCodeMapper) {
		this.exitCodeMapper = exitCodeMapper;
	}

	public void setSystemExiter(SystemExiter systemExitor) {
		this.systemExiter = systemExitor;
	}

	public void exit(int status) {
		systemExiter.exit(status);
	}

	int start(String filePath) {
		ConfigurableApplicationContext context = null;

		try {
			context = new AnnotationConfigApplicationContext("com.kata.mow.*");
			
			context.getAutowireCapableBeanFactory().autowireBeanProperties(this,
					AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, false);
			

			Job job = (Job) context.getBean("mowJob");
			
		
			JobParametersBuilder builder = new JobParametersBuilder();
            builder.addString("startDate", LocalDateTime.now().toString());
            builder.addString("inputData", filePath);

			JobExecution jobExecution = launcher.run(job, builder.toJobParameters());
			return exitCodeMapper.intValue(jobExecution.getExitStatus().getExitCode());
		}
		catch (Throwable e) {
			logger.error("Job Terminated with error:", e);
			return exitCodeMapper.intValue(ExitStatus.FAILED.getExitCode());
		}
		finally {
			if (context != null) {
				context.close();
			}
		}
	}
	
	public static void main(String[] args) {

		CommandLineJobRunner command = new CommandLineJobRunner();
		if (args.length < 1) {
			logger.error("File path is mandatory !");
			command.exit(1);
		}
		int result = command.start(args[0]);
		command.exit(result);
	}

}


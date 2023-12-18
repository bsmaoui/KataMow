package com.kata.mow;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBatchTest	
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-testko.properties")
public class SpringbtDemoApplicationTestsKO {
	
	private JobLauncherTestUtils jobLaucherTestUtils;
	private JobLauncher jobLauncher;
    private Job myJob;
    
	@Before
    public void setUp() {
        // spring context
		@SuppressWarnings("resource")
		ConfigurableApplicationContext resource = new AnnotationConfigApplicationContext("com.kata.mow.*");
        jobLauncher = resource.getBean(JobLauncher.class);
        myJob = (Job) resource.getBean("mowJob");

        jobLaucherTestUtils = new JobLauncherTestUtils();
        jobLaucherTestUtils.setJobLauncher(jobLauncher);
        jobLaucherTestUtils.setJob(myJob);
    }
	
	@Test
	public void testSpringbtDemoApplicationTests() throws Exception {
		JobExecution jobExecution = jobLaucherTestUtils.launchJob();
		Assert.assertEquals(BatchStatus.FAILED, jobExecution.getStatus());
	}

}

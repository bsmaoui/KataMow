package com.kata.mow.reader;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class FlatFileReader<InputData> extends FlatFileItemReader<InputData> {

	// Custom constructor to set the lineMapper and resource
	public FlatFileReader(LineMapper<InputData> lineMapper, @Value("#{jobParameters['inputData']}") String inputDataPath) {
		super();
		// Set the lineMapper to map lines to domain objects
		this.setLineMapper(lineMapper);
		FileSystemResource fileResource = new FileSystemResource(inputDataPath);
		// Set the input resource (file to read)
		this.setResource(fileResource);
		this.setStrict(false);
	}

}

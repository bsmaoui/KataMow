package com.kata.mow.processor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.transform.FlatFileFormatException;
import org.springframework.stereotype.Component;

import com.kata.mow.CommandLineJobRunner;
import com.kata.mow.model.InputData;
import com.kata.mow.utils.Constants;

@Component
public class MowItemProcessor implements ItemProcessor<InputData, InputData> {

	protected static final Log logger = LogFactory.getLog(MowItemProcessor.class);	

	private int mowX = -1;
	private int mowY = -1;
	private String orientation = null;
	
    private ExecutionContext executionContext;
	
	@BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        executionContext = stepExecution.getExecutionContext();
    }
	
	@AfterStep
    public void afterStep(StepExecution stepExecution) {
		// clear context after step finished
		executionContext.put("x", null);
		executionContext.put("y", null);
    }
	
	@Override
	public InputData process(InputData inputData) throws Exception {
		// Manage first line of file (x and y) of grid
		String[] splittedLine = inputData.getLine().split(" ");
		if(executionContext.get("x") == null && executionContext.get("y") == null) {
			initiliazeGrid(inputData, splittedLine);
		}
		// manage first line of mower
		if(orientation == null) {
			mowerPosition(inputData, splittedLine);
		}
		// manage seconde line of mower
		// check if valid param
		isValidCommands(inputData, splittedLine); 
		
		inputData.setOrientation(orientation);
		inputData.setCoordX(mowX);
		inputData.setCoordY(mowY);
		inputData.setControl(inputData.getLine());
		
		var sb = new StringBuilder();
		sb.append("Input  : X= ").append(inputData.getCoordX()).append(" , Y = ").append(inputData.getCoordY())
		.append(" , Orientation = ").append(inputData.getOrientation());
		System.out.println(sb);
		// execute command
		for (int i = 0; i < inputData.getControl().length(); i++) {
			char c = inputData.getControl().charAt(i);
			if (c == Constants.D) {
				inputData.turnD();
			}
			if (c == Constants.G) {
				inputData.turnG();
			}
			if (c == Constants.A) {
				inputData.move(executionContext.getInt("x"), executionContext.getInt("y"));
			}
		}
		orientation = null; 
		return inputData;
	}

	private void isValidCommands(InputData inputData, String[] splittedLine) {
		if(splittedLine.length == 1 && !isValidControl(inputData.getLine())) {
			logger.error(inputData.toString());
			throw new FlatFileFormatException("Incorrect format in the second line of mower "+inputData.getLine(),"bloking");
		}
	}

	private void mowerPosition(InputData inputData, String[] splittedLine) {
		if(splittedLine.length == 3) {
			mowX = Integer.parseInt(splittedLine[0]);
			mowY = Integer.parseInt(splittedLine[1]);
			orientation = splittedLine[2];
		}
		// check if valid param
		if(mowX <0 || mowX > executionContext.getInt("x") || mowY < 0 || mowY > executionContext.getInt("y") || !isValidOrientation(orientation)){
			logger.error(inputData.toString());
			throw new FlatFileFormatException("Incorrect format in the firt line of mower :"+inputData.getLine(),"bloking");
		}
		//skip first line of mower without error
		throw new FlatFileFormatException("Skip first line of mow");
	}

	private void initiliazeGrid(InputData inputData, String[] splittedLine) {
		if(splittedLine.length == 2) {  
			//save x and y in context
			executionContext.putInt("x",  Integer.parseInt(splittedLine[0]));
			executionContext.putInt("y",  Integer.parseInt(splittedLine[1]));
		}
		// check if valid param
		if(executionContext.getInt("x") < 0 || executionContext.getInt("y") < 0) {
			logger.error(inputData.toString());
			throw new FlatFileFormatException("Incorrect format in the first line of file X Y = "+inputData.getLine(),"bloking");
		}
		//skip first line of file without error
		throw new FlatFileFormatException("Skip first line of file");
	}
	
	private boolean isValidControl(String control) {
		if(control == null) {
			return false;
		}
		// Accept only D,G,A letters
        String motif = "^[DGA]+$";
        // create Pattern
        Pattern pattern = Pattern.compile(motif);
        // create Matcher
        Matcher matcher = pattern.matcher(control);
        // Check if string matches pattern
        return matcher.matches();
    }
	
	private boolean isValidOrientation(String orientation) {
		if(orientation == null) {
			return false;
		}
		// Accept only N,E,W,S letters
        String motif = "^[NEWS]+$";
        // create Pattern
        Pattern pattern = Pattern.compile(motif);
        // create Matcher
        Matcher matcher = pattern.matcher(orientation);
        // Check if string matches pattern
        return matcher.matches();
    }
}
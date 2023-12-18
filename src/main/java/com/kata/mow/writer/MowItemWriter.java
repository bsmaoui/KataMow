package com.kata.mow.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.kata.mow.model.InputData;

@Component
public class MowItemWriter implements ItemWriter<InputData>{

	 
	@Override
	public void write(Chunk<? extends InputData> inputData) throws Exception {
		inputData.getItems().stream().filter(i-> i.getOrientation() != null && !i.getOrientation().isEmpty()).forEach( i->{
			StringBuilder sb = new StringBuilder();
			sb.append("Output : X= ").append(i.getCoordX()).append(" , Y = ").append(i.getCoordY()).append(" , Orientation = ").append(i.getOrientation());
			System.out.println(sb.toString());
			System.out.println("*********");
		});
	}



}

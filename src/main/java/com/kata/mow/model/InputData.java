package com.kata.mow.model;


import com.kata.mow.utils.Constants;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class InputData {

   private  Integer coordX;
   private Integer coordY;
   private String orientation;
   private String control;
   private String line;
   
   public void turnD() {
	   switch (orientation) {
		case Constants.NORTH -> this.setOrientation(Constants.EAST);
		case Constants.EAST -> this.setOrientation(Constants.SOUTH);
		case Constants.SOUTH -> this.setOrientation(Constants.WEST);
		case Constants.WEST -> this.setOrientation(Constants.NORTH);
	   }
   }
   
   public void turnG() {
	   switch (orientation) {
		case Constants.NORTH ->	this.setOrientation(Constants.WEST);
		case Constants.WEST ->	this.setOrientation(Constants.SOUTH);
		case Constants.SOUTH -> this.setOrientation(Constants.EAST);
		case Constants.EAST -> this.setOrientation(Constants.NORTH);
	   }
   }
   
   public void move(int maxX, int maxY) {
	   switch(orientation){
		case(Constants.NORTH) -> {if(this.getCoordY() < maxY) this.setCoordY(this.getCoordY() + 1);}
		case(Constants.SOUTH) -> {if( this.getCoordY() > 0) this.setCoordY(this.getCoordY() - 1);}
		case(Constants.EAST) -> {if(this.getCoordX() < maxX) this.setCoordX(this.getCoordX() + 1);}
		case(Constants.WEST) -> {if(this.getCoordX() > 0) this.setCoordX(this.getCoordX() - 1);}
	   }
   }
    
}
package projectRaycast;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.lang.*;
import java.util.concurrent.TimeUnit;

public class MapRenderer {
	
	Map map;
	GraphicsContext gc;
	Canvas canvas;
	double squareSize;

	public MapRenderer (GraphicsContext gc,Canvas canvas, Scene scene, Map map) {
		this.map = map;
		this.gc = gc;
		this.canvas = canvas;
		this.squareSize = canvas.getHeight()/map.getMapLength();
		gc.setStroke(Color.RED);
        gc.setLineWidth(1);      
	}
	
	public void renderMap () {
		
		//create blank screen
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		//draw map squares on left part of screen
		for (int i=0; i<map.getMapLength();i++) {
			for (int j=0; j<map.getMapHeight();j++) {
				switch (map.getSquareType(i, j)) {
				case 0:
					gc.setFill(Color.LIGHTBLUE);
					break;
				case 1:
					gc.setFill(Color.GRAY);
					break;
				case 2:
					gc.setFill(Color.RED);
					break;
				case 3:
					gc.setFill(Color.GREEN);
					break;
				case 4:
					gc.setFill(Color.BLUE);
					break;
				case 5:
					gc.setFill(Color.YELLOW);
					break;
				}
				gc.fillRect(j*squareSize, i*squareSize, squareSize, squareSize);
			}
		}
		
		//draw map lines on left part of screen
		for (int i=0; i<map.getMapLength(); i++) {
			gc.setStroke(Color.GRAY);
			gc.strokeLine(i*squareSize,0,i*squareSize,map.getMapHeight()*squareSize);
			gc.strokeLine(0,i*squareSize,map.getMapHeight()*squareSize,i*squareSize);
		}
	}
	
	public void updatePlayerOnMap (double playerPositionX, double playerPositionY, 
			double playerDirectionX, double playerDirectionY, double planeX, double planeY) {

		//draw player on left side of screen
		gc.setFill(Color.BLACK);
		gc.fillOval(playerPositionY*squareSize-2, playerPositionX*squareSize-2, 4, 4);
		
		//draw field of view on left side of screen
		gc.strokeLine(playerPositionY*squareSize,
					  playerPositionX*squareSize, 
					  playerPositionY*squareSize+500*(playerDirectionY+planeY),
				      playerPositionX*squareSize+500*(playerDirectionX+planeX));
		gc.strokeLine(playerPositionY*squareSize, 
					  playerPositionX*squareSize, 
					  playerPositionY*squareSize+500*(playerDirectionY-planeY),
				      playerPositionX*squareSize+500*(playerDirectionX-planeX));
		gc.setFill(Color.rgb(250,128,114,0.5));
		gc.fillPolygon(new double[] {playerPositionY*squareSize,
				  	   playerPositionY*squareSize+800*(playerDirectionY+planeY),
				       playerPositionY*squareSize+800*(playerDirectionY-planeY)},
				       new double[] {playerPositionX*squareSize,
					   playerPositionX*squareSize+800*(playerDirectionX+planeX),
					   playerPositionX*squareSize+800*(playerDirectionX-planeX)}, 
					   3);
	}
	
	public void renderView (double playerPositionX, double playerPositionY, 
				double playerDirectionX, double playerDirectionY, double planeX, double planeY) {
		//clear right side of screen
		gc.clearRect(map.getMapLength()*squareSize, 0, canvas.getWidth(), canvas.getHeight());
		
		//render right part of screen
		for (double x = 0; x <= canvas.getWidth()/2;x++) {
			
			//calculating ray position
			double screenX = 2 * x / (canvas.getWidth()/2) - 1;
			double rayDirectionX = (playerDirectionX + planeX * screenX);
			double rayDirectionY = (playerDirectionY + planeY * screenX);
			
			//calculating current map square
			int currentSquareX = (int)(playerPositionX);
		    int currentSquareY = (int)(playerPositionY);
		    
		    //distance to next x-wall
		    double distanceToNextXLine;
		    
		    //distance to next y-wall
		    double distanceToNextYLine;
		    
		    //distance between x sides
		    double distanceBetweenXLines = Math.abs(1 / rayDirectionX);
		    
		    //distance between y sides
		    double distanceBetweenYLines = Math.abs(1 / rayDirectionY);
		    
		    //perpendicular distance from player to hit wall
		    double distanceToHitWall;
		    
		    //direction to step in x or y-direction
		    int stepX;
		    int stepY;
		    
		    //wall hit indicator
		    int hit = 0;
		    
		    //NS or EW hit indicator
	        int whatSide = 0;
	        
	        //prepare calculation
	        if (rayDirectionX < 0)
	        {
	        	stepX = -1;
	        	distanceToNextXLine = (playerPositionX - currentSquareX) * distanceBetweenXLines;
	        }
	        else
	        {
	        	stepX = 1;
	        	distanceToNextXLine = (currentSquareX + 1.0 - playerPositionX) * distanceBetweenXLines;
	        }
	        if (rayDirectionY < 0)
	        {
	        	stepY = -1;
	        	distanceToNextYLine = (playerPositionY - currentSquareY) * distanceBetweenYLines;
	        }
	        else
	        {
	        	stepY = 1;
	        	distanceToNextYLine = (currentSquareY + 1.0 - playerPositionY) * distanceBetweenYLines;
	        }
	        
	        //digital differential analyzer algorithm used for drawing a line
	        while (hit == 0)
		    {
		    	//jump to next map square, OR in x-direction, OR in y-direction
		        if (distanceToNextXLine < distanceToNextYLine)
		        {
		          distanceToNextXLine += distanceBetweenXLines;
		          currentSquareX += stepX;
		          whatSide = 0;
		        }
		        else
		        {
		          distanceToNextYLine += distanceBetweenYLines;
		          currentSquareY += stepY;
		          whatSide = 1;
		        }
		        if (map.getSquareType(currentSquareX, currentSquareY) > 0) hit = 1;
		    }
	        
			//Calculate distance projected on camera direction 
			if (whatSide == 0) 
				distanceToHitWall = (currentSquareX - playerPositionX + (1 - stepX) / 2) / rayDirectionX;
			else           
				distanceToHitWall = (currentSquareY - playerPositionY + (1 - stepY) / 2) / rayDirectionY;
			
			//Calculate height of line to draw on screen
			int h=(int)canvas.getHeight();
		    int lineHeight = (int)(h / distanceToHitWall);
		    
		    //Calculate lowest and highest pixel of line
			int drawStart = -lineHeight / 2 + h / 2;
			if(drawStart < 0)drawStart = 0;
			int drawEnd = lineHeight / 2 + h / 2;
			if(drawEnd >= h)drawEnd = h - 1;
			
			Color chosenColor = Color.WHITE;
			switch(map.getSquareType(currentSquareX, currentSquareY))
		      {
		        case 1:  
		        	chosenColor=Color.GREY;  
		        	break;
		        case 2:  
		        	chosenColor=Color.RED;  
		        	break;
		        case 3:
		        	chosenColor=Color.GREEN;  
		        	break;
		        case 4:
		        	chosenColor=Color.BLUE;  
		        	break;
		        case 5:  
		        	chosenColor=Color.YELLOW;  
		        	break;
		      }
			if (whatSide == 1)
				chosenColor = chosenColor.darker();
			gc.setStroke(chosenColor);
			gc.strokeLine(x+canvas.getWidth()/2, drawStart, x+canvas.getWidth()/2, drawEnd);
		}
	}
}
	
	
	
	


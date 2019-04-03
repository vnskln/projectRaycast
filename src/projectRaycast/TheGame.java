package projectRaycast;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class TheGame extends Application {
	//game settings
	Map map = new Map();
	double playerPositionX = 4;
    double playerPositionY = 12;
    double playerDirectionX = -1;
    double playerDirectionY = 0;
    double planeX = 0;
    double planeY = 0.66;
    double playerSpeed = 0.1;
    double playerRotationSpeed = 0.1;
    //end of game settings
    
	//controllers setup
    public void setEventHandlers (Scene scene) {
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
            	double previousDirectionX;
            	double oldPlaneX;
                switch (event.getCode()) {
                    case UP: 
                    	if (map.getSquareType((int)(playerPositionX+playerDirectionX*playerSpeed), (int)playerPositionY)==0)
                    		playerPositionX += playerDirectionX*playerSpeed;
                    	if (map.getSquareType((int)(playerPositionX), (int)(playerPositionY+playerDirectionY*playerSpeed))==0)
                        	playerPositionY += playerDirectionY*playerSpeed; 
                    	break;
                    case DOWN:
                    	if (map.getSquareType((int)(playerPositionX-playerDirectionX*playerSpeed), (int)playerPositionY)==0)
                    		playerPositionX -= playerDirectionX*playerSpeed;
                    	if (map.getSquareType((int)(playerPositionX), (int)(playerPositionY-playerDirectionY*playerSpeed))==0)
                        	playerPositionY -= playerDirectionY*playerSpeed; 
                    	break;
                    case LEFT:
                    	previousDirectionX = playerDirectionX;
                    	playerDirectionX = playerDirectionX * Math.cos(playerRotationSpeed) - playerDirectionY * Math.sin(playerRotationSpeed);
                    	playerDirectionY = previousDirectionX * Math.sin(playerRotationSpeed) + playerDirectionY * Math.cos(playerRotationSpeed);
                        oldPlaneX = planeX;
                        planeX = planeX * Math.cos(playerRotationSpeed) - planeY * Math.sin(playerRotationSpeed);
                        planeY = oldPlaneX * Math.sin(playerRotationSpeed) + planeY * Math.cos(playerRotationSpeed); 
                    	break;
                    case RIGHT: 
                    	previousDirectionX = playerDirectionX;
                    	playerDirectionX = playerDirectionX * Math.cos(-playerRotationSpeed) - playerDirectionY * Math.sin(-playerRotationSpeed);
                    	playerDirectionY = previousDirectionX * Math.sin(-playerRotationSpeed) + playerDirectionY * Math.cos(-playerRotationSpeed);
                        oldPlaneX = planeX;
                        planeX = planeX * Math.cos(-playerRotationSpeed) - planeY * Math.sin(-playerRotationSpeed);
                        planeY = oldPlaneX * Math.sin(-playerRotationSpeed) + planeY * Math.cos(-playerRotationSpeed); 
                    	break; 
                }
            }
        });
	}
    //end of controllers setup

	@Override
	public void start(Stage primaryStage) throws Exception {
        
		//preparing javafx stage elements
		primaryStage.setTitle("raycastEngine");
		Group root = new Group();
		Canvas canvas = new Canvas(1200, 600);	
		GraphicsContext gc = canvas.getGraphicsContext2D();	
        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        setEventHandlers(scene);
        primaryStage.setScene(scene);
        MapRenderer mapRenderer = new MapRenderer(gc, canvas, scene, map);
        
        //preparing game loop
        final long startNanoTime = System.nanoTime();
        new AnimationTimer()
	    {
        	long previousFrameTime = System.nanoTime();
        	double timeCounter = 0;
        	public void handle(long currentNanoTime)
	        {
				timeCounter += (currentNanoTime-previousFrameTime)/1000000000.0*120;
				if (timeCounter>1) {
					
					//main game routine
					mapRenderer.renderMap();
					mapRenderer.updatePlayerOnMap(playerPositionX, playerPositionY,playerDirectionX, playerDirectionY,planeX, planeY);
					mapRenderer.renderView(playerPositionX, playerPositionY,playerDirectionX, playerDirectionY,planeX, planeY);
					//end of main game routine
		            timeCounter=0;
				}
				previousFrameTime = currentNanoTime;
	        }
	    }.start();
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}

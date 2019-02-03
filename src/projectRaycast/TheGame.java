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
	double playerPositionX = 300;
    double playerPositionY = 300;
	public static void main(String[] args) {
		launch(args);
	}
	
	public void setEventHandlers (Scene scene) {
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case UP:    playerPositionY -= 1; break;
                    case DOWN:  playerPositionY += 1; break;
                    case LEFT:  playerPositionX -= 1; break;
                    case RIGHT: playerPositionX += 1; break;
                }
            }
        });
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
        
		primaryStage.setTitle("raycastEngine");
		Group root = new Group();
		Canvas canvas = new Canvas(600, 600);	
		GraphicsContext gc = canvas.getGraphicsContext2D();	
        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        setEventHandlers(scene);
        primaryStage.setScene(scene);
        final long startNanoTime = System.nanoTime();
        MapRenderer mapRenderer = new MapRenderer(gc, canvas, scene);
        new AnimationTimer()
	    {
        	long previousFrameTime = System.nanoTime();
        	double timeCounter = 0;
        	public void handle(long currentNanoTime)
	        {
				timeCounter += (currentNanoTime-previousFrameTime)/1000000000.0*60;
				double t = (currentNanoTime - startNanoTime) / 1000000000.0;
				if (timeCounter>1)  {
					mapRenderer.renderMap(playerPositionX, playerPositionY);
		            timeCounter-=1;
				}
				previousFrameTime = currentNanoTime;
	        }
	    }.start();
		
		primaryStage.show();
		
	}

}

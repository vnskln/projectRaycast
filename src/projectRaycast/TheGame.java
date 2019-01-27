package projectRaycast;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class TheGame extends Application {

	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("raycastEngine");
		Group root = new Group();
		Canvas canvas = new Canvas(600, 480);	
		GraphicsContext gc = canvas.getGraphicsContext2D();		
		gc.setStroke(Color.BLUE);
        gc.setLineWidth(5);
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(1);
        final long startNanoTime = System.nanoTime();
        new AnimationTimer()
	    {
        	long previousFrameTime = System.nanoTime();
        	double timeCounter = 0;
        	public void handle(long currentNanoTime)
	        {
				timeCounter += (currentNanoTime-previousFrameTime)/1000000000.0*60;
				double t = (currentNanoTime - startNanoTime) / 1000000000.0;
				if (timeCounter>1)  {
		        	double x = 300 + 100 * Math.cos(t);
		            double y = 240 + 100 * Math.sin(t);
		            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		            gc.strokeLine(300, 240, x, y);
					timeCounter-=1;
				}
				previousFrameTime = currentNanoTime;
	        }
	    }.start();
		
		primaryStage.show();
		
	}

}

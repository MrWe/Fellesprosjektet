package gui;

import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class SecretPane extends Pane {
	
	private ImageView imageView = new ImageView();
	private int timerDelay, xSpeed, ySpeed;
	private double rotationDegree;
	private Text text;
	
	public SecretPane() {
		super();
		text = new Text(":D");
		text.setFont(new Font(72));
		text.setFill(Paint.valueOf("WHITE"));
		//text.setStyle("-fx-font-color: #FFFFFF;");
		text.setLayoutX(550);
		text.setLayoutY(500);
		text.setVisible(false);
		imageView.setImage(new Image("/Images/secret.gif"));
		imageView.setLayoutX(-500);
		imageView.setLayoutY(200);
		this.getChildren().addAll(imageView, text);
		Random rng = new Random();
		rotationDegree = rng.nextInt(360);
//		System.out.println(rotationDegree);
//		System.out.println(Math.sin(Math.toRadians(rotationDegree)) + " " + Math.cos(Math.toRadians(rotationDegree)));
		double rng2 = rng.nextDouble();
		double xInfluence = rng.nextDouble() > 0.5 ? rng2 : -rng2;
		double yInfluence = rng.nextDouble() > 0.5 ? 1 - rng2 : -(1 - rng2);
		System.out.println(xInfluence + " " + yInfluence);
		System.out.println(Math.acos(xInfluence) + " " + Math.asin(yInfluence));
//		double rad = Math.toRadians(rotationDegree);
//		System.out.println(rad);
//		System.out.println(xInfluence + " " + yInfluence);
//		System.out.println("d: " + Math.toDegrees(Math.atan(xInfluence/yInfluence)));
		//imageView.setRotate(rotationDegree);
		
		timerDelay = 25;
		
		Timeline timer = new Timeline(new KeyFrame(Duration.millis(timerDelay), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				imageView.setLayoutX(imageView.getLayoutX() + 14/* * xInfluence*/);
				//imageView.setLayoutY(imageView.getLayoutY() + 14 * yInfluence);
			}
		}));
		timer.setCycleCount(Timeline.INDEFINITE);
		timer.play();
		
		Timeline trailTimer = new Timeline(new KeyFrame(Duration.millis(timerDelay * 4), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ImageView trail = new ImageView(new Image("/Images/secretTrail.gif"));
				//trail.setRotate(rotationDegree);
				trail.setLayoutX(imageView.getLayoutX() - 5);
				trail.setLayoutY(imageView.getLayoutY() + 90);
				addTrail(trail);
			}
		}));
		trailTimer.setCycleCount(Timeline.INDEFINITE);
		trailTimer.play();
		
		Timeline happyTimer = new Timeline(new KeyFrame(Duration.millis(timerDelay * 150), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				text.setVisible(true);
			}
		}));
		happyTimer.setCycleCount(Timeline.INDEFINITE);
		happyTimer.play();
		
	}
	
	public void addTrail(ImageView trail) {
		this.getChildren().add(trail);
	}

}

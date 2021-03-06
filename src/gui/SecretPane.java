package gui;

import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class SecretPane extends Pane {

	private ImageView imageView = new ImageView();
	private int timerDelay;
	private double deg;
	private Scene scene;

	public SecretPane(Scene scene) {
		super();
		this.scene = scene;
		Timeline timer = new Timeline(new KeyFrame(Duration.millis(3000), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				summonNyan();
			}
		}));
		timer.setCycleCount(Timeline.INDEFINITE);
		timer.play();
	}

	private void summonNyan() {
		imageView.setImage(new Image("/Images/secret2.gif"));
		Pane pane = new Pane(); 
		pane.setTranslateX(300);
		pane.setTranslateY(200);
		pane.setPrefSize(256, 256);
		pane.getChildren().add(imageView);
		this.getChildren().add(pane);
		Random rng = new Random();
		double rng2 = rng.nextDouble();
		double xInfluence = rng.nextDouble() > 0.5 ? rng2 : -rng2;
		double yInfluence = rng.nextDouble() > 0.5 ? 1 - rng2 : -(1 - rng2);
		if (Math.abs(yInfluence) > Math.abs(xInfluence)) {
			pane.setTranslateX(rng.nextInt(400) + 400);
			pane.setTranslateY(yInfluence < 0 ? scene.getHeight() + 200 : -200);
		} else {
			pane.setTranslateY(rng.nextInt(300) + 200);
			pane.setTranslateX(xInfluence < 0 ? scene.getWidth() + 200 : -400);
		}
		imageView.setScaleY(xInfluence < 0 ? -1 : 1);
		deg = Math.toDegrees(Math.atan(yInfluence/xInfluence));
		if (xInfluence < 0) {
			deg = 180 + deg;
		}
		pane.setRotate(deg);
		timerDelay = 15;
		Timeline timer = new Timeline(new KeyFrame(Duration.millis(timerDelay), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				pane.setLayoutX(pane.getLayoutX() + 14 * xInfluence);
				pane.setLayoutY(pane.getLayoutY() + 14 * yInfluence);
			}
		}));
		timer.setCycleCount(Timeline.INDEFINITE);
		timer.play();
	}

}

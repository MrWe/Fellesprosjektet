package gui;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class TreeItemGraphicPane extends Pane {
	
	public TreeItemGraphicPane(String color, boolean checked) {
		super();
		Rectangle rect = new Rectangle(20, 20, Paint.valueOf(color));
		this.getChildren().add(rect);
		if (checked) {
			ImageView imageView = new ImageView(new Image("check_icon.png"));
			this.getChildren().add(imageView);
		}
	}

}

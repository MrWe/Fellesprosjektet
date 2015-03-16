package gui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class TreeItemGraphicPane extends Pane {
	
	private ImageView imageView;
	private Rectangle rect;
	
	public TreeItemGraphicPane(String color, boolean checked) {
		super();
		rect = new Rectangle(20, 20, Paint.valueOf(color));
		imageView = new ImageView(new Image("/Images/box.png"));
		this.getChildren().addAll(rect, imageView);
		if (!checked) {
			imageView.setVisible(false);
		}
	}
	
	public void setChecked(boolean b) {
		if (b) {
			imageView.setVisible(true);
		} else {
			imageView.setVisible(false);
		}
	}

}
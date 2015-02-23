package app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

public class CalendarSquarePane extends Pane {
	
	private Text text;
	private MainApp mainApp;
	private ListView<Appointment> appointments;
	final ObservableList<Appointment> listItems = FXCollections.observableArrayList();
	private VBox appointmentList;
	
	public CalendarSquarePane(MainApp mainApp, int prefWidth, int prefHeight, String date) {
		super();
		this.mainApp = mainApp;
		text = new Text(date);
		text.setLayoutX(5);
		text.setLayoutY(15);
		this.getChildren().add(text);
		this.setStyle("-fx-border-color: #000000;");
		this.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				showPopup();
			}
		});
		
		appointmentList = new VBox();
		appointmentList.getChildren().addAll(new Text("Hei"), new Text("på"), new Text("deg!"));
		appointmentList.setLayoutX(1);
		appointmentList.setLayoutY(20);
		this.getChildren().add(appointmentList);
		
		Rectangle clipRectangle = new Rectangle();
		this.setClip(clipRectangle);
		this.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
		    clipRectangle.setWidth(newValue.getWidth());
		    clipRectangle.setHeight(newValue.getHeight());
		});
	}
	
	public String getDate() {
		return text.getText();
	}
	
	private void showPopup() {
		mainApp.showPopUp(this);
	}
	
	public void addAppointment(Appointment appointment) {
		listItems.add(appointment);
	}

}
package gui;

import core.Appointment;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class CalendarSquarePane extends Pane {
	
	private Text text;
	private MainApp mainApp;
	private VBox appointmentList;
	
	public CalendarSquarePane(MainApp mainApp, String date) {
		super();
		this.mainApp = mainApp;
		text = new Text(date);
		text.setLayoutX(5);
		text.setLayoutY(15);
		this.getChildren().add(text);
		this.setStyle("-fx-border-color: #000000;");
		this.setOnMousePressed(new EventHandler<MouseEvent>() { // when a square is pressed
			@Override
			public void handle(MouseEvent event) {
				System.out.println(event.getSource() + " " + event.getTarget());
				if (event.getTarget() instanceof AppointmentSquarePane) {
					showPopup((AppointmentSquarePane) event.getTarget());
				} else {					
					showPopup();
				}
			}
		});
		
		appointmentList = new VBox();
		appointmentList.setLayoutX(1);
		appointmentList.setLayoutY(20);
		appointmentList.setSpacing(2);
		this.getChildren().add(appointmentList);
		
		Rectangle clipRectangle = new Rectangle(); // makes sure the contents of a square is not shown when the square is too small to display it
		this.setClip(clipRectangle);
		this.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
		    clipRectangle.setWidth(newValue.getWidth());
		    clipRectangle.setHeight(newValue.getHeight());
		    appointmentList.setPrefSize(newValue.getWidth() - 2, newValue.getHeight() - 2);
		});
	}
	
	public String getDate() {
		return text.getText();
	}
	
	private void showPopup() {
		mainApp.showAppointmentPopup(this, null);
	}
	
	private void showPopup(AppointmentSquarePane asp) {
		mainApp.showAppointmentPopup(null, asp);
	}
	
	public void addAppointment(Appointment appointment) {
		AppointmentSquarePane asp = new AppointmentSquarePane(appointment);
		appointmentList.getChildren().add(asp);
	}

}
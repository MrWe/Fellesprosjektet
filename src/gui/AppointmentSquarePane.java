package gui;

import core.Appointment;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class AppointmentSquarePane extends Pane {
	
	private Text text;
	private String color;
	private Appointment appointment;
	
	public AppointmentSquarePane(Appointment appointment) {
		super();
		this.appointment = appointment;
		color = appointment.getColor();
		text = new Text(appointment.getStartTime().toString() + " " + appointment.getDescription());
		text.setLayoutY(15);
		text.setDisable(true); // so that mouseEvent.getTarget() in CalendarSquarePane will return the appointmentSquarePane object instead of the text object
		this.getChildren().add(text);
		//System.out.println(color);
		this.setStyle("-fx-background-color: #" + color + ";");
		this.setOnMousePressed(new EventHandler<MouseEvent>() { // when a square is pressed
			@Override
			public void handle(MouseEvent event) {
				showExistingAppointment();
			}
		});
	}
	
	public void update() {
		color = appointment.getColor();
		text.setText(appointment.getStartTime().toString() + " " + appointment.getDescription());
		this.setStyle("-fx-background-color: #" + color + ";");
	}
	
	private void showExistingAppointment() {
		//System.out.println(appointment);
	}
	
	public Appointment getAppointment()	{
		return appointment;
	}

}

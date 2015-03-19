package gui;

import java.time.LocalDate;

import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import core.Appointment;

public class AppointmentSquarePane extends Pane {
	
	private Text text;
	private String color;
	private Appointment appointment;
	private LocalDate date;
	
	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public AppointmentSquarePane(Appointment appointment, String date) {
		super();
		this.date = LocalDate.parse(date);
		this.appointment = appointment;
		color = appointment.getColor();
		text = new Text(appointment.getStartTime().toString() + " " + appointment.getDescription());
		text.setLayoutY(15);
		text.setDisable(true); // so that mouseEvent.getTarget() in CalendarSquarePane will return the appointmentSquarePane object instead of the text object
		this.getChildren().add(text);
		this.setStyle("-fx-background-color: #" + color + ";");
	}
	
	public void update() {
		color = appointment.getColor();
		text.setText(appointment.getStartTime().toString() + " " + appointment.getDescription());
		this.setStyle("-fx-background-color: #" + color + ";");
	}
	
	public Appointment getAppointment()	{
		return appointment;
	}

}

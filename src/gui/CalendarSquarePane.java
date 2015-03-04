package gui;

import java.util.ArrayList;
import java.util.List;

import core.Appointment;
import core.Group;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class CalendarSquarePane extends Pane {
	
	private Text text;				// displays the date
	private MainApp mainApp;
	private VBox appointmentList;	// list of AppointmentSquarePane-objects
	private Group group;			// the group that is currently selected in the list of groups
	private ObservableList<Appointment> observableAppointments;  // list of appointments, currently does nothing
	
	public CalendarSquarePane(MainApp mainApp, String date, Group group) {
		super();
		this.mainApp = mainApp;
		this.group = group;
		text = new Text(date);
		text.setLayoutX(5);
		text.setLayoutY(15);
		text.setFill(Color.rgb(06, 84, 81));
		this.getChildren().add(text);
		
		List<Appointment> listAppointments = new ArrayList<Appointment>();					// ignore this stuff
		observableAppointments = FXCollections.observableList(listAppointments);
		observableAppointments.addListener(new ListChangeListener<Appointment>() {			// can be used to listen for changes in the observableList

			@Override																		//
			public void onChanged(ListChangeListener.Change<? extends Appointment> c) {
					c.next();																//
					System.out.println(c.getAddedSize());
					System.out.println(c.getAddedSubList().get(0));							//
			}
		});																					//
		
		// border between squares
		this.setStyle("-fx-border-color: #333333;");
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
		mainApp.showAppointmentPopup(this, null, group);
	}
	
	private void showPopup(AppointmentSquarePane asp) {
		mainApp.showAppointmentPopup(null, asp, group);
	}
	
	public void addAppointment(Appointment appointment) {
		AppointmentSquarePane asp = new AppointmentSquarePane(appointment);
		appointmentList.getChildren().add(asp);
		observableAppointments.add(asp.getAppointment());
	}

}
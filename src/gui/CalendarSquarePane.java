package gui;

import java.sql.SQLException;
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

	public Text text;				// displays the date
	private MainApp mainApp;
	private VBox appointmentList;	// list of AppointmentSquarePane-objects
	private Group group;			// the group that is currently selected in the list of groups
	private ObservableList<Appointment> observableAppointments;  // list of appointments, currently does nothing
	private String date;

	public CalendarSquarePane(MainApp mainApp, String date, Group group) {
		super();
		this.date = date;
		this.mainApp = mainApp;
		this.group = group;
		text = new Text(date.substring(8, 10));
		text.setLayoutX(5);
		text.setLayoutY(15);
		text.setFill(Color.WHITE);
		this.getChildren().add(text);

		List<Appointment> listAppointments = new ArrayList<Appointment>();					// ignore this stuff
		observableAppointments = FXCollections.observableList(listAppointments);
		observableAppointments.addListener(new ListChangeListener<Appointment>() {			// can be used to listen for changes in the observableList

			@Override																		//
			public void onChanged(ListChangeListener.Change<? extends Appointment> c) {
				c.next();																//
				//System.out.println(c.getAddedSize());
				//System.out.println(c.getAddedSubList().get(0));							//
			}
		});																					//

		// border between squares
		this.setStyle("-fx-border-color: #124364;");
		//this.setStyle("-fx-border-width: 0.3;");
		this.setOnMousePressed(new EventHandler<MouseEvent>() { // when a square is pressed
			@Override
			public void handle(MouseEvent event) {
				//.out.println(event.getSource() + " " + event.getTarget());
				if (event.getTarget() instanceof AppointmentSquarePane) {
					try {
						showPopup((AppointmentSquarePane) event.getTarget());
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} else {					
					try {
						showPopup();
					} catch (SQLException e) {
						e.printStackTrace();
					}
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
			text.setLayoutX(newValue.getWidth() - text.getLayoutBounds().getWidth() -2);
			clipRectangle.setWidth(newValue.getWidth());
			clipRectangle.setHeight(newValue.getHeight());
			appointmentList.setPrefSize(newValue.getWidth() - 2, newValue.getHeight() - 2);
		});
	}

	public String getDate() {
		return date;
	}

	private void showPopup() throws SQLException {
		mainApp.showAppointmentPopup(this, null, group);
	}

	//edit en appointment
	private void showPopup(AppointmentSquarePane asp) throws SQLException {
		mainApp.showEditAppointmentPopup(null, asp, group);
	}

	public void addAppointment(Appointment appointment) {
		AppointmentSquarePane asp = new AppointmentSquarePane(appointment, date);
		appointmentList.getChildren().add(asp);
		observableAppointments.add(asp.getAppointment());
	}
	
	@Override
	public String toString() {
		return super.toString() + " " + date;
	}
	
	public Text getText(){
		return text;
	}

}
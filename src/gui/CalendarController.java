package gui;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import core.Appointment;
import core.Group;
import database.DBConnection;

public class CalendarController {

	@FXML
	private Text monthText;
	@FXML
	private Text yearText;
	@FXML
	private GridPane calendar;
	@FXML
	private Group group;
	private int currentYear, currentMonth;
	private KeyCode[] konamiCode = { KeyCode.UP, KeyCode.UP, KeyCode.DOWN,
			KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.LEFT,
			KeyCode.RIGHT, KeyCode.B, KeyCode.A };
	private int konamiCodeCounter;
	private ArrayList<String> appointmentIDs;
	private MainApp mainApp;
	private DBConnection dbConnection = new DBConnection();

	@FXML
	private void initialize() {
	}

	public void setKeyEventHandler(Scene scene) {
		EventHandler<KeyEvent> keyHandler = new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyCode) {
				if (keyCode.getCode() == KeyCode.LEFT) {
					goLeft();
				} else if (keyCode.getCode() == KeyCode.RIGHT) {
					goRight();
				}
				if (keyCode.getCode().equals(konamiCode[konamiCodeCounter])) {
					if (konamiCodeCounter == 9) {
						((BorderPane) scene.getRoot())
						.setCenter(new SecretPane(scene));
						AudioClip ac = new AudioClip(CalendarController.class
								.getResource("/res/secret.mp3").toString());
						ac.play();
					} else {
						konamiCodeCounter++;
					}
				} else {
					konamiCodeCounter = 0;
				}
			}
		};
		scene.setOnKeyPressed(keyHandler);
	}

	public void fillCalendar(Group group) throws SQLException {
		this.group = group;
		currentYear = Calendar.getInstance().get(Calendar.YEAR);
		currentMonth = Calendar.getInstance().get(Calendar.MONTH);

		monthText.setText(new DateFormatSymbols().getMonths()[currentMonth]);
		yearText.setText("" + currentYear);

		for (int i = 0; i < 7; i++) { // sets contraints on each of the 7
			// columns
			ColumnConstraints columnConstraints = new ColumnConstraints();
			columnConstraints.setFillWidth(true);
			columnConstraints.setHgrow(Priority.ALWAYS);
			columnConstraints.setMinWidth(75);
			calendar.getColumnConstraints().add(columnConstraints);
		}
		for (int i = 0; i < 6; i++) { // sets contraints on each of the 5 rows
			RowConstraints rowConstraints = new RowConstraints();
			rowConstraints.setFillHeight(true);
			rowConstraints.setVgrow(Priority.ALWAYS);
			rowConstraints.setPrefHeight(200);
			rowConstraints.setMinHeight(80);
			calendar.getRowConstraints().add(rowConstraints);
		}
		constructCalendar(group, currentYear, currentMonth);
	}

	@SuppressWarnings("deprecation")
	private void constructCalendar(Group group, int year, int month)
			throws SQLException {
		calendar.getChildren().clear();

		Calendar c1 = Calendar.getInstance();
		c1.set(year, month, 1);

		int day = c1.get(Calendar.DAY_OF_WEEK);
		c1.add(Calendar.DATE, -1);
		day = c1.get(Calendar.DAY_OF_WEEK); // the last monday of the month
		// before the current month (first
		// date to make a square for)
		while (day != 2) {
			c1.add(Calendar.DATE, -1);
			day = c1.get(Calendar.DAY_OF_WEEK);
		}

		ResultSet appointmentsRS = null;
		ResultSet allAppointmentMembers = null;
		// Henter avtalene til gruppen som er markert.
		if (!(group.getName().equals(""))) {
			appointmentsRS = dbConnection.getAppointmentsWithGroup(Integer.parseInt(group
					.getGroupID()));
			appointmentIDs = new ArrayList<String>();
			while (appointmentsRS.next()) {
				appointmentIDs.add(appointmentsRS.getString("appointmentID"));
			}
			appointmentsRS.beforeFirst();
			allAppointmentMembers = dbConnection.getAllAppointmentMembers(appointmentIDs);
		}
		for (int i = 0; i < 6; i++) { // for each date: create string on the
			// format dd/mm/yyyy and yyyy-mm-dd
			for (int j = 0; j < 7; j++) { // and create a new CalendarSquarePane
				// object for each of them
				String date = String.format("%02d", c1.getTime().getDate())
						+ "/"
						+ String.format("%02d", (c1.getTime().getMonth() + 1))
						+ "/" + (c1.getTime().getYear() + 1900);

				// et annet format
				String date2 = (c1.getTime().getYear() + 1900) + "-"
						+ String.format("%02d", (c1.getTime().getMonth() + 1))
						+ "-" + String.format("%02d", c1.getTime().getDate());
				CalendarSquarePane csp = new CalendarSquarePane(mainApp, date2,
						group, date2.equals(LocalDate.now().toString()) ? true : false);
				if (j >= 5) {
					csp.setStyle("-fx-background-color:#e6e6e6; -fx-border-color:#dcdcdc"); //-fx-border-color:#dcdcdc;
					csp.getText().setFill(Color.BLACK);
				}else{
					csp.setStyle("-fx-background-color:#6e6e6e; -fx-border-color:#7c7c7c");
					csp.getText().setFill(Color.WHITE);
				}

				if (!(group.getName().equals(""))) {
					while (appointmentsRS != null && appointmentsRS.next()) {
						if (!date2.equals(appointmentsRS.getString("timeFrom").substring(0, 10))) {
							continue;
						}
						if( allAppointmentMembers.next() && !(allAppointmentMembers.getString("status").equals("a") || allAppointmentMembers.getString("isAdmin").equals("1"))){
							continue;
						}
						allAppointmentMembers.previous();
						ArrayList<String> members = new ArrayList<String>();
						ArrayList<String> admins = new ArrayList<String>();
						ArrayList<String> invited = new ArrayList<String>();
						while (allAppointmentMembers.next()) {
							if (!allAppointmentMembers.getString("APPOINTMENT_appointmentID").equals(appointmentsRS.getString("appointmentID"))) {
								continue;
							}
							if (allAppointmentMembers.getString("status") == "a") {
								members.add(dbConnection.getUsername(allAppointmentMembers
										.getInt("USER_userID")));
							}
							if (allAppointmentMembers.getString("status") == "i") {
								invited.add(dbConnection.getUsername(allAppointmentMembers
										.getInt("USER_userID")));
							}
							if (allAppointmentMembers.getInt("isAdmin") == 1) {
								admins.add(dbConnection.getUsername(allAppointmentMembers
										.getInt("USER_userID")));
							}
						}

						Appointment appointment = new Appointment(
								appointmentsRS.getString("description"), appointmentsRS.getString("place"),
								LocalDate.parse(appointmentsRS.getString("timeFrom")
										.substring(0, 10)), LocalTime.parse(appointmentsRS
												.getString("timeFrom").substring(11, 16)),
												LocalTime.parse(appointmentsRS.getString("timeTo").substring(11,
														16)), invited, members, admins,
														appointmentsRS.getString("appColor"), group);
						appointment.setAppointmentID(appointmentsRS.getString("appointmentID"));
						allAppointmentMembers.beforeFirst();
						csp.addAppointment(appointment);
					}

				}
				calendar.add(csp, j, i); // adds the calendar square to the
				// calendar gridPane
				c1.add(Calendar.DATE, 1); // increase date by 1
				if (appointmentsRS != null) {
					appointmentsRS.beforeFirst();
				}
			}
		}
	}

	@FXML
	private void goLeft() {
		if (currentMonth == 0) {
			currentYear--;
			currentMonth = 11;
			yearText.setText("" + currentYear);
		} else {
			currentMonth--;
		}
		monthText.setText(new DateFormatSymbols().getMonths()[currentMonth]);
		try {
			constructCalendar(group, currentYear, currentMonth);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void goRight() {
		if (currentMonth == 11) {
			currentYear++;
			currentMonth = 0;
			yearText.setText("" + currentYear);
		} else {
			currentMonth++;
		}
		monthText.setText(new DateFormatSymbols().getMonths()[currentMonth]);
		try {
			constructCalendar(group, currentYear, currentMonth);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

}
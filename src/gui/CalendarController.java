package gui;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;


import javafx.fxml.FXML;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import core.Appointment;
import core.Group;
import database.DBConnection;

public class CalendarController {

	@FXML private Text monthText;
	@FXML private Text yearText;
	@FXML private GridPane calendar;
	@FXML private Group group;

	// Reference to the main application.
	private MainApp mainApp;
	private DBConnection dbConnection = new DBConnection();

	@FXML
	private void initialize() {
	}

	@SuppressWarnings("deprecation")

	public void fillCalendar(Group group) throws SQLException {
		System.out.println(group.getName());
		for (int i = 0; i < 7; i++) { // sets contraints on each of the 7 columns
			ColumnConstraints columnConstraints = new ColumnConstraints();
			columnConstraints.setFillWidth(true);
			columnConstraints.setHgrow(Priority.ALWAYS);
			columnConstraints.setMinWidth(75);
			calendar.getColumnConstraints().add(columnConstraints);
		}
		for (int i = 0; i < 5; i++) { // sets contraints on each of the 5 rows
			RowConstraints rowConstraints = new RowConstraints();
			rowConstraints.setFillHeight(true);
			rowConstraints.setVgrow(Priority.ALWAYS);
			rowConstraints.setPrefHeight(200);
			rowConstraints.setMinHeight(80);
			calendar.getRowConstraints().add(rowConstraints);
		}

		int[] days = {
				6,0,1,2,3,4,5
		};
		Calendar cal = Calendar.getInstance();
		int today = cal.get(Calendar.DAY_OF_MONTH);
		int total = today-1;
		cal.add(Calendar.DAY_OF_MONTH, -(today-1));
		int firstDay = cal.get(Calendar.DAY_OF_WEEK);
		total += days[firstDay-1];

		Calendar c = Calendar.getInstance(); // date today
		c.add(Calendar.DAY_OF_MONTH, -total);
		ResultSet rs = null;
		if (!(group.getName().equals(""))) {
			rs = dbConnection.getAppointmentsWithGroup(Integer.parseInt(group.getGroupID()));
		}
		for (int i = 0; i < 5; i++) {		// for each date: create string on the format dd/mm/yyyy and yyyy-mm-dd
			for (int j = 0; j < 7; j++){	// and create a new CalendarSquarePane object for each of them
				String date = String.format("%02d", c.getTime().getDate()) + "/" + String.format("%02d", (c.getTime().getMonth() + 1)) + "/" + (c.getTime().getYear() + 1900);

				//en annen format
				String date2 = (c.getTime().getYear() + 1900) + "-" + String.format("%02d", (c.getTime().getMonth() + 1)) + "-" + String.format("%02d", c.getTime().getDate());
				
				CalendarSquarePane csp = new CalendarSquarePane(mainApp, date, group);
				if (!(group.getName().equals(""))) {
					while (rs != null && rs.next()) {
						if (!date2.equals(rs.getString(3).substring(0, 10))) {
							continue;
						}
						ResultSet memberRs = dbConnection.getAppointmentMembers(rs.getInt(1));
						ArrayList<String> members = new ArrayList<String>();
						ArrayList<String> admins = new ArrayList<String>();
						ArrayList<String> invited = new ArrayList<String>();
						while (memberRs.next()) {
							if (memberRs.getString(1) == "a") {members.add(dbConnection.getUsername(memberRs.getInt(3)));}
							if (memberRs.getString(1) == "i") {invited.add(dbConnection.getUsername(memberRs.getInt(3)));}
							if (memberRs.getInt(2) == 1) {
								admins.add(dbConnection.getUsername(memberRs.getInt(3)));
								}
						}
						Appointment appointment = new Appointment(rs.getString(2), 
								rs.getString(5),
								LocalDate.parse(rs.getString(3).substring(0, 10)),
								LocalTime.parse(rs.getString(3).substring(11, 16)),
								LocalTime.parse(rs.getString(4).substring(11, 16)),
								invited,
								members,
								admins,
								rs.getString(11),
								group);
						memberRs.beforeFirst(); 
						csp.addAppointment(appointment);
					}

					
				}
				for (Appointment appointment : group.getAppointments()) { // for each of the groups appointments
					if (appointment.getDate().toString().equals(date2)) { // if the date of the appointment equals the current date in the for-loop
						csp.addAppointment(appointment);				  // add the appointment to the calendar square corresponding to the current date in the for-loop
					}
				}
				calendar.add(csp, j, i); // adds the calendar square to the calendar gridPane
				c.add(Calendar.DATE, 1); // increase date by 1
				if (rs != null) {rs.beforeFirst();}
			}
		}
	}
	
	@FXML
	private void goLeft() throws SQLException {
		mainApp.showCalendar(group);
	}
	
	@FXML
	private void goRight() throws SQLException {
		mainApp.showCalendar(group);
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

}
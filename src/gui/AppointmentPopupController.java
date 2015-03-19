package gui;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;




import core.Appointment;
import core.Group;
import database.DBConnection;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

public class AppointmentPopupController {

	// @FXML private Text text;
	@FXML
	private TextArea descriptionField;
	@FXML
	private ComboBox<String> locationField;
	@FXML
	private TextField startTimeField;
	@FXML
	private TextField endTimeField;
	@FXML
	private ColorPicker colorPicker;
	@FXML
	private Button deleteBtn;
	@FXML
	private Text errorText;
	private Stage popupStage;
	private ObservableList<CheckListObject> memberList = FXCollections
			.observableArrayList();
	private ArrayList<String> allMembers; // temporary until database is up
	private CalendarSquarePane csp;
	private AppointmentSquarePane asp;
	@FXML
	private VBox members;
	private boolean editingExisting;
	private Group group;
	private DBConnection db;
	private String username;

	@FXML
	private void initialize() throws SQLException {
		db = new DBConnection();
		allMembers = new ArrayList<String>();
	}

	public void setPopupStage(Stage popupStage) {
		this.popupStage = popupStage;
	}

	@FXML
	private void handleOk() throws SQLException { // when OK is clicked, create
													// a new appointment with
													// the info given and give
													// it to the
													// CalendarSquarePane that
													// opened the popup
		String validInput = isValidInput();
		if (validInput.length() != 0) {
			errorText.setVisible(true);
			errorText.setText(validInput);
			return;
		}
		ArrayList<String> invited = new ArrayList<String>();
		for (CheckListObject clo : memberList) {
			if (clo.getSelected()) {
				invited.add(clo.getName());
			}
		}
			System.out.println(csp.getDate());
			LocalDate date = LocalDate.parse(csp.getDate());
			System.out.println("invited: " + invited);
			
			addAppointmentToCalendar(descriptionField.getText(),
					locationField.getPromptText(), date,
					LocalTime.parse(startTimeField.getText()),
					LocalTime.parse(endTimeField.getText()), invited,
					invited, new ArrayList<String>(),
					colorPicker.getValue().toString().substring(2, 8).toUpperCase(), group, 1, 0);
			popupStage.close();
	}

	private void addAppointmentToCalendar(String description, String location,
			LocalDate date, LocalTime startTime, LocalTime endTime,
			ArrayList<String> invited, ArrayList<String> members,
			ArrayList<String> admins, String color, Group owner,
			int addToDatabase, int changeAppointment) throws SQLException {

			Appointment appointment = new Appointment(description, location, date,
				startTime, endTime, invited, members, admins, color, owner);

			System.out.println("happens");
			csp.addAppointment(appointment);
			group.addAppointment(appointment);
			System.out.println("groupName " + group.getName());
			
			db.addAppointment(username, appointment.getDescription(),
					appointment.getDate().toString() + " "
							+ appointment.getStartTime().toString() + ":00",
					appointment.getDate().toString() + " "
							+ appointment.getEndTime().toString() + ":00",
					null, null, 1, group.getName(), color);
			appointment.setAppointmentID(db.getLastAppointmentID());
			
			System.out.println("Appointment id: " + db.getLastAppointmentID());
			System.out.println("Appointment members: " + members);
			db.addAppointmentMembers(Integer.parseInt(db.getLastAppointmentID()), members);
			//db.addAlarm(appointment.getDate().toString() + " " + appointment.getStartTime().toString() + ":00", "App", members, appointment.getAppointmentID());

	}
	

	private String isValidInput() {
		String errorText = "";
		if (descriptionField.getText().equals("")) {
			errorText += "Beskrivelse kan ikke v�re tom\n";
		}
		if (locationField.getPromptText().equals("")) {
			errorText += "Sted kan ikke v�re tom\n";
		}
		if (!startTimeField.getText().matches("[0-9][0-9][:][0-9][0-9]")) {
			errorText += "Ugyldig starttid\n";
		}
		if (!endTimeField.getText().matches("[0-9][0-9][:][0-9][0-9]")) {
			errorText += "Ugyldig sluttid\n";
		}
		if (!colorPicker.getValue().toString().substring(2, 8).toUpperCase().matches(
				"[0-9A-F][0-9A-F][0-9A-F][0-9A-F][0-9A-F][0-9A-F]")) {
			errorText += "Ugyldig farge\n";
		}
		return errorText;
	}
	
	private boolean validTime() {
		String start = startTimeField.getText();
		String end = endTimeField.getText();
		if (start.matches("[0-9][0-9][:][0-9][0-9]") && end.matches("[0-9][0-9][:][0-9][0-9]") && 
				(
						Integer.parseInt(end.substring(0, 2)) > Integer.parseInt(start.substring(0, 2))
						|| 
								((Integer.parseInt(end.substring(0, 2)) == Integer.parseInt(start.substring(0, 2)))
								&& (Integer.parseInt(end.substring(3, 5)) > Integer.parseInt(start.substring(3, 5))))
				)
				) {
			return true;
		}
		else {
			System.out.println("Wrong input");
			return false;
			}
	}

	@FXML
	private void handleCancel() {
		popupStage.close();
	}

	@FXML
	public void handleFindRoom() throws SQLException {
		if (validTime()) {
			ArrayList<String> rooms = db.getAvailableRooms(csp.getDate(), LocalTime.parse(startTimeField.getText()+":00"), LocalTime.parse(endTimeField.getText()+":00"));
			System.out.println(rooms);
			locationField.setItems(FXCollections.observableArrayList(rooms));
		} else {
			locationField.setItems(null);
		}
	}

	@FXML
	private void handleDelete() {
		((VBox) asp.getParent()).getChildren().remove(asp);
		db.deleteAppointment(asp.getAppointment().getAppointmentID());
		asp.getAppointment().getOwner().removeAppointment(asp.getAppointment());
		popupStage.close();
	}

	public void fillPopup(CalendarSquarePane csp, AppointmentSquarePane asp,
			Group group, String username) { // called whenever the popup is
											// opened
		this.username = username;
		this.group = group;

		ResultSet rs = db.getGroupMembers(group.getGroupID());
		try {

			while(rs.next()){
				allMembers.add(rs.getString("fullName"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		memberList.clear();
		members.getChildren().clear();

		ListView<CheckListObject> members = new ListView<CheckListObject>();
		members.setPrefSize(200, 250);
		members.setEditable(true);
		members.setItems(memberList);
		Callback<CheckListObject, ObservableValue<Boolean>> getProperty = new Callback<CheckListObject, ObservableValue<Boolean>>() {
			public BooleanProperty call(CheckListObject object) {
				return object.selectedProperty();
			}
		};
		Callback<ListView<CheckListObject>, ListCell<CheckListObject>> forListView = CheckBoxListCell
				.forListView(getProperty);
		members.setCellFactory(forListView);

		if (csp != null) {
			deleteBtn.setDisable(true);
			editingExisting = false;
			this.csp = csp;
			for (String member : allMembers) {
				memberList.add(new CheckListObject(member));
			}
			this.members.getChildren().add(members);
		} else {
			deleteBtn.setDisable(false);
			editingExisting = true;
			this.asp = asp;
			descriptionField.setText(asp.getAppointment().getDescription());
			locationField.setPromptText(asp.getAppointment().getLocation());
			startTimeField.setText(asp.getAppointment().getStartTime()
					.toString());
			endTimeField.setText(asp.getAppointment().getEndTime().toString());
			colorPicker.setValue(Color.valueOf(asp.getAppointment().getColor()));
			for (String member : allMembers) {
				CheckListObject clo = new CheckListObject(member);
				if (asp.getAppointment().getInvited().contains(member)) {
					clo.setSelectedProperty(true);
				}
				memberList.add(clo);
			}
			this.members.getChildren().add(members);
		}

	}

}
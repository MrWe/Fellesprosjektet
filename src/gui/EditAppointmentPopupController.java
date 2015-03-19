package gui;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import core.Appointment;
import core.Group;
import database.DBConnection;

public class EditAppointmentPopupController {
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
	private ObservableList<CheckListObject> invitableMemberList = FXCollections.observableArrayList();
	private ObservableList<String> memberList = FXCollections.observableArrayList();
	private ArrayList<String> allMembers; // temporary until database is up 
	private CalendarSquarePane csp;
	private AppointmentSquarePane asp;
	@FXML
	private ListView<String> members;
	@FXML
	private VBox invitableMembers;
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
		for (CheckListObject clo : invitableMemberList) {
			if (clo.getSelected()) {
				invited.add(clo.getName());
			}
		}
		if (!editingExisting) {
			System.out.println(csp.getDate());
			LocalDate date = LocalDate.parse(csp.getDate());

			addAppointmentToCalendar(descriptionField.getText(),
					locationField.getPromptText(), date,
					LocalTime.parse(startTimeField.getText()),
					LocalTime.parse(endTimeField.getText()), invited,
					new ArrayList<String>(), new ArrayList<String>(),
					colorPicker.getValue().toString().substring(2, 8).toUpperCase(), group, 1, 0);
			popupStage.close();
		} else {
			asp.getAppointment().setDescription(descriptionField.getText());
			asp.getAppointment().setLocation(locationField.getPromptText());
			asp.getAppointment().setStartTime(
					LocalTime.parse(startTimeField.getText()));
			asp.getAppointment().setEndTime(
					LocalTime.parse(endTimeField.getText()));
			asp.getAppointment().setInvited(invited);
			asp.getAppointment().setMembers(new ArrayList<String>());
			asp.getAppointment().setAdmins(new ArrayList<String>());
			asp.getAppointment().setColor(colorPicker.getValue().toString().substring(2, 8).toUpperCase());
			asp.update();

			addAppointmentToCalendar(descriptionField.getText(),
					locationField.getPromptText(), asp.getDate(),
					LocalTime.parse(startTimeField.getText()),
					LocalTime.parse(endTimeField.getText()), invited,
					new ArrayList<String>(), new ArrayList<String>(),
					colorPicker.getValue().toString().substring(2, 8).toUpperCase(), group, 0, 1);

			popupStage.close();
		}

	}

	private void addAppointmentToCalendar(String description, String location,
			LocalDate date, LocalTime startTime, LocalTime endTime,
			ArrayList<String> invited, ArrayList<String> members,
			ArrayList<String> admins, String color, Group owner,
			int addToDatabase, int changeAppointment) throws SQLException {

		Appointment appointment = new Appointment(description, location, date,
				startTime, endTime, invited, members, admins, color, owner);

		// Used when a new appointment is created
		if (addToDatabase == 1) {
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
			// Used when editingExisting is true
		} else if (changeAppointment == 1) {
			String appointmentId = asp.getAppointment().getAppointmentID();
			System.out.println(appointment);
			db.updateAppointment(appointmentId, appointment.getDescription(),
					appointment.getDate().toString() + " "
							+ appointment.getStartTime().toString() + ":00",
							appointment.getDate().toString() + " "
									+ appointment.getEndTime().toString() + ":00",
									null, null, db.getRoomId(locationField.getValue()), group.getName());
			// Used when appointments are retrieved from db
		}
		// else {
		// csp.addAppointment(appointment);
		// group.addAppointment(appointment);
		// }
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

	@FXML
	private void handleCancel() {
		popupStage.close();
	}


	@FXML
	public void handleFindRoom() throws SQLException {
		if (validTime()) {
			System.out.println(csp);
			ArrayList<String> rooms = new ArrayList<String>();
			if (csp != null) {
				rooms = db.getAvailableRooms(csp.getDate(), LocalTime.parse(startTimeField.getText()+":00"), LocalTime.parse(endTimeField.getText()+":00"));
			}else {
				rooms = db.getAvailableRooms(asp.getDate().toString(), LocalTime.parse(startTimeField.getText()+":00"), LocalTime.parse(endTimeField.getText()+":00"));
			}
			
			System.out.println(rooms);
			locationField.setItems(FXCollections.observableArrayList(rooms));
		} else {
			locationField.setItems(null);
		}
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
	private void handleDelete() {
		((VBox) asp.getParent()).getChildren().remove(asp);
		db.deleteAppointment(asp.getAppointment().getAppointmentID());
		asp.getAppointment().getOwner().removeAppointment(asp.getAppointment());
		popupStage.close();
	}

	private void updateMemberList(){
		members.setEditable(true);
		members.setItems(memberList);
		memberList.clear();

		members.setEditable(true);
		members.setItems(memberList);
		memberList.clear();

		System.out.println("groupID" +  Integer.parseInt(asp
				.getAppointment().getAppointmentID()));
		
//		ResultSet rs = db.getAppointmentMemberNames(Integer.parseInt(asp
//				.getAppointment().getAppointmentID()));
//		
//		try {
//			while (rs.next()) {
//				memberList.add(rs.getString("fullName"));
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
	}

	public void fillPopup(CalendarSquarePane csp, AppointmentSquarePane asp,
			Group group, String username) throws SQLException { // called whenever the popup is
		// opened
		//updateMemberList();

		this.username = username;
		this.group = group;

		ResultSet rs = db.getGroupMembers(group.getGroupID());
		try {
			while (rs.next()) {
				allMembers.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}



		invitableMemberList.clear();
		invitableMembers.getChildren().clear();

		ListView<CheckListObject> invitableMembers = new ListView<CheckListObject>();
		invitableMembers.setEditable(true);
		invitableMembers.setItems(invitableMemberList);
		Callback<CheckListObject, ObservableValue<Boolean>> getProperty = new Callback<CheckListObject, ObservableValue<Boolean>>() {
			public BooleanProperty call(CheckListObject object) {
				return object.selectedProperty();
			}
		};
		Callback<ListView<CheckListObject>, ListCell<CheckListObject>> forListView = CheckBoxListCell
				.forListView(getProperty);
		invitableMembers.setCellFactory(forListView);

		deleteBtn.setDisable(false);
		editingExisting = true;
		this.asp = asp;
		System.out.println("appointmen: " + asp.getAppointment().getDescription());
		descriptionField.setText(asp.getAppointment().getDescription());
		locationField.setPromptText(db.getRoomFromAppointmentId(asp.getAppointment().getAppointmentID()));
		startTimeField.setText(asp.getAppointment().getStartTime()
				.toString());
		endTimeField.setText(asp.getAppointment().getEndTime().toString());
		//colorField.setText(asp.getAppointment().getColor());
		for (String member : allMembers) {
			CheckListObject clo = new CheckListObject(member);
			if (asp.getAppointment().getInvited().contains(member)) {
				clo.setSelectedProperty(true);
			}
			invitableMemberList.add(clo);
		}
		this.invitableMembers.getChildren().add(invitableMembers);


	}

}

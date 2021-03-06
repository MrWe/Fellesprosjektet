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
import javafx.scene.control.Label;
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
import core.Appointment;
import core.Group;
import database.DBConnection;

public class EditAppointmentPopupController {
	@FXML private TextArea descriptionField;
	@FXML private ComboBox<String> locationField;
	@FXML private TextField startTimeField, endTimeField;
	@FXML private ColorPicker colorPicker;
	@FXML private Text errorText;
	private Stage popupStage;
	private ObservableList<CheckListObject> invitableMemberList = FXCollections.observableArrayList();
	private ObservableList<String> memberList = FXCollections.observableArrayList();
	private ArrayList<String> allMembers, admins;
	private CalendarSquarePane csp;
	private AppointmentSquarePane asp;
	@FXML private ListView<String> members;
	@FXML private VBox invitableMembers;
	private Group group;
	private DBConnection db;
	@FXML private Button OKBtn, deleteBtn, slettMedlemBtn, inviteMedlemBtn;
	@FXML private Label adminLabel;
	private MainApp mainApp;

	@FXML
	private void initialize() throws SQLException {
		db = new DBConnection();
		allMembers = new ArrayList<String>();
		admins = new ArrayList<String>();
		startTimeField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (startTimeField.getText().matches("[0-9][0-9][:][0-9][0-9]") && endTimeField.getText().matches("[0-9][0-9][:][0-9][0-9]")) {
				try {
					handleFindRoom();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		endTimeField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (startTimeField.getText().matches("[0-9][0-9][:][0-9][0-9]") && endTimeField.getText().matches("[0-9][0-9][:][0-9][0-9]")) {
				try {
					handleFindRoom();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void setPopupStage(Stage popupStage) {
		this.popupStage = popupStage;
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	@FXML
	private void handleSlettMedlemBtn(){
		String memberToDelete = new String();
		memberToDelete = members.getSelectionModel().getSelectedItem();

		ArrayList<String> members = new ArrayList<String>();
		for (String member : memberList) {
			if(member != memberToDelete ){
				members.add(member);
			}
		}
		asp.getAppointment().setMembers(members);

		updateMemberList();
		updateInviteList();
	}

	@FXML
	private void handleInviteMedlemBtn(){
		ArrayList<String> invited = new ArrayList<String>();
		for (String member: memberList){
			invited.add(member);
		}
		for (CheckListObject clo : invitableMemberList) {	// gets all the names that have been selected in the list of members
			if (clo.getSelected()) {
				invited.add(clo.getName());
			}
		}
		asp.getAppointment().setMembers(invited);


		updateMemberList();
		updateInviteList();
	}

	@FXML
	private void handleOk() throws SQLException {
		String validInput = isValidInput();
		if (validInput.length() != 0) {
			errorText.setVisible(true);
			errorText.setText(validInput);
			return;
		}
		ArrayList<String> members = new ArrayList<String>();
		for (String member : memberList) {
			if(!admins.contains(member)){
				members.add(member);
			}
		}
		asp.getAppointment().setDescription(descriptionField.getText());
		asp.getAppointment().setLocation(locationField.getPromptText());
		asp.getAppointment().setStartTime(LocalTime.parse(startTimeField.getText()));
		asp.getAppointment().setEndTime(LocalTime.parse(endTimeField.getText()));
		asp.getAppointment().setInvited(members);
		asp.getAppointment().setMembers(members);
		asp.getAppointment().setAdmins(new ArrayList<String>());
		asp.getAppointment().setColor(colorPicker.getValue().toString().substring(2, 8).toUpperCase());
		asp.update();
		
		asp.getAppointment().setAdmins(admins);
		
		addAppointmentToCalendar(
				descriptionField.getText(),
				locationField.getPromptText(), 
				asp.getDate(),
				LocalTime.parse(startTimeField.getText()),
				LocalTime.parse(endTimeField.getText()), 
				members,
				members, 
				asp.getAppointment().getAdmins(),
				colorPicker.getValue().toString().substring(2, 8).toUpperCase(), 
				group, 
				0, 
				1);
		try {
			db.addAlarm(asp.getAppointment().getDate().toString() + " " + asp.getAppointment().getStartTime() + ":00", "App", asp.getAppointment().getMembers(), asp.getAppointment().getAppointmentID());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		popupStage.close();
	}

	private void addAppointmentToCalendar(String description, String location,
			LocalDate date, LocalTime startTime, LocalTime endTime,
			ArrayList<String> invited, ArrayList<String> members,
			ArrayList<String> admins, String color, Group owner,
			int addToDatabase, int changeAppointment) throws SQLException {

		Appointment appointment = new Appointment(
				description, 
				location, 
				date,
				startTime, 
				endTime, 
				invited, 
				members, 
				admins, 
				color, 
				owner);

		String appointmentId = asp.getAppointment().getAppointmentID();
		db.updateAppointment(
				appointmentId, 
				appointment.getDescription(),
				appointment.getDate().toString() + " "	+ appointment.getStartTime().toString() + ":00",
				appointment.getDate().toString() + " " + appointment.getEndTime().toString() + ":00",
				null, 
				null, 
				db.getRoomId(locationField.getValue()), 
				group.getName(),
				appointment.getColor());
		db.setAppointmentMembers(Integer.parseInt(asp.getAppointment().getAppointmentID()), members);
		db.addAppointmentAdmins(Integer.parseInt(asp.getAppointment().getAppointmentID()), admins);
	}

	private String isValidInput() {
		String errorText = "";
		if (descriptionField.getText().equals("")) {
			errorText += "Beskrivelse kan ikke v�re tom\n";
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

	public void handleFindRoom() throws SQLException {
		if (validTime()) {
			ArrayList<String> rooms = new ArrayList<String>();
			if (csp != null) {
				rooms = db.getAvailableRooms(csp.getDate(), LocalTime.parse(startTimeField.getText()+":00"), LocalTime.parse(endTimeField.getText()+":00"));
			} else {
				rooms = db.getAvailableRooms(asp.getDate().toString(), LocalTime.parse(startTimeField.getText()+":00"), LocalTime.parse(endTimeField.getText()+":00"));
			}
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
		for(String member: asp.getAppointment().getMembers()){
			if(!admins.contains(member)){
				memberList.add(member);
			}
		}
	}

	private void updateInviteList(){
		invitableMembers.getChildren().clear();
		invitableMemberList.clear();
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

		allMembers.clear();
		ResultSet rs = db.getGroupMembers(group.getGroupID());
		try {
			while (rs.next()) {
				allMembers.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for (String member : allMembers) {
			CheckListObject clo = new CheckListObject(member);
			if (!memberList.contains(member) && !admins.contains(member)) {
				invitableMemberList.add(clo);
			}
		}
		this.invitableMembers.getChildren().clear();
		this.invitableMembers.getChildren().add(invitableMembers);
	}

	public void fillPopup(CalendarSquarePane csp, AppointmentSquarePane asp,
			Group group) throws SQLException { // called whenever the popup is opened
		this.group = group;
		this.asp = asp;

		//setAdmin
		admins = asp.getAppointment().getAdmins();

		//set naavarende members
		ResultSet rs = db.getAppointmentMemberNames(Integer.parseInt(asp
				.getAppointment().getAppointmentID()));
		ArrayList<String> memberList = new ArrayList<String>();
		try {
			while (rs.next()) {
				memberList.add(rs.getString("fullName"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		adminLabel.setText("Admin: " + admins.get(0));
		asp.getAppointment().setMembers(memberList);

		//update
		updateMemberList();
		updateInviteList();

		descriptionField.setText(asp.getAppointment().getDescription());
		locationField.setValue(db.getRoomFromAppointmentId(asp.getAppointment().getAppointmentID()));
		startTimeField.setText(asp.getAppointment().getStartTime()
				.toString());
		endTimeField.setText(asp.getAppointment().getEndTime().toString());
		colorPicker.setValue(Color.valueOf(asp.getAppointment().getColor()));
		
		if (!admins.contains(mainApp.getUser().getName())) {
			descriptionField.setEditable(false);
			locationField.setDisable(true);
			startTimeField.setEditable(false);
			endTimeField.setEditable(false);
			colorPicker.setDisable(true);
			slettMedlemBtn.setDisable(true);
			inviteMedlemBtn.setDisable(true);
			invitableMembers.setDisable(true);
			OKBtn.setDisable(true);
			deleteBtn.setDisable(true);
		}
	}

}

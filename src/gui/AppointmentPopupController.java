package gui;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

import core.Appointment;
import core.Group;
import database.DBConnection;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

public class AppointmentPopupController {

	//@FXML private Text text;
	@FXML private TextArea descriptionField;
	@FXML private TextField locationField;
	@FXML private TextField startTimeField;
	@FXML private TextField endTimeField;
	@FXML private TextField colorField;
	@FXML private Button deleteBtn;
	@FXML private Text errorText;
	private Stage popupStage;
	private ObservableList<CheckListObject> memberList = FXCollections.observableArrayList();
	private ArrayList<String> allMembers; //temporary until database is up
	private CalendarSquarePane csp;
	private AppointmentSquarePane asp;
	@FXML private VBox members;
	private boolean editingExisting;
	private Group group;
	private DBConnection db;
	private String username;

	@FXML
	private void initialize() throws SQLException {
		db = new DBConnection();
		allMembers = new ArrayList<String>(Arrays.asList("Kristoffer Lervik", "Trym Nilsen", "Hoang Hai Nguyen", "Erik Wiker", "Patricia Zemer", "Jens Stoltenberg", "Erna Solberg", "Kong Harald", "Madonna", "Will Smith", "Kanye West", "Julenissen", "Postman Pat"));
	}
	
	private void addAppointment(String username, String description, String from, String to, String place, String appointmentType, int roomID, String groupName) throws SQLException {
		db.addAppointment(username, description, from, to, place, appointmentType, roomID, groupName);
	}
	
	private void updateAppointment(String description, String location, LocalDate date, LocalTime startTime, LocalTime endTime, 
			ArrayList<String> invited, ArrayList<String> members, ArrayList<String> admins, String color, Group owner) throws SQLException {
		
		Appointment appointment = new Appointment(description, location, date, startTime, endTime, invited, members, admins, color, owner);
//		csp.addAppointment(appointment);
//		group.addAppointment(appointment);
		
		System.out.println(appointment);
		
		//db.updateAppointment(username, appointment.getDescription(), appointment.getDate().toString() + " " + appointment.getStartTime().toString() + ":00", appointment.getDate().toString() + " " + appointment.getEndTime().toString() + ":00", null, null, 1, group.getName());

	}

	public void setPopupStage(Stage popupStage) {
		this.popupStage = popupStage;
	}

	@FXML
	private void handleOk() throws SQLException { // when OK is clicked, create a new appointment with the info given and give it to the CalendarSquarePane that opened the popup
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
		if (!editingExisting) {
			LocalDate date = LocalDate.parse(csp.getDate().substring(6) + "-" + csp.getDate().substring(3, 5) + "-" + csp.getDate().substring(0, 2));

			addAppointmentToCalendar(descriptionField.getText(),
					locationField.getText(),
					date,
					LocalTime.parse(startTimeField.getText()),
					LocalTime.parse(endTimeField.getText()),
					invited,
					new ArrayList<String>(),
					new ArrayList<String>(),
					colorField.getText(),
					group,1);
			asp.setDate(date);
			popupStage.close();
		} else {
			asp.getAppointment().setDescription(descriptionField.getText());
			asp.getAppointment().setLocation(locationField.getText());
			asp.getAppointment().setStartTime(LocalTime.parse(startTimeField.getText()));
			asp.getAppointment().setEndTime(LocalTime.parse(endTimeField.getText()));
			asp.getAppointment().setInvited(invited);
			asp.getAppointment().setMembers(new ArrayList<String>());
			asp.getAppointment().setAdmins(new ArrayList<String>());
			asp.getAppointment().setColor(colorField.getText());
			asp.update();
			
			if (csp == null){System.out.println("!!!!!!!!");}
			updateAppointment(descriptionField.getText(),
					locationField.getText(),
					asp.getDate(),
					LocalTime.parse(startTimeField.getText()),
					LocalTime.parse(endTimeField.getText()),
					invited,
					new ArrayList<String>(),
					new ArrayList<String>(),
					colorField.getText(),
					group);
			
			popupStage.close();
		}


	}

	private void addAppointmentToCalendar(String description, String location, LocalDate date, LocalTime startTime, LocalTime endTime, 
			ArrayList<String> invited, ArrayList<String> members, ArrayList<String> admins, String color, Group owner, int addToDatabase) throws SQLException {

		Appointment appointment = new Appointment(description, location, date, startTime, endTime, invited, members, admins, color, owner);
		csp.addAppointment(appointment);
		group.addAppointment(appointment);

		if (addToDatabase == 1) {
			System.out.println("groupName " + group.getName());
			db.addAppointment(username, appointment.getDescription(), appointment.getDate().toString() + " " + appointment.getStartTime().toString() + ":00", appointment.getDate().toString() + " " + appointment.getEndTime().toString() + ":00", null, null, 1, group.getName());
			appointment.setAppointmentID(db.getLastAppointmentID());
		}
	}

	private String isValidInput() {
		String errorText = "";
		if (descriptionField.getText().equals("")) {
			errorText += "Beskrivelse kan ikke v�re tom\n";
		}
		if (locationField.getText().equals("")) {
			errorText += "Sted kan ikke v�re tom\n";
		}
		if (!startTimeField.getText().matches("[0-9][0-9][:][0-9][0-9]")) {
			errorText += "Ugyldig starttid\n";
		}
		if (!endTimeField.getText().matches("[0-9][0-9][:][0-9][0-9]")) {
			errorText += "Ugyldig sluttid\n";
		}
		if (!colorField.getText().matches("[0-9A-F][0-9A-F][0-9A-F][0-9A-F][0-9A-F][0-9A-F]")) {
			errorText += "Ugyldig farge\n";
		}
		return errorText;
	}

	@FXML
	private void handleCancel() {
		popupStage.close();
	}

	@FXML
	private void handleDelete() {
		((VBox) asp.getParent()).getChildren().remove(asp);
		asp.getAppointment().getOwner().removeAppointment(asp.getAppointment());
		popupStage.close();
	}

	public void fillPopup(CalendarSquarePane csp, AppointmentSquarePane asp, Group group, String username) { // called whenever the popup is opened
		this.username = username;
		this.group = group;
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
		Callback<ListView<CheckListObject>, ListCell<CheckListObject>> forListView = CheckBoxListCell.forListView(getProperty);
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
			locationField.setText(asp.getAppointment().getLocation());
			startTimeField.setText(asp.getAppointment().getStartTime().toString());
			endTimeField.setText(asp.getAppointment().getEndTime().toString());
			colorField.setText(asp.getAppointment().getColor());
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
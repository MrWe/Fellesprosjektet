package app;

import java.time.LocalDate;
import java.time.LocalTime;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

public class PopUpController {

	//@FXML private Text text;
	@FXML private TextField descriptionField;
	@FXML private TextField locationField;
	@FXML private TextField startTimeField;
	@FXML private TextField endTimeField;
	private Stage popupStage;
	private ObservableList<CheckListObject> memberList = FXCollections.observableArrayList();
	private CalendarSquarePane csp;
	@FXML private VBox members;

	@FXML
	private void initialize() {
	}

	public void setPopupStage(Stage popupStage) {
		this.popupStage = popupStage;
	}

	@FXML
	private void handleOk() { // when OK is clicked, create a new appointment with the info given and give it to the CalendarSquarePane that opened the popup
		Appointment appointment = new Appointment(
				descriptionField.getText(),
				locationField.getText(),
				LocalDate.parse(csp.getDate().substring(6) + "-" + csp.getDate().substring(3, 5) + "-" + csp.getDate().substring(0, 2)),
				LocalTime.parse(startTimeField.getText()),
				LocalTime.parse(endTimeField.getText()),
				null,
				null,
				null);
		csp.addAppointment(appointment);
		popupStage.close();
	}

	@FXML
	private void handleCancel() {
		popupStage.close();
	}

	public void fillPopup(CalendarSquarePane csp) { // called whenever the popup is opened
		//text.setText("Lag avtale for " + csp.getDate());
		this.csp = csp;
		memberList.addAll(new CheckListObject("Kristoffer Lervik"), new CheckListObject("Trym Nilsen"), new CheckListObject("Hoang Hai Nguyen"), new CheckListObject("Erik Wiker"), new CheckListObject("Patricia Zemer"));
		memberList.addAll(new CheckListObject("Kristoffer Lervik"), new CheckListObject("Trym Nilsen"), new CheckListObject("Hoang Hai Nguyen"), new CheckListObject("Erik Wiker"), new CheckListObject("Patricia Zemer"));
		memberList.addAll(new CheckListObject("Kristoffer Lervik"), new CheckListObject("Trym Nilsen"), new CheckListObject("Hoang Hai Nguyen"), new CheckListObject("Erik Wiker"), new CheckListObject("Patricia Zemer"));
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
		this.members.getChildren().add(members);

	}

}
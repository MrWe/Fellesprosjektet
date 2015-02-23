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

	@FXML private Text text;
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
	private void handleOk() {
		Appointment appointment = new Appointment(
				descriptionField.getText(),
				locationField.getText(),
				LocalDate.parse(csp.getDate()),
				LocalTime.parse(startTimeField.getText()),
				LocalTime.parse(endTimeField.getText()),
				null,
				null,
				null);
		csp.addAppointment(appointment);
		popupStage.close();
	}
	
	public void fillPopup(CalendarSquarePane csp) {
		text.setText("Lag avtale for " + csp.getDate());
		this.csp = csp;
		memberList.addAll(new CheckListObject("hei"), new CheckListObject("på"), new CheckListObject("deg"));
		ListView<CheckListObject> members = new ListView<CheckListObject>();
		members.setPrefSize(200, 250);
		members.setEditable(true);
		members.setItems(memberList);
		Callback<CheckListObject, ObservableValue<Boolean>> getProperty = new Callback<CheckListObject, ObservableValue<Boolean>>() {
			public BooleanProperty call(CheckListObject layer) {
				return layer.selectedProperty();
			}
		};
		Callback<ListView<CheckListObject>, ListCell<CheckListObject>> forListView = CheckBoxListCell.forListView(getProperty);
		members.setCellFactory(forListView);
		this.members.getChildren().add(members);

	}

}
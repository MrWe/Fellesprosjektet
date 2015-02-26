package app;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

public class GroupPopupController {

	//@FXML private Text text;
	@FXML private TextField nameField;
	@FXML private Text errorText;
	private Stage popupStage;
	private ObservableList<CheckListObject> memberList = FXCollections.observableArrayList();
	private ArrayList<String> allMembers; //temporary until database is up
	@FXML private VBox members;
	private TreeView<Group> treeView;

	@FXML
	private void initialize() {
		allMembers = new ArrayList<String>(Arrays.asList("Kristoffer Lervik", "Trym Nilsen", "Hoang Hai Nguyen", "Erik Wiker", "Patricia Zemer", "Jens Stoltenberg", "Erna Solberg", "Kong Harald", "Madonna", "Will Smith", "Kanye West", "Julenissen", "Postman Pat"));
	}

	public void setPopupStage(Stage popupStage) {
		this.popupStage = popupStage;
	}

	public void setTreeView(TreeView<Group> treeView) {
		this.treeView = treeView;
	}

	@FXML
	private void handleOk() { // when OK is clicked, create a new appointment with the info given and give it to the CalendarSquarePane that opened the popup
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
		Group group = new Group(nameField.getText(),
				invited, invited);
		treeView.getRoot().getChildren().add(new TreeItem<Group>(group));
		popupStage.close();
	}

	private String isValidInput() {
		String errorText = "";
		if (nameField.getText().equals("")) {
			errorText += "Beskrivelse kan ikke være tom\n";
		}
		return errorText;
	}

	@FXML
	private void handleCancel() {
		popupStage.close();
	}

	public void fillPopup() { // called whenever the popup is opened
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

		for (String member : allMembers) {
			memberList.add(new CheckListObject(member));
		}
		this.members.getChildren().add(members);
	}

}

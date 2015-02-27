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
	//private ArrayList<String> allMembers; //temporary until database is up
	@FXML private VBox members;
	private TreeView<Group> treeView;
	private boolean editingExisting;
	private boolean createSub;
	private TreeItem<Group> group;

	@FXML
	private void initialize() {
		//allMembers = new ArrayList<String>();
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
		ArrayList<String> subgroupInvited = new ArrayList<String>();
		for (CheckListObject clo : memberList) {
			if (clo.getSelected()) {
				invited.add(clo.getName());
//				if (this.group.getValue().getMembers().contains(clo.getName())) {
//					subgroupInvited,
//				}
			}
		}
		if (!editingExisting) {
			Group group = new Group(nameField.getText(), invited, invited);
			if (createSub) {
				TreeItem<Group> newGroup = new TreeItem<Group>(group);
				this.group.getChildren().add(newGroup);
				this.group.setExpanded(true);
				treeView.getSelectionModel().select(newGroup);
			} else {				
				treeView.getRoot().getChildren().add(new TreeItem<Group>(group));
			}
		} else {
			group.getValue().setName(nameField.getText());
			group.getValue().setMembers(invited);
		}
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

	public void fillPopup(TreeItem<Group> group, boolean createSub) { // called whenever the popup is opened
		if (group != null && group.getParent() != null) {
			System.out.println(group.getParent());
		} else {
			System.out.println("parent null");
		}
		this.createSub = createSub;
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

		if (group != null && !createSub) {
			editingExisting = true;
			nameField.setText(group.getValue().getName());
			for (String member : group.getParent().getValue().getMembers()) {
				CheckListObject clo = new CheckListObject(member);
				if (group.getValue().getMembers().contains(member)) {
					clo.setSelectedProperty(true);
				}
				memberList.add(clo);
			}
			this.members.getChildren().add(members);
		} else {
			editingExisting = false;
			if (createSub) {
				for (String member : this.group.getValue().getMembers()) {
					memberList.add(new CheckListObject(member));
				}
			} else {				
				for (String member : treeView.getRoot().getValue().getMembers()) {
					memberList.add(new CheckListObject(member));
				}
			}
			this.members.getChildren().add(members);
		}
	}

}

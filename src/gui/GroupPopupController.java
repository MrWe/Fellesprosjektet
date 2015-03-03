package gui;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
import core.Group;
import database.DBConnection;

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
	private DBConnection db;

	@FXML
	private void initialize() {
		//allMembers = new ArrayList<String>();
		db = new DBConnection();
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
		for (CheckListObject clo : memberList) {			// gets all the names that have been selected in the list of members
			if (clo.getSelected()) {
				invited.add(clo.getName());
			}
		}
		if (!editingExisting) {				// if either creating a new group or a new subgroup
			Group group = new Group(nameField.getText(), "0", invited, invited);
			TreeItem<Group> newGroup = new TreeItem<Group>(group);
			if (createSub) {				// if creating a new subgroup
				this.group.getChildren().add(newGroup);
				try {
					int superGroupID = db.getGroupID(this.group.getValue().getName());	// finds the usergroupID of the selected group when the popup was opened
					db.createGroup(nameField.getText(), 0, superGroupID);	// sets the USERGROUP_usergroupID field of the new group equal to the number above
					db.addGroupMembers(nameField.getText(), invited);
					this.group.setExpanded(true);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {						// if creating a new group
				treeView.getRoot().getChildren().add(newGroup);
				try {
					db.createGroup(nameField.getText(), 0, 0); // 0 is the id of the root group
					db.addGroupMembers(nameField.getText(), invited);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			treeView.getSelectionModel().select(newGroup);
		} else {							// if editing a group
			db.editGroupName(group.getValue().getName(), nameField.getText());
			group.getValue().setName(nameField.getText());
			group.getValue().setMembers(invited);
			treeView.getSelectionModel().select(group);
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
				ResultSet rs = db.getAllUsers();
				try {
					while (rs.next()) {
						memberList.add(new CheckListObject(rs.getString(2)));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			this.members.getChildren().add(members);
		}
	}

}

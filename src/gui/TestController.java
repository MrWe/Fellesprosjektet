package gui;

import java.sql.SQLException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import core.Group;
import database.DBConnection;

public class TestController {

	@FXML private TextField nameField;
	@FXML private Text errorText;
	private Stage popupStage;
	private ObservableList<CheckListObject> memberList = FXCollections.observableArrayList();
	private ObservableList<CheckListObject> adminList = FXCollections.observableArrayList();
	@FXML private VBox members;
	@FXML private VBox admins;
	@FXML private Label memberListText;
	@FXML private Button OKBtn;
	private TreeView<Group> treeView;
	private boolean editingExisting;
	private boolean createSub;
	private boolean isPrivate;
	private TreeItem<Group> group;
	private DBConnection db;
	private MainApp mainApp;
	
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
			Group group = new Group(nameField.getText(), false, "0", invited, invited);
			TreeItem<Group> newGroup = new TreeItem<Group>(group);
			if (createSub) {				// if creating a new subgroup
				this.group.getChildren().add(newGroup);
				try {
					int superGroupID = db.getGroupID(this.group.getValue().getName());	// finds the usergroupID of the selected group when the popup was opened
					//System.out.println(nameField.getText());
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
			try {
				db.editGroupName(group.getValue().getName(), nameField.getText());  // parameteres are oldName, newName
				if (!isPrivate) {					
					db.setGroupMembers(nameField.getText(), invited);					// deletes the current members of the group and adds all currently selected
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			group.getValue().setName(nameField.getText());	// update values client-side as well
			group.getValue().setMembers(invited);
		}
		popupStage.close();
	}
	
	private String isValidInput() {
		String errorText = "";
		if (nameField.getText().equals("")) {
			errorText += "Beskrivelse kan ikke v�re tom\n";
		}
		return errorText;
	}

	@FXML
	private void handleCancel() {
		popupStage.close();
	}
}

package gui;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

	@FXML private TextField nameField;
	@FXML private Text errorText;
	private Stage popupStage;
	private ObservableList<CheckListObject> memberList = FXCollections.observableArrayList();
	@FXML private VBox members;
	@FXML private Label memberListText;
	@FXML private Button OKBtn;
	private TreeView<Group> treeView;
	private TreeItem<Group> group;
	private boolean createSub, isPrivate;
	private DBConnection db;
	private MainApp mainApp;

	@FXML
	private void initialize() {
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
		ArrayList<String> admins = new ArrayList<String>(Arrays.asList(mainApp.getUser().getName()));
		ArrayList<String> invited = new ArrayList<String>();
		for (CheckListObject clo : memberList) {	// gets all the names that have been selected in the list of members
			if (clo.getSelected()) {
				invited.add(clo.getName());
			}
		}
		Group group = new Group(nameField.getText(), false, "0", "0", invited, admins);
		TreeItem<Group> newGroup = new TreeItem<Group>(group);
		if (createSub) {	// if creating a new subgroup
			this.group.getChildren().add(newGroup);
			try {
				int superGroupID = db.getGroupID(this.group.getValue().getName());	// finds the usergroupID of the selected group when the popup was opened
				db.createGroup(nameField.getText(), 0, superGroupID, mainApp.getUser().getUsername());	// sets the USERGROUP_usergroupID field of the new group equal to the number above
				group.setGroupID(db.getLastGroupID());
				db.addGroupMembers(nameField.getText(), invited);
				this.group.setExpanded(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {	// if creating a new group
			treeView.getRoot().getChildren().add(newGroup);
			try {
				db.createGroup(nameField.getText(), 0, 0, mainApp.getUser().getUsername()); // 0 is the id of the root group
				group.setGroupID(db.getLastGroupID());
				db.addGroupMembers(nameField.getText(), invited);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		treeView.getSelectionModel().select(newGroup);
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

	public void fillPopup(TreeItem<Group> group, boolean createSub, MainApp mainApp) { // called whenever the popup is opened		
		this.mainApp = mainApp;
		this.createSub = createSub;
		this.group = group;
		memberList.clear();
		members.getChildren().clear();

		ResultSet rs = db.getPrivateGroup(mainApp.getUser().getUsername());
		try {
			rs.next();
			if (group != null && rs.getString("groupName").equals(group.getValue().getName())) {
				isPrivate = true;
			} else {
				isPrivate = false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//for members
		ListView<CheckListObject> members = new ListView<CheckListObject>();
		members.setEditable(true);
		members.setItems(memberList);

		Callback<CheckListObject, ObservableValue<Boolean>> 
		getProperty = new Callback<CheckListObject, ObservableValue<Boolean>>() {
			public BooleanProperty call(CheckListObject object) {
				return object.selectedProperty();
			}
		};
		Callback<ListView<CheckListObject>, 
		ListCell<CheckListObject>> forListView = CheckBoxListCell.forListView(getProperty);
		members.setCellFactory(forListView);
		try {
			if (createSub) {
				if (isPrivate) {
					memberListText.setText("Kan ikke lage subgrupper\nav private grupper.");
					OKBtn.setDisable(true);
					nameField.setDisable(true);
				} else if (!db.isAdmin(mainApp.getUser().getUsername(), group.getValue().getName())) {
					memberListText.setText("M� v�re admin av gruppe for\n � lage subgruppe");
					nameField.setDisable(true);
					OKBtn.setDisable(true);
				} else {
					for (String member : this.group.getValue().getMembers()) {
						if (!member.equals(mainApp.getUser().getName())) {							
							memberList.add(new CheckListObject(member));
						}
					}
					this.members.getChildren().add(members);
				}
			} else {
				rs = db.getAllUsers();
				try {
					while (rs.next()) {
						if(!rs.getString("fullName").equals(mainApp.getUser().getName())){
							memberList.add(new CheckListObject(rs.getString("fullName")));
						}
					}
					this.members.getChildren().add(members);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

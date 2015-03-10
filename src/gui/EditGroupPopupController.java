package gui;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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

public class EditGroupPopupController {

	@FXML private TextField nameField;
	@FXML private Text errorText;
	private Stage popupStage;
	private ObservableList<CheckListObject> invitableMemberList = FXCollections.observableArrayList();
	private ObservableList<String> adminList = FXCollections.observableArrayList();
	private ObservableList<String> memberList = FXCollections.observableArrayList();
	@FXML private VBox members;
	@FXML private VBox admins;
	@FXML private VBox invitableMembers;
	@FXML private Label memberListText;
	@FXML private Label invitableMemberListText;
	@FXML private Button OKBtn;
	@FXML private Button inviteBtn, deleteMemberButton, deleteAdminButton, makeAdminButton;
	private TreeView<Group> treeView;
	private boolean editingExisting;
	private boolean createSub;
	private boolean isPrivate;
	private TreeItem<Group> group;
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
	private void handleInvite() { //handle the invite button
		System.out.println("jeg trykker paa handleInvite knappen");
		String validInput = isValidInput();
		if (validInput.length() != 0) {
			errorText.setVisible(true);
			errorText.setText(validInput);
			return;
		}

		ArrayList<String> invited = new ArrayList<String>();
		for (CheckListObject clo : invitableMemberList) {			// gets all the names that have been selected in the list of members
			if (clo.getSelected()) {
				invited.add(clo.getName());
			}
		}
		if (!editingExisting) {				// if either creating a new group or a new subgroup
			Group group = new Group(nameField.getText(), false, "0", "0", invited, invited);
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
					db.addGroupMembers(nameField.getText(), invited);					// deletes the current members of the group and adds all currently selected
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			group.getValue().setName(nameField.getText());	// update values client-side as well
			group.getValue().addMembers(invited);
		}
		
		updateMemberList();
		updateInvitableMemberList();
		
	}
	
	@FXML
	private void updateMemberList(){
		ListView<String> members = new ListView<String>();
		members.setEditable(true);
		members.setItems(memberList);
		memberList.clear();
		for (String member : group.getValue().getMembers()) {
			memberList.add(member);
		}

		this.members.getChildren().clear();
		this.members.getChildren().add(members);
	}
	
	@FXML
	private void deleteAdmin(){
		System.out.println("I'm deleting an admin");
	}
	
	@FXML
	private void deleteMember(){
		System.out.println("I'm deleting a member");
	}
	
	@FXML
	private void makeAdmin(){
		System.out.println("I'm making an member a admin");
	}
	
	private void updateInvitableMemberList(){
		ListView<CheckListObject> invitableMembers = new ListView<CheckListObject>();
		invitableMembers.setEditable(true);
		invitableMembers.setItems(invitableMemberList);
		invitableMemberList.clear();
		Callback<CheckListObject, ObservableValue<Boolean>> 
		getProperty = new Callback<CheckListObject, ObservableValue<Boolean>>() {
			public BooleanProperty call(CheckListObject object) {
				return object.selectedProperty();
			}
		};
		Callback<ListView<CheckListObject>, 
		ListCell<CheckListObject>> forListView = CheckBoxListCell.forListView(getProperty);
		invitableMembers.setCellFactory(forListView);
		
		for (String member : group.getParent().getValue().getMembers()) {
			CheckListObject clo = new CheckListObject(member);
			if (!group.getValue().getMembers().contains(member)) {
				invitableMemberList.add(clo);
			}
		}
		this.invitableMembers.getChildren().clear();
		this.invitableMembers.getChildren().add(invitableMembers);
	}

	private String isValidInput() {
		String errorText = "";
		if (nameField.getText().equals("")) {
			errorText += "Beskrivelse kan ikke v�re tom\n";
		}
		return errorText;
	}

	@FXML
	private void handleOK() {
		popupStage.close();
	}

	public void fillPopup(TreeItem<Group> group, boolean createSub, MainApp mainApp) { // called whenever the popup is opened		
		this.mainApp = mainApp;
		try {
			if(group != null) {				
				System.out.println("isAdmin " + db.isAdmin(mainApp.getUser().getUsername(), group.getValue().getName()));
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		this.createSub = createSub;
		this.group = group;
		memberList.clear();
		members.getChildren().clear();

		ResultSet rs = db.getPrivateGroup(mainApp.getUser().getUsername());
		try {
			rs.next();
			if (group != null && rs.getString(2).equals(group.getValue().getName())) {
				isPrivate = true;
				System.out.println("isPrivate true");
			} else {
				isPrivate = false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		//for admins 
		ListView<String> admins = new ListView<String>();
		admins.setEditable(true);
		adminList.addAll("Kristoffer","The Mountain","The Rock");
		admins.setItems(adminList);
		this.admins.getChildren().add(admins);


		//Show members
		ListView<String> members = new ListView<String>();
		members.setEditable(true);
		members.setItems(memberList);
		
		for (String member : group.getValue().getMembers()) {
			memberList.add(member);
		}
		this.members.getChildren().add(members);



		//for invite members
		ListView<CheckListObject> invitableMembers = new ListView<CheckListObject>();
		invitableMembers.setEditable(true);
		invitableMembers.setItems(invitableMemberList);

		Callback<CheckListObject, ObservableValue<Boolean>> 
		getProperty = new Callback<CheckListObject, ObservableValue<Boolean>>() {
			public BooleanProperty call(CheckListObject object) {
				return object.selectedProperty();
			}
		};
		Callback<ListView<CheckListObject>, 
		ListCell<CheckListObject>> forListView = CheckBoxListCell.forListView(getProperty);
		invitableMembers.setCellFactory(forListView);

		if (group != null && !createSub) {
			editingExisting = true;
			nameField.setText(group.getValue().getName());

			if (isPrivate) {
				//
			} else {				
				for (String member : group.getParent().getValue().getMembers()) {
					CheckListObject clo = new CheckListObject(member);
					if (!group.getValue().getMembers().contains(member)) {
						invitableMemberList.add(clo);
					}
				}
				this.invitableMembers.getChildren().add(invitableMembers);
			}
		} else {
			editingExisting = false;
			if (createSub) {
				if (isPrivate) {
					invitableMemberListText.setText("Kan ikke lage subgrupper\nav private grupper.");
					OKBtn.setDisable(true);
					nameField.setDisable(true);
				} else {
					for (String member : this.group.getValue().getMembers()) {
						invitableMemberList.add(new CheckListObject(member));
					}
					this.invitableMembers.getChildren().add(invitableMembers);
				}
			} else {
				rs = db.getAllUsers();
				try {
					while (rs.next()) {
						if(!memberList.contains(rs.getString(4))){
							invitableMemberList.add(new CheckListObject(rs.getString(4)));
						}
					}
					this.invitableMembers.getChildren().add(invitableMembers);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

}

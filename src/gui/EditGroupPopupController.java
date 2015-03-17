package gui;

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
	@FXML private VBox invitableMembers;
	@FXML private ListView<String> members,admins;
	@FXML private Label memberListText, invitableMemberListText;
	@FXML private Button OKBtn, inviteBtn, deleteMemberButton, deleteAdminButton, makeAdminButton,deleteGroupButton;
	private TreeItem<Group> group;
	private TreeView<Group> treeView;
	private DBConnection db;

	@FXML
	private void initialize() {// Setter opp dbConnection
		db = new DBConnection();
	}

	public void setPopupStage(Stage popupStage) {
		this.popupStage = popupStage;
	}

	@FXML
	private void handleInvite() { //handle the invite button

		//				// if editing a group
		//		try {
		//			db.editGroupName(group.getValue().getName(), nameField.getText());  // parameteres are oldName, newName			
		//			db.addGroupMembers(nameField.getText(), invited);// deletes the current members of the group and adds all currently selected
		//							}
		//			catch (SQLException e) {
		//				e.printStackTrace();
		//		}

		ArrayList<String> invited = new ArrayList<String>();
		for (CheckListObject clo : invitableMemberList) {			// gets all the names that have been selected in the list of members
			if (clo.getSelected()) {
				invited.add(clo.getName());
			}
		}
		group.getValue().setName(nameField.getText());	// update values client-side as well
		group.getValue().addMembers(invited);
		
		updateMemberList();
		updateInvitableMemberList();
	}

	private void updateMemberList(){
		members.setEditable(true);
		members.setItems(memberList);
		memberList.clear();
		System.out.println("Group members:" + group.getValue().getMembers());
		for (String member : group.getValue().getMembers()) {
			memberList.add(member);
		}
		
		
		//skjuler admins i memberList
		for(String admin: group.getValue().getAdmins()){
			//for(String member: memberList){
			for (String member : group.getValue().getMembers()) {
				if(member.equals(admin)){
					memberList.remove(member);
				}
			}
		}

	}

	private void updateAdminList(){
		admins.setEditable(true);
		admins.setItems(adminList);
		adminList.clear();

		for (String admin : group.getValue().getAdmins()) {
			adminList.add(admin);
		};
		System.out.println("Group admins:" + group.getValue().getAdmins());
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

		System.out.println("name " + group.getValue().getName());
		System.out.println("group :" + group);
		System.out.println("getparent :" + group.getParent());
		System.out.println("getvalue " + group.getParent().getValue());
		System.out.println("getmembers " + group.getParent().getValue().getMembers());
		for (String member : group.getParent().getValue().getMembers()) {
			CheckListObject clo = new CheckListObject(member);
			if (!group.getValue().getMembers().contains(member) && !group.getValue().getAdmins().contains(member)) {
				invitableMemberList.add(clo);
			}
		}

		this.invitableMembers.getChildren().clear();
		this.invitableMembers.getChildren().add(invitableMembers);
	}

	//buttons in EditGroupPopup.fxml

	@FXML
	private void deleteAdmin(){
		String admin = admins.getSelectionModel().getSelectedItem();
		adminList.remove(admin);
		memberList.add(admin);
		group.getValue().removeAdmin(admin);
		System.out.println("I'm deleting an admin");
		updateMemberList();
		updateInvitableMemberList();
	}

	@FXML
	private void deleteMember(){
		String memberToDelete = new String();
		memberToDelete = members.getSelectionModel().getSelectedItem();

		ArrayList<String> members = new ArrayList<String>();
		for (String member : memberList) {
			if(member != memberToDelete ){
				members.add(member);
			}
		}
		group.getValue().setMembers(members);
		updateMemberList();
		updateInvitableMemberList();
	}

	@FXML
	private void handleDeleteGroupButton(){
		System.out.println("im deleting the group: " + group.getValue());
		db.deleteGroup(group.getValue().getGroupID());
		group.getParent().getChildren().remove(group);
		popupStage.close();
	}

	@FXML
	private void makeAdmin(){
		String newAdmin = new String();
		newAdmin = members.getSelectionModel().getSelectedItem();
		adminList.add(newAdmin);
		System.out.println("I'm making " + newAdmin + "a admin");
		group.getValue().addAdmin(newAdmin);
		updateMemberList();
		updateInvitableMemberList();
	}

	private String isValidInput() {
		String errorText = "";
		if (nameField.getText().equals("")) {
			errorText += "Beskrivelse kan ikke vaere tom\n";
		}
		return errorText;
	}

	@FXML
	private void handleOK() {//change name doesnt work
		updateAdminList();
		String validInput = isValidInput();
		if (validInput.length() != 0) {
			errorText.setVisible(true);
			errorText.setText(validInput);
			return;
		}

		//System.out.println("Old group name: " + group.getValue().getName());
		//db.editGroupName(group.getValue().getName(), nameField.getText());
		//group.getValue().setName(nameField.getText());
		//System.out.println("New group name: " + group.getValue().getName());
		//db.editGroupName(group.getValue().getName(), nameField.getText());  // parameteres are oldName, newName			
		//db.addGroupMembers(nameField.getText(), invited);// deletes the current members of the group and adds all currently selected
		
		//Trying to save data to DB. doesnt work. Kristoffer help!
		
		ArrayList<String> toBeMembers = new ArrayList<String>();
		for(String member: memberList){
			toBeMembers.add(member);
		}
		
		System.out.println("OK-knapp clicked!");
		System.out.println("Member list:: " + memberList);
		System.out.println("Admin list: " + adminList);
		
		for(String admin: adminList){
			toBeMembers.add(admin);
		} 

		System.out.println("All members in the group: " + toBeMembers);
		
		try {
			//db.editGroupName(group.getValue().getName(), nameField.getText());  // parameteres are oldName, newName			
			db.setGroupMembers(nameField.getText(), toBeMembers);// deletes the current members of the group and adds all currently selected
			
			for(String admin: adminList){
				System.out.println("setting"+ admin + "to admin list");
				db.editGroupAdminRights(group.getValue().getName(), admin, 1);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
		System.out.println("Member list in the database: " + group.getValue().getMembers());
		System.out.println("Admin list in the database: " + group.getValue().getAdmins());
		
		popupStage.close();
	}

	public void fillPopup(TreeView treeView, TreeItem<Group> group, String username) { // called whenever the popup is opened
		try {
			if (!db.isAdmin(username, group.getValue().getName())) { // if not admin
				invitableMembers.setDisable(true);
				nameField.setDisable(true);
				inviteBtn.setDisable(true);
				deleteMemberButton.setDisable(true);
				deleteAdminButton.setDisable(true);
				makeAdminButton.setDisable(true);
				deleteGroupButton.setDisable(true);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.treeView = treeView;
		this.group = group;

		updateAdminList(); 				//for admins 
		updateMemberList();  			//Show members	
		updateInvitableMemberList(); 	//Show invitable Members();

		nameField.setText(group.getValue().getName());
	}

}

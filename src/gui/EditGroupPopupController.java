package gui;

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
	//private DBConnection db;
	private ArrayList<String> allMembers = new ArrayList<String>(Arrays.asList("Kristoffer Lervik", "Erik Wiker"));
	private ArrayList<String> allInvited = new ArrayList<String>(Arrays.asList("Hoang Hai Nguyen", "Trym Nilsen"));


	@FXML
	private void initialize() {// Setter opp dbConnection
		//db = new DBConnection();
	}

	public void setPopupStage(Stage popupStage) {
		this.popupStage = popupStage;
	}

	@FXML
	private void handleInvite() { //handle the invite button
		ArrayList<String> invited = new ArrayList<String>();
		for (CheckListObject clo : invitableMemberList) {	// gets all the names that have been selected in the list of members
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
		for (String member : allMembers) {
			memberList.add(member);
		}
		//skjuler admins i memberList
		for(String admin: group.getValue().getAdmins()){
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
		}
		if (adminList.size() < 2) {
			deleteAdminButton.setDisable(true);
		} else {
			deleteAdminButton.setDisable(false);
		}
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

		for (String member : allInvited) {
			CheckListObject clo = new CheckListObject(member);
//			if (!group.getValue().getMembers().contains(member) && !group.getValue().getAdmins().contains(member)) {
				invitableMemberList.add(clo);
//			}
		}

		this.invitableMembers.getChildren().clear();
		this.invitableMembers.getChildren().add(invitableMembers);
	}

	@FXML
	private void deleteAdmin(){
		String admin = admins.getSelectionModel().getSelectedItem();
		if (admin == null) {
			return;
		}
		adminList.remove(admin);
		if (adminList.size() == 1) {
			deleteAdminButton.setDisable(true);
		}
		memberList.add(admin);
		group.getValue().removeAdmin(admin);
		updateMemberList();
		updateInvitableMemberList();
	}

	@FXML
	private void deleteMember(){
		String memberToDelete = new String();
		memberToDelete = members.getSelectionModel().getSelectedItem();
		if (memberToDelete == null) {
			return;
		}
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
//		db.deleteGroup(group.getValue().getGroupID());
//		group.getParent().getChildren().remove(group);
		popupStage.close();
	}

	@FXML
	private void makeAdmin(){
		String newAdmin = new String();
		newAdmin = members.getSelectionModel().getSelectedItem();
		if (newAdmin == null) {
			return;
		}
		adminList.add(newAdmin);
		if (adminList.size() > 1) {
			deleteAdminButton.setDisable(false);
		}
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
		ArrayList<String> toBeMembers = new ArrayList<String>();
		for(String member: memberList){
			toBeMembers.add(member);
		}
		for(String admin: adminList){
			toBeMembers.add(admin);
		} 
//		if (!nameField.getText().equals(group.getValue().getName())) {
//			db.editGroupName(group.getValue().getName(), nameField.getText());
//			group.getValue().setName(nameField.getText());
//		}
//		try {
//			db.setGroupMembers(nameField.getText(), toBeMembers);// deletes the current members of the group and adds all currently selected
//			
//			for(String admin: adminList){
//				db.editGroupAdminRights(group.getValue().getName(), admin, 1);
//			}
//		}catch (SQLException e) {
//			e.printStackTrace();
//		}
		popupStage.close();
	}

	public void fillPopup(TreeItem<Group> group, String username) { // called whenever the popup is opened
//		try {
//			if (db.isPrivateGroup(group.getValue().getName())) {
//				invitableMembers.setDisable(true);
//				inviteBtn.setDisable(true);
//				deleteMemberButton.setDisable(true);
//				deleteAdminButton.setDisable(true);
//				makeAdminButton.setDisable(true);
//				deleteGroupButton.setDisable(true);
//			} else if (!db.isAdmin(username, group.getValue().getName())) { // if not admin
//				invitableMembers.setDisable(true);
//				nameField.setDisable(true);
//				inviteBtn.setDisable(true);
//				deleteMemberButton.setDisable(true);
//				deleteAdminButton.setDisable(true);
//				makeAdminButton.setDisable(true);
//				deleteGroupButton.setDisable(true);
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
		this.group = group;

		updateAdminList(); 				//for admins 
		updateMemberList();  			//Show members	
		updateInvitableMemberList(); 	//Show invitable Members();

		nameField.setText(group.getValue().getName());
	}

}

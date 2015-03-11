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
	@FXML private VBox invitableMembers;
	@FXML private ListView<String> members,admins;
	@FXML private Label memberListText, invitableMemberListText;
	@FXML private Button OKBtn, inviteBtn, deleteMemberButton, deleteAdminButton, makeAdminButton,deleteGroupButton;
	private TreeView<Group> treeView;
	private boolean editingExisting, createSub, isPrivate;
	private TreeItem<Group> group;
	private DBConnection db;
	private MainApp mainApp;

	@FXML
	private void initialize() {// Setter opp dbConnection
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

//		ArrayList<String> invited = new ArrayList<String>();
//		for (CheckListObject clo : invitableMemberList) {			// gets all the names that have been selected in the list of members
//			if (clo.getSelected()) {
//				invited.add(clo.getName());
//			}
//		}
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
			System.out.println("haha" + clo);
			invited.add(clo.getName());
			}
		}
		
//		for (CheckListObject clo2 : invitableMemberList) {			// gets all the names that have been selected in the list of members
//			if (!memberList.contains(clo2.getName())) {
//				invited.add(clo2.getName());	
//			}
//		}
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

	}
	
	private void updateAdminList(){
		admins.setEditable(true);
		admins.setItems(adminList);
		adminList.clear();
		
		for (String admin : group.getValue().getAdmins()) {
			adminList.add(admin);
		};
		System.out.println(group.getValue().getAdmins());
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
		if (!group.getValue().getMembers().contains(member) && !member.equals(mainApp.getUser().getName())) {
					invitableMemberList.add(clo);
				}
		}
		this.invitableMembers.getChildren().add(invitableMembers);
		this.invitableMembers.getChildren().clear();
		this.invitableMembers.getChildren().add(invitableMembers);
	}
	
	
	//buttons in EditGroupPopup.fxml
	@FXML
	private void deleteAdmin(){
		System.out.println("I'm deleting an admin");
	}
	
	@FXML
	private void deleteMember(){
		
		String memberToDelete = new String();
		memberToDelete = members.getSelectionModel().getSelectedItem();
		System.out.println("I'm deleting a member");
		System.out.println(memberToDelete);
		
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
		db.deleteGroup(group.getValue().getGroupID());
		group.getParent().getChildren().remove(group);
		popupStage.close();
	}
	
	@FXML
	private void makeAdmin(){
		System.out.println("I'm making an member a admin");
	}

	private String isValidInput() {
		String errorText = "";
		if (nameField.getText().equals("")) {
			errorText += "Beskrivelse kan ikke vaere tom\n";
		}
		return errorText;
	}

	@FXML
	private void handleOK() {
		popupStage.close();
	}

	public void fillPopup(TreeItem<Group> group, boolean createSub, MainApp mainApp) { // called whenever the popup is opened
		System.out.println(group.getValue().getName() + " " + group.getValue().getGroupID());
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
		updateAdminList(); 

		//Show members
		updateMemberList();


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

		
		editingExisting = true;
		nameField.setText(group.getValue().getName());
					
		for (String member : group.getParent().getValue().getMembers()) {
		CheckListObject clo = new CheckListObject(member);
		if (!group.getValue().getMembers().contains(member) && !member.equals(mainApp.getUser().getName())) {
					invitableMemberList.add(clo);
				}
		}
		this.invitableMembers.getChildren().add(invitableMembers);
		
	}

}

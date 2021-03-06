package gui;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import core.Group;
import database.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class ListController {

	private MainApp mainApp;
	private DBConnection db;
	@FXML private TreeView<Group> treeView;
	@FXML private Button newGroupBtn, editGroupBtn, newSubGroupBtn;
	private TreeItem<Group> privateGroup;

	@FXML
	private void initialize() { // can't use this method since it is called before mainApp is set
	}

	public void init2() {
		try {
		newSubGroupBtn.setDisable(true);
		db = new DBConnection();
		ResultSet AllGroupsRS = db.getAllGroupsOfUser(mainApp.getUser().getUsername()); // gets all groups the user is registered in
		ArrayList<String> groupIDs = new ArrayList<String>(); // the groupIDs of all the groups the user is registered in
		while (AllGroupsRS.next()) {
			groupIDs.add(AllGroupsRS.getString("usergroupID"));
		}
		AllGroupsRS.beforeFirst();
		ResultSet groupMembersRS = db.getAllGroupMembers(groupIDs); // gets all members of the all groups the user is registered in
		Map<String, TreeItem<Group>> groups = new HashMap<String, TreeItem<Group>>(); // HashMap that contains the usergroupID of a group and a TreeItem containing the group
			while (AllGroupsRS.next()) { // fills the HashMap with all the groups from the database
				ArrayList<String> groupMembers = new ArrayList<String>();
				ArrayList<String> groupAdmins = new ArrayList<String>();
				while (groupMembersRS.next()) {		// for all members of all the groups the user is registered in
					if (groupMembersRS.getString("groupName").equals(AllGroupsRS.getString("groupName"))) {	 // if the person is a member of the current group in the loop			
						if (groupMembersRS.getString("groupAdmin").equals("0")){
							groupMembers.add(groupMembersRS.getString("fullName"));
						} else {
							groupAdmins.add(groupMembersRS.getString("fullName"));
						}
					}
				}
				groupMembersRS.beforeFirst();
				TreeItem<Group> treeItem = new TreeItem<Group>(
						new Group(
								AllGroupsRS.getString("groupName"), 
								AllGroupsRS.getString("isPrivate").equals("1") ? true : false, 
								AllGroupsRS.getString("usergroupID"), 
								AllGroupsRS.getString("USERGROUP_usergroupID"), 
								groupMembers, 
								groupAdmins));
				groups.put(AllGroupsRS.getString("usergroupID"), treeItem);
			}
			TreeItem<Group> root = groups.get("0"); // The root group is the one with usergroupID = 0
			root.setExpanded(true);
			treeView.setRoot(root);
			treeView.setShowRoot(false);
			int higheshGroupID = db.getHighestGroupID(); // finds the highest usergroupID in the database
			for (int i = 0; i <= higheshGroupID; i++) {
				for (int j = 0; j <= higheshGroupID; j++) {
					// if USERGROUP_usergroupID of group j equals the usergroupID of group i (if group j is a subgroup of group i):
					if (groups.containsKey("" + i) && groups.containsKey("" + j) && groups.get("" + j).getValue().getSupergroupID() != null && groups.get("" + j).getValue().getSupergroupID().equals("" + i)) {
						// add the TreeItem of group j to the children of the TreeItem of grup i	
						groups.get("" + i).getChildren().add(groups.get("" + j));
						if (groups.get("" + j).getValue().isPrivateGroup()) {	// if the currently selected group is a private group
							privateGroup = groups.get("" + j);
							treeView.getSelectionModel().select(groups.get("" + j));
							mainApp.showCalendar(groups.get("" + j).getValue());
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<Group>>() {
			@Override
			public void changed(ObservableValue<? extends TreeItem<Group>> observableValue, TreeItem<Group> oldValue, TreeItem<Group> newValue) {
				if (newValue == null) {
					return;
				}
				if (newValue.getValue().isPrivateGroup() == true) {
					newGroupBtn.setDisable(false);
					editGroupBtn.setDisable(false);
					newSubGroupBtn.setDisable(true);
				} else {
					newGroupBtn.setDisable(false);
					editGroupBtn.setDisable(false);
					newSubGroupBtn.setDisable(false);
				}
				if (!newValue.getValue().getAdmins().contains(mainApp.getUser().getName())){
					newSubGroupBtn.setDisable(true);
				};
				try {
					if (!newValue.getValue().equals(treeView.getRoot().getValue())) {						
						mainApp.showCalendar(newValue.getValue());
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		// selects private group after a group has been deleted
		treeView.getRoot().getChildren().addListener(new ListChangeListener<TreeItem<Group>>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends TreeItem<Group>> c) {
				c.next();
				if (c.getRemovedSize() > 0) { // if a group was removed
					treeView.getSelectionModel().clearSelection();
					treeView.getSelectionModel().select(privateGroup);
				}				
			}
		});
	}

	public void selectPrivateGroup() {
		treeView.getSelectionModel().clearSelection();
		treeView.getSelectionModel().select(privateGroup);
	}
	
	public void deselectGroup() {
		newGroupBtn.setDisable(true);
		editGroupBtn.setDisable(true);
		newSubGroupBtn.setDisable(true);
		treeView.getSelectionModel().clearSelection();
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	@FXML
	private void newGroup() {
		mainApp.showGroupPopup(treeView, null, false);
	}

	@FXML
	private void editGroup() {
		mainApp.showEditGroupPopup(treeView.getSelectionModel().getSelectedItem());
	}

	@FXML
	private void newSubGroup() {
		mainApp.showGroupPopup(treeView, treeView.getSelectionModel().getSelectedItem(), true);
	}

}

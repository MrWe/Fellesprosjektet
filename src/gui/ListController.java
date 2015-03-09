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
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class ListController {

	private MainApp mainApp;
	private DBConnection db;
	@FXML private TreeView<Group> treeView;
	@FXML private Button editGroupBtn;

	@FXML
	private void initialize() { // can't use this method since it is called before mainApp is set
	}

	public void init2() {
		editGroupBtn.setDisable(true); //dårlig løsning til editBtn
		db = new DBConnection();
		ResultSet AllGroupsRS = db.getAllGroupsOfUser(mainApp.getUser().getUsername());
		Map<String, TreeItem<Group>> groups = new HashMap<String, TreeItem<Group>>(); // HashMap that contains the usergroupID of a group and a TreeItem containing the group
		try {
			while (AllGroupsRS.next()) { // fills the HashMap with all the groups from the database
				ResultSet groupMembersRS = db.getGroupMembers(AllGroupsRS.getString(1)); // gets all groupmembers of the current group
				ArrayList<String> groupMembers = new ArrayList<String>();
				while (groupMembersRS.next()) {
					//System.out.println(AllGroupsRS.getString(1) + " " + groupMembersRS.getString(1));
					groupMembers.add(groupMembersRS.getString(1));
				}
				//System.out.println(groupMembers);
				//System.out.println(AllGroupsRS.getString(1) + " " + AllGroupsRS.getString(2) + " " + AllGroupsRS.getString(3) + " " + AllGroupsRS.getString(4));
				TreeItem<Group> treeItem = new TreeItem<Group>(new Group(AllGroupsRS.getString(3), AllGroupsRS.getString(2).equals("1") ? true : false, AllGroupsRS.getString(1), AllGroupsRS.getString(4), groupMembers, new ArrayList<String>()));
				//System.out.println(treeItem.getValue().isPrivateGroup());
				groups.put(AllGroupsRS.getString(1), treeItem);
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
						if (groups.get("" + j).getValue().isPrivateGroup()) {
							treeView.getSelectionModel().select(groups.get("" + j));
							mainApp.showCalendar(groups.get("" + j).getValue());
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// when a TreeItem in the TreeView is clicked
		treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<Group>>() {
			@Override
			public void changed(ObservableValue<? extends TreeItem<Group>> observableValue, TreeItem<Group> oldValue, TreeItem<Group> newValue) {
				// show the calendar of the chosen group
				System.out.println("hei");
				if (newValue == null) {
					return;
				}

				if(newValue.getValue().isPrivateGroup() == true){
					editGroupBtn.setDisable(true);
				}else{
					editGroupBtn.setDisable(false);
				}
				mainApp.showCalendar(newValue.getValue());
				try {
					mainApp.showCalendar(newValue.getValue());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
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
		mainApp.showEditGroupPopup(treeView, treeView.getSelectionModel().getSelectedItem(), false);
	}

	@FXML
	private void newSubGroup() {
		mainApp.showGroupPopup(treeView, treeView.getSelectionModel().getSelectedItem(), true);
	}

}

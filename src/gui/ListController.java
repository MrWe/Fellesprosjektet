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
import javafx.collections.ListChangeListener.Change;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeView.EditEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

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
				ArrayList<String> groupAdmins = new ArrayList<String>();
				while (groupMembersRS.next()) {
					if(groupMembersRS.getString(2).equals("0")){
						groupMembers.add(groupMembersRS.getString(1));
					}else{
						groupAdmins.add(groupMembersRS.getString(1));
					}
					
				}
				//System.out.println(groupMembers);
				//System.out.println(AllGroupsRS.getString(1) + " " + AllGroupsRS.getString(2) + " " + AllGroupsRS.getString(3) + " " + AllGroupsRS.getString(4));
				TreeItem<Group> treeItem = new TreeItem<Group>(new Group(AllGroupsRS.getString(3), AllGroupsRS.getString(2).equals("1") ? true : false, AllGroupsRS.getString(1), AllGroupsRS.getString(4), groupMembers, groupAdmins));
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
							treeView.getSelectionModel().clearSelection();
							treeView.getSelectionModel().select(groups.get("" + j));
							System.out.println(groups.get("" + j).getValue());
							mainApp.showCalendar(groups.get("" + j).getValue());
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// when a TreeItem in the TreeView is clicked
		treeView.expandedItemCountProperty().addListener(new ChangeListener() {

			@Override
			public void changed(ObservableValue observable, Object oldValue,
					Object newValue) {
					System.out.println("expandedItemCountProperty: " + newValue);
					treeView.getSelectionModel().select(treeView.getRoot());
				
			}
		});

		treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<Group>>() {
			@Override
			public void changed(ObservableValue<? extends TreeItem<Group>> observableValue, TreeItem<Group> oldValue, TreeItem<Group> newValue) {
				// show the calendar of the chosen group
				//System.out.println("chose another group");
				if (newValue == null) {
					return;
				}
				System.out.println("selectedItemProperty listener: " + newValue.getValue().getName());
				if(newValue.getValue().isPrivateGroup() == true){
					editGroupBtn.setDisable(true);
				}else{
					editGroupBtn.setDisable(false);
				}
				try {
					mainApp.showCalendar(newValue.getValue());
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		treeView.setOnEditCommit(new EventHandler<TreeView.EditEvent<Group>>() {

			@Override
			public void handle(EditEvent<Group> event) {
				System.out.println("woho");
				System.out.println(event);
				
			}
		});
	}
	
	
	public void setKeyEventHandler(Scene scene)	{
		EventHandler<KeyEvent> keyHandler = new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyCode) {
				System.out.println(keyCode.getCode());
				if (keyCode.getCode() == KeyCode.H) {
					System.out.println("before: " + treeView.getSelectionModel().getSelectedItem().getValue().getName());
					treeView.getSelectionModel().select(0);
					System.out.println("after: " + treeView.getSelectionModel().getSelectedItem().getValue().getName());
				}
			}
		};
		scene.setOnKeyPressed(keyHandler);
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
		mainApp.showEditGroupPopup(treeView, treeView.getSelectionModel().getSelectedItem());
	}

	@FXML
	private void newSubGroup() {
		mainApp.showGroupPopup(treeView, treeView.getSelectionModel().getSelectedItem(), true);
	}

}

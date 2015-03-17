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
	//@FXML private TreeItem treeItem;
	@FXML private Button editGroupBtn;
	@FXML private Button newSubGroupBtn;
	
	private TreeItem<Group> privateGroup;

	@FXML
	private void initialize() { // can't use this method since it is called before mainApp is set
	}

	public void init2() {
		newSubGroupBtn.setDisable(true);
		db = new DBConnection();
		ResultSet AllGroupsRS = db.getAllGroupsOfUser(mainApp.getUser().getUsername());
		Map<String, TreeItem<Group>> groups = new HashMap<String, TreeItem<Group>>(); // HashMap that contains the usergroupID of a group and a TreeItem containing the group
		try {
			while (AllGroupsRS.next()) { // fills the HashMap with all the groups from the database
				ResultSet groupMembersRS = db.getGroupMembers(AllGroupsRS.getString("usergroupID")); // gets all groupmembers of the current group
				ArrayList<String> groupMembers = new ArrayList<String>();
				ArrayList<String> groupAdmins = new ArrayList<String>();
				while (groupMembersRS.next()) {
					if(groupMembersRS.getString("groupAdmin").equals("0")){
						groupMembers.add(groupMembersRS.getString("fullName"));
					}else{
						groupAdmins.add(groupMembersRS.getString("fullName"));
					}

				}
				TreeItem<Group> treeItem = new TreeItem<Group>(new Group(AllGroupsRS.getString("groupName"), AllGroupsRS.getString("isPrivate").equals("1") ? true : false, AllGroupsRS.getString("usergroupID"), AllGroupsRS.getString("USERGROUP_usergroupID"), groupMembers, groupAdmins));
				
//				treeItem.expandedProperty().addListener(new ChangeListener<Boolean>() {
//					@Override
//					public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//						System.out.println("efef");
//						System.out.println("newValue = " + newValue);
//						BooleanProperty bb = (BooleanProperty) observable;
//						System.out.println("bb.getBean() = " + bb.getBean());
//						TreeItem t = (TreeItem) bb.getBean();
//						// Do whatever with t
//					}
//				});
				
				
				//System.out.println(treeItem.getValue().isPrivateGroup());
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
						if (groups.get("" + j).getValue().isPrivateGroup()) {
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
				System.out.println("selection listener " + oldValue + " " + newValue);
				if (!treeView.getRoot().getChildren().contains(oldValue)) {
					System.out.println("oldValue not contained");
				}
				if (!treeView.getRoot().getChildren().contains(newValue)) {
					System.out.println("newValue not contained");
				}
				// show the calendar of the chosen group
				//System.out.println("chose another group");
				if (newValue == null) {
					return;
				}
				System.out.println("selectedItemProperty listener: " + newValue.getValue().getName());
				if(newValue.getValue().isPrivateGroup() == true){
					newSubGroupBtn.setDisable(true);
				}else{
					editGroupBtn.setDisable(false);
					newSubGroupBtn.setDisable(false);
				}
				
				if(!newValue.getValue().getAdmins().contains(mainApp.getUser().getName())){
					newSubGroupBtn.setDisable(true);
				};
				
				try {
					if (!newValue.getValue().equals(treeView.getRoot().getValue())) {						
						mainApp.showCalendar(newValue.getValue());
					} else {
						System.out.println("root group has been declined selection");
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
//				treeView.setMinHeight(treeView.getExpandedItemCount()*37 + 2);
//				treeView.setMaxHeight(treeView.getExpandedItemCount()*37 + 2);
			}
		});
		treeView.getRoot().getChildren().addListener(new ListChangeListener<TreeItem<Group>>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends TreeItem<Group>> c) {
				c.next();
				if (c.getRemovedSize() > 0) { // a group was removed
					treeView.getSelectionModel().clearSelection();
					treeView.getSelectionModel().select(privateGroup);
				}
				System.out.println("removed size: " + c.getRemovedSize());
				
			}
		});

		//treeView.setMaxHeight(treeView.getExpandedItemCount()*37);		
		//why the hell doesnt the codes below work?
//		treeView.getSelectionModel().getSelectedItem().expandedProperty().addListener(new ChangeListener<Boolean>() {
//			@Override
//			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//				System.out.println("efef");
//				System.out.println("newValue = " + newValue);
//				BooleanProperty bb = (BooleanProperty) observable;
//				System.out.println("bb.getBean() = " + bb.getBean());
//				TreeItem t = (TreeItem) bb.getBean();
//				// Do whatever with t
//			}
//		});

		treeView.expandedItemCountProperty().addListener(new ChangeListener() {

			@Override
			public void changed(ObservableValue observable, Object oldValue,
					Object newValue) {
				System.out.println(oldValue + " " + newValue);
				treeView.setMinHeight(((int) oldValue)*37 + 2);
				treeView.setMaxHeight(((int) oldValue)*37 + 2);
			}
		});
		treeView.setMaxHeight(treeView.getExpandedItemCount()*37);
	}
	
	public void selectPrivateGroup() {
		treeView.getSelectionModel().clearSelection();
		treeView.getSelectionModel().select(privateGroup);
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

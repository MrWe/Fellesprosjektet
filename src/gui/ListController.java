package gui;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import core.Group;
import database.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class ListController {

	private MainApp mainApp;
	private DBConnection db;
	@FXML private TreeView<Group> treeView;

	@FXML
	private void initialize() {
		db = new DBConnection();
		ResultSet rs = db.getAllGroups();
		Map<String, TreeItem<Group>> groups = new HashMap<String, TreeItem<Group>>(); // HashMap that contains the usergroupID of a group and a TreeItem containing the group
		try {
			while (rs.next()) { // fills the HashMap with all the groups from the database
				groups.put(rs.getString(1), new TreeItem<Group>(new Group(rs.getString(3), rs.getString(4), new ArrayList<String>(), new ArrayList<String>())));
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
				System.out.println(newValue);
				// show the calendar of the chosen group
				mainApp.showCalendar(newValue.getValue());
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
		mainApp.showGroupPopup(treeView, treeView.getSelectionModel().getSelectedItem(), false);
	}

	@FXML
	private void newSubGroup() {
		mainApp.showGroupPopup(treeView, treeView.getSelectionModel().getSelectedItem(), true);
	}

}

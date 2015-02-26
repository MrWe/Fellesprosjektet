package app;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class ListController {

	private MainApp mainApp;
	@FXML private TreeView<Group> treeView;

	@FXML
	private void initialize() {
		Group rootGroup = new Group("Alle grupper", new ArrayList<String>(Arrays.asList("hei", "du")), new ArrayList<String>());
		Group fproot = new Group("Fellesprosjekt", new ArrayList<String>(Arrays.asList("hei", "du")), new ArrayList<String>());
		Group fp1 = new Group("Fellesprosjekt gruppe 1", new ArrayList<String>(Arrays.asList("hei", "du")), new ArrayList<String>());
		Group fp2 = new Group("Fellesprosjekt gruppe 2", new ArrayList<String>(Arrays.asList("hei", "du")), new ArrayList<String>());
		Group fp3 = new Group("Fellesprosjekt gruppe 3", new ArrayList<String>(Arrays.asList("hei", "du")), new ArrayList<String>());
		Group abakus = new Group("Abakus", new ArrayList<String>(Arrays.asList("hei", "du")), new ArrayList<String>());
		Group online = new Group("Online", new ArrayList<String>(Arrays.asList("hei", "du")), new ArrayList<String>());
		Group triKom = new Group("triKom", new ArrayList<String>(Arrays.asList("hei", "du")), new ArrayList<String>());
		rootGroup.addSubgroups(fproot, abakus, online);
		fproot.addSubgroups(fp1, fp2, fp3);
		online.addSubgroup(triKom);
		
		
		treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                TreeItem treeItem = (TreeItem)newValue;
                System.out.println("Selected item is" + treeItem);
            }
        });

		TreeItem<Group> root = new TreeItem<Group>(rootGroup);
		root.setExpanded(true);
		root.getChildren().add(new TreeItem<Group>(fproot));
		root.getChildren().add(new TreeItem<Group>(abakus));
		root.getChildren().add(new TreeItem<Group>(online));
		root.getChildren().get(0).getChildren().add(new TreeItem<Group>(fp1));
		root.getChildren().get(0).getChildren().add(new TreeItem<Group>(fp2));
		root.getChildren().get(0).getChildren().add(new TreeItem<Group>(fp3));
		root.getChildren().get(2).getChildren().add(new TreeItem<Group>(triKom));
		treeView.setShowRoot(false);
		treeView.setRoot(root);
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	@FXML
	private void newGroup() {
		mainApp.showGroupPopup(treeView);
	}

}

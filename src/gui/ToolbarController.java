package gui;

import java.sql.SQLException;
import java.util.ArrayList;

import javafx.fxml.FXML;
import core.Group;

public class ToolbarController {

	private MainApp mainApp;

	@FXML
	private void initialize() {

	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	@FXML
	private void openHome() throws SQLException {
		mainApp.showCalendar(new Group("",false, "0", "0", new ArrayList<String>(), new ArrayList<String>()));
		System.out.println("Opened Home");
	}

	@FXML
	private void openNotice() {
		mainApp.showNotification();
		System.out.println("Opened Notice");
	}

	@FXML
	private void openSettings() {
		mainApp.showSettings();
		System.out.println("Opened Settings");
	}

	@FXML
	private void logOut() {
		mainApp.logOut();
		System.out.println("Logged Out");
	}
}

package gui;

import javafx.fxml.FXML;

public class ToolbarController {

	private MainApp mainApp;

	@FXML
	private void initialize() {

	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	@FXML
	private void openHome() {
		mainApp.showWelcome();
	}

	@FXML
	private void openNotice() {
		mainApp.deselectGroup();
		mainApp.showNotification();
	}

	@FXML
	private void openSettings() {
		mainApp.deselectGroup();
		mainApp.showSettings();
	}

	@FXML
	private void logOut() {
		mainApp.logOut();
	}

}

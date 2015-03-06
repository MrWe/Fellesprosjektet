package gui;

import database.DBConnection;
import javafx.fxml.FXML;

public class NotificationController {
	
	private MainApp mainApp;
	private DBConnection db;

	@FXML
	private void initialize() {
		db = new DBConnection();
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

}

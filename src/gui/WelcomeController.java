package gui;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import database.DBConnection;

public class WelcomeController {

	private MainApp mainApp;
	private DBConnection db;
	@FXML private Text text;

	@FXML
	private void initialize() {
		db = new DBConnection();
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	public void initText() {
		text.setText("Velkommen, " + mainApp.getUser().getName());
	}

}

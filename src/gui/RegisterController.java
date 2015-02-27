package gui;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class RegisterController {
	
	private MainApp mainApp;
	@FXML private TextField usernameField;
	@FXML private TextField passwordField;
	@FXML private TextField repeatPasswordField;
	@FXML private TextField emailField;
	@FXML private DatePicker birthdayDatePicker;
	
	@FXML
	private void initialize() {

	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	@FXML private void back() {
		mainApp.showLogin();
	}
	
	@FXML private void register() {
		
	}


}

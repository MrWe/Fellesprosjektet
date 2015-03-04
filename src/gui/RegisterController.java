package gui;

import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import database.DBConnection;

public class RegisterController {

	private MainApp mainApp;
	@FXML private TextField usernameField;
	@FXML private TextField passwordField;
	@FXML private TextField repeatPasswordField;
	@FXML private TextField fullNameField;
	@FXML private TextField emailField;
	@FXML private DatePicker birthdayDatePicker;
	@FXML private Text errorText;
	private DBConnection db;

	@FXML
	private void initialize() {
		db = new DBConnection();
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	@FXML private void back() { // when back button is pressed
		mainApp.showLogin();
	}

	@FXML private void register() { // when register button is pressed
		String validInput = isValidInput();
		if (validInput.length() != 0) {
			errorText.setText(validInput);
		} else {
			try {
				db.registerUser(usernameField.getText(), passwordField.getText(), fullNameField.getText(), birthdayDatePicker.getValue().toString(), emailField.getText());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private String isValidInput() { // if all the if-tests fail, the text will return empty (length = 0)
		String errorText = "";
		ResultSet rs = db.getUser(usernameField.getText());
		try {
			if (rs.next()) {
				errorText += "Username already exists\n";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (usernameField.getText().length() == 0) {
			errorText += "Username can't be empty\n";
		}
		if (passwordField.getText().length() == 0) {
			errorText += "Password can't be empty\n";
		}
		if (!passwordField.getText().equals(repeatPasswordField.getText())) {
			errorText += "Passwords don't match\n";
		}
		if (fullNameField.getText().length() == 0) {
			errorText += "Full name can't be emoty\n";
		}
		if (emailField.getText().length() == 0) {
			errorText += "Email can't be empty\n";
		}
		if (birthdayDatePicker.getValue() == null) {
			errorText += "Birthday can't be empty\n";
		}
		return errorText;
	}

}

package gui;

import java.sql.ResultSet;
import java.sql.SQLException;
import database.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class SettingsController {

	private MainApp mainApp;
	private DBConnection db;
	@FXML private TextField emailField;
	@FXML private PasswordField oldPasswordField;
	@FXML private PasswordField newPasswordField;
	@FXML private PasswordField	 repeatNewPasswordField;
	@FXML private Text errorText;

	@FXML
	private void initialize() {
		db = new DBConnection();
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	@FXML
	private void changeValues() {
		String validInput = isValidInput();
		if (validInput.length() == 0) {
			if (emailField.getText().length() != 0) {				
				db.setEmail(mainApp.getUser().getUsername(), emailField.getText());
			}
			try {
				ResultSet rs = db.getLoginInfo(mainApp.getUser().getUsername());
				rs.next();
				if (rs.getString("pswd").equals(oldPasswordField.getText()) && newPasswordField.getText().equals(repeatNewPasswordField.getText())) {
					db.setPassword(mainApp.getUser().getUsername(), newPasswordField.getText());
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			errorText.setText(validInput);
		}
		emailField.setText("");
		oldPasswordField.setText("");
		newPasswordField.setText("");
		repeatNewPasswordField.setText("");
	}

	private String isValidInput() {
		String errorText = "";
		try {
			ResultSet rs = db.getLoginInfo(mainApp.getUser().getUsername());
			rs.next();
			if (!rs.getString("pswd").equals(oldPasswordField.getText()) && newPasswordField.getText().length() != 0) {
				errorText += "Gammelt passord stemmer ikke\n";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (newPasswordField.getText().length() == 0) {
			errorText += "Nytt passord er tomt";
		}
		if (!newPasswordField.getText().equals(repeatNewPasswordField.getText())) {
			errorText += "Nytt passord og gjenta nytt passord matcher ikke\n";
		}
		return errorText;
	}

	@FXML
	private void cancel() throws SQLException {
		emailField.setText("");
		oldPasswordField.setText("");
		newPasswordField.setText("");
		repeatNewPasswordField.setText("");
		errorText.setText("");
	}
}

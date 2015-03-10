package gui;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import database.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class LoginController {
	
	private MainApp mainApp;
	@FXML private Text dateText;
	@FXML private Text errorText;
	@FXML private VBox vbox;
	@FXML private TextField usernameField;
	@FXML private PasswordField passwordField;
	private DBConnection db;
	
	@SuppressWarnings("deprecation")
	@FXML
	private void initialize() {
		db = new DBConnection();
		Calendar c = Calendar.getInstance();
		String date = String.format("%02d", c.getTime().getDate()) + "/" + String.format("%02d", (c.getTime().getMonth() + 1)) + "/" + (c.getTime().getYear() + 1900);
		dateText.setText("Dagens dato: " + date);

	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	@FXML
	private void login() { // when log in button is pressed
		try {
			ResultSet rs = db.getLoginInfo(usernameField.getText()); // gets all database entries with the given username
			if (rs.next()) {								  // if a database entry with the username exists
				if (rs.getString(2).equals(passwordField.getText())) { // if the password given matches the password in the database
					mainApp.login(usernameField.getText(),rs.getString(3));
				} else {
					errorText.setText("Error logging in"); // if password is wrong
				}
			} else {
				errorText.setText("Username not found"); // if username is not found
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@FXML private void showRegister() { // when new user button is pressed
		mainApp.showRegister();
	}

}

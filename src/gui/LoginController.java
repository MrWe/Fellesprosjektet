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
	@FXML private VBox vbox;
	@FXML private TextField usernameField;
	@FXML private PasswordField passwordField;
	private DBConnection db;
	
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
	private void login() {
		try {
			ResultSet rs = db.login(usernameField.getText());
			if (rs.next()) {
				System.out.println(rs.getString(1) + " " + rs.getString(2));
				if (rs.getString(2).equals(passwordField.getText())) {
					System.out.println("logged in!");
					mainApp.login();
				} else {
					System.out.println("error logging in");
				}
			} else {
				System.out.println("username not found");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@FXML private void showRegister() {
		mainApp.showRegister();
	}

}

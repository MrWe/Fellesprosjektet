package app;

import java.util.Calendar;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class LoginController {
	
	private MainApp mainApp;
	@FXML private Text dateText;
	@FXML private VBox vbox;
	
	@FXML
	private void initialize() {
		Calendar c = Calendar.getInstance();
		String date = String.format("%02d", c.getTime().getDate()) + "/" + String.format("%02d", (c.getTime().getMonth() + 1)) + "/" + (c.getTime().getYear() + 1900);
		dateText.setText("Dagens dato: " + date);

	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	@FXML
	private void login() {
		mainApp.login();
	}

}

package gui;

import java.sql.SQLException;
import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
	private void openHome() {
//		try {
			//mainApp.showCalendar(new Group("",false, "0", "0", new ArrayList<String>(), new ArrayList<String>()));
			//mainApp.login(mainApp.getUser().getUsername(), mainApp.getUser().getName());
			mainApp.showWelcome();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
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

	public void setKeyEventHandler(Scene scene) {
		EventHandler<KeyEvent> keyHandler = new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyCode) {
				System.out.println(keyCode.getCode());
				if (keyCode.getCode() == KeyCode.H) {
					openHome();
				} else if (keyCode.getCode() == KeyCode.N) {
					openNotice();
				} else if (keyCode.getCode() == KeyCode.S) {
					openSettings();
				}
			}
		};
		scene.setOnKeyPressed(keyHandler);
	}
}

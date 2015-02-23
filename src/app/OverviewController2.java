package app;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class OverviewController2 {

	@FXML private Text text;

	// Reference to the main application.
	private MainApp mainApp;

	/**
	 * The constructor.
	 * The constructor is called before the initialize() method.
	 */
	public OverviewController2() {
	}


	@FXML
	private void initialize() {

	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	@FXML
	private void handlePopup() {
		mainApp.showPopUp();
	}
	
	@FXML
	private void gotoOverview() {
		mainApp.showOverview();
	}

}
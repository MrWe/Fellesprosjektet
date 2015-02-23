package app;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class OverviewController {

	@FXML private Text text;

	// Reference to the main application.
	private MainApp mainApp;

	/**
	 * The constructor.
	 * The constructor is called before the initialize() method.
	 */
	public OverviewController() {
	}


	@FXML
	private void initialize() {

	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	@FXML
	private void gotoOverview2() {
		mainApp.showOverview2();
	}

}
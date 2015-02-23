package app;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PopUpController {

	@FXML private Text text;
	private Stage popupStage;

	@FXML
	private void initialize() {
	}

	public void setPopupStage(Stage popupStage) {
		this.popupStage = popupStage;
	}

	@FXML
	private void handleOk() {
		popupStage.close();

	}

}
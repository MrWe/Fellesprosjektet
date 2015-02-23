package app;

import java.util.Calendar;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;

public class CalendarController {

	@FXML private Text text;
	@FXML private GridPane calendar;

	// Reference to the main application.
	private MainApp mainApp;

	@FXML
	private void initialize() {

	}

	public void fillCalendar() {
		calendar.setStyle("-fx-border-color: #000000;");
		for (int i = 0; i < 7; i++) { // sets contraints on each of the 7 columns
			ColumnConstraints columnConstraints = new ColumnConstraints();
			columnConstraints.setFillWidth(true);
			columnConstraints.setHgrow(Priority.ALWAYS);
			columnConstraints.setMinWidth(75);
			calendar.getColumnConstraints().add(columnConstraints);
		}
		for (int i = 0; i < 5; i++) { // sets contraints on each of the 5 rows
			RowConstraints rowConstraints = new RowConstraints();
			rowConstraints.setFillHeight(true);
			rowConstraints.setVgrow(Priority.ALWAYS);
			rowConstraints.setMinHeight(20);
			calendar.getRowConstraints().add(rowConstraints);
		}
		Calendar c = Calendar.getInstance(); // date today
		for (int i = 0; i < 5; i++) {		// for each date: create string on the format dd/mm/yyyy
			for (int j = 0; j < 7; j++){	// and create a new CalendarSquarePane object for each of them
				String date = String.format("%02d", c.getTime().getDate()) + "/" + String.format("%02d", (c.getTime().getMonth() + 1)) + "/" + (c.getTime().getYear() + 1900);
				calendar.add(new CalendarSquarePane(mainApp, 70, 70, date), j, i);
				c.add(Calendar.DATE, 1); // increase date by 1
			}
		}
		AnchorPane.setLeftAnchor(calendar, 5.0);
		AnchorPane.setRightAnchor(calendar, 5.0);
		AnchorPane.setTopAnchor(calendar, 5.0);
		AnchorPane.setBottomAnchor(calendar, 40.0);
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

}
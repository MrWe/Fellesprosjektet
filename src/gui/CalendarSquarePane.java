package gui;

<<<<<<< HEAD
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

=======
>>>>>>> d406bf75acf9fbb50e8a3e59f5a4bcedf02cbc26
import core.Appointment;
import core.Group;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class CalendarSquarePane extends Pane {

	public Text text;				// displays the date
	private MainApp mainApp;
	private VBox appointmentList;	// list of AppointmentSquarePane-objects
	private ScrollPane scrollPane;
	private Group group;			// the group that is currently selected in the list of groups
	private String date;

	public CalendarSquarePane(MainApp mainApp, String date, Group group) {
		super();
		this.date = date;
		this.mainApp = mainApp;
		this.group = group;
		text = new Text(date.substring(8, 10));
		text.setLayoutX(5);
		text.setLayoutY(15);
		text.setFill(Color.WHITE);
		this.getChildren().add(text);

		this.setStyle("-fx-border-color: #124364;");
		this.setOnMousePressed(new EventHandler<MouseEvent>() { // when a square is pressed
			@Override
			public void handle(MouseEvent event) {
				if (event.getTarget() instanceof AppointmentSquarePane) {
					try {
						showPopup((AppointmentSquarePane) event.getTarget());
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} else {					
					try {
						showPopup();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		});	
		appointmentList = new VBox();
		scrollPane = new ScrollPane(appointmentList);
		scrollPane.setLayoutY(20);
		scrollPane.setPrefSize(74, 60);
		scrollPane.setFocusTraversable(false);
		appointmentList.setAlignment(Pos.TOP_LEFT);
		scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		appointmentList.setSpacing(2);
		
		this.getChildren().add(scrollPane);

		Rectangle clipRectangle = new Rectangle(); // makes sure the contents of a square is not shown when the square is too small to display it
		this.setClip(clipRectangle);
		this.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
			text.setLayoutX(newValue.getWidth() - text.getLayoutBounds().getWidth() -2);
			clipRectangle.setWidth(newValue.getWidth());
			clipRectangle.setHeight(newValue.getHeight());
			scrollPane.setPrefSize(newValue.getWidth(), newValue.getHeight() - 20);
			appointmentList.setPrefWidth(newValue.getWidth());
		});
	}

	public String getDate() {
		return date;
	}

	private void showPopup() throws SQLException {
		mainApp.showAppointmentPopup(this, null, group);
	}

<<<<<<< HEAD
	//edit en appointment
	private void showPopup(AppointmentSquarePane asp) throws SQLException {
=======
	private void showPopup(AppointmentSquarePane asp) {
>>>>>>> d406bf75acf9fbb50e8a3e59f5a4bcedf02cbc26
		mainApp.showEditAppointmentPopup(null, asp, group);
	}

	public void addAppointment(Appointment appointment) {
		AppointmentSquarePane asp = new AppointmentSquarePane(appointment, date);
		appointmentList.getChildren().add(asp);
	}

	@Override
	public String toString() {
		return super.toString() + " " + date;
	}

	public Text getText(){
		return text;
	}

}
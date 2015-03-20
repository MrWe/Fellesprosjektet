package gui;

import java.sql.SQLException;

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
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class CalendarSquarePane extends Pane {

	public Text text;	// displays the date
	private MainApp mainApp;
	private VBox appointmentList;	// list of AppointmentSquarePane-objects
	private ScrollPane scrollPane;
	private Group group;	// the group that is currently selected in the list of groups
	private String date;
	private Rectangle clipRectangle;

	public CalendarSquarePane(MainApp mainApp, String date, Group group, boolean today) {
		super();
		if (today) {
			Arc arc = new Arc(0, 0, 20, 20, -90, 90);
			arc.setType(ArcType.ROUND);
			arc.setFill(Paint.valueOf("RED"));
			this.getChildren().add(arc);
		}
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
		appointmentList = new VBox();	// list of appointment square panes
		appointmentList.setAlignment(Pos.TOP_LEFT);
		appointmentList.setSpacing(2);
		appointmentList.setFocusTraversable(false);
		scrollPane = new ScrollPane(appointmentList); // scroll pane containing the list of appointment square panes
		scrollPane.setLayoutY(20);
		scrollPane.setPrefSize(74, 60);
		scrollPane.setFocusTraversable(false);
		scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		this.getChildren().add(scrollPane);
		clipRectangle = new Rectangle(); // makes sure the contents of a square is not shown when the square is too small to display it
		this.setClip(clipRectangle);
		this.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
			clipRectangle.setWidth(newValue.getWidth());
			clipRectangle.setHeight(newValue.getHeight());
			scrollPane.setPrefSize(newValue.getWidth(), newValue.getHeight() - 20);
			appointmentList.setPrefWidth(newValue.getWidth());
		});
		this.focusedProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println(oldValue + " " + newValue);
			if (newValue) {
				this.getParent().requestFocus();
			}
		});
	}

	public String getDate() {
		return date;
	}

	private void showPopup() throws SQLException {
		mainApp.showAppointmentPopup(this, null, group);
	}

	private void showPopup(AppointmentSquarePane asp) throws SQLException {
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
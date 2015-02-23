package app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

public class CalendarSquarePane extends Pane {
	
	private Text text;
	private MainApp mainApp;
	private ListView<Appointment> appointments;
	final ObservableList<Appointment> listItems = FXCollections.observableArrayList();
	private VBox appointmentList;
	
	public CalendarSquarePane(MainApp mainApp, int prefWidth, int prefHeight, String date) {
		super();
		this.mainApp = mainApp;
		text = new Text(date);
		this.setPrefSize(prefWidth, prefHeight);
		text.setLayoutX(5);
		text.setLayoutY(15);
		this.getChildren().add(text);
		this.setStyle("-fx-border-color: #000000;");
		this.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				showPopup();
			}
		});
		appointmentList = new VBox();
		appointmentList.getChildren().addAll(new Text("Hei"), new Text("på"), new Text("deg!"));
		appointmentList.setLayoutX(1);
		appointmentList.setLayoutY(20);
		this.getChildren().add(appointmentList);
		
		
//		appointments = new ListView<Appointment>();
//		appointments.setPrefSize(100, 50);
//		appointments.setLayoutX(1);
//		appointments.setLayoutY(20);
//		appointments.setItems(listItems);
//		this.getChildren().add(appointments);
		
		
//		this.widthProperty().addListener(new ChangeListener<Number>() {
//		    @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth, Number newWidth) {
//		        System.out.println("Width: " + newWidth);
//		        //onResize((double) newWidth, -1);
//		    }
//		});
//		this.heightProperty().addListener(new ChangeListener<Number>() {
//		    @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldHeight, Number newHeight) {
//		        System.out.println("Height: " + newHeight);
//		        //onResize(-1, (double) newHeight);
//		    }
//		});
	}
	
	public String getDate() {
		return text.getText();
	}
	
	private void showPopup() {
		mainApp.showPopUp(this);
	}
	
	public void addAppointment(Appointment appointment) {
		listItems.add(appointment);
	}
	

//	private void onResize(double width, double height) {
//		if (width != -1 && width / 8 >= 5) {
//			text.setLayoutX(width / 8);
//		}
//		if (height!= -1 && height / 4 >= 15) {
//			text.setLayoutY(height / 4);
//		}
//	}
	

}

package gui;

import java.awt.Label;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;



public class NotificationController {

	private MainApp mainApp;
	private DBConnection db;
	@FXML
	private TableColumn<Alert, String> date;
	@FXML
	private TableColumn<Alert, String> time;
	@FXML private TableView<Alert> notification;
	private final ObservableList<Alert> data =
			FXCollections.observableArrayList();
	
	private Label dateLabel;
	private Label timeLabel;



	@FXML
	private void initialize() {
		db = new DBConnection();	
	}

	public void init2(){
		
		date.setCellValueFactory(
				cellData -> cellData.getValue().getDateString());
		time.setCellValueFactory(
				cellData -> cellData.getValue().getTimeString());
		ResultSet rs = null;


		try {
			rs = getAlert(getUsername());
			while(rs.next()){
				String dateText = rs.getString(2).substring(0, 10);
				String timeText = rs.getString(2).substring(10, 16);

				data.add(new Alert(dateText, timeText));
				data.add(new Alert("Hei", "Heisann"));
				data.add(new Alert("lol", "lollan"));

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		notification.setItems(data);

	


	}


	public void acceptAppointment(){

	}

	public void declineAppointment(){

	}



	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;

	}

	public ResultSet getAlert(String username) throws SQLException{
		return db.getAlert(username);
	}


	public String getUsername() throws SQLException{
		return mainApp.getUser().getUsername();

	}

}


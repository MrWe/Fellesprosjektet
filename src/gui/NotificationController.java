package gui;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import core.Alert;
import javafx.event.ActionEvent;
import database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class NotificationController {

	private MainApp mainApp;
	private DBConnection db;
	@FXML
	private Button acceptBtn;
	@FXML
	private Button declineBtn;
	@FXML private ListView<Object> notification;
	private final ObservableList<Object> data =
			FXCollections.observableArrayList();
	private ArrayList<Alert> alerts = new ArrayList<Alert>();
	
	@FXML
	private void initialize() {
		db = new DBConnection();
	}

	public void init2(){
		ResultSet rs = null;
		try {
			rs = getAlert(getUsername());
			while(rs.next()){
				int id = rs.getInt(1);
				String dateText = rs.getString(2).substring(0, 10);
				String timeText = rs.getString(2).substring(10, 16);
				int userId = rs.getInt(4);
				
				int appointmentId = rs.getInt(5);
				
				Alert temp = new Alert(id, dateText, timeText, userId, appointmentId);
				
				alerts.add(temp);
				
				data.add(temp.toString());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		notification.setItems(data);
		
		acceptBtn.setOnAction((event -> {
			try {
				acceptAppointment(event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}));
		declineBtn.setOnAction((event -> {
			try {
				declineAppointment(event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}));

	}

	public void acceptAppointment(ActionEvent event) throws SQLException{
		
		int index = notification.getSelectionModel().getSelectedIndex();
		if(index == -1){
			return;
		}
		Alert alert = alerts.get(index);
		db.updateAcceptedAppointmentMembers(alert.getAppointmentId(), alert.getUserId());
		
		
		//db.addAppointmentMembers(Integer.parseInt(db.getLastAppointmentID()), members);
		
		removeAlert(alert.getID(), index);
	}

	public void declineAppointment(ActionEvent event) throws SQLException{
		
		int index = notification.getSelectionModel().getSelectedIndex();
		if(index == -1){
			return;
		}
		Alert alert = alerts.get(index);
		
		removeAlert(alert.getID(), index);

	}
	
	private void removeAlert(int id, int index) throws SQLException{
		alerts.remove(index);
		data.remove(index);
		db.deleteAlarm(id);
		notification.setItems(data);
		
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


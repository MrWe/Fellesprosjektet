package gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class Alert {
	
	private SimpleStringProperty dateString;
	private SimpleStringProperty timeString;
	
	

	public Alert(String date, String time){
		this.dateString = new SimpleStringProperty(date);
		this.timeString = new SimpleStringProperty(time);
	}
	
	public StringProperty getDateString(){
		return dateString;
	}
	
	public StringProperty getTimeString(){
		return timeString;
	}
	
}

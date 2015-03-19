package gui;

public class Alert {
	
	private String dateString;
	private String timeString;
	private int userId;
	private int AppointmentId;
	private int id;

	
	
	public Alert(int id, String dateText, String timeText, int userId, int  appointmentId) {
		this.dateString = dateText;
		this.timeString = timeText;
		this.userId = userId;
		this.AppointmentId = appointmentId;
		this.id = id;
	}

	public String getDateString(){
		return dateString;
	}
	
	public String getTimeString(){
		return timeString;
	}

	public int getUserId(){
		return userId;
	}
	
	public int getAppointmentId(){
		return AppointmentId;
	}
	
	public int getID(){
		return id;
	}
	
	public String toString(){
		return "Invited to appointment on " + dateString + " " + timeString;
	}
	
}

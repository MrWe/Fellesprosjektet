package app;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Appointment {
	
	String description;
	String location;
	private LocalDate date;
	private LocalTime startTime;
	private LocalTime endTime;
	ArrayList<String> invited;
	ArrayList<String> members;
	ArrayList<String> admins;
	
	public Appointment(String description, String location, LocalDate date, LocalTime startTime, LocalTime endTime, ArrayList<String> invited, ArrayList<String> members, ArrayList<String> admins) {
		this.description = description;
		this.location = location;
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
		this.invited = invited;
		this.members = members;
		this.admins = admins;
	}

	public String getDescription() {
		return description;
	}

	public String getLocation() {
		return location;
	}

	public LocalDate getDate() {
		return date;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public ArrayList<String> getInvited() {
		return invited;
	}

	public ArrayList<String> getMembers() {
		return members;
	}

	public ArrayList<String> getAdmins() {
		return admins;
	}

}

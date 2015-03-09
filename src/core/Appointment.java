package core;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import core.Group;

public class Appointment {
	
	String description;
	String location;
	private LocalDate date;
	private LocalTime startTime;
	private LocalTime endTime;
	ArrayList<String> invited;
	ArrayList<String> members;
	ArrayList<String> admins;
	private String color;
	private Group owner;
	
	public Appointment(String description, String location, LocalDate date, LocalTime startTime, LocalTime endTime, ArrayList<String> invited, ArrayList<String> members, ArrayList<String> admins, String color, Group owner) {
		this.description = description;
		this.location = location;
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
		this.invited = invited;
		this.members = members;
		this.admins = admins;
		this.color = color;
		this.owner = owner;
	}


	public Group getOwner() {
		return owner;
	}

	public void setOwner(Group owner) {
		this.owner = owner;
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
	
	public String getColor() {
		return color;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public void setInvited(ArrayList<String> invited) {
		this.invited = invited;
	}

	public void setMembers(ArrayList<String> members) {
		this.members = members;
	}

	public void setAdmins(ArrayList<String> admins) {
		this.admins = admins;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
	@Override
	public String toString() {
		return "Appointment:\n" + description + "\n" + location + "\nTime: " + date + " " + startTime + "-" + endTime; 
	}

}

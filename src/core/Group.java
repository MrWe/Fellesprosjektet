package core;

import java.util.ArrayList;
import java.util.Arrays;

public class Group {
	
	private String name;
	private boolean privateGroup;
	private String groupID;
	private String supergroupID;
	private ArrayList<String> members;
	private ArrayList<String> admins;
	private ArrayList<Group> subGroups;
	private ArrayList<Appointment> appointments;
	
	public Group(String name, boolean privateGroup, String groupID, String supergroupID, ArrayList<String> members, ArrayList<String> admins) {
		this.name = name;
		this.privateGroup = privateGroup;
		this.groupID = groupID;
		this.supergroupID = supergroupID;
		this.members = members;
		this.admins = admins;
		for (String member : admins) {
			if (!members.contains(member)) {
				members.add(member);
			}
		}
		subGroups = new ArrayList<Group>();
		appointments = new ArrayList<Appointment>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isPrivateGroup() {
		return privateGroup;
	}
	
	public String getGroupID() {
		return groupID;
	}
	
	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}
	
	public String getSupergroupID() {
		return supergroupID;
	}

	public ArrayList<String> getMembers() {
		return members;
	}

	public ArrayList<String> getAdmins() {
		return admins;
	}

	public ArrayList<Group> getSubGroups() {
		return subGroups;
	}
	
	public void addMember(String name) {
		members.add(name);
	}
	
	public void addMembers(String... names) {
		members.addAll(Arrays.asList(names));
	}
	
	public void addMembers(ArrayList<String> names) {
		members.addAll(names);
	}
	
	public void setMembers(ArrayList<String> names) {
		members = names;
	}
	
	public void removeMember(String name) {
		members.remove(name);
	}
	
	public void addAdmin(String name) {
		if (!members.contains(name)) {
			members.add(name);
		}
		admins.add(name);
	}
	
	public void addAdmins(String... names) {
		if (!members.contains(names)) {
			members.addAll(Arrays.asList(names));
		}
		admins.addAll(Arrays.asList(names));
	}
	
	public void removeAdmin(String name) {
		admins.remove(name);
	}
	
	public void addSubgroup(Group subGroup) {
		subGroups.add(subGroup);
	}
	
	public void addSubgroups(Group... subGroups) {
		this.subGroups.addAll(Arrays.asList(subGroups));
	}
	
	public void removeSubgroup(Group subGroup) {
		subGroups.remove(subGroup);
	}
	
	public void addAppointment(Appointment appointment) {
		appointments.add(appointment);
	}
	
	public ArrayList<Appointment> getAppointments() {
		return appointments;
	}
	
	public void removeAppointment(Appointment appointment) {
		appointments.remove(appointment);
	}
	
	@Override
	public String toString() {
		return getName();
	}

}

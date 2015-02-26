package app;

import java.util.ArrayList;
import java.util.Arrays;

public class Group {
	
	private String name;
	private ArrayList<String> members;
	private ArrayList<String> admins;
	private ArrayList<Group> subGroups;
	
	public Group(String name, ArrayList<String> members, ArrayList<String> admins) {
		this.name = name;
		this.members = members;
		this.admins = admins;
		subGroups = new ArrayList<Group>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		admins.add(name);
	}
	
	public void addAdmins(String... names) {
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
	
	@Override
	public String toString() {
		return getName();
	}

}

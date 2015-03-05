package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DBConnection {

	private DB db;

	public DBConnection() {
		db = new DB();
	}

	/**
	 * Returns a ResultSet with username and password for the username specified.
	 * 
	 * @param username username of the user
	 * @return a ResultSet with username and password for the username specified
	 */
	public ResultSet login(String username) {
		String q = "SELECT username, pswd FROM USER WHERE username = '"
				+ username
				+ "';";
		return db.queryDB(q);
	}

	/**
	 * Inserts a new user into the database, 
	 * then adds the user to the root group, 
	 * then adds a private group for the user, 
	 * then adds the user to the private calendar.
	 * 
	 * @param username username of the user
	 * @param pswd password of the user
	 * @param fullName full Name of the user
	 * @param birthday birthday of the user
	 * @param email email of the user
	 * @throws SQLException
	 */
	public void registerUser(String username, String pswd, String fullName, String birthday, String email) throws SQLException {
		// inserts a user into the database
		String q = "INSERT INTO USER(username, pswd, fullName, birthday, email) VALUES ('"
				+ username
				+ "','"
				+ pswd
				+ "','"
				+ fullName
				+ "','"
				+ birthday
				+ "','"
				+ email
				+ "');";
		db.updateDB(q);

		// adds the user to the root group
		int userID = getUserID(username);
		q = "INSERT INTO USER_has_USERGROUP(groupAdmin, USER_userID, USERGROUP_usergroupID) VALUES ('"
				+ 0
				+ "','"
				+ userID
				+ "','"
				+ 0
				+ "');";
		db.updateDB(q);

		// inserts a new private group for the user
		q = "INSERT INTO USERGROUP(isPrivate, groupName, color, USERGROUP_usergroupID) VALUES ('"
				+ 1
				+ "','"
				+ (username + "Private")
				+ "','"
				+ "FFFFFF"
				+ "','"
				+ 0
				+ "');";
		db.updateDB(q);

		// adds the user to the group
		int groupID = getGroupID(username + "Private");
		q = "INSERT INTO USER_has_USERGROUP(groupAdmin, USER_userID, USERGROUP_usergroupID) VALUES ('"
				+ 1
				+ "','"
				+ userID
				+ "','"
				+ groupID
				+ "');";
		db.updateDB(q);
	}

	/**
	 * Returns a ResultSet with all the info on a user
	 * 
	 * @param username username of the user
	 * @return a ResultSet with all the info on a user
	 */
	public ResultSet getUser(String username) {
		String q = "SELECT * FROM USER WHERE username = '"
				+ username
				+"';";
		return db.queryDB(q);
	}

	/**
	 * Returns the userID of the specified username as an int
	 * 
	 * @param username username of the user
	 * @return the userID of the specified username as an int
	 * @throws SQLException
	 */
	public int getUserID(String username) throws SQLException {
		String q = "SELECT userID from USER WHERE username = '"
				+ username
				+ "';";
		ResultSet rs = db.queryDB(q);
		rs.next();
		return Integer.parseInt(rs.getString(1));
	}

	/**
	 * Returns a ResultSet with all the information of all the users
	 * 
	 * @return a ResultSet with all the information of all the users
	 */
	public ResultSet getAllUsers() {
		String q = "SELECT * FROM USER;";
		return db.queryDB(q);
	}

	/**
	 * Returns a ResultSet with all the information of all the groups
	 * 
	 * @return a ResultSet with all the information of all the groups
	 */
	public ResultSet getAllGroups() {
		String q = "SELECT * FROM USERGROUP;";
		return db.queryDB(q);
	}
	
	/**
	 * Returns a ResultSet with the id and name of the private group of the specified user
	 * 
	 * @param username username of the user
	 * @return a ResultSet with the id and name of the private group of the specified user
	 */
	public ResultSet getPrivateGroup(String username) {
		String q = "select usergroupID, groupName from Calendar.USER inner join Calendar.USER_has_USERGROUP inner join Calendar.USERGROUP "
				+ "where userID = USER_userID and usergroupID = USER_has_USERGROUP.USERGROUP_usergroupID "
				+ "and isPrivate = 1 "
				+ "and username = '" + username + "';";
		return db.queryDB(q);
	}

	/**
	 * Returns a ResultSet with all the info of all the groups where the specified user is a member
	 * 
	 * @param username username of the specified user
	 * @return a ResultSet with all the info of all the groups where the specified user is a member
	 */
	public ResultSet getAllGroupsOfUser(String username) {
		String q = "SELECT usergroupID, isPrivate, groupName, Calendar.USERGROUP.USERGROUP_usergroupID "
				+ "FROM Calendar.USER INNER JOIN Calendar.USER_has_USERGROUP INNER JOIN Calendar.USERGROUP "
				+ "WHERE Calendar.USER.userID = Calendar.USER_has_USERGROUP.USER_userID "
				+ "AND Calendar.USERGROUP.usergroupID = Calendar.USER_has_USERGROUP.USERGROUP_usergroupID "
				+ "AND username = '" + username + "';";
		return db.queryDB(q);
	}

	/**
	 * Returns the id of the group with the highest id, as an int
	 * 
	 * @return the id of the group with the highest id, as an int
	 * @throws SQLException
	 */
	public int getHighestGroupID() throws SQLException {
		String q = "select MAX(usergroupID)from USERGROUP;";
		ResultSet rs = db.queryDB(q);
		rs.next();
		return Integer.parseInt(rs.getString(1));
	}

	/**
	 * Returns the id of the group with the specified name as an int
	 * 
	 * @param groupName name of the group specified
	 * @return the id of the group with the specified name as an int
	 * @throws SQLException
	 */
	public int getGroupID(String groupName) throws SQLException {
		String q = "SELECT usergroupID FROM USERGROUP WHERE groupName = '"
				+ groupName
				+ "';";
		System.out.println(groupName);
		ResultSet rs = db.queryDB(q);
		rs.next();
		return Integer.parseInt(rs.getString(1));
	}

	/**
	 * Inserts a new group into the database
	 * 
	 * @param groupName name of the group
	 * @param isPrivate if the group is private
	 * @param superGroupID the id of the supergroup, 0 is the id of the root
	 */
	public void createGroup(String groupName, int isPrivate, int superGroupID) {
		String q = "INSERT INTO USERGROUP(isPrivate, groupName, color, USERGROUP_usergroupID) VALUES ('"
				+ isPrivate
				+ "','"
				+ groupName
				+ "','"
				+ "FFFFFF"
				+ "','"
				+ superGroupID
				+ "');";
		db.updateDB(q);
	}
	
	/**
	 * Inserts an appointment into the database
	 * 
	 * @param description description of the appointment
	 * @param from start time of the appointment
	 * @param to end time of the appointment
	 * @param place room the appointment takes place
	 * @param appointmentType type of appointment
	 * @param roomID the database id of the room
	 * @param usergroupID the database id of the group
	 */
	public void addAppointment(String description, String from, String to, String place, String appointmentType, int roomID, int usergroupID) {
		String q = "INSERT INTO APPOINTMENT(description, timeFrom, timeTo, place, appointmentType, ROOM_roomID, USERGROUP_usergroupID) VALUES ('"
				+ description
				+ "','"
				+ from
				+ "','"
				+ to
				+ "','"
				+ place
				+ "','"
				+ appointmentType
				+ "','"
				+ roomID
				+ "','"
				+ usergroupID
				+ "');";
		db.updateDB(q);
	}
	// Doesnt retrieve userID from calendar. Needs fix.
	public void addAppointmentMembers(int appointmentID, ArrayList<String> members) {
		for (String member : members) {
			String q = "INSERT INTO APPOINTMENTMEMBERS(status, isAdmin, USER_userID, APPOINTMENT_appointmentID) VALUES ('"
					+ "i',"
					+ 0
					+ ","
					+ "(SELECT userID from Calendar.USER WHERE fullName =" + member + ";)"
					+ ","
					+ appointmentID
					+ ");";
			db.updateDB(q);
		}
	}

	/**
	 * For each member in the ArrayList, insert the user into the group specified with groupName
	 * 
	 * @param groupName the name of the group
	 * @param members list of the members to be added
	 * @throws SQLException
	 */
	public void addGroupMembers(String groupName, ArrayList<String> members) throws SQLException {
		int groupID = getGroupID(groupName);
		ResultSet rs = getAllUsers();
		Map<String, String> memberIDs = new HashMap<String, String>();
		while (rs.next()) {
			memberIDs.put(rs.getString(4), rs.getString(1));
		}
		for (String member : members) {
			String q = "INSERT INTO USER_has_USERGROUP(groupAdmin, USER_userID, USERGROUP_usergroupID) VALUES ('"
					+ 0
					+ "',"
					+ memberIDs.get(member)
					+ ",'"
					+ groupID
					+ "');";
			db.updateDB(q);
		}
	}

	/**
	 * Deletes all current memberships of a group, then inserts every person in the list into the group
	 * 
	 * @param groupName the name of the group
	 * @param members list of the members to be added
	 * @throws SQLException
	 */
	public void setGroupMembers(String groupName, ArrayList<String> members) throws SQLException {
		int groupID = getGroupID(groupName);
		String q = "DELETE FROM USER_has_USERGROUP WHERE USERGROUP_usergroupID='"
				+ groupID
				+"';";
		db.updateDB(q);
		addGroupMembers(groupName, members);
	}

	/**
	 * Returns a ResultSet with the full name of every member of the group specified
	 * 
	 * @param groupID the id of the group specified
	 * @return a ResultSet with the full name of every member of the group specified
	 */
	public ResultSet getGroupMembers(String groupID) {
		String q = "select fullName from Calendar.USER_has_USERGROUP join Calendar.USERGROUP join Calendar.USER "
				+ "WHERE Calendar.USER_has_USERGROUP.USERGROUP_usergroupID = Calendar.USERGROUP.usergroupID "
				+ "AND Calendar.USER_has_USERGROUP.USER_userID = Calendar.USER.userID "
				+ "AND Calendar.USERGROUP.usergroupID = '" + groupID + "';";
		return db.queryDB(q);
	}

	/**
	 * Updates the name of the group specified
	 * 
	 * @param oldName the old name of the group
	 * @param newName the new name of the group
	 */
	public void editGroupName(String oldName, String newName) {
		String q = "UPDATE USERGROUP SET groupName = '" + newName + "' WHERE groupName = '" + oldName + "';";
		db.updateDB(q);
	}

	////////////////////////////////////////////////////////////////////
	///////////////////////IGNORE EVERYTHING BELOW//////////////////////
	////////////////////////////////////////////////////////////////////

	/**
	 * Returnerer et ResultSet som sier hvor mange reservasjoner det er på en koie på en bestemt dato
	 * 
	 * @param koieID Koien informasjonen skal hentes fra
	 * @param dato Den aktuelle datoen
	 * @return ResultSet med antall reservasjoner (kun et tall, ikke informasjon om reservasjonene)
	 * @throws SQLException
	 */
	public ResultSet getReservertePlasser(String koieID, String dato) throws SQLException {
		String q = ("select count(*) from Reservasjon where ReservertKoieID = '" 
				+ koieID 
				+ "' and Dato = '" 
				+ dato 
				+ "';");

		return db.queryDB(q);
	}

	/**
	 * Fikser et utstyr 
	 * 
	 * @param koie Koien det gjelder.
	 * @param tingnavn Utstyret som skal fikses.
	 * @throws SQLException
	 */
	public void fixUtstyr(String koie, String tingnavn) throws SQLException {
		String q = ("delete from ErOdelagt where UtstyrsID = (select distinct UtstyrsID from Utstyr where Navn = '" + tingnavn + "' and FraktesTilID = '" + koie + "');");
		db.updateDB(q);

		q = ("update Utstyr set stat = '1' where Navn = '" + tingnavn + "' and FraktesTilID = '" + koie + "';");
		db.updateDB(q);
	}

}

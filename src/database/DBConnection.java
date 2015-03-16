package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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
	public ResultSet getLoginInfo(String username) {
		String q = "SELECT username, pswd, fullName FROM USER WHERE username = '"
				+ username
				+ "';";
		return db.queryDB(q);
	}

	public void setEmail(String username, String email) {
		String q = "UPDATE USER SET email = '"
				+ email
				+ "' WHERE username = '"
				+ username
				+ "';";
		db.updateDB(q);
	}

	public void setPassword(String username, String password) {
		String q = "UPDATE USER SET pswd = '"
				+ password
				+ "' WHERE username = '"
				+ username
				+ "';";
		db.updateDB(q);
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

		// adds the user t workbeo the root group
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

	public String getUsername(int userId) throws SQLException {
		String q = "SELECT * FROM USER WHERE userID = " + userId;
		ResultSet rs = db.queryDB(q);
		rs.next();
		return rs.getString(4);
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
		System.out.println("Group Name:" + groupName + "          Src: DBConnection");
		ResultSet rs = db.queryDB(q);
		rs.next();
		return Integer.parseInt(rs.getString(1));
	}

	public String getLastGroupID() throws SQLException {
		String q = "SELECT MAX(usergroupID) from USERGROUP;";
		ResultSet rs = db.queryDB(q);
		rs.next();
		return rs.getString(1);
	}

	public ResultSet getAppointmentMembers(int appointmentId) {
		String q = "SELECT * FROM APPOINTMENTMEMBER WHERE APPOINTMENT_appointmentID = " + appointmentId;
		return db.queryDB(q);
	}

	/**
	 * Inserts a new group into the database
	 * 
	 * @param groupName name of the group
	 * @param isPrivate if the group is private
	 * @param superGroupID the id of the supergroup, 0 is the id of the root
	 */
	public void createGroup(String groupName, int isPrivate, int superGroupID, String username) throws SQLException {
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

		String groupID = getLastGroupID();
		int userID = getUserID(username);
		q = "INSERT INTO USER_has_USERGROUP(groupAdmin, USER_userID, USERGROUP_usergroupID) values ("
				+ 1
				+ ","
				+ userID
				+ ","
				+ groupID
				+ ");";
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
	 * @throws SQLException 
	 */
	public void addAppointment(String username, String description, String from, String to, String place, String appointmentType, int roomID, String groupName) throws SQLException {
		int groupID = getGroupID(groupName);
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
				+ groupID
				+ "');";
		db.updateDB(q);

		String lastId = getLastAppointmentID(); 
		int userId = getUserID(username);
		String qu = "INSERT INTO APPOINTMENTMEMBER(status, isAdmin, USER_userID, APPOINTMENT_appointmentID) Values ('"
				+ "i"
				+ "',"
				+ 1
				+ ","
				+ userId
				+ ","
				+ lastId
				+ ");";
		System.out.println(qu);
		db.updateDB(qu);
	}

	public String getLastAppointmentID() throws SQLException {
		String q = "SELECT MAX(appointmentID) from APPOINTMENT;";
		ResultSet rs = db.queryDB(q);
		rs.next();
		return rs.getString(1);
	}

	public ResultSet getAppointmentsWithGroup(int group) {
		String q = "SELECT * FROM APPOINTMENT AS A JOIN USERGROUP AS U ON(A.USERGROUP_usergroupID = U.usergroupID) WHERE A.USERGROUP_usergroupID = " + group;
		return db.queryDB(q);
	}

	// Doesnt retrieve userID from calendar. Needs fix.
	public void addAppointmentMembers(int appointmentID, ArrayList<String> members) throws SQLException {
		for (String member : members) {
			int userID = getUserID(member);
			String q = "INSERT INTO APPOINTMENTMEMBER(status, isAdmin, USER_userID, APPOINTMENT_appointmentID) VALUES ('"
					+ "i',"
					+ 0
					+ ","
					+ userID
					+ ","
					+ appointmentID
					+ ");";
			db.updateDB(q);
		}
	}

	//Returns ArrayList with all available rooms at the given time and date
	public ArrayList<String> getAvailableRooms(String date, LocalTime from, LocalTime to) throws SQLException {
		String q = "SELECT R.roomName, A.timeFrom, A.timeTo "
				+ "FROM ROOM as R JOIN APPOINTMENT AS A ON(R.roomID = A.ROOM_roomID)"
				+ "WHERE DATE_FORMAT(A.timeFrom, '%Y-%m-%d') = '" + date + "';";
		ResultSet rs = db.queryDB(q);
		ArrayList<String> al = new ArrayList<String>();
		while(rs.next()) {
			if (ChronoUnit.MINUTES.between(to, LocalTime.parse(rs.getString(2).substring(11, 16))) >=
					0 
					|| ChronoUnit.MINUTES.between(from, LocalTime.parse(rs.getString(3).substring(11, 16))) <= 0) {
				String name = rs.getString(1);
				if (!al.contains(name)) {
					al.add(name);
				}
			}
		}
		return al;
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
		String q = "select fullName, groupAdmin from Calendar.USER_has_USERGROUP join Calendar.USERGROUP join Calendar.USER "
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

	public boolean isAdmin(String username, String groupName) throws SQLException {
		int userID = getUserID(username);
		int groupID = getGroupID(groupName);
		String q = "SELECT groupAdmin from USER_has_USERGROUP where USER_userID = " + userID + " and USERGROUP_usergroupID = " + groupID + ";";
		ResultSet rs = db.queryDB(q);
		if (rs.next() && rs.getString(1).equals("1")) {
			return true;
		}
		return false;
	}

	public void editGroupAdminRights(String groupName, String username, int adminStatus) throws SQLException {
		int groupID = getGroupID(groupName);
		int userID = getFullNameUserID(username);
		String q = "UPDATE USER_has_USERGROUP SET groupAdmin = '"
				+ adminStatus
				+ "' WHERE USERGROUP_usergroupID = "
				+ groupID
				+ " AND USER_userID = "
				+ userID
				+ ";";
		db.updateDB(q);
	}
	
	public int getFullNameUserID(String name) throws SQLException {
		String q = "SELECT userID from USER WHERE fullName = '"
				+ name
				+ "';";
		ResultSet rs = db.queryDB(q);
		rs.next();
		return Integer.parseInt(rs.getString(1));
	}

	
	/**
	 * Deletes the specified group from the database. Because of the ON DELETE CASCADE property, all subgroups will also be deleted
	 * 
	 * @param groupID group name of the group to be deleted
	 */
	public void deleteGroup(String groupID) {
		String q = "DELETE FROM USERGROUP WHERE usergroupID = '" + groupID + "';";
		db.updateDB(q);
	}

	public void updateAppointment(String appointmentId, String description,
			String from, String to, String place, String appointmentType,
			int roomID, String groupName) throws SQLException {
		int groupID = getGroupID(groupName);
		String q = "UPDATE APPOINTMENT SET description='"+description
				+"',timeFrom='"+from
				+"',timeTo='"+to
				+"',place="+place
				+",appointmentType="+appointmentType
				+",ROOM_roomID="+roomID
				+",USERGROUP_usergroupID="+groupID
				+" WHERE appointmentID = "+appointmentId+";";
		System.out.println(q);
		db.updateDB(q);
	}

	public void deleteAppointment(String appointmentId) {
		String q = "DELETE FROM APPOINTMENT WHERE appointmentID ='"+appointmentId+"';";
		db.updateDB(q);
	}

}

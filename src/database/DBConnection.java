package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DBConnection {

	private DB db;

	/**
	 * Initialiserer DBConnection
	 */
	public DBConnection() {
		db = new DB();
	}

	public ResultSet login(String username) {
		String q = "SELECT username, pswd FROM USER WHERE username = '"
				+ username
				+ "';";
		return db.queryDB(q);
	}

	public void registerUser(String username, String pswd, String fullName, String birthday, String email) throws SQLException {
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

		int userID = getUserID(username);
		System.out.println(userID);
		// have to add the user to the root group
		q = "INSERT INTO USER_has_USERGROUP(groupAdmin, USER_userID, USERGROUP_usergroupID) VALUES ('"
				+ 0
				+ "','"
				+ userID
				+ "','"
				+ 0
				+ "');";
		db.updateDB(q);

		q = "INSERT INTO USERGROUP(isPrivate, groupName, USERGROUP_usergroupID) VALUES ('"
				+ 1
				+ "','"
				+ (username + "Private")
				+ "','"
				+ 0
				+ "');";
		db.updateDB(q);

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

	public ResultSet getUser(String username) {
		String q = "SELECT * FROM USER WHERE username = '"
				+ username
				+"';";
		return db.queryDB(q);
	}

	public int getUserID(String username) throws SQLException {
		String q = "SELECT userID from USER WHERE username = '"
				+ username
				+ "';";
		ResultSet rs = db.queryDB(q);
		rs.next();
		return Integer.parseInt(rs.getString(1));
	}

	public ResultSet getAllUsers() {
		String q = "SELECT * FROM USER;";
		return db.queryDB(q);
	}

	public ResultSet getAllGroups() {
		String q = "SELECT * FROM USERGROUP;";
		return db.queryDB(q);
	}
	
	public ResultSet getPrivateGroup(String username) {
		String q = "select usergroupID, groupName from Calendar.USER inner join Calendar.USER_has_USERGROUP inner join Calendar.USERGROUP "
				+ "where userID = USER_userID and usergroupID = USER_has_USERGROUP.USERGROUP_usergroupID "
				+ "and isPrivate = 1 "
				+ "and username = '" + username + "';";
		return db.queryDB(q);
	}

	public ResultSet getAllGroupsOfUser(String username) {
		String q = "SELECT usergroupID, isPrivate, groupName, Calendar.USERGROUP.USERGROUP_usergroupID "
				+ "FROM Calendar.USER INNER JOIN Calendar.USER_has_USERGROUP INNER JOIN Calendar.USERGROUP "
				+ "WHERE Calendar.USER.userID = Calendar.USER_has_USERGROUP.USER_userID "
				+ "AND Calendar.USERGROUP.usergroupID = Calendar.USER_has_USERGROUP.USERGROUP_usergroupID "
				+ "AND username = '" + username + "';";
		return db.queryDB(q);
	}

	public int getHighestGroupID() throws SQLException {
		String q = "select MAX(usergroupID)from Calendar.USERGROUP;";
		ResultSet rs = db.queryDB(q);
		rs.next();
		return Integer.parseInt(rs.getString(1));
	}

	public int getGroupID(String groupName) throws SQLException {
		String q = "SELECT usergroupID FROM USERGROUP WHERE groupName = '"
				+ groupName
				+ "';";
		System.out.println(groupName);
		ResultSet rs = db.queryDB(q);
		rs.next();
		return Integer.parseInt(rs.getString(1));
	}

	public void createGroup(String groupName, int isPrivate, int superGroupID) {
		String q = "INSERT INTO USERGROUP(isPrivate, groupName, USERGROUP_usergroupID) VALUES ('"
				+ isPrivate
				+ "','"
				+ groupName
				+ "','"
				+ superGroupID
				+ "');";
		db.updateDB(q);
	}

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

	public void setGroupMembers(String groupName, ArrayList<String> members) throws SQLException {
		int groupID = getGroupID(groupName);
		String q = "DELETE FROM USER_has_USERGROUP WHERE USERGROUP_usergroupID='"
				+ groupID
				+"';";
		db.updateDB(q);
		addGroupMembers(groupName, members);
	}

	public ResultSet getGroupMembers(String groupID) {
		String q = "select fullName from Calendar.USER_has_USERGROUP join Calendar.USERGROUP join Calendar.USER "
				+ "WHERE Calendar.USER_has_USERGROUP.USERGROUP_usergroupID = Calendar.USERGROUP.usergroupID "
				+ "AND Calendar.USER_has_USERGROUP.USER_userID = Calendar.USER.userID "
				+ "AND Calendar.USERGROUP.usergroupID = '" + groupID + "';";
		return db.queryDB(q);
	}

	public void editGroupName(String oldName, String newName) {
		String q = "UPDATE USERGROUP SET groupName = '" + newName + "' WHERE groupName = '" + oldName + "';";
		db.updateDB(q);
	}

	////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////
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

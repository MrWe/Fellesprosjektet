package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
		System.out.println(q);
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
	
	public static void main(String[] args) {
		DBConnection db = new DBConnection();
		try {
		System.out.println(db.getGroupID("Fellesprosjektet"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
//	
//	public static void main(String[] args) {
//		DBConnection db = new DBConnection();
//		db.editGroupName("Abakus2", "AbakusTest");
//	}

	/**
	 * Returnerer et ResultSet med brukerinfo som hører til brukernavnet til den som prøver å logge inn
	 * 
	 * @param brukernavn Brukernavnet til den som prøver å logge inn
	 * @return ResultSet med en rad der col1 = brukernavn, col2 = passord, col3 = fullt navn, col4 = telefonnummer, col5 = epost, col6 = isAdmin
	 * @throws SQLException
	 */
	/*public ResultSet login(String brukernavn) throws SQLException {

		String q = ("select Brukernavn, Passord, Navn, Tlf, Epost, isAdmin from Admin where Brukernavn = '"
				+ brukernavn 
				+ "';");

		String q = ("INSERT INTO USER(username, pswd, fullName, birthday, email) VALUES('erik', 'kåre', 'Erik Kåre', '1995-01-22', 'lol@gmail.com');");
		return db.sporDB(q);

	}*/

	/**
	 * Registrerer en ny bruker i databasesystemet
	 * 
	 * @param brukernavn Brukernavnet til brukeren
	 * @param passord Passordet til brukeren
	 * @param navn Fullt navn til brukeren
	 * @param tlf Telefonnummer til brukeren
	 * @param epost Epostadressen til brukeren
	 * @throws SQLException
	 */
	public void registrerBruker(String brukernavn, String passord, String navn, String tlf, String epost) throws SQLException {
		String q = ("insert into Admin (Brukernavn, Passord, Navn, Tlf, Epost, isAdmin) values ('"
				+ brukernavn
				+ "','"
				+ passord
				+ "','"
				+ navn
				+ "','"
				+ tlf
				+ "','"
				+ epost
				+ "','"
				+ "0" // vanlig bruker (ikke admin)
				+ "');");

		db.updateDB(q);
	}

	/**
	 * Setter inn en rapport i databasesystemet
	 * 
	 * @param odelagt Liste over ting som er ødelagt, separert med semikolon
	 * @param gjenglemt Liste over ting som er gjenglemt, separert med semikolon
	 * @param vedstatus Vedstatus for koie (gitt som prosent?)
	 * @param koienavn Navnet på koie
	 * @param dato Datoen rapporten blir satt inn
	 * @throws SQLException
	 */
	public void settinnRapport(String odelagt, String gjenglemt, int vedstatus,
			String koienavn, String dato) throws SQLException {
		String q = ("insert into Rapport (odelagt, gjenglemt, vedstatus, koierapportID, dato) values ('"
				+ odelagt
				+ "','"
				+ gjenglemt
				+ "','"
				+ (new String("" + vedstatus))
				+ "','"
				+ koienavn 
				+ "','" 
				+ dato 
				+ "');");

		db.updateDB(q);
	}

	/**
	 * Setter inn en reservasjon i databasesystemet
	 * 
	 * @param epost Epostadresse til den som reserverer
	 * @param dato Dato for reservajon
	 * @param koienavn Hvilken koie reservasjonen går til
	 * @throws SQLException
	 */
	public void settinnReservasjon(String epost, String dato, String koienavn) throws SQLException {
		String q = ("insert into Reservasjon (epost,dato,reservertkoieid) values ('"
				+ epost 
				+ "','" 
				+ dato 
				+ "','" 
				+ koienavn 
				+ "');");

		db.updateDB(q);
	}

	/**
	 * Oppdaterer en koies vedstatus
	 * 
	 * @param koienavn Navnet på koien som skal oppdateres
	 * @param vedstatus Ny vedstatus
	 * @throws SQLException
	 */
	public void oppdaterVedstatus(String koienavn, int vedstatus) throws SQLException {
		String q = ("update Koie set Vedstatus = '"
				+ (new String("" + vedstatus)) 
				+ "' where KoieID = '"
				+ koienavn + "';");

		db.updateDB(q);
	}

	/**
	 * Oppdaterer status for et bestemt utstyr
	 * 
	 * @param utstyrsID Hvilket utstyr som skal oppdateres
	 * @param status 0 = ødelagt, 1 = fungerer
	 * @throws SQLException
	 */
	public void oppdaterUtstyr(int utstyrsID, int status) throws SQLException {
		String q = ("update Utstyr set stat = '" + (new String("" + status))
				+ "' where UtstyrsID = '" + (new String("" + utstyrsID)) + "';");

		db.updateDB(q);
	}

	/**
	 * Legg inn ødelagt Utstyr
	 * 
	 * @param utstyrsID Hva som er ødelagt.
	 * @param rapportID Rapporten som sa det var ødelagt.
	 * @throws SQLException
	 */
	public void leggInnOdelagtUtstyr(int utstyrsID, int rapportID) throws SQLException {
		String q = ("insert into ErOdelagt values ('"
				+ (new String("" + utstyrsID)) + "','"
				+ (new String("" + rapportID)) + "');");

		db.updateDB(q);
	}

	/**
	 * Legg inn gjenglemte ting
	 * 
	 * @param Navn Navn på gjenglemt ting.
	 * @param rapportID ID'en på rapporten som sa den var gjenglemt.
	 * @param koieID Koien det gjelder.
	 * @throws SQLException
	 */
	public void leggInnGjenglemteTing(String Navn, int rapportID, String koieID) throws SQLException {
		String q = ("insert into Gjenglemt (Navn,RapportID,KoieID) values('" + Navn + "','" + rapportID + "','" + koieID + "');");

		db.updateDB(q);
	}

	/**
	 * Få et utstyrsID som matcher navn og Koie.
	 * 
	 * @param navn Navnet på utstyret.
	 * @param koie Navnet på koia.
	 * @return ResultSet med tingen som stemmer med parametrene.
	 * @throws SQLException
	 */
	public ResultSet getUtstyrID(String navn, String koie) throws SQLException {
		String q = ("select UtstyrsID from Utstyr where Navn = '" + navn + "' and FraktesTilID = '" + koie + "';");

		return db.queryDB(q);
	}

	/**
	 * Returnerer et ResultSet med personlig informasjon for alle registrerte medlemmer
	 * 
	 * @return ResultSet der col1 = fullt navn, col2 = telefonnummber, col3 = epostadresse, col4 = isAdmin
	 */
	public ResultSet getMembers() {
		String q = ("select Navn, Tlf, Epost, isAdmin from Admin");

		return db.queryDB(q);
	}

	/**
	 * Få rapportID fra en rapport.
	 * 
	 * @param odelagt Ødelagt String.
	 * @param gjenglemt Gjenglemt String.
	 * @param vedstatus Veddugnad String.
	 * @return ResultSet med rapportID til rapport som matcher parametrene.
	 * @throws SQLException
	 */
	public ResultSet getRapportID(String odelagt, String gjenglemt,
			int vedstatus) throws SQLException {
		String q = ("select RapportID from Rapport where Odelagt = '" 
				+ odelagt
				+ "' and Gjenglemt = '" 
				+ gjenglemt 
				+ "' and Vedstatus = '"
				+ vedstatus 
				+ "';");

		return db.queryDB(q);
	}

	/**
	 * Legger inn nytt utstyr i en koie
	 * 
	 * @param navn Navnet til det nye utstyret
	 * @param dato Datoen utstyret blir lagt til
	 * @param status 0 = ødelagt, 1 = fungerer
	 * @param adminID ID til den innloggede brukeren
	 * @param koie Koien utstyret skal legges til
	 * @throws SQLException
	 */
	public void registrerUtstyr(String navn, String dato, int status, String adminID, String koie) throws SQLException {
		String q = ("insert into Utstyr (Navn, Innkjopsdato, stat, AdminID, FraktesTilID) VALUES ('" + navn + "','" + dato + "','" + (new String("" + status)) + "','" + adminID + "','" + koie + "');");

		db.updateDB(q);
	}

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
	 * Returnerer et ResultSet med antall totale sengeplasser på en koie
	 * 
	 * @param koieID Koien informasjonen skal hentes fra
	 * @return ResultSet med antall sengeplasser
	 * @throws SQLException
	 */
	public ResultSet getSengeplasser(String koieID) throws SQLException {
		String q = ("select Storrelse from Koie where KoieID = '" 
				+ koieID 
				+ "';");

		return db.queryDB(q);
	}

	/**
	 * Returnerer en liste med datoer med laveste vedstatus for hver dag i synkende rekkefølge etter dato, starter etter innsendt dato
	 * 
	 * @param koieID Koien informasjon skal hentes fra.
	 * @param dato Datoen lista skal begynne på
	 * @return ResultSet med unike datoer etter startdato i synkende rekkefølge
	 * @throws SQLException
	 */
	public ResultSet getDatoListe(String koieID, String dato) throws SQLException {
		String q = ("select Dato, min(Vedstatus) from Rapport where Dato > '" + dato + "' and KoieRapportID = '" + koieID + "' group by Dato order by Dato desc limit 14;");

		return db.queryDB(q);
	}

	/**
	 * Returnerer liste med alt ødelagt utstyr på en koie
	 * 
	 * @param koieID Koien det gjelder.
	 * @return ResultSet med ødelagt utstyr på koia.
	 * @throws SQLException
	 */
	public ResultSet getOdelagt(String koieID) throws SQLException {
		String q = ("select Navn from Utstyr where FraktesTilID = '" + koieID + "' and stat = '0';");

		return db.queryDB(q);
	}

	/**
	 * Returnerer en liste med gjenglemte ting på en koie
	 * 
	 * @param koieID  Koien det gjelder.
	 * @return ResultSet med gjenglemte ting på Koien
	 * @throws SQLException
	 */
	public ResultSet getGjenglemt(String koieID) throws SQLException {

		String q = ("select Navn from Gjenglemt where KoieID = '" + koieID + "';");

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

	/**
	 * Si ifra om at en ting er blitt funnet.
	 * 
	 * @param ting Hvilken ting som er funnet.
	 * @throws SQLException
	 */
	public void funnetTing(String ting) throws SQLException {
		String q = ("delete from Gjenglemt where navn = '" + ting + "';");

		db.updateDB(q);
	}

	/**
	 * Oppdater dato for veddugnad i Koie 
	 * 
	 * @param koie Hvilken koie det gjelder.
	 * @param dato Hvilken dato en dugnad har vært/skal være
	 * @throws SQLException
	 */
	public void datoVeddugnad(String koie, String dato) throws SQLException {
		String q = ("update Koie set Veddugnad = '" + dato + "' where KoieID = '" + koie + "';");

		db.updateDB(q);
	}

	/**
	 * Få dato for veddugnad fra en Koie
	 * 
	 * @param koie Hvilken koie det gjelder.
	 * @return ResultSet med datoen for veddugnad.
	 * @throws SQLException
	 */
	public ResultSet getForrigeVeddugnad(String koie) throws SQLException {
		String q = ("select Veddugnad from Koie where KoieID = '" + koie + "';");

		return db.queryDB(q);
	}

	/**
	 * Få alt utstyr fra ei Koie 
	 * 
	 * @param koie Koien det gjelder.
	 * @return ResultSet med alt ødelagt utstyr på ei Koie.
	 * @throws SQLException
	 */
	public ResultSet getAltUtstyr(String koie) throws SQLException {
		String q = ("select Navn from Utstyr where FraktesTilID = '" + koie + "';");

		return db.queryDB(q);
	}

	/**
	 * Returnerer en liste med eposter for innsendt dato.
	 * 
	 * @param koieID Koia det gjelder.
	 * @param dato Eposter for denne datoen.
	 * @return ResultSet med alle eposter som har reservert på den koie på den datoen
	 */
	public ResultSet getReservasjonsEpostIDag(String koieID, String dato){
		String q = ("select Epost from Reservasjon where ReservertKoieID = '" + koieID + "' and Dato = '" + dato + "' order by Dato asc;");

		return db.queryDB(q);
	}

	/**
	 * Returnerer en liste med eposter for datoer fra innsendt dato.
	 * 
	 * @param koieID Koia det gjelder.
	 * @param dato Eposter for denne datoen.
	 * @return ResultSet med alle eposter som har reservert på den koie på den datoen og framover
	 */
	public ResultSet getReservasjonsEpostFremover(String koieID, String dato){
		String q = ("select Epost from Reservasjon where ReservertKoieID = '" + koieID + "' and Dato >= '" + dato + "' order by Dato asc;");

		return db.queryDB(q);
	}
}

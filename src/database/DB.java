package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class DB {
	private static String bruker = "fellesprosjekt";
	private static String passord = "gruppe1";
	private static String url = "jdbc:mysql://188.166.34.46:22/sdfs";
	private Connection connection = null;
	private static Statement statement;
	
	
	public Connection getConnection() {
		return connection;
	}
	
	public static void main(String[] args) {
		DB db = new DB();
	}
	
	/**
	 * Kobler til databasen
	 */
	public DB() {
		try {
			//connection = DriverManager.getConnection(url, bruker, passord);
			
			MysqlDataSource dataSource = new MysqlDataSource();
			dataSource.setUser(bruker);
			dataSource.setPassword(passord);
			dataSource.setServerName("188.166.34.46");
			dataSource.setDatabaseName("Calendar");
			connection = dataSource.getConnection();
			System.out.println("22");
			statement = connection.createStatement();
			System.out.println("hei");
			ResultSet rs = statement.executeQuery("SELECT * FROM USER");
			if (rs.next()) {
				System.out.println(rs.getString(2));
			}
			System.out.println("2");
			//statement.execute("SET SESSION max_allowed_packet = 8 * 1024 * 1024;");

		} catch (Exception e) {
			System.out.println("Tilkoblingsfeil: "
					+ e.getMessage());
		}
	}
	
	/**
	 * Stenger av databasen
	 */
	public void stengDB() {
		try {
			connection.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Sender spørsmål til databasen
	 * 
	 * @param q Query som sendes til database
	 * @return ResultSet med forespurte data
	 */
	public ResultSet sporDB(String q) {
		Statement s;
		try {
			s = connection.createStatement();
			ResultSet resultat;

			resultat = s.executeQuery(q);
			return resultat;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Gjør forandringer i databasen
	 * 
	 * @param q Query som sendes til database
	 */
	public void oppdaterDB(String q){
		Statement s;
		try {
			s = connection.createStatement();
			s.executeUpdate(q);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	
}

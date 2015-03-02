package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class DB {
	
	private static String bruker = "fellesprosjekt";
	private static String passord = "gruppe1";
	private static String serverName = "188.166.34.46";
	private static String databaseName = "Calendar";
	//private static String url = "jdbc:mysql://188.166.34.46:22/sdfs";
	private static Connection connection = null;
	private static Statement statement;

	public Connection getConnection() {
		return connection;
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
			dataSource.setServerName(serverName);
			dataSource.setDatabaseName(databaseName);
			connection = dataSource.getConnection();
			statement = connection.createStatement();
		} catch (SQLException e) {
			System.out.println("Connection error: " + e.getMessage());
		}
	}

	/**
	 * Stenger av databasen
	 */
	public void closeDB() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sender spørsmål til databasen
	 * 
	 * @param q Query som sendes til database
	 * @return ResultSet med forespurte data
	 */
	public ResultSet queryDB(String q) {
		try {
			statement = connection.createStatement();
			ResultSet result = statement.executeQuery(q);
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Gjør forandringer i databasen
	 * 
	 * @param q Query som sendes til database
	 */
	public void updateDB(String q){
		try {
			statement = connection.createStatement();
			statement.executeUpdate(q);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


}

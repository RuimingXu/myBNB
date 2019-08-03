package com.shekhargulati.database;

import java.beans.Statement;
import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import com.shekhargulati.pack.Pack;

public class Database {
	
	public static void createDatabase(String name) {  
		   
        String url = "jdbc:sqlite:" + System.getProperty("user.dir") + File.separator + name + ".sqlite";
   
        try {  
            Connection conn = DriverManager.getConnection(url);  
            if (conn != null) {  
                DatabaseMetaData meta = conn.getMetaData();  
                System.out.println("Driver name: " + meta.getDriverName());  
                System.out.println("Database created successfully.");  
            }

        } catch (SQLException e) {  
            System.out.println(e.getMessage());  
        }
	}
	
	
	public static void clearTable(Connection conn) {
        String sql = "DROP TABLE IF EXISTS House;"; //"DELETE FROM House WHERE 1 = 1"
        try {
        	PreparedStatement pstmt = conn.prepareStatement(sql);
        	// execute the delete statement
        	pstmt.executeUpdate();
        	System.out.println("The table has been dropped.");
		} catch (SQLException e) {
        	System.out.println("Table drop failure.");
			e.printStackTrace();
		}
        return;
    }
	
	
	public static Connection connect(String name) {
		Connection conn = null;  
        try {
            String url = "jdbc:sqlite:" + System.getProperty("user.dir") + File.separator + name + ".sqlite";
            // create a connection to the database  
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");  
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
	}
	
	
	public static void createTable(Connection conn) {		
		String sql = "CREATE TABLE IF NOT EXISTS House (\n"  
                + " id text PRIMARY KEY,\n"
                + " address text NOT NULL,\n"
                + " rent text NOT NULL,\n"
				+ " postDate text NOT NULL,\n"
				+ " description text NOT NULL,\n"
				+ " latitude text,\n"
				+ " longitude text"
                + ");";
		try {
			java.sql.Statement statement = conn.createStatement();
			statement.execute(sql);
			System.out.println("The table has been created.");
		}
		catch (Exception e) {
			System.out.println("Table create failure.");
			e.printStackTrace();
		}
	}
	
	//todo
	public static void putInfo(Connection conn, Pack pack) {
		String sql = "INSERT INTO House(id,address,rent,postDate,description,latitude,longitude) VALUES(?,?,?,?,?,?,?)";
		try {
			PreparedStatement pStatement = conn.prepareStatement(sql);
			pStatement.setString(1, pack.getID());
			pStatement.setString(2, pack.getAddress());
			pStatement.setString(3, pack.getRent());
			pStatement.setString(4, pack.getPostDate());
			pStatement.setString(5, pack.getDescription());
			pStatement.setString(6,pack.getLatitude());
			pStatement.setString(7,pack.getLongitude());
			pStatement.executeUpdate();
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	public static String getInfo(Connection conn) {
		String sql = "SELECT id, address, rent, postDate, description FROM House";
		String info = null;
		try {
			java.sql.Statement statement = conn.createStatement();
			ResultSet result = statement.executeQuery(sql);
			int i = 1;
			while (result.next()) {
				info += "House " + i + ":\n";
				info += "ID: " + result.getString("id") + "\n" +
						"address: " + result.getString("address") + "\n" +
						"rent: " + result.getString("rent") + "\n" +
						"postDate: " + result.getString("postDate") + "\n" +
						"description: " + result.getString("description") + "\n";
				info += "-----------------------------------------------------\n";
				i ++;
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return info;
	}

	public static Set<Pack> getAll(Connection conn) {
		String sql = "SELECT id, address, rent, postDate, description FROM House";
		String info = null;
		Set<Pack> packInfo = new HashSet<>();
		try {
			java.sql.Statement statement = conn.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while (result.next()) {
				Pack curr = new Pack.Builder(
						result.getString("id"),
						result.getString("address"),
						result.getString("rent"))
						.setDate(result.getString("postDate"))
						.setDescription(result.getString("description"))
						.build();
				packInfo.add(curr);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return packInfo;
	}

	public static String getMaxVal(Connection conn, String field) {
		String sql = "SELECT * FROM House;"; //MAX('" + field + "')
		ResultSet result = null;
		try {
			java.sql.Statement statement = conn.createStatement();
			result = statement.executeQuery(sql);

			String max = "0";
			while (result.next()) {
				String curr = result.getString(field);
				String intFormCurr = curr.replaceAll("[^\\.0123456789]","");
				String price = intFormCurr.split("\\.")[0];
				try {
					if (Integer.parseInt(price) > Integer.parseInt(max)) {
						max = price;
					}
				} catch (NumberFormatException e) {
					continue;
				}
			}
			return max;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}

package assignment8;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Ass8 {
	
	static final String DB_URL = "jdbc:mysql://localhost:3306/stealth";
    static final String USER = "root";           // your MySQL username
    static final String PASS = "kaushalw@568";   // your MySQL password
    

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
            // Register the driver (optional for latest JDBC, but fine for clarity)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Scanner sc = new Scanner(System.in);

            while (true) {
                System.out.println("\n1.Add 2.View 3.Edit 4.Delete 5.Exit");
                int choice = sc.nextInt();

                if (choice == 1) {
                    System.out.print("Name: ");
                    String name = sc.next();
                    System.out.print("Email: ");
                    String email = sc.next();
                    System.out.print("Dept: ");
                    String dept = sc.next();

                    String sql = "INSERT INTO students(name, email, department) VALUES (?, ?, ?)";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, name);
                    ps.setString(2, email);
                    ps.setString(3, dept);
                    ps.executeUpdate();
                    ps.close();
                    System.out.println("Student added.");

                } else if (choice == 2) {
                    ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM students");
                    while (rs.next()) {
                        System.out.println(rs.getInt("id") + " " +
                                           rs.getString("name") + " " +
                                           rs.getString("email") + " " +
                                           rs.getString("department"));
                    }
                    rs.close();

                } else if (choice == 3) {
                    System.out.print("Edit Student ID: ");
                    int id = sc.nextInt();
                    System.out.print("New Name: ");
                    String name = sc.next();
                    System.out.print("New Email: ");
                    String email = sc.next();
                    System.out.print("New Dept: ");
                    String dept = sc.next();

                    String sql = "UPDATE students SET name=?, email=?, department=? WHERE id=?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, name);
                    ps.setString(2, email);
                    ps.setString(3, dept);
                    ps.setInt(4, id);
                    ps.executeUpdate();
                    ps.close();
                    System.out.println("Student updated.");

                } else if (choice == 4) {
                    System.out.print("Delete Student ID: ");
                    int id = sc.nextInt();
                    String sql = "DELETE FROM students WHERE id=?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setInt(1, id);
                    ps.executeUpdate();
                    ps.close();
                    System.out.println("Student deleted.");

                } else if (choice == 5) {
                    break;
                }
            }
            sc.close();
            conn.close();
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found. Add mysql-connector-java JAR in your build path.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("SQL error occurred.");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

	}

}

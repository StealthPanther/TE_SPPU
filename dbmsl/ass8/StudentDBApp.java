
import java.sql.*;
import java.util.Scanner;

public class StudentDBApp {

    static final String DB_URL = "jdbc:mysql://localhost:3306/stealth";
    static final String USER = "root";       // your MySQL username
    static final String PASS = "kaushalw@568";   // your MySQL password

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
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
                    System.out.println("Student added.");
                } else if (choice == 2) {
                    ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM students");
                    while (rs.next()) {
                        System.out.println(rs.getInt("id") + " " + rs.getString("name") + " " + rs.getString("email") + " " + rs.getString("department"));
                    }
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
                    System.out.println("Student updated.");
                } else if (choice == 4) {
                    System.out.print("Delete Student ID: ");
                    int id = sc.nextInt();
                    String sql = "DELETE FROM students WHERE id=?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setInt(1, id);
                    ps.executeUpdate();
                    System.out.println("Student deleted.");
                } else if (choice == 5) {
                    break;
                }
            }
            sc.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// When moving your Java MySQL connectivity program from Windows to Linux with a different database password, you need to make a few changes in your code and commands:
// Changes in Java Code
// Update Database URL, Username, and Password
// In your StudentDBApp.java, update these constants to match the Linux MySQL details:
// java
// static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";  // Same if DB name is same
// static final String USER = "your_linux_mysql_username";
// static final String PASS = "your_linux_mysql_password";
// No other code changes required unless your database schema or table names are different.
// Changes in Compilation and Run Commands (Linux)
// Linux uses colon : as classpath separator, unlike Windows which uses semicolon ;.
// Assuming your connector jar is mysql-connector-java-8.0.22.jar and Java file is StudentDBApp.java, commands will be:
// bash
// javac -cp .:mysql-connector-java-8.0.22.jar StudentDBApp.java
// java -cp .:mysql-connector-java-8.0.22.jar StudentDBApp
// Make sure your .jar file and .java file are in the current directory where you run these commands on Linux.
// Summary of what changes on Linux vs Windows:
// Aspect	Windows Command	Linux Command
// Classpath separator	Semicolon (;)	Colon (:)
// Enclose classpath in quotes?	Yes, in PowerShell (optional in cmd)	Usually no need unless spaces
// File paths format	Backslashes (\)	Forward slashes (/)
// Example Linux code snippet update
// java
// static final String USER = "linuxuser";
// static final String PASS = "linuxpassword";
// Make these changes, and your Java program will compile and run fine on Linux connecting to your Linux MySQL database with its credentials.

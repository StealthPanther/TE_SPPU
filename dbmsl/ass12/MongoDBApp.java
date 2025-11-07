
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import java.util.Scanner;

public class MongoDBApp {

    public static void main(String[] args) {
        // Connect to MongoDB server at localhost:27017
        MongoClient mongoClient = new MongoClient("localhost", 27017);

        // Get your database (creates if doesn't exist)
        MongoDatabase database = mongoClient.getDatabase("collegeDB");

        // Get your collection (like table)
        MongoCollection<Document> collection = database.getCollection("students");

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n1.Add 2.View 3.Edit 4.Delete 5.Exit");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            if (choice == 1) {
                System.out.print("Name: ");
                String name = sc.nextLine();
                System.out.print("Email: ");
                String email = sc.nextLine();
                System.out.print("Department: ");
                String dept = sc.nextLine();

                Document doc = new Document("name", name)
                        .append("email", email)
                        .append("department", dept);
                collection.insertOne(doc);
                System.out.println("Document added.");
            } else if (choice == 2) {
                for (Document doc : collection.find()) {
                    System.out.println(doc.toJson());
                }
            } else if (choice == 3) {
                System.out.print("Enter name to edit: ");
                String nameToEdit = sc.nextLine();
                System.out.print("New Email: ");
                String newEmail = sc.nextLine();

                UpdateResult result = collection.updateOne(
                        Filters.eq("name", nameToEdit),
                        new Document("$set", new Document("email", newEmail))
                );
                System.out.println(result.getModifiedCount() + " document(s) updated.");
            } else if (choice == 4) {
                System.out.print("Enter name to delete: ");
                String nameToDelete = sc.nextLine();

                DeleteResult result = collection.deleteOne(Filters.eq("name", nameToDelete));
                System.out.println(result.getDeletedCount() + " document(s) deleted.");
            } else if (choice == 5) {
                break;
            }
        }
        sc.close();
        mongoClient.close();
    }
}

// You need to create MongoCredential with those credentials in your Java code, like this:
// java
// MongoCredential credential = MongoCredential.createCredential("your_username", "31479_db", "your_password".toCharArray());
// MongoClient mongoClient = new MongoClient(new ServerAddress("host", 27017), Arrays.asList(credential));
// MongoDatabase db = mongoClient.getDatabase("31479_db");
// If your college’s MongoDB server does not require authentication, then you can simply use:
// java
// MongoClient mongoClient = new MongoClient("host", 27017); // or "localhost"
// MongoDatabase db = mongoClient.getDatabase("31479_db");
// If you’re unsure about the host (IP or domain), port, or authentication, again, request that info from your college IT or administrator.
// 2. Compile and Run Commands in Linux (classpath separator difference)
// Use colon : instead of semicolon ; for classpath separator.
// bash
// javac -cp .:mongo-java-driver-x.x.x.jar MongoDBApp.java
// java -cp .:mongo-java-driver-x.x.x.jar MongoDBApp

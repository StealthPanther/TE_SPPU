package assignment12;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import org.bson.Document;

import java.util.Arrays;
import java.util.Scanner;
public class Ass12 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// ✅ If your MongoDB does NOT need login (local DB)
        MongoClient mongoClient = new MongoClient("localhost", 27017);

        // ✅ OR use your college DB login (if they gave username/password)
        // MongoCredential credential = MongoCredential.createCredential("your_username", "31479_db", "your_password".toCharArray());
        // MongoClient mongoClient = new MongoClient(new ServerAddress("host", 27017), Arrays.asList(credential));

        MongoDatabase database = mongoClient.getDatabase("collegeDB");
        MongoCollection<Document> collection = database.getCollection("students");

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n1.Add   2.View   3.Edit   4.Delete   5.Exit");
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
                System.out.println("Inserted.");
            }

            else if (choice == 2) {
                for (Document d : collection.find()) {
                    System.out.println(d.toJson());
                }
            }

            else if (choice == 3) {
                System.out.print("Enter name to edit: ");
                String editName = sc.nextLine();
                System.out.print("New Email: ");
                String newEmail = sc.nextLine();

                UpdateResult result = collection.updateOne(
                        Filters.eq("name", editName),
                        new Document("$set", new Document("email", newEmail))
                );

                System.out.println(result.getModifiedCount() + " document(s) updated.");
            }

            else if (choice == 4) {
                System.out.print("Enter name to delete: ");
                String deleteName = sc.nextLine();

                DeleteResult result = collection.deleteOne(Filters.eq("name", deleteName));
                System.out.println(result.getDeletedCount() + " document(s) deleted.");
            }

            else if (choice == 5) {
                break;
            }
        }

        sc.close();
        mongoClient.close();

	}

}

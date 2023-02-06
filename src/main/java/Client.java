import database.User;

import java.net.*;
import java.io.*;
import java.util.Objects;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Client {
    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket("localhost", 3333);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        Scanner scanner = new Scanner(System.in);
        boolean ifLogged = false;
        while (true) {
            if(!ifLogged) {
                System.out.println("Hello in Uncle Andy's Library!");
                System.out.println("1. Log in.");
                System.out.println("2. Register.");
                System.out.println("3. Exit.");
                System.out.println("Your choice: ");

                int choice = scanner.nextInt();

                if (choice == 1) {
                    System.out.println("Name:");
                    String name = scanner.nextLine();
                    name = scanner.nextLine();
                    System.out.println("Last name: ");
                    String lastName = scanner.nextLine();

                    out.println("1;" + name + ";" + lastName);

                    String response = in.readLine();
                    System.out.println(response);
                    if (response.startsWith("Successfully logged.")) ifLogged = true;
                } else if (choice == 2) {
                    System.out.println("Registration:");
                    System.out.println("Name: ");
                    String name = scanner.nextLine();
                    name = scanner.nextLine();
                    System.out.println("Last name: ");
                    String lastName = scanner.nextLine();
                    System.out.println("Email: ");
                    String email = scanner.nextLine();
                    System.out.println("Address: ");
                    String address = scanner.nextLine();

                    out.println("2;" + name + ";" + lastName + ";" + email + ";" + address);

                    String response = in.readLine();
                    System.out.println(response);
                    ifLogged = true;
                } else if (choice == 3) {
                    out.println("exit");
                    System.out.println("Leaving the library ... See u next time!");
                    break;
                } else{
                    System.out.println("Incorrect option. Choose again.");
                }
            }
            else if(ifLogged) {
                DataInputStream din = new DataInputStream(socket.getInputStream());

                System.out.println("Menu:");
                System.out.println("1. Add book.");
                System.out.println("2. View books.");
                System.out.println("3. Search a book.");
                System.out.println("4. View library's statistics.");
                System.out.println("5. Rent a book.");
                System.out.println("6. Return the book.");
                System.out.println("7. History of your rentals.");
                System.out.println("8. Log out.");
                System.out.println("9. Exit.");
                System.out.println("Your choice: ");

                int choice = scanner.nextInt();

                if (choice == 1) {
                    System.out.println("Adding a new book:");
                    System.out.println("Title: ");
                    String title = scanner.nextLine();
                    title = scanner.nextLine();
                    System.out.println("Author: ");
                    String author = scanner.nextLine();
                    System.out.println("Year of publication:");
                    int yearOfPublication = scanner.nextInt();

                    out.println("1;" + title + ";" + author + ";" + yearOfPublication);

                    String response = din.readUTF();
                    System.out.println("Server response:\n" + response);
                } else if (choice == 2) {
                    out.println("2");

                    String response = din.readUTF();
                    System.out.println("Server response:\n" + response);
                } else if (choice == 3) {
                    System.out.println("Set title: ");
                    String title = scanner.nextLine();
                    title = scanner.nextLine();
                    out.println("3;" + title);

                    String response = din.readUTF();
                    System.out.println("Server response:\n" + response);
                } else if (choice == 4) {
                    out.println("4");

                    String response = din.readUTF();
                    System.out.println("Server response:\n" + response);
                } else if (choice == 5) {
                    System.out.println("Set book's id: ");
                    int idBook = scanner.nextInt();

                    out.println("5;" + idBook);

                    String response = din.readUTF();
                    System.out.println("Server response:\n" + response);
                } else if (choice == 6) {
                    System.out.println("Set rental's id: ");
                    int idRental = scanner.nextInt();

                    out.println("6;" + idRental);

                    String response = din.readUTF();
                    System.out.println("Server response:\n" + response);
                } else if (choice == 7) {
                    out.println("7");

                    String response = din.readUTF();
                    System.out.println("Server response:\n" + response);
                } else if (choice == 8) {
                    out.println("8");
                    System.out.println("Logging out...");
                    String response = din.readUTF();
                    System.out.println("Server response:\n" + response);
                    if(response.startsWith("Logged out.")) {
                        ifLogged = false;
                    }
                } else if (choice == 9) {
                    out.println("exit");
                    System.out.println("Leaving the library ... See u next time!");
                    break;
                } else {
                    System.out.println("Incorrect option. Choose again.");
                }
                sleep(1000);
            }
        }
        socket.close();
    }
}

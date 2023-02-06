import java.net.*;
import java.io.*;
import java.util.Date;
import java.util.List;

import database.Book;
import database.User;
import database.UserRentals;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import static java.lang.Integer.parseInt;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(3333);

        System.out.println("Server listening on port 3333");

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("New client connected");

            new Thread(() -> handleRequest(socket)).start();
        }
    }

    private static void handleRequest(Socket socket) {
        int UserId = 0;
        int RegisteredUserId = 0;
        boolean ifLogged = false;

        try {
            while (true) {
                if(ifLogged == false) {
                    SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
                    Session session = sessionFactory.openSession();

                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                    String request = in.readLine();

                    String[] words = request.split(";");

                    if (words[0].equals("1")) {
                        String name = words[1];
                        String lastName = words[2];

                        String hqlUser = "from User U where U.name= :name and U.lastName= :lastName";
                        Query queryUser = session.createQuery(hqlUser);
                        queryUser.setParameter("name", name);
                        queryUser.setParameter("lastName", lastName);
                        List<User> userList = queryUser.list();

                        if (userList.isEmpty()) {
                            out.println("User does not exist. Try again.");
                        } else {
                            UserId = (userList.get(0)).getId();
                            ifLogged = true;
                            out.println("Successfully logged. Your id: " + UserId);
                        }
                    } else if (words[0].equals("2")) {
                        session.beginTransaction();

                        User user = new User();
                        user.setName(words[1]);
                        user.setLastName(words[2]);
                        user.setEmail(words[3]);
                        user.setAddress(words[4]);

                        session.save(user);

                        UserId = user.getId();

                        session.getTransaction().commit();
                        session.close();

                        ifLogged = true;

                        out.println("User add. Your id: " + UserId);
                    } else {
                        break;
                    }
                }
                else if(ifLogged){
                    SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
                    Session session = sessionFactory.openSession();
                    session.beginTransaction();

                    // Read from and write to the socket as needed
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    DataOutputStream dout = new DataOutputStream(socket.getOutputStream());


                    // Read the request from the client
                    String request = in.readLine();

                    String[] words = request.split(";");

                    if (words[0].equals("1")) {

                        Book book = new Book();
                        book.setTitle(words[1]);
                        book.setAuthor(words[2]);
                        book.setAmountOfLoans(0);
                        book.setYearOfPublication(parseInt(words[3]));
                        session.merge(book);

                        String response = "New book added.";
                        dout.writeUTF(response);
                    } else if (words[0].equals("2")) {
                        String hql = "from Book";
                        Query query = session.createQuery(hql);
                        List<Book> bookList = query.list();
                        String response = "List of books:\n";
                        for (Book book : bookList) {
                            response += book.getTitle() + " | author: " + book.getAuthor() + "\n";
                        }

                        dout.writeUTF(response);
                    } else if (words[0].equals("3")) {
                        String hql = "from Book B where B.title= :name";
                        Query query = session.createQuery(hql);
                        query.setParameter("name", words[1]);

                        List<Book> bookList = query.list();
                        String response = "";

                        if (bookList.isEmpty())
                            response = "Did not find this book. :(";
                        else {
                            System.out.println("Free copies:");
                            for (Book book : bookList) {
                                response += book.getId() + " | " + book.getTitle() + " | Year of publication: " + book.getYearOfPublication() + "\n";
                            }
                        }

                        dout.writeUTF(response);
                    } else if (words[0].equals("4")) {

                        Long totalBooks = (Long) session.createQuery("select count(*) from Book b").getSingleResult();
                        Long totalUsers = (Long) session.createQuery("select count(*) from User u").getSingleResult();
                        Long totalUserRentals = (Long) session.createQuery("select count(*) from UserRentals ur").getSingleResult();

                        String response;
                        response = "Amount of users: " + totalUsers + "\n";
                        response += "Amount of book: " + totalBooks + "\n";
                        response += "Amount of rentals: " + totalUserRentals + "\n";

                        dout.writeUTF(response);
                    } else if (words[0].equals("5")) {
                        int idBook = parseInt(words[1]);
                        int id = UserId;

                        UserRentals userRental = new UserRentals();

                        String hqlUser = "from User U where U.id= :name";
                        Query queryUser = session.createQuery(hqlUser);
                        queryUser.setParameter("name", id);
                        List<User> userList = queryUser.list();

                        String hqlBook = "from Book B where B.id= :name";
                        Query queryBook = session.createQuery(hqlBook);
                        queryBook.setParameter("name", idBook);
                        List<Book> bookList = queryBook.list();

                        boolean ifAvailable = checkIfAvailable(idBook, session);

                        String response = "";

                        if (userList.isEmpty() || bookList.isEmpty()) {
                            response = "Cannot add a rental. This book does not exist.";
                        }
                        else if (!ifAvailable) {
                            response = "Cannot add a rental. Someone else has it.";
                        }
                        else {
                            addRental(bookList.get(0).getId(), session);

                            Date DateOfRental = new Date();

                            userRental.setBook(bookList.get(0));
                            userRental.setUser(userList.get(0));
                            userRental.setDateOfRental(DateOfRental);

                            session.merge(userRental);

                            response = "Rental added.";
                        }
                        dout.writeUTF(response);
                    } else if (words[0].equals("6")) {
                        int idRental = parseInt(words[1]);
                        Date dateOfReturn = new Date();

                        String response = "";

                        String hqlCheck = "from UserRentals UR where UR.id= :idRental";
                        Query queryCheck = session.createQuery(hqlCheck);
                        queryCheck.setParameter("idRental", idRental);
                        List<Book> bookList = queryCheck.list();

                        if (bookList.isEmpty()) {
                            response = "This rental does not exist in database.";
                        } else {
                            String hql = "update UserRentals UR set UR.dateOfReturn = :dateOfReturn where UR.id = :idRental";
                            Query query = session.createQuery(hql);
                            query.setParameter("idRental", idRental);
                            query.setParameter("dateOfReturn", dateOfReturn);
                            query.executeUpdate();
                            response = "Book returned.";
                        }

                        dout.writeUTF(response);
                    } else if (words[0].equals("7")) {
                        String hql = "from UserRentals UR where UR.user.id = :userId";
                        Query query = session.createQuery(hql);
                        query.setParameter("userId", UserId);
                        List<UserRentals> userRentalsList = query.list();

                        String response = "";

                        if(userRentalsList.isEmpty()){
                            response = "No rentals.";
                        }
                        else{
                            response = "List of rentals:";
                            for (UserRentals ur : userRentalsList) {
                                response +=  "\n";
                                response += ur.getBook().getTitle() + " | author: " + ur.getBook().getAuthor() + " | date of rental: ";
                                response += ur.getDateOfRental() + " | date of return: " + ur.getDateOfReturn();
                            }
                        }
                        dout.writeUTF(response);
                    } else if (words[0].equals("8")) {
                        ifLogged = false;
                        String response = "Logged out.";
                        dout.writeUTF(response);
                    } else {
                        break;
                    }
                    dout.flush();
                    session.getTransaction().commit();
                    session.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean checkIfAvailable(int bookId, Session session)
    {
        String hql = "from UserRentals UR where UR.book.id = :bookId and UR.dateOfReturn is null";
        Query query = session.createQuery(hql);
        query.setParameter("bookId", bookId);
        List<UserRentals> RentalsList = query.list();

        if(RentalsList.isEmpty()){
            return true;
        }else {
            return false;
        }
    }

    public static void addRental(int bookId, Session session)
    {
        String hqlCheck = "from Book B where B.id= :bookId";
        Query queryCheck = session.createQuery(hqlCheck);
        queryCheck.setParameter("bookId", bookId);
        List<Book> bookList = queryCheck.list();

        String hql = "update Book B set B.amountOfLoans = :addedAmount where B.id = :bookId";
        Query query = session.createQuery(hql);
        query.setParameter("addedAmount", bookList.get(0).getAmountOfLoans() + 1);
        query.setParameter("bookId", bookList.get(0).getId());
        query.executeUpdate();
    }
}

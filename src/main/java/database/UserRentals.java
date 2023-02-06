package database;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "UserLoans", schema = "", catalog = "")

public class UserRentals {

    private int id;
    private Date dateOfRental;
    private Date dateOfReturn;
    private User user;
    private Book book;

    public UserRentals() {

    }

    @Column(name = "Id")
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "dateOfRental")
    public Date getDateOfRental() {
        return dateOfRental;
    }

    public void setDateOfRental(Date dateOfRental) {
        this.dateOfRental = dateOfRental;
    }

    @Basic
    @Column(name = "dateOfReturn")
    public Date getDateOfReturn() {
        return dateOfReturn;
    }

    public void setDateOfReturn(Date dateOfReturn) {
        this.dateOfReturn = dateOfReturn;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="idUser")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="idBook")
    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    @Override
    public String toString() {
        return "UserRentals{" +
                "id=" + id +
                ", dateOfRental=" + dateOfRental +
                ", dateOfReturn=" + dateOfReturn +
                ", user=" + user +
                ", book=" + book +
                '}';
    }
}

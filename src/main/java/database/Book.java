package database;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "Books", schema = "", catalog = "")

public class Book {
    private int id;
    private String title;
    private String author;
    private int yearOfPublication;
    private int amountOfLoans;

    private Set<UserRentals> userRentals  = new HashSet<>(0);

    public Book() {

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
    @Column(name = "Title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "Author")
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Basic
    @Column(name = "YearOfPublication")
    public int getYearOfPublication() {
        return yearOfPublication;
    }

    public void setYearOfPublication(int yearOfPublication) {
        this.yearOfPublication = yearOfPublication;
    }

    @Basic
    @Column(name = "AmountOfLoans")
    public int getAmountOfLoans() {
        return amountOfLoans;
    }

    public void setAmountOfLoans(int amountOfLoans) {
        this.amountOfLoans = amountOfLoans;
    }

    @OneToMany(mappedBy = "book")
    //@JoinColumn(name = "idBook")
    public Set<UserRentals> getUserRentals() {
        return this.userRentals;
    }

    public void setUserRentals(Set<UserRentals> userRentals) {
        this.userRentals = userRentals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id == book.id && yearOfPublication == book.yearOfPublication && amountOfLoans == book.amountOfLoans && Objects.equals(title, book.title) && Objects.equals(author, book.author);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", yearOfPublication=" + yearOfPublication +
                ", amountOfLoans=" + amountOfLoans +
                '}';
    }
}

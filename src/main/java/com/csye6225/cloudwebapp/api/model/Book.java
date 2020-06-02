/**
 * 
 */
package com.csye6225.cloudwebapp.api.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author Snehal Patel
 *
 */
@Entity
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long bookId;
    private String bookISBN;
    private String bookTitle;
    private String bookAuthors;
    private Date bookPubDate;
    private Long bookQuantity;
    private Double bookPrice;
    private String bookSoldBy;
    private Date bookAdded;
    private Date bookLastModified;
    
    public Long getBookId() {
        return bookId;
    }
    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
    public String getBookISBN() {
        return bookISBN;
    }
    public void setBookISBN(String bookISBN) {
        this.bookISBN = bookISBN;
    }
    public String getBookTitle() {
        return bookTitle;
    }
    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }
    public String getBookAuthors() {
        return bookAuthors;
    }
    public void setBookAuthors(String bookAuthors) {
        this.bookAuthors = bookAuthors;
    }
    public Date getBookPubDate() {
        return bookPubDate;
    }
    public void setBookPubDate(Date bookPubDate) {
        this.bookPubDate = bookPubDate;
    }
    public Long getBookQuantity() {
        return bookQuantity;
    }
    public void setBookQuantity(Long bookQuantity) {
        this.bookQuantity = bookQuantity;
    }
    public Double getBookPrice() {
        return bookPrice;
    }
    public void setBookPrice(Double bookPrice) {
        this.bookPrice = bookPrice;
    }
    public String getBookSoldBy() {
        return bookSoldBy;
    }
    public void setBookSoldBy(String bookSoldBy) {
        this.bookSoldBy = bookSoldBy;
    }
    public Date getBookAdded() {
        return bookAdded;
    }
    public void setBookAdded(Date bookAdded) {
        this.bookAdded = bookAdded;
    }
    public Date getBookLastModified() {
        return bookLastModified;
    }
    public void setBookLastModified(Date bookLastModified) {
        this.bookLastModified = bookLastModified;
    }
}

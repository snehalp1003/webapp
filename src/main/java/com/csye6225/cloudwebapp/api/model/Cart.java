/**
 * 
 */
package com.csye6225.cloudwebapp.api.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author Snehal Patel
 *
 */
@Entity
public class Cart {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long cartItemId;
    private String bookBoughtBy;
    private String bookISBN;
    private String bookSoldBy;
    
    public Long getCartItemId() {
        return cartItemId;
    }
    public void setCartItemId(Long cartItemId) {
        this.cartItemId = cartItemId;
    }
    public String getBookBoughtBy() {
        return bookBoughtBy;
    }
    public void setBookBoughtBy(String bookBoughtBy) {
        this.bookBoughtBy = bookBoughtBy;
    }
    public String getBookISBN() {
        return bookISBN;
    }
    public void setBookISBN(String bookISBN) {
        this.bookISBN = bookISBN;
    }
    public String getBookSoldBy() {
        return bookSoldBy;
    }
    public void setBookSoldBy(String bookSoldBy) {
        this.bookSoldBy = bookSoldBy;
    }
}

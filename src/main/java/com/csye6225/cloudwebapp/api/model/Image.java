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
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long imageId;
    private String bookISBN;
    private String bookSoldBy;
    private String imageURL;
    private String imageName;
    private Date bookAdded;
    
    public Long getImageId() {
        return imageId;
    }
    public void setImageId(Long imageId) {
        this.imageId = imageId;
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
    public String getImageURL() {
        return imageURL;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
    public String getImageName() {
        return imageName;
    }
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
    public Date getBookAdded() {
        return bookAdded;
    }
    public void setBookAdded(Date bookAdded) {
        this.bookAdded = bookAdded;
    }
}

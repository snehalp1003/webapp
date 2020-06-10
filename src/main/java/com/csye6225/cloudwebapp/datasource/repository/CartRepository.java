/**
 * 
 */
package com.csye6225.cloudwebapp.datasource.repository;

import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;

import com.csye6225.cloudwebapp.api.model.Book;
import com.csye6225.cloudwebapp.api.model.Cart;

/**
 * @author Snehal Patel
 *
 */
public interface CartRepository extends CrudRepository<Cart, Integer> {
    
    /**
     * @param bookBoughtBy
     * @param bookISBN
     */
    Cart findByBookBoughtByAndBookISBN(String bookBoughtBy, String bookISBN);
    
    ArrayList<Cart> findByBookSoldByAndBookISBN(String bookSoldBy, String BookISBN);
    
    ArrayList<Cart> findAll();
    
    
}

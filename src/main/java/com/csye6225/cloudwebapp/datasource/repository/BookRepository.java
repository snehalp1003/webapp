/**
 * 
 */
package com.csye6225.cloudwebapp.datasource.repository;

import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;

import com.csye6225.cloudwebapp.api.model.Book;

/**
 * @author Snehal Patel
 *
 */
public interface BookRepository extends CrudRepository<Book, Integer> {
    
    /**
     * @param bookISBN
     * @param bookSoldBy
     */
    Book findByBookISBNAndBookSoldBy(String bookISBN, String bookSoldBy);
    ArrayList<Book> findAll();

}

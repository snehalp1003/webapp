/**
 * 
 */
package com.csye6225.cloudwebapp.api.rest.book;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.csye6225.cloudwebapp.api.model.Book;
import com.csye6225.cloudwebapp.datasource.repository.BookRepository;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Snehal Patel
 *
 */
@RestController
@RequestMapping("/v1/updateBookDetails/bookISBN/{bookISBN}/bookSoldBy/{bookSoldBy}/userLoggedIn/{userLoggedIn}")
public class UpdateBookDetails {

    @Autowired
    private BookRepository bookRepository;
    
    @PutMapping
    @ApiOperation(value = "Updates book details", notes = "Updates new book details")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Book details inserted successfully."),
            @ApiResponse(code = 401, message = "User is Unauthorized to access this method."),
            @ApiResponse(code = 403, message = "Forbidden to access this method."),
            @ApiResponse(code = 404, message = "Requested details not found."),
            @ApiResponse(code = 500, message = "Internal error, not able to perform the operation.") })
    // Specific method to update book details
    public ResponseEntity updateBookDetails(@PathVariable(value = "bookISBN") String bookISBN,
            @PathVariable(value = "userLoggedIn") String userLoggedIn,
            @RequestBody Book bookDetails) throws IOException {

        if(!userLoggedIn.equals(bookDetails.getBookSoldBy())) {
            return new ResponseEntity("Cannot update book belonging to another seller !", HttpStatus.FORBIDDEN);
        } else if (bookRepository.findByBookISBNAndBookSoldBy(bookISBN, bookDetails.getBookSoldBy()) != null) {
            Book book = bookRepository.findByBookISBNAndBookSoldBy(bookISBN, bookDetails.getBookSoldBy());
            book.setBookISBN(bookDetails.getBookISBN());
            book.setBookTitle(bookDetails.getBookTitle());
            book.setBookAuthors(bookDetails.getBookAuthors());
            book.setBookPubDate(bookDetails.getBookPubDate());
            book.setBookQuantity(bookDetails.getBookQuantity());
            book.setBookPrice(bookDetails.getBookPrice());
            book.setBookSoldBy(bookDetails.getBookSoldBy());
            book.setBookAdded(bookDetails.getBookAdded());
            book.setBookLastModified(new Date());
            bookRepository.save(book);
            
            return new ResponseEntity(book, HttpStatus.OK);
        } else {
            return new ResponseEntity("Book not found !", HttpStatus.NOT_FOUND);
        }

    }
}

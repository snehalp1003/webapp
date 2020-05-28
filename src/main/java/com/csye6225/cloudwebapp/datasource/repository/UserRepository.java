package com.csye6225.cloudwebapp.datasource.repository;

import org.springframework.data.repository.CrudRepository;

import com.csye6225.cloudwebapp.api.model.User;

/**
 * @author Snehal Patel
 *
 */
public interface UserRepository extends CrudRepository<User, Integer> {

    /**
     * @param userEmailAddress
     */
    User findByUserEmailAddress(String userEmailAddress);

}

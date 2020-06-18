/**
 * 
 */
package com.csye6225.cloudwebapp.datasource.repository;

import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;

import com.csye6225.cloudwebapp.api.model.Image;

/**
 * @author Snehal Patel
 *
 */
public interface ImageRepository extends CrudRepository<Image, Integer> {

    /**
     * @param fileNameWithDate
     * @return
     */
    Image findByImageName(String fileNameWithDate);
    ArrayList<Image> findAll();

}

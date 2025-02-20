/**
 * 
 */
package com.jpmc.cache.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jpmc.cache.model.Employee;
import com.jpmc.cache.service.CacheService;

import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for managing cache operations related to {@link Employee} entities.
 * This controller exposes endpoints for adding, removing, and clearing entries in the cache,
 * as well as retrieving employee data either from the cache or the database.
 * 
 * The controller interacts with the {@link CacheService} to perform operations on the cache and 
 * ensures that the correct HTTP response is returned after each action.
 * 
 * @author r.pandiarajan
 */
@RestController
@RequestMapping("/cache")
@Slf4j
public class CacheController
{
    
    /** Service for managing the cache of {@link Employee} entities. */
    @Autowired
    CacheService cacheService;
    
    private static final String ADD_MSG = "Entry added successfully.";
    private static final String REMOVE_MSG = "Entry removed from cache and DB successfully.";
    private static final String REMOVE_ALL_MSG = "All entries removed from cache and DB successfully.";
    private static final String CLEAR_MSG = "All entries cleared from cache successfully.";
    
    /**
     * Endpoint to add an {@link Employee} entity to the cache.
     * If the cache is full, the least recently used entry will be evicted to the database.
     * 
     * @param employee The {@link Employee} entity to be added to the cache.
     * @return A {@link ResponseEntity} containing a success message.
     */
    @PostMapping("/add")
    public ResponseEntity<String> addEntity(@RequestBody Employee employee) {
        log.info("Adding new entry to cache service {}", employee.getId());
        cacheService.add(employee);
        return new ResponseEntity<>(ADD_MSG, HttpStatus.OK);
    }

    /**
     * Endpoint to remove an {@link Employee} entity from the cache and the database.
     * 
     * @param employee The {@link Employee} entity to be removed.
     * @return A {@link ResponseEntity} containing a success message.
     */
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeEntity(@RequestBody Employee employee) {
        log.info("Remove entry from cache and db {}", employee.getId());
        cacheService.remove(employee);
        return new ResponseEntity<>(REMOVE_MSG, HttpStatus.OK);
    }

    /**
     * Endpoint to remove all {@link Employee} entities from the cache and the database.
     * 
     * @return A {@link ResponseEntity} containing a success message.
     */
    @DeleteMapping("/removeAll")
    public ResponseEntity<String> removeAllEntities() {
        log.info("Remove all entries from cache and db");
        cacheService.removeAll();
        return new ResponseEntity<>(REMOVE_ALL_MSG, HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve an {@link Employee} entity by its ID.
     * If the entity is not found in the cache, it will be fetched from the database 
     * and added to the cache.
     * 
     * @param id The ID of the {@link Employee} entity to retrieve.
     * @return The {@link Employee} entity.
     * @throws EntityNotFoundException if the entity is not found in both the cache and the database.
     */
    @GetMapping("/get/{id}")
    public Employee getEntity(@PathVariable Long id) {
        log.info("Getting entry from cache or db with {}", id);
        return cacheService.get(id);
    }

    /**
     * Endpoint to clear all entries from the cache, without affecting the database.
     * 
     * @return A {@link ResponseEntity} containing a success message.
     */
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCache() {
        log.info("Clear entries from the cache");
        cacheService.clear();
        return new ResponseEntity<>(CLEAR_MSG, HttpStatus.OK);
    }

}

/**
 *
 */
package com.jpmc.cache.service;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jpmc.cache.model.Employee;
import com.jpmc.cache.repository.EmployeeRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class responsible for managing a cache of {@link Employee} entities.
 * It provides methods to add, remove, and retrieve {@link Employee} objects
 * from a local cache with automatic eviction to a database when the cache exceeds 
 * the maximum size.
 * 
 * @author r.pandiarajan
 */
@Service
@Slf4j
public class CacheService
{
    /** The cache storing {@link Employee} objects by their ID. */
    @Getter
    private final Map<Long, Employee> cache;
    
    /** Maximum number of entries allowed in the cache. */
    @Value("${cache.max-size}")
    @Setter
    private int maxCacheSize;
    
    private static final String ENTITY_NOT_FOUND = "Entry not found for the id - {0}";
    
    /** Repository used to interact with the database for {@link Employee} entities. */
    @Autowired
    EmployeeRepository employeeRepository;

    /**
     * Constructor that initializes the cache with a maximum size and 
     * an eviction policy where the least recently used (LRU) entries 
     * are removed when the cache exceeds the size limit.
     */
    @SuppressWarnings("serial")
    public CacheService() {
        this.cache = new LinkedHashMap<Long, Employee>(maxCacheSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, Employee> eldest) {
                return size() > maxCacheSize;
            }
        };
    }
    
    /**
     * Adds an {@link Employee} to the cache. If the cache is full, it evicts 
     * the least recently used entry to the database before adding the new entry.
     * 
     * @param employee The {@link Employee} to be added to the cache.
     */    
    public void add(Employee employee) {
        logCacheKeys();
        log.info("Current cache size is {}, max cache size is {}", cache.size(), maxCacheSize);
        if (cache.size() >= maxCacheSize) {
            evictToDatabase();
        }
        cache.put(employee.getId(), employee);
    }

    /**
     * Removes an {@link Employee} from the cache and deletes it from the database.
     * 
     * @param employee The {@link Employee} to be removed from the cache and database.
     */
    public void remove(Employee employee) {
        logCacheKeys();
        cache.remove(employee.getId());
        employeeRepository.delete(employee);
    }

    /**
     * Clears all entries from the cache and deletes all {@link Employee} entries 
     * from the database.
     */
    public void removeAll() {
        logCacheKeys();
        cache.clear();
        employeeRepository.deleteAll();
    }

    /**
     * Retrieves an {@link Employee} by its ID. If the employee is not found in the 
     * cache, it will be fetched from the database and added to the cache.
     * 
     * @param id The ID of the {@link Employee} to retrieve.
     * @return The {@link Employee} if found.
     * @throws EntityNotFoundException if no {@link Employee} is found for the given ID.
     */
    public Employee get(Long id) {
        logCacheKeys();
        Employee employee = cache.get(id);
        if (employee == null) {
            log.info("Entry is not in cache, getting it from DB with id - {}", id);
            Optional<Employee> employeeOptional = employeeRepository.findById(id);
            if (employeeOptional.isPresent()) {
                log.info("Entry present in DB for the id - {}", id);
                employee = employeeOptional.get();
                evictToDatabase();   
                cache.put(id, employee);
            }
            else {
                throw new EntityNotFoundException(MessageFormat.format(ENTITY_NOT_FOUND, id));
            }
        }
        return employee;
    }

    /**
     * Clears all entries from the cache without affecting the database.
     */
    public void clear() {
        logCacheKeys();
        cache.clear();
    }

    /**
     * Evicts the least recently used (LRU) entry from the cache and saves it 
     * to the database.
     */
    private void evictToDatabase() {
        Long lruKey = cache.entrySet().iterator().next().getKey();
        log.info("Evicting LRU entry with key - {} from cache to DB", lruKey);
        Employee lruEmployee = cache.remove(lruKey);
        if (lruEmployee != null) {
            employeeRepository.save(lruEmployee);
        }
    }
    
    /**
     * Logs the current keys in the cache for debugging purposes.
     */
    private void logCacheKeys() {
        log.info("Current cache - {}", cache.keySet());
    }


}

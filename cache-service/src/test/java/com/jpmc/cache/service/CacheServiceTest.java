/**
 * 
 */
package com.jpmc.cache.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.jpmc.cache.model.Employee;
import com.jpmc.cache.repository.EmployeeRepository;

import jakarta.persistence.EntityNotFoundException;

/**
 * Unit test class for testing the methods of {@link CacheService}.
 * This test suite ensures that cache operations such as adding, removing, eviction, and clearing 
 * behave as expected, as well as interacting correctly with the {@link EmployeeRepository}.
 * 
 * @author r.pandiarajan
 *
 */
@SpringBootTest
class CacheServiceTest
{
    
    @InjectMocks
    CacheService cacheService;
    
    @Mock
    EmployeeRepository employeeRepository;
    
    private Employee entry1;
    private Employee entry2; 
    
    @BeforeEach
    public void setup() {
        
        MockitoAnnotations.openMocks(this);
        
        entry1 = new Employee();
        entry1.setId(1L);
        entry1.setName("test1");
        entry1.setSalary(1000.0);
        
        entry2 = new Employee();
        entry2.setId(2L);
        entry2.setName("test2");
        entry2.setSalary(2000.0);
        
        cacheService.setMaxCacheSize(2);
        
    }
    
    @Test
    void testAddEntityToCache() {
        // Add entity1 to cache
        cacheService.add(entry1);

        // Check that the entity is added to the cache
        assertNotNull(cacheService.get(entry1.getId()));
    }
    
    @Test
    void testEvictionWhenCacheExceedsMaxSize() {
        
        Integer expectedCacheSize = 2;              
        
        // Add entity1 and entity2 to cache (exceeding the max size)
        cacheService.add(entry1);
        cacheService.add(entry2);

        // Verify the cache has both entries
        assertEquals(expectedCacheSize, cacheService.getCache().size());

        // Add a new entity which should trigger eviction
        Employee entry3 = new Employee();
        entry3.setId(3L);
        entry3.setName("test3");
        entry3.setSalary(3000.0);
                
        cacheService.add(entry3);

        // Ensure that the cache evicted the least used entity (entity1 should be evicted)
        assertEquals(2, cacheService.getCache().size());
        verify(employeeRepository, times(1)).save(entry1);
        assertNull(cacheService.getCache().get(entry1.getId()));
    }
    
    @Test
    void testRemoveEntityFromCacheAndDatabase() {
        // Add entity1 to cache
        cacheService.add(entry1);
        
        // Remove entity1 from cache and database
        cacheService.remove(entry1);

        // Verify that the entity is removed from cache and database
        assertNull(cacheService.getCache().get(entry1.getId()));
        verify(employeeRepository, times(1)).delete(entry1);
    }
    
    @Test
    void testGetEntityFromCache() {
        // Add entity1 to cache
        cacheService.add(entry1);

        // Fetch entity from cache
        Employee fetchedEntity = cacheService.get(entry1.getId());

        // Verify that the correct entity is returned from cache
        assertNotNull(fetchedEntity);
        assertEquals(entry1.getId(), fetchedEntity.getId());
    }
    
    @Test
    void testGetEntityFromDatabaseWhenNotInCache() {
        // Entity is not in cache, so it should be fetched from the database
        when(employeeRepository.findById(entry1.getId())).thenReturn(Optional.of(entry1));
        
        Employee entry3 = new Employee();
        entry3.setId(3L);
        entry3.setName("test3");
        entry3.setSalary(3000.0);
                
        // Add entity2 and entity3 to cache
        cacheService.add(entry2);
        cacheService.add(entry3);

        Employee fetchedEntity = cacheService.get(entry1.getId());

        // Verify that the entity is fetched from the database
        assertNotNull(fetchedEntity);
        assertEquals(entry1.getId(), fetchedEntity.getId());
        verify(employeeRepository, times(1)).findById(entry1.getId());
        verify(employeeRepository, times(1)).save(entry2);
    }
    
    @Test
    void testGetEmployeeNotFound() {
        // Simulate DB miss
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty()); 

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            cacheService.get(1L);
        });

        String expectedMessage = "Entry not found for the id - 1";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    
    @Test
    void testRemoveAllEntities() {
        // Add entities to cache
        cacheService.add(entry1);
        cacheService.add(entry2);

        // Remove all entities from cache and database
        cacheService.removeAll();

        // Verify cache is cleared and entities are deleted from the database
        assertTrue(cacheService.getCache().isEmpty());
        verify(employeeRepository, times(1)).deleteAll();
    }
    
    @Test
    void testClearCache() {
        // Add entities to cache
        cacheService.add(entry1);
        cacheService.add(entry2);

        // Clear the cache
        cacheService.clear();

        // Verify that the cache is cleared but database is unaffected
        assertTrue(cacheService.getCache().isEmpty());
        verify(employeeRepository, never()).deleteById(anyLong());
    }



}

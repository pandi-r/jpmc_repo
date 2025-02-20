/**
 * 
 */
package com.jpmc.cache.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.text.MessageFormat;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpmc.cache.model.Employee;
import com.jpmc.cache.service.CacheService;

import jakarta.persistence.EntityNotFoundException;

/**
 * Test class for testing the endpoints in the {@link CacheController}.
 * This class uses mock MVC to simulate HTTP requests and verify the behavior of the cache controller.
 * The class contains tests for adding, removing, retrieving, and clearing entities in the cache.
 * 
 * @author r.pandiarajan
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
class CacheControllerTest {

    @Autowired
    MockMvc mockMvc;
        
    @MockitoBean
    CacheService cacheService;
    
    private Employee entry1;
    private static final String ENTITY_NOT_FOUND = "Entry not found for the id - {0}";

    @BeforeEach
    public void setUp() {
        // Set up a sample entity
        entry1 = new Employee();
        entry1.setId(1L);
        entry1.setName("test1");
        entry1.setSalary(1000.0);
    }
    
    @Test
    void testAddEntity() throws Exception {
        // Simulate a POST request to add the entity
        mockMvc.perform(post("/cache/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(entry1)))
                .andExpect(status().isOk())
                .andExpect(content().string("Entry added successfully."));
        
        // Verify that the add method in CacheService is called
        verify(cacheService, times(1)).add(any());
    }
    
    @Test
    void testRemoveEntity() throws Exception {
        // Simulate a DELETE request to remove the entity
        mockMvc.perform(delete("/cache/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(entry1)))
                .andExpect(status().isOk())
                .andExpect(content().string("Entry removed from cache and DB successfully."));
        
        // Verify that the remove method in CacheService is called
        verify(cacheService, times(1)).remove(any());
    }
    
    @Test
    void testRemoveAllEntity() throws Exception {
        // Simulate a DELETE request to remove all the entities
        mockMvc.perform(delete("/cache/removeAll"))
                .andExpect(status().isOk())
                .andExpect(content().string("All entries removed from cache and DB successfully."));
        
        // Verify that the removeAll method in CacheService is called
        verify(cacheService, times(1)).removeAll();
        
    }
    
    @Test
    void testClearCache() throws Exception {
        // Simulate a DELETE request to clear the entities from cache
        mockMvc.perform(delete("/cache/clear"))
                .andExpect(status().isOk())
                .andExpect(content().string("All entries cleared from cache successfully."));
        
        // Verify that the clear method in CacheService is called
        verify(cacheService, times(1)).clear();
    }
    
    @Test
    void testGetEntity() throws Exception {
        // Mock the behavior of the cacheService to return an entity
        when(cacheService.get(1L)).thenReturn(entry1);

        // Simulate a GET request to fetch the entity
        mockMvc.perform(get("/cache/get/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(entry1.getId()))
                .andExpect(jsonPath("$.name").value(entry1.getName()))
                .andExpect(jsonPath("$.salary").value(entry1.getSalary()));

        // Verify that the get method in CacheService is called
        verify(cacheService, times(1)).get(1L);
    }
    
    @Test
    void testGetEntityNotFound() throws Exception {
        // Mock the behavior of the cacheService to throw exception
        when(cacheService.get(1L)).thenThrow(new EntityNotFoundException(MessageFormat.format(ENTITY_NOT_FOUND, 1)));
        
        // Simulate a GET request to fetch the entity
        mockMvc.perform(get("/cache/get/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Resource Not found"))
                .andExpect(jsonPath("$.message").value("Entry not found for the id - 1"));
    }
    
    @Test
    void testInternalError() throws Exception {
        
        // Simulate a GET request to fetch the entity
        mockMvc.perform(get("/cache/add"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("An Error Occured"))
                .andExpect(jsonPath("$.message").value("Request method 'GET' is not supported"));
    }

    
}
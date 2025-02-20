package com.jpmc.cache;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.jpmc.cache.controller.CacheController;

/**
 * Test class for verifying the basic application context loading and 
 * ensuring that necessary beans are properly initialized.
 * 
 * This test checks that the Spring application context loads successfully 
 * and that the {@link CacheController} is correctly injected.
 * 
 * @author r.pandiarajan
 */
@SpringBootTest
class CacheServiceApplicationTests {
    
    @Autowired
    CacheController cacheController;

	@Test
	void contextLoads() {
	    assertNotNull(cacheController);
	}

}

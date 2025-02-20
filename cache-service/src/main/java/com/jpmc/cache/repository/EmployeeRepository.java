/**
 * 
 */
package com.jpmc.cache.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jpmc.cache.model.Employee;

/**
 * Repository interface for accessing {@link Employee} entities in the database.
 * This interface extends {@link JpaRepository}, which provides built-in methods 
 * for basic CRUD operations and querying the database.
 * 
 * The repository is typically used to perform operations such as saving, 
 * updating, deleting, and finding {@link Employee} entities.
 * 
 * @author r.pandiarajan
 *
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long>
{

}

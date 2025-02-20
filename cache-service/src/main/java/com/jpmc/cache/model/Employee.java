/**
 * 
 */
package com.jpmc.cache.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity class representing an {@link Employee} in the system.
 * This class is mapped to the "employees" table in the database and contains 
 * the attributes of an employee such as ID, name, and salary.
 * It is used for persistence operations through JPA (Java Persistence API).
 * 
 * @author r.pandiarajan
 *
 */
@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
public class Employee {
    
    @Id
    private Long id;
    
    private String name;
    
    private Double salary;
}

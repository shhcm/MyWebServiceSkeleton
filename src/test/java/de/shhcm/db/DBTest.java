package de.shhcm.db;
import static org.junit.Assert.*;
import static org.eclipse.persistence.config.PersistenceUnitProperties.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.spi.PersistenceUnitTransactionType;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.junit.After; 
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.shhcm.model.Event;

public class DBTest {
    
    private EntityManager entityManager;
    private static EntityManagerFactory entityManagerFactory;
    
    @BeforeClass
    public static void setUpBeforeClass() {
        Map<String,String> properties = new HashMap<String,String>();
        properties.put(TRANSACTION_TYPE,
                PersistenceUnitTransactionType.RESOURCE_LOCAL.name());
        properties.put(JTA_DATASOURCE, "");
        properties.put(NON_JTA_DATASOURCE, "");
        properties.put(JDBC_DRIVER, "org.h2.Driver");
        properties.put(JDBC_URL, "jdbc:h2:mem:h2db_tests");
        properties.put(JDBC_USER, "sa");
        properties.put(JDBC_PASSWORD, "");
        properties.put(DDL_GENERATION, "drop-and-create-tables");
        
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory("de.shhcm.model", properties); // Pass the persistence unit name here.
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    @Before
    public void setUp() {
        // If the DB should be empty before each test, this has to be implemented here!
        try {
            entityManager = entityManagerFactory.createEntityManager();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    @After
    public void tearDown() {
        entityManager.close();
    }
    
    @AfterClass
    public static void tearDownAfterClass() {
        entityManagerFactory.close();
    }
    
    @Test
    public void canWriteToDb() {
        // Given
        entityManager.getTransaction().begin();
        
        Event event = new Event();
        event.setDate( new Date(System.currentTimeMillis()));
        event.setTitle("Received GET request!");
        
        // When 
        entityManager.persist(event);
        entityManager.getTransaction().commit();
        
        Query query=entityManager.createQuery("SELECT COUNT(e.id) FROM Event e");
        Number count = (Number)query.getSingleResult();
        
        // Then
        assertEquals(1,count.intValue());
    }
}

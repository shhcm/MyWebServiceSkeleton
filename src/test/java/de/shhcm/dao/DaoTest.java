package de.shhcm.dao;
import static org.junit.Assert.*;
import static org.eclipse.persistence.config.PersistenceUnitProperties.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Persistence;
import javax.persistence.spi.PersistenceUnitTransactionType;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.shhcm.dao.DependencyInjectedDao;
import de.shhcm.model.Event;

public class DaoTest {
    
    private static DependencyInjectedDao dao;
    
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
            DependencyInjectedDao.entityManagerFactory = Persistence.createEntityManagerFactory("de.shhcm.model", properties); // Pass the persistence unit name here.
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    @Before
    public void setUp() {
        dao = new DependencyInjectedDao();
        // Load fixtures.
    }
    
    @After
    public void tearDown() {
        // Clean up.
    }
    
    @AfterClass
    public static void tearDownAfterClass() {
        // Clean up.
    }
    
    @Test
    public void canWriteToDb() {
        // Given
        Event event = new Event();
        event.setDate( new Date(System.currentTimeMillis()));
        event.setTitle("Test event.");
        
        // When 
        dao.saveEvent(event);
        int count = dao.countEvents();
        
        // Then
        assertEquals(1, count);
    }
    
    @Test
    public void dbIsInitiallyEmpty() {
        // Given.
        int count = -1;
        // When.
        count = dao.countEvents();
        // Then.
        assertEquals(0, count);
    }
}

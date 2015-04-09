package de.shhcm.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.springframework.stereotype.Component;

import de.shhcm.model.Event;

@Component
public class DependencyInjectedDao implements IDao{
    private String bar = "foo";
    protected static EntityManagerFactory entityManagerFactory;
    
    static {
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory("de.shhcm.model"); // Pass the persistence unit name here.
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getBar() {
        return bar;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }
    
    @Override
    public int countEvents() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Query query=entityManager.createQuery("SELECT COUNT(e.id) FROM Event e");
        Number count = (Number)query.getSingleResult();
        entityManager.close();
        return count.intValue();
    }
    
    @Override
    public void saveEvent(Event event) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(event);
        entityManager.getTransaction().commit();
        entityManager.close();
    }
}

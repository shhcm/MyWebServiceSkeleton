package de.shhcm.dao;

import de.shhcm.model.Event;

public interface IDao {
    // Just in case we want an interface for mocking...
    
    public int countEvents();
    
    public void saveEvent(Event event);
}

package de.shhcm.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * Simple DTO representing the events that are stored to DB.
 * An event DTO has an EventCounter child element for
 * the purpose of demonstrating that we return
 * some state that is actually stored to a DB.
 */
@XmlRootElement(name="serializableEvent")
@XmlAccessorType(XmlAccessType.FIELD)
public class SerializableEvent {

    @XmlElement(required=true)
    protected String title;
    
    @XmlElement(required=false)
    protected EventCounter eventCounter;

    public EventCounter getEventCounter() {
        return eventCounter;
    }

    public void setEventCounter(EventCounter eventCounter) {
        this.eventCounter = eventCounter;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

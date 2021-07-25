package tn.esprit.spring.Entity.RequestApiForm;

import java.util.Date;
import java.util.Set;

import tn.esprit.spring.Entity.Dbo_User;

public class EventRequest {
    private String name;
    private String description;
    private Date start_date;
    private Date end_date;
    private int capacity;
    private boolean hasFinished;
    private Set<Long> child_participant;
    private Dbo_User user_Event_Creator;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Dbo_User getUser_Event_Creator() {
        return user_Event_Creator;
    }

    public void setUser_Event_Creator(Dbo_User user_Event_Creator) {
        this.user_Event_Creator = user_Event_Creator;
    }

    public Set<Long> getChild_participant() {
        return child_participant;
    }

    public void setChild_participant(Set<Long> child_participant) {
        this.child_participant = child_participant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isHasFinished() {
        return hasFinished;
    }

    public void setHasFinished(boolean hasFinished) {
        this.hasFinished = hasFinished;
    }
}

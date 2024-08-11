package org.jbpt.pm.log;

import java.util.ArrayList;
import java.util.List;

public class ActivityOps extends EventDataOps<String> {

    public ActivityOps(EventLog eventLog) {
        super(eventLog);
    }

    @Override
    List<String> getEventData() {
        return getEvents();
    }

    private List<String> getEvents() {
        List<String> activities = new ArrayList<>();

        for (Trace trace : this.eventLog) {
            activities.addAll(trace);
        }
        return activities;
    }
}

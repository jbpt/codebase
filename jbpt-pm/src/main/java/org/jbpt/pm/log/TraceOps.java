package org.jbpt.pm.log;

import java.util.List;

public class TraceOps extends EventDataOps<Trace> {

    public TraceOps(EventLog eventLog) {
        super(eventLog);
    }

    @Override
    List<Trace> getEventData() {
        return this.eventLog;
    }

}

package org.jbpt.pm.log;

public class EventLogOps {

    private final EventLog eventLog;

    public EventLogOps(EventLog eventLog) {
        this.eventLog = eventLog;
    }

    public double getLRA(EventDataEnum eventDataType) {
        EventDataOps eventDataOps = getEventDataOps(eventDataType);
        return 1 - (double) eventDataOps.getDistinctEventDataCount() / eventDataOps.getEventDataCount();
    }

    public double getCompleteness(EventDataEnum eventDataType) {
        EventDataOps eventDataOps = getEventDataOps(eventDataType);

        int q1 = eventDataOps.getSingletonCount();
        int q2 = eventDataOps.getDoubletonCount();
        int obs = eventDataOps.getDistinctEventDataCount();

        double richness;
        if (q2 != 0) {
            richness = obs + (double) (q1 * q1) / (2 * q2);
        } else {
            richness = obs + (double) q1 * (q1 - 1) / 2;
        }

        return (double) obs / richness;
    }

    public double getCoverage(EventDataEnum eventDataType) {
        if (getCompleteness(eventDataType) == 1) return 1;

        EventDataOps eventDataOps = getEventDataOps(eventDataType);

        int q1 = eventDataOps.getSingletonCount();
        int q2 = eventDataOps.getDoubletonCount();
        int n = eventDataOps.getEventDataCount();
        double part1 = (double) q1 * (n - 1) / ((n - 1) * q1 + 2 * q2);
        double part2 = (double) q1 / n;
        return 1 - part1 * part2;
    }

    private EventDataOps getEventDataOps(EventDataEnum eventDataType) {
        EventDataOps eventDataOps;
        if (eventDataType == EventDataEnum.TRACE) {
            eventDataOps = new TraceOps(this.eventLog);
        } else if (eventDataType == EventDataEnum.DF_RELATION) {
            eventDataOps = new DFRelationOps(this.eventLog);
        } else if (eventDataType == EventDataEnum.ACTIVITY) {
            eventDataOps = new ActivityOps(this.eventLog);
        } else {
            throw new IllegalArgumentException("Unsupported event data type: " + eventDataType);
        }
        return eventDataOps;
    }
}

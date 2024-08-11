package org.jbpt.pm.log;

import java.util.*;

public abstract class EventDataOps<Object> {

    final EventLog eventLog;

    public EventDataOps(EventLog eventLog) {
        this.eventLog = eventLog;
    }

    public Set<Object> getDistinctEventData() {
        List<Object> eventDataList = getEventData();
        return new HashSet<>(eventDataList);
    }

    public int getDistinctEventDataCount() {
        return getDistinctEventData().size();
    }

    public int getEventDataCount() {
        return getEventData().size();
    }

    abstract List<Object> getEventData();

    public List<Object> getSingletons() {
        Map<Integer, List<Object>> groupedEventData = groupEventData();
        return groupedEventData.getOrDefault(1, new ArrayList<>());
    }

    public List<Object> getDoubletons() {
        Map<Integer, List<Object>> groupedEventData = groupEventData();
        return groupedEventData.getOrDefault(2, new ArrayList<>());
    }

    public int getSingletonCount() {
        return getSingletons().size();
    }

    public int getDoubletonCount() {
        return getDoubletons().size();
    }

    private HashMap<Integer, List<Object>> groupEventData() {
        List<Object> eventDataList = getEventData();
        HashMap<Object, Integer> eventDataCount = new HashMap<>();

        // Create a HashMap to count the occurrences of each distinct event data
        for (Object eventData : eventDataList) {
            eventDataCount.put(eventData, eventDataCount.getOrDefault(eventData, 0) + 1);
        }

        // Create a HashMap to group strings by the count of occurrences
        HashMap<Integer, List<Object>> groupedEventData = new HashMap<>();

        for (Object distinctEventData : eventDataCount.keySet()) {
            int count = eventDataCount.get(distinctEventData);
            groupedEventData.putIfAbsent(count, new ArrayList<>());
            groupedEventData.get(count).add(distinctEventData);
        }

        return groupedEventData;
    }

}

package org.jbpt.pm.log;

import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class DFRelationOps extends EventDataOps<Pair> {

    public DFRelationOps(EventLog eventLog) {
        super(eventLog);
    }

    @Override
    List<Pair> getEventData() {
        return getRelations();
    }

    private List<Pair> getRelations() {
        List<Pair> relations = new ArrayList<>();

        for (Trace trace : this.eventLog) {
            for (int i = 0; i < trace.size() - 1; i++) {
                String first = trace.get(i);
                String second = trace.get(i + 1);
                relations.add(new Pair<>(first, second));
            }
        }
        return relations;
    }
}

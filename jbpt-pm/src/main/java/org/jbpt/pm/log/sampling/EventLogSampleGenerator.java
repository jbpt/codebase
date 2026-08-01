package org.jbpt.pm.log.sampling;

import org.jbpt.pm.log.EventLog;

/**
 * This is the log sample generator class which will take an event log in .xes
 * format and generate a breeding sample to a .xes file.
 */
public class EventLogSampleGenerator {

    public void generateBreedingSampleLog(EventLog log, int n, int g, int k, double p, String outputFile) {
        EventLog breedingSample = EventLogSampling.logSamplingWithBreeding(log, n, g, k, p);
        EventLog.serializeEventLogToXES(breedingSample, outputFile);
        System.out.println("Breeding sample generated at " + outputFile);
    }

}

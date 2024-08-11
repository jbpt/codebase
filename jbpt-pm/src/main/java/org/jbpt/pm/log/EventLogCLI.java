package org.jbpt.pm.log;

import org.jbpt.pm.utils.Utils;

public class EventLogCLI {

    private final EventLog eventLog;

    public EventLogCLI(EventLog eventLog) {
        this.eventLog = eventLog;
    }

    public void printEventLogStats(boolean bCom, boolean bCov, boolean bLRA, boolean bAct, boolean bDFR, boolean bTra, boolean bSilent) {
        EventLogOps eventLogOps = new EventLogOps(eventLog);
        if (bAct) {
            System.out.println("--------------------------- Activity-based Analysis ----------------------------");
            ActivityOps activityOps = new ActivityOps(eventLog);
            System.out.println("Activities: " + activityOps.getDistinctEventData());
            System.out.println("Number of distinct activities: " + activityOps.getDistinctEventDataCount());
            System.out.println("Number of total activities: " + activityOps.getEventDataCount());
            if (bCom) {
                double aCompleteness = eventLogOps.getCompleteness(EventDataEnum.ACTIVITY);
                System.out.println("Completeness: " + aCompleteness);
                if (bSilent) {
                    Utils.forcePrinting();
                    if (!bCov && !bLRA) {
                        if (!bDFR && !bTra) {
                            System.out.println(aCompleteness);
                        } else {
                            System.out.println("Completeness: " + aCompleteness);
                        }
                    } else {
                        System.out.println("Activity completeness: " + aCompleteness);
                    }
                    Utils.restorePrinting();
                }
            }
            if (bCov) {
                double aCoverage = eventLogOps.getCoverage(EventDataEnum.ACTIVITY);
                System.out.println("Coverage: " + aCoverage);
                if (bSilent) {
                    Utils.forcePrinting();
                    if (!bCom && !bLRA) {
                        if (!bDFR && !bTra) {
                            System.out.println(aCoverage);
                        } else {
                            System.out.println("Coverage: " + aCoverage);
                        }
                    } else {
                        System.out.println("Activity coverage: " + aCoverage);
                    }
                    Utils.restorePrinting();
                }
            }
            if (bLRA) {
                double ALRA = eventLogOps.getLRA(EventDataEnum.ACTIVITY);
                System.out.println("Log Representativeness Approximation: " + ALRA);
                if (bSilent) {
                    Utils.forcePrinting();
                    if (!bCov && !bCom) {
                        if (!bDFR && !bTra) {
                            System.out.println(ALRA);
                        } else {
                            System.out.println("LRA: " + ALRA);
                        }
                    } else {
                        System.out.println("ALRA: " + ALRA);
                    }
                    Utils.restorePrinting();
                }
            }
            System.out.println();
        }

        if (bDFR) {
            System.out.println("------------------- Directly-follows-relation-based Analysis -------------------");
            DFRelationOps dfRelationOps = new DFRelationOps(eventLog);
//            System.out.println("Distinct DF-relations: " + dfRelationOps.getDistinctEventData());
            System.out.println("Number of distinct DF-relations: " + dfRelationOps.getDistinctEventDataCount());
            System.out.println("Number of total DF-relations: " + dfRelationOps.getEventDataCount());
            if (bCom) {
                double dfCompleteness = eventLogOps.getCompleteness(EventDataEnum.DF_RELATION);
                System.out.println("Completeness: " + dfCompleteness);
                if (bSilent) {
                    Utils.forcePrinting();
                    if (!bCov && !bLRA) {
                        if (!bAct && !bTra) {
                            System.out.println(dfCompleteness);
                        } else {
                            System.out.println("Completeness: " + dfCompleteness);
                        }
                    } else {
                        System.out.println("DF-relation completeness: " + dfCompleteness);
                    }
                    Utils.restorePrinting();
                }
            }
            if (bCov) {
                double dfCoverage = eventLogOps.getCoverage(EventDataEnum.DF_RELATION);
                System.out.println("Coverage: " + dfCoverage);
                if (bSilent) {
                    Utils.forcePrinting();
                    if (!bCom && !bLRA) {
                        if (!bAct && !bTra) {
                            System.out.println(dfCoverage);
                        } else {
                            System.out.println("Coverage: " + dfCoverage);
                        }
                    } else {
                        System.out.println("DF-relation coverage: " + dfCoverage);
                    }
                    Utils.restorePrinting();
                }
            }
            if (bLRA) {
                double DLRA = eventLogOps.getLRA(EventDataEnum.DF_RELATION);
                System.out.println("Log Representativeness Approximation: " + DLRA);
                if (bSilent) {
                    Utils.forcePrinting();
                    if (!bCov && !bCom) {
                        if (!bAct && !bTra) {
                            System.out.println(DLRA);
                        } else {
                            System.out.println("LRA: " + DLRA);
                        }
                    } else {
                        System.out.println("DFR-LRA: " + DLRA);
                    }
                    Utils.restorePrinting();
                }
            }
            System.out.println();
        }

        if (bTra) {
            System.out.println("----------------------------- Trace-based Analysis -----------------------------");
            TraceOps traceOps = new TraceOps(eventLog);
//            System.out.println("Distinct traces: " + traceOps.getDistinctEventData());
            System.out.println("Number of distinct traces: " + traceOps.getDistinctEventDataCount());
            System.out.println("Number of total traces: " + traceOps.getEventDataCount());
            if (bCom) {
                double tCompleteness = eventLogOps.getCompleteness(EventDataEnum.TRACE);
                System.out.println("Completeness: " + tCompleteness);
                if (bSilent) {
                    Utils.forcePrinting();
                    if (!bCov && !bLRA) {
                        if (!bDFR && !bAct) {
                            System.out.println(tCompleteness);
                        } else {
                            System.out.println("Completeness: " + tCompleteness);
                        }
                    } else {
                        System.out.println("Trace completeness: " + tCompleteness);
                    }
                    Utils.restorePrinting();
                }
            }
            if (bCov) {
                double tCoverage = eventLogOps.getCoverage(EventDataEnum.TRACE);
                System.out.println("Coverage: " + tCoverage);
                if (bSilent) {
                    Utils.forcePrinting();
                    if (!bCom && !bLRA) {
                        if (!bDFR && !bAct) {
                            System.out.println(tCoverage);
                        } else {
                            System.out.println("Coverage: " + tCoverage);
                        }
                    } else {
                        System.out.println("Trace coverage: " + tCoverage);
                    }
                    Utils.restorePrinting();
                }
            }
            if (bLRA) {
                double TLRA = eventLogOps.getLRA(EventDataEnum.TRACE);
                System.out.println("Log Representativeness Approximation: " + TLRA);
                if (bSilent) {
                    Utils.forcePrinting();
                    if (!bCov && !bCom) {
                        if (!bDFR && !bAct) {
                            System.out.println(TLRA);
                        } else {
                            System.out.println("LRA: " + TLRA);
                        }
                    } else {
                        System.out.println("TLRA: " + TLRA);
                    }
                    Utils.restorePrinting();
                }
            }
        }
    }

}

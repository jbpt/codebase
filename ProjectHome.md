Latest version 0.2.429 - download from [here](https://drive.google.com/folderview?id=0B3r4IkZX6MFBRzdEVXdfZjZQNEk&usp=drive_web).

# Business Process Technologies 4 Java #

The jBPT code library is a compendium of process technologies, i.e., a concise but comprehensive collection of techniques for process model analysis that support research on the design, execution, and evaluation of processes. The idea originated from observations on the evolution of process-related research disciplines. The library offers a broad range of basis analysis and utility functionality and, due to its open publishing model, can easily be extended.

### The project includes: ###
  * Directed graph, undirected graph, directed multi graph, undirected multi graph
  * Directed hypergraph, undirected hypergraph, directed multi hypergraph, undirected multi hypergraph
  * Petri nets
  * Event-driven Process Chains (EPC) object model
  * Flexible Process Graphs (FPG) object model - hypergraph-based process formalism
  * Techniques for workflow graph parsing:
    * The tree of the biconnected components
    * The tree of the triconnected components (SPQR-tree)
    * The Refined Process Structure Tree (RPST)
  * Techniques for net system unfolding:
    * Unfolding (maximal branching process)
    * McMillan adequate order
    * Esparza adequate order for arbitrary net systems
    * Esparza total adequate order for safe net systems
    * Proper complete prefix unfolding for structuring
  * Theory of behavioral profiles:
    * Behavioral profiles
    * Causal behavioral profiles


## Latest News: ##
  * 8 December 2014 - jBPT version 0.2.429 released. In this version: improved NetSystem Graphviz (dot) serialization, fixes to the PNML serializer implementation of Automaton, improvements of tree data structures, minor bug fixes.
  * 15 June 2013 - new experiments on net system [untangling](http://code.google.com/p/jbpt/downloads/detail?name=UntanglingsExperimentJun13.zip) and [querying](http://code.google.com/p/jbpt/downloads/detail?name=QueryingExperimentJun13.zip) published in the Downloads section.
  * 11 May 2013 - jBPT version 0.2.393 released. The new release includes more tests, minor fix of the RPST implementation, first version of GED (graph edit distance) algorithm, improvements of core Petri net classes, and initial implementation of the results of the theory of untanglings of net systems.
  * 5 April 2013 - A paper on the mission of the jBPT library will be published at [CAiSE Forum](http://www.pros.upv.es/index.php/en/call-for-papers-caise2013/caise-forum). The paper can be downloaded [here](http://eprints.qut.edu.au/59637/).
  * 31 March 2013 - Published an [experiment](https://code.google.com/p/jbpt/downloads/detail?name=UntanglingsExperiment.zip) on the reduction-based construction of representative untanglings of net systems.
  * 28 January 2013 - jBPT version 0.2.363 released. The new release includes more tests, as well as new functionality on runs and state space of Petri net systems, and an implementation of an algorithm for computation of minimal distances between all vertices of a directed graph.
  * 1 November 2012 - jBPT version 0.2.348 released; from now on with documentation.
  * 1 November 2012 - jBPT is now split into several modules. The inter-module dependencies are configured for both Maven and Eclipse.
  * 19 September 2012 - jBPT version 0.2.314 released. Twice more functionality in the twice smaller library!
  * 18 September 2012 - jBPT is now prepared for Eclipse to be imported either as a Maven or as standard Java project. Please never check in your local .classpath and/or .project file.

The complete list of news/announcements is available [here](News.md).

## Publications: ##

  * Artem Polyvyanyy and Matthias Weidlich.<br> <a href='http://eprints.qut.edu.au/59637/'>Towards a Compendium of Process Technologies: The jBPT Library for Process Model Analysis</a>.<br>Proceedings of the Forum of the 25th International Conference on Advanced Information Systems Engineering (CAiSE Forum), Valencia, Spain, 2013.</li></ul>

## Friend projects: ##
  * `bpstruct` - a tool for structuring process models with concurrency. http://code.google.com/p/bpstruct/
  * `apromore` - advanced process model repository. http://code.google.com/p/apromore/
  * `promnicat` - a collection and analysis famework for process model collections. http://code.google.com/p/promnicat/
  * Please contact us in case you feel you need to be in this list ;)

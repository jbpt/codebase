# Overview #

This page shall show the usage of the algorithms implemented in jBPT using simple example snippets.

## Process Graphs ##

### How to create a process graph ###

We create a process graph with the following structure
```
         -- t3 --- t4 --
         |             |
   t1 -- s2 ---------- j5 -- t9
         |             |    
         |_ s6 --- j7 _|   
            |_ t8 _|         

```
```
// Create the process graph
Process p = new Process();

// Create the tasks
Task t1 = new Task("1");
Task t3 = new Task("3");
Task t4 = new Task("4");
Task t8 = new Task("8");
Task t9 = new Task("9");

// Add tasks to process graph
p.addTask(t1);
p.addTask(t3);
p.addTask(t4);
p.addTask(t8);
p.addTask(t9);

// Create gateways
Gateway s2 = new Gateway(GatewayType.XOR, "2");
Gateway s6 = new Gateway(GatewayType.AND, "6");
Gateway j7 = new Gateway(GatewayType.AND, "7");
Gateway j5 = new Gateway(GatewayType.XOR, "5");

// Add gateways to process graph
p.addGateway(s2);
p.addGateway(s6);
p.addGateway(j7);
p.addGateway(j5);

// Add control flow edges
p.addControlFlow(t1, s2);
p.addControlFlow(s2, t3);
p.addControlFlow(s2, s6);
p.addControlFlow(s2, j5);
p.addControlFlow(t3, t4);
p.addControlFlow(t4, j5);
p.addControlFlow(s6, j7);
p.addControlFlow(s6, t8);
p.addControlFlow(t8, j7);
p.addControlFlow(j7, j5);
p.addControlFlow(j5, t9);
```

### How to check some basic properties of a process graph ###

We want to know whether a process graph has cycles and how many nodes have no predecessors or successors, respectively.

```

/* Create an algorithms object for a process graph, which 
 * is a directed graph defined with nodes and control flow
 */
DirectedGraphAlgorithms<ControlFlow, Node> dga = new DirectedGraphAlgorithms<ControlFlow, Node>();

/*
 * Assume that p is an object of type Process. Then, we use the
 * algorithms object to check the properties.
 */
System.out.println("Process graph has cycles: " + dga.hasCycles(p));
System.out.println("Process graph has " + dga.getInputVertices(p).size() + " nodes without predecessors.");
System.out.println("Process graph has " + dga.getOutputVertices(p).size() + " nodes without successors.");
```

### How to create the RPST of a process graph ###

We want to create the Refined Process Structure Tree of a process graph.

```
/*
 * Assume that p is an object of type Process. 
 * Then, we create the RPST as follows.
 */
RPST<ControlFlow,Node> rpst = new RPST<ControlFlow,Node>(p);
System.out.println(rpst);
```

## Petri Nets ##

### How to create a Petri net ###

We create a simple workflow net with one initial place, one final place, and four transitions. Transitions b and c may be enabled concurrently.

```
// Create the net
PetriNet net1 = new PetriNet();

// Create the transitions with according labels
Transition a = new Transition("a");
Transition b = new Transition("b");
Transition c = new Transition("c");
Transition d = new Transition("d");

// Add transitions to net
net1.addNode(a);
net1.addNode(b);
net1.addNode(c);
net1.addNode(d);

// Create places
Place p1 = new Place("1");
Place p2 = new Place("2");
Place p3 = new Place("3");
Place p4 = new Place("4");
Place p5 = new Place("5");
Place p6 = new Place("6");

// Set one token as the initial marking for this place
p1.setTokens(1);

// Add places to net 
net1.addNode(p1);
net1.addNode(p2);
net1.addNode(p3);
net1.addNode(p4);
net1.addNode(p5);
net1.addNode(p6);

// Add control flow edges
net1.addFlow(p1, a);
net1.addFlow(a, p2);
net1.addFlow(a, p3);
net1.addFlow(p2, b);
net1.addFlow(p3, c);
net1.addFlow(b, p4);
net1.addFlow(c, p5);
net1.addFlow(p4, d);
net1.addFlow(p5, d);
net1.addFlow(d, p6);
```

A Petri net object carries information on certain properties that are satisfied by the net. If those are used frequently, they shall not be computed each time they are needed. Therefore, the class CachePetriNet extends the standard Petri net model by some caching routines. When using this class make sure to call _invalidateCache_ whenever the structure or the marking of the net changes. For the methods _addNode_ and _addFlow_ this is done automatically.

```
/* 
 * Create a Petri net that caches basic properties.
 */
PetriNet net = new CachePetriNet();
```


## How to check some basic properties of a Petri net ##

Let us check whether a given Petri net has cycles and whether it belongs to certain structural classes of Petri nets.

```
/* 
 * Create an algorithms object for a Petri net, which 
 * is a directed graph defined with Petri net nodes and Petri net flows
 */
DirectedGraphAlgorithms<Flow, Node> dga = new DirectedGraphAlgorithms<Flow, Node>();

/*
 * Assume that n is an object of type PetriNet. 
 */
System.out.println("Petri net has cycles: " + dga.hasCycles(n));

/*
 * The Petri net related properties are accessed directly at the Petri net object. 
 */
System.out.println("Petri net is free-choice: " + n.isFreeChoice());
System.out.println("Petri net is extended free-choice: " + n.isExtendedFreeChoice());
System.out.println("Petri net is S-net: " + n.isSNet());
System.out.println("Petri net is T-net: " + n.isTNet());
System.out.println("Petri net is workflow net: " + n.isWFNet());
```

## How to derive behavioural profiles of a Petri net ##

There are different methods for the computation of behavioural profiles of a Petri net system. These methods impose different assumptions on the Petri net structure and its behavioural properties.

```
/*
 * Assume that n is an object of class Petri net that is free-choice, sound, and has workflow structure.
 * Then, we compute the (causal) behavioural profile with a methods that leverages the 
 * RPST of the Petri net as follows.
 */
BehaviouralProfile<PetriNet, Node> bp = BPCreatorTree.getInstance().deriveBehaviouralProfile(n);
CausalBehaviouralProfile<PetriNet, Node> cbp = CBPCreatorTree.getInstance().deriveCausalBehaviouralProfile(n);

/*
 * Assume that n is an object of class Petri net that is free-choice, sound, and has workflow structure.
 * As an alternative, we use an approach that leverages the concurrency relation and the flow dependencies.
 * Note that this approach applied for the causal behavioural profile also requires that the Petri net 
 * is acyclic or an S-net. This assumption is not required if only the (non-causal) behavioural profile
 * is computed.
 */
BehaviouralProfile<PetriNet, Node> bp2 = BPCreatorNet.getInstance().deriveBehaviouralProfile(n);
CausalBehaviouralProfile<PetriNet, Node> cbp2 = CBPCreatorNet.getInstance().deriveCausalBehaviouralProfile(n);

/*
 * Finally, if n is an object of class Petri net that is bounded, we may use a methods based on 
 * Petri net unfolding. It is way more costly than the aforementioned approaches, but assumes only
 * boundedness.
 */
BehaviouralProfile<PetriNet, Node> bp3 = BPCreatorUnfolding.getInstance().deriveBehaviouralProfile(n);
CausalBehaviouralProfile<PetriNet, Node> cbp3 = CBPCreatorUnfolding.getInstance().deriveCausalBehaviouralProfile(n);
```

The classes for behavioural profiles and causal behavioural profiles offers the following methods as an API.

```
/*
 * Assume that bp / cbp is a behavioural profile / causal behavioural profile, and x and y 
 * are nodes of a Petri net over which the behavioural profiles has been defined.
 */
System.out.println("X and Y are exclusive: " + bp.areExclusive(x,y));
System.out.println("X and Y are in strict order: " + bp.areInOrder(x,y));
System.out.println("X and Y are interleaving: " + bp.areInterleaving(x,y));

System.out.println("X and Y has the following relation: " + bp.getRelationForNodes(x,y));
System.out.println("X and Y has the following relation: " +
BehaviouralProfile.getSymbolForRelation(bp.getRelationForNodes(x,y)));

System.out.println("X and Y are co-occurring: " + cbp.areCooccurring(x,y));
```
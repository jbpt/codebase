package org.jbpt.pm.mspd.model;

import java.util.*;

class DFA {
    private final Set<String> states;
    private final String startState;
    private final Set<String> acceptStates;
    private final Map<String, Map<Character, String>> transitionFunction;

    public DFA(Set<String> states, String startState, Set<String> acceptStates, 
               Map<String, Map<Character, String>> transitionFunction) {
        this.states = states;
        this.startState = startState;
        this.acceptStates = acceptStates;
        this.transitionFunction = transitionFunction;
    }

    public boolean accepts(String input) {
        String currentState = startState;
        for (char symbol : input.toCharArray()) {
            currentState = transitionFunction.get(currentState).get(symbol);
        }
        return acceptStates.contains(currentState);
    }

    public void toDFG() {
        System.out.println("Data Flow Graph Representation:");
        for (String state : states) {
            System.out.print("State " + state + " transitions: ");
            for (Map.Entry<Character, String> entry : transitionFunction.get(state).entrySet()) {
                System.out.print("[" + entry.getKey() + " -> " + entry.getValue() + "] ");
            }
            System.out.println();
        }
    }
}

public class DFAToDFG {
    public static void main(String[] args) {
        Set<String> states = new HashSet<>(Arrays.asList("S0", "S1", "S2"));
        String startState = "S0";
        Set<String> acceptStates = new HashSet<>(Collections.singletonList("S2"));

        Map<String, Map<Character, String>> transitionFunction = new HashMap<>();
        transitionFunction.put("S0", Map.of('0', "S0", '1', "S1"));
        transitionFunction.put("S1", Map.of('0', "S2", '1', "S1"));
        transitionFunction.put("S2", Map.of('0', "S0", '1', "S1"));

        DFA dfa = new DFA(states, startState, acceptStates, transitionFunction);
        
        // Test the DFA
        String testInput = "1101";
        System.out.println("Input: " + testInput + " Accepted: " + dfa.accepts(testInput));

        // Convert to DFG
        dfa.toDFG();
    }
}
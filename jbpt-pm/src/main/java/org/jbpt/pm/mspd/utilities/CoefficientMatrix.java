package org.jbpt.pm.mspd.utilities;

import java.util.*;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class CoefficientMatrix {
	public static Map<String,Double> findCoefficient(List<String> formulates) {
		List<String> variables = new ArrayList<String>();
		Map<String,Double> funcList = new HashMap<>();
		double[] constants = new double[formulates.size()];
    	List<double[]> coefficients = extractEquations(formulates,constants,variables);
    	double [][] cof = new double[formulates.size()][coefficients.get(0).length];
    	int i=0;
    	for(double row[] : coefficients)
    		cof[i++]= row;
    	List<Double> cof1= solveProblems(cof,constants);
    	List<String> list = extractVariables(variables);
    	for(int j=0;j<cof1.size();j++)
    	{
    		funcList.put(list.get(j), cof1.get(j));
    	}
    	return funcList;
	}
    public static void main(String[] args) {
    	List<String> formulates = new ArrayList<String>();
    
    
    	
    	
    	formulates.add("f(I,n1)=f(n1,n2)+f(n1,n3)");
    	formulates.add("f(n1,n2)+f(n2,n2)=f(n2,n2)+f(n2,n3)");
    	formulates.add("f(n1,n3)+f(n2,n3)+f(n5,n3)=f(n3,n5)+f(n3,O)");
    	formulates.add("f(n3,n5)=f(n5,n3)+f(n5,O)");
    	formulates.add("f(n3,O)+f(n5,O)=1493.0");
    	formulates.add("f(n1,n2)=f(n2,n3)");
    	formulates.add("1.0f(n1,n2)=0.17f(I,n1)");
    	formulates.add("1.0f(n1,n3)=0.83f(I,n1)");
    	formulates.add("1.0f(n2,n2)=0.17f(n1,n2)+0.17f(n2,n2)");
    	formulates.add("1.0f(n2,n3)=0.83f(n1,n2)+0.83f(n2,n2)");
     	formulates.add("1.0f(n3,n5)=0.5f(n1,n3)+0.5f(n2,n3)+0.5f(n5,n3)");
    	formulates.add("1.0f(n3,O)=0.5f(n1,n3)+0.5f(n2,n3)+0.5f(n5,n3)");
    	formulates.add("1.0f(n5,n3)=0.8f(n3,n5)");
    	formulates.add("1.0f(n5,O)=0.2f(n3,n5)");
    	formulates.add("1493.0=f(I,n1)");
        Map<String,Double> x =  CoefficientMatrix.findCoefficient(formulates);

         printMatrix(x);
        // Print the constants array
       // printConstants(constants);*/
       
    }
    public static List<Double> solveProblems(double [][]coefficients, double[] constants) {
    	 RealMatrix A = MatrixUtils.createRealMatrix(coefficients);
	        RealVector b = MatrixUtils.createRealVector(constants);
	        // Solve the system of equations using least squares
	        DecompositionSolver solver = new QRDecomposition(A).getSolver();
	        RealVector solution = solver.solve(b);
	        // Output the results

	        List<Double> functions= new ArrayList<Double>();
	        for (int i = 0; i < solution.getDimension(); i++) {
	        	functions.add(solution.getEntry(i));
	          //  System.out.printf("f(%d) = %.2f%n", i + 1, solution.getEntry(i));
	        }
	        return functions;
    }
    public static List<String> extractVariables(List<String> functionCalls) {
        List<String> variables = new ArrayList<>();
        
        for (String call : functionCalls) {
            // Remove the function name and parentheses
            String params = call.substring(call.indexOf('(') + 1, call.indexOf(')'));
            // Split the parameters by comma and trim whitespace
            String[] args = params.split(",");
            // Add the trimmed arguments to the list
            variables.add(args[0].trim() + "," + args[1].trim());
        }
        
        return variables;
    }
    public static List<double[]> extractEquations(List<String> equations,double[] constants,List<String> variableList) {
    	 // Step 1: Extract all unique variables
        Set<String> variableSet = new HashSet<>();
        for (String equation : equations) {
            extractVariables(equation, variableSet);
        }

        // Convert the set to a list for indexing
        List<String> variables = new ArrayList<>(variableSet);
        
        // Step 2: Create the coefficient matrix and constant array
        List<double[]> coefficients = new ArrayList<>();

        for (int i = 0; i < equations.size(); i++) {
        	
            String equation = equations.get(i);
            String[] parts = equation.split("=");
            String leftSide = parts[0].trim();
            String rightSide = parts[1].trim();

            // Create a map to hold coefficients for the current equation
            Map<String, Double> coeffMap = new HashMap<>();

            // Check if either side has a constant
            boolean leftHasConstant = hasConstant(leftSide);
            boolean rightHasConstant = hasConstant(rightSide);

            // Process both sides
            processSide(leftSide, coeffMap, 1);
            processSide(rightSide, coeffMap, leftHasConstant ? 1.0 : -1.0);
            // Store constant value if found
            if (leftHasConstant) {
                constants[i] = extractConstant(leftSide);
            } else if (rightHasConstant) {
                constants[i] = extractConstant(rightSide);
            } else {
                constants[i] = 0; // No constant found
            }

            // Add coefficients to the list
            coefficients.add(createCoefficientArray(variables, coeffMap));
           
        }
        for(String var : variables)
        	variableList.add(var);
        // Print the coefficient matrix
    
    	return coefficients;
    }
    private static void extractVariables(String equation, Set<String> variableSet) {
        String[] parts = equation.split("[=+]");
        for (String part : parts) {
            part = part.trim();
            if (part.contains("f(")) {
                String variable = part.substring(part.indexOf("f("));
                variableSet.add(variable);
            }
        }
    }

    private static boolean hasConstant(String side) {
        String[] terms = side.split("\\+");
        for (String term : terms) {
            term = term.trim();
            if (term.matches("^[0-9]*\\.?[0-9]+$")) { // Check if it's a constant
                return true;
            }
        }
        return false;
    }

    private static double extractConstant(String side) {
        String[] terms = side.split("\\+");
        for (String term : terms) {
            term = term.trim();
            if (term.matches("^[0-9]*\\.?[0-9]+$")) { // Check if it's a constant
                return Double.parseDouble(term);
            }
        }
        return 0; // Default return if no constant found
    }

    private static void processSide(String side, Map<String, Double> coeffMap, double sign) {
        String[] terms = side.split("\\+");

        for (String term : terms) {
            term = term.trim();
            if (term.matches("^[0-9]*\\.?[0-9]+$")) { // Skip constant terms
                continue;
            }

            String[] parts = term.split("f\\(");
            if (parts.length > 1) {
                String variable = "f(" + parts[1];
                double coefficient = parts[0].isEmpty() ? 1.0 : Double.parseDouble(parts[0]);
                coeffMap.put(variable, coeffMap.getOrDefault(variable, 0.0) + sign * coefficient);
            }
        }
    }

    private static double[] createCoefficientArray(List<String> variables, Map<String, Double> coeffMap) {
        double[] row = new double[variables.size()]; // No constant term
        Arrays.fill(row, 0.0);

        for (Map.Entry<String, Double> entry : coeffMap.entrySet()) {
            String var = entry.getKey();
            double coeff = entry.getValue();

            int index = variables.indexOf(var);
            if (index != -1) {
                row[index] = coeff;
            }
        }
        return row;
    }

    private static void printMatrix(Map<String,Double> list) {
       for(String s:list.keySet())
       {
    	   System.out.println("f("+s+") --->"+list.get(s));
       }
    }

    private static void printConstants(double[] constants) {
        System.out.println("Constants Array:");
        System.out.print("[ ");
        for (double constant : constants) {
            System.out.printf("%.2f ", constant);
        }
        System.out.println("]");
    }
}
package org.jbpt.pm.mspd.utilities;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class MatrixConverter {
	  public static List<Double> extractCoefficients(String inputStrings[]) {
	        List<Double> coefficients = new ArrayList<>();
	        // Regular expression to match coefficients
	        Pattern pattern = Pattern.compile("(\\d*\\.\\d+|\\d+)(?=\\s*[f\\(])");

	        for (String str : inputStrings) {
	            Matcher matcher = pattern.matcher(str);
	            while (matcher.find()) {
	                coefficients.add(Double.parseDouble(matcher.group(1)));
	            }
	        }
	        return coefficients;
	    }

	public static void main(String[] args) {
		double[][] coefficients = {
				{0,0,0,0,0,0,0,1,0},
				{-1,0,0,0,0,0,0,0.17,0},
				{0,0,-1,0,0,0,0,0.83,0},
				{-0.17,0,0,0.83,0,0,0,0,0},
				{0,0.5,0.5,0,0.5,-1,0,0,0},
				{0,0.5,0.5,0,0.5,0,0,0,-1},
				{0,0,0,0,-1,0.8,0,0,0},
				{0,0,0,0,0,0.2,-1,0,0},
				{0,0,0,0,0,0,1,0,1}
	        };

	        // Constants vector (b)
	        double[] constants = {
	            1493.0,0,0,0,0,0,0,0,1493.0
	        };

	        // Create the matrices
	       
	        String[] equations = {
	                "1493.0=f(i,n1)",
	                "f(i,n1)=f(n1,n2)+f(n1,n3)",
	                "f(n1,n2)=f(n2,n3)",
	                "f(n1,n3)+f(n2,n3)+f(n5,n3)=f(n3,o)+f(n3,n5)",
	                "f(n3,n5)=f(n5,n3)+f(n5,o)",
	                "f(n3,o)+f(n5,o)=1493.0",
	                "0.17f(i,n1)=f(n1,n2)",
	                "0.83f(i,n1)=f(n1,n3)",
	                "0.17 (f(n1,n2)+f(n2,n2))=f(n2,n2)",
	                "0.83 (f(n1,n2)+f(n2,n2))=f(n2,n3)",
	                "0.50 (f(n1,n3)+f(n2,n3)+f(n5,n3))=f(n3,n5)",
	                "0.50 (f(n1,n3)+f(n2,n3)+f(n5,n3))=f(n3,o)",
	                "0.80f(n3,n5)=f(n5,n3)",
	                "0.20f(n3,n5)=f(n5,o)"
	            };

	        Set<String> variables = new HashSet<>();
	        Pattern pattern = Pattern.compile("f\\(([^)]+)\\)");

	        for (String equation : equations) {
	            Matcher matcher = pattern.matcher(equation);
	            while (matcher.find()) {
	                // Add the entire function call as a variable
	                String variable = "f(" + matcher.group(1) + ")";
	                variables.add(variable);
	            }
	        }

	        System.out.println("Extracted Variables: " + variables);

	        List<Double> coefficients1 = extractCoefficients(equations);
	        System.out.println(coefficients);
	        }
	    
}
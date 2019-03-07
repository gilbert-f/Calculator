package calculator.ast;

import calculator.interpreter.Environment;
import calculator.errors.EvaluationError;
import datastructures.concrete.DoubleLinkedList;
import datastructures.concrete.dictionaries.ArrayDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;

/**
 * All of the static methods in this class are given the exact same parameters for
 * consistency. You can often ignore some of these parameters when implementing your
 * methods.
 *
 * Some of these methods should be recursive. You may want to consider using public-private
 * pairs in some cases.
 */
public class ExpressionManipulators {
    /**
     * Takes the given AstNode node and attempts to convert it into a double.
     *
     * Returns a number AstNode containing the computed double.
     *
     * @throws EvaluationError  if any of the expressions contains an undefined variable.
     * @throws EvaluationError  if any of the expressions uses an unknown operation.
     */
    public static AstNode toDouble(Environment env, AstNode node) {
        return new AstNode(toDoubleHelper(env.getVariables(), node));
    }

    private static double toDoubleHelper(IDictionary<String, AstNode> variables, AstNode node) {
        // There are three types of nodes, so we have three cases.
        if (node.isNumber()) {
            return node.getNumericValue(); // just return the numeric value
        } else if (node.isVariable()) {
            if (!variables.containsKey(node.getName())) {
                // If the expression contains an undefined variable, we give up.
                throw new EvaluationError("Undefined variable: " + node.getName());
            } else { // replace the variable node with the corresponding numeric node
            		return toDoubleHelper(variables, variables.get(node.getName())); 
            }
        } else {
            String name = node.getName();
            // most operators have two children, most functions have only one
            double child1value = toDoubleHelper(variables, node.getChildren().get(0));
            double child2value = 0;
            if (node.getChildren().size() == 2)
            		child2value = toDoubleHelper(variables, node.getChildren().get(1));
            
            if (name.equals("toDouble")) {
            		return child1value; // ignore first node
            } else if (name.equals("+")) {
                return child1value + child2value;
            } else if (name.equals("-")) {
            		return child1value - child2value;
            } else if (name.equals("*")) {
            		return child1value * child2value;
            } else if (name.equals("/")) {
            		return child1value / child2value;
            } else if (name.equals("^")) {
            		return Math.pow(child1value, child2value);
            } else if (name.equals("negate")) {
            		return -child1value;
            } else if (name.equals("sin")) {
            		return Math.sin(child1value);
            } else if (name.equals("cos")) {
            		return Math.cos(child1value);
            } else if (name.equals("tan")) {
        			return Math.tan(child1value);
            } else if (name.equals("csc")) {
        			return 1/Math.sin(child1value);
            } else if (name.equals("sec")) {
        			return 1/Math.cos(child1value);
            } else if (name.equals("cot")) {
        			return 1/Math.tan(child1value);
            } else if (name.equals("asin")) {
	        		return Math.asin(child1value);
	        } else if (name.equals("acos")) {
	        		return Math.acos(child1value);
	        } else if (name.equals("atan")) {
	    			return Math.atan(child1value);
	        } else if (name.equals("sqrt")) {
        			return Math.sqrt(child1value);
            }  else if (name.equals("cbrt")) {
        			return Math.cbrt(child1value);
            } else if (name.equals("toRadians")) {
        			return Math.toRadians(child1value);
            } else if (name.equals("toDegrees")) {
        			return Math.toDegrees(child1value);
            } else if (name.equals("log")) {
        			return Math.log(child1value);
            } else if (name.equals("log10")) {
        			return Math.log10(child1value);
            } else if (name.equals("exp")) {
        			return Math.exp(child1value);
            } else if (name.equals("abs")) {
            		if (child1value < 0 )
            			return -child1value;
            		else
            			return child1value;
            } else {
                throw new EvaluationError("Unknown operation: " + name);
            }
        }
    }

    /**
     * Recursive method that takes the given AstNode node and attempts to simplify the AST.
     * Returns a simplified AstNode.
     */
    public static AstNode simplify(Environment env, AstNode node) {
    		IDictionary<String, AstNode> variables = env.getVariables();
        if (node.isNumber()) {
            return node; // base case: return numeric node
        } else if (node.isVariable()) {
            if (!variables.containsKey(node.getName())) {
                return node; // base case: return variable node with no value in dictionary
            } else {
            		return simplify(env, variables.get(node.getName())); // replace the variable node with corresponding numeric node
            }
        } else {
            String name = node.getName();
            // same as the one in toDoubleHelper
            AstNode child1 = simplify(env, node.getChildren().get(0));
            AstNode child2 = null;
            if (node.getChildren().size() == 2)
            		child2 = simplify(env, node.getChildren().get(1));
            
            if (name.equals("simplify")) {
            		return child1; // ignore first node
            } else if (name.equals("+")) {
            		if (child1.isNumber() && child2.isNumber()) // only if the cildren node(s) is/are numeric
            			return new AstNode(child1.getNumericValue() + child2.getNumericValue());
            } else if (name.equals("-")) {
        			if (child1.isNumber() && child2.isNumber())
        				return new AstNode(child1.getNumericValue() - child2.getNumericValue());
            } else if (name.equals("*")) {
	        		if (child1.isNumber() && child2.isNumber())
	        			return new AstNode(child1.getNumericValue() * child2.getNumericValue());
            } else if (name.equals("^")) {
            		if (child1.isNumber() && child2.isNumber())
            			return new AstNode(Math.pow(child1.getNumericValue(), child2.getNumericValue()));
            } else if (name.equals("negate")) {
	        		if (child1.isNumber())
	        			return new AstNode(-child1.getNumericValue());
            } else if (name.equals("abs")) {
	        		if (child1.getNumericValue() < 0 )
	        			return new AstNode(-child1.getNumericValue());
	        		else
	        			return new AstNode(child1.getNumericValue());
	        }
            // if the children node(s) are/is not numeric
            int nodeChildrenSize = node.getChildren().size();
	    		if (nodeChildrenSize == 2) 
	    			node.getChildren().remove();
	    		node.getChildren().remove();
	    		node.getChildren().add(child1);
	    		if (nodeChildrenSize == 2) 
	    			node.getChildren().add(child2);
	    		return new AstNode(node.getName(), node.getChildren());	
        }
    }

    /**
     * Expected signature of plot:
     *
     * >>> plot(exprToPlot, var, varMin, varMax, step)
     *
     * Example 1:
     *
     * >>> plot(3 * x, x, 2, 5, 0.5)
     *
     * This command will plot the equation "3 * x", varying "x" from 2 to 5 in 0.5
     * increments. In this case, this means you'll be plotting the following points:
     *
     * [(2, 6), (2.5, 7.5), (3, 9), (3.5, 10.5), (4, 12), (4.5, 13.5), (5, 15)]
     *
     * ---
     *
     * Another example: now, we're plotting the quadratic equation "a^2 + 4a + 4"
     * from -10 to 10 in 0.01 increments. In this case, "a" is our "x" variable.
     *
     * >>> c := 4
     * 4
     * >>> step := 0.01
     * 0.01
     * >>> plot(a^2 + c*a + a, a, -10, 10, step)
     *
     * ---
     *
     * @throws EvaluationError  if any of the expressions contains an undefined variable.
     * @throws EvaluationError  if varMin > varMax
     * @throws EvaluationError  if 'var' was already defined
     * @throws EvaluationError  if 'step' is zero or negative
     */
    public static AstNode plot(Environment env, AstNode node) {
    		
    		// simplify the children of node
        AstNode step = simplify(env, node.getChildren().get(4));
        AstNode varMax = simplify(env, node.getChildren().get(3));
        AstNode varMin = simplify(env, node.getChildren().get(2));
        AstNode var = simplify(env, node.getChildren().get(1));
        AstNode exprToPlot = simplify(env, node.getChildren().get(0));
        
        // precondition to check that the exprToPlot has no undefined variables, except var.
        // if there is an undefined variable, an EvaluationError will be thrown in the toDoubleHelper method
        IDictionary<String, AstNode> dict = new ArrayDictionary<>();
        dict.put(var.getName(), new AstNode(1.0));
        toDoubleHelper(dict, exprToPlot);
        // preconditions
        if (!step.isNumber() || !varMax.isNumber() || !varMin.isNumber()) {
        		throw new EvaluationError("undefined variable");
        }
        if (varMin.getNumericValue() > varMax.getNumericValue()) {
        		throw new EvaluationError("varMin > varMax");
        }
        if (!var.isVariable()) {
    			throw new EvaluationError("var is already defined");
        }
        if (step.getNumericValue() <= 0) {
    			throw new EvaluationError("step <= 0");
        }
        
        // lists to store x and y values
        IList<Double> xValues = new DoubleLinkedList<Double>();
        IList<Double> yValues = new DoubleLinkedList<Double>();
        double x = varMin.getNumericValue();
        while(x < varMax.getNumericValue() + step.getNumericValue()) {
        		xValues.add(x);
        		x += step.getNumericValue();
        }
        for (int i = 0; i < xValues.size(); i++) {
        		IDictionary<String, AstNode> xVals = new ArrayDictionary<>();
        		xVals.put(var.getName(), new AstNode(xValues.get(i)));
        		yValues.add(toDoubleHelper(xVals, exprToPlot));
        }
        
        // plot the graph
		env.getImageDrawer().drawScatterPlot("Scatter Plot", var.getName(), "y", xValues, yValues);
        return new AstNode(1); // arbitrary AstNode
    }
}

package apps;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	/**
	 * Expression to be evaluated
	 */
	String expr;                
    
	/**
	 * Scalar symbols in the expression 
	 */
	ArrayList<ScalarSymbol> scalars;   
	
	/**
	 * Array symbols in the expression
	 */
	ArrayList<ArraySymbol> arrays;
    
    /**
     * String containing all delimiters (characters other than variables and constants), 
     * to be used with StringTokenizer
     */
    public static final String delims = " \t*+-/()[]";
    
    /**
     * Initializes this Expression object with an input expression. Sets all other
     * fields to null.
     * 
     * @param expr Expression
     */
    public Expression(String expr) {
        this.expr = expr;
    }

    /**
     * Populates the scalars and arrays lists with symbols for scalar and array
     * variables in the expression. For every variable, a SINGLE symbol is created and stored,
     * even if it appears more than once in the expression.
     * At this time, values for all variables are set to
     * zero - they will be loaded from a file in the loadSymbolValues method.
     */
    public void buildSymbols() {
    		/** COMPLETE THIS METHOD **/
    		scalars = new ArrayList<ScalarSymbol>();
    		arrays = new ArrayList<ArraySymbol>();
    		StringTokenizer stExpr = new StringTokenizer(expr, delims, true);
    		Stack<String> eStack = new Stack<String>();
    		
    		// push tokens onto stack
    		while(stExpr.hasMoreTokens()) {
    			eStack.push(stExpr.nextToken());
    		}
    		
    		String token;
    		while (!eStack.isEmpty()) {
    			token = eStack.pop();
    			if (Character.isDigit(token.charAt(0)) || token.equals("\\t") || token.equals(" ") || token.equals("*")
    					 || token.equals("+") || token.equals("/") || token.equals("-") || token.equals("(") || token.equals(")")
    					 || token.equals("]"))
    				continue;
    			else if (token.equals("[")) { // if open bracket, pop next for array variable
    				ArraySymbol newArray = new ArraySymbol(eStack.pop());
    				if (!arrays.contains(newArray))
    					arrays.add(newArray);
    			} else {	
    				ScalarSymbol newScalar = new ScalarSymbol(token);
    				if (!scalars.contains(newScalar))
    					scalars.add(newScalar);
    			}
    		}
    		printScalars();
    		printArrays();
    }
    
    /**
     * Loads values for symbols in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     */
    public void loadSymbolValues(Scanner sc) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String sym = st.nextToken();
            ScalarSymbol ssymbol = new ScalarSymbol(sym);
            ArraySymbol asymbol = new ArraySymbol(sym);
            int ssi = scalars.indexOf(ssymbol);
            int asi = arrays.indexOf(asymbol);
            if (ssi == -1 && asi == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                scalars.get(ssi).value = num;
            } else { // array symbol
            	asymbol = arrays.get(asi);
            	asymbol.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    String tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    asymbol.values[index] = val;              
                }
            }
        }
    }
    
    
    /**
     * Evaluates the expression, using RECURSION to evaluate subexpressions and to evaluate array 
     * subscript expressions.
     * 
     * @return Result of evaluation
     */
    public float evaluate() {
    		/** COMPLETE THIS METHOD **/
    		// following line just a placeholder for compilation
    		expr = expr.replaceAll("\\s+","");
    		return evaluateRecur(0, expr.length()-1);
    }
    
    private float evaluateRecur(int start, int end) {
		Stack<Float> opStack = new Stack<Float>(); // stack for operands
		Stack<Character> pmStack = new Stack<Character>(); // stack for + and - operators

		for (int i = start; i <= end; i++) {
			char c = expr.charAt(i);
			
			if (c == '[' || expr.charAt(i) == '(') {
				int closingIndex = getClosing(i);
				opStack.push(evaluateRecur(i+1, closingIndex-1));
				i = closingIndex;
			} else if (c == '-' || c == '+') {
				pmStack.push(c);	
			} else if (c == '*' || c == '/') {
				int opIndex = i+1;
				int closingIndex = getClosing(opIndex);
				char opA = expr.charAt(opIndex);
				int closeAdj = -1;
				if (opA == '(' || opA == '[') {
					opIndex++;
					closeAdj = 0;
				}
				switch (c) {
					case '*': {
						opStack.push(opStack.pop() * evaluateRecur(opIndex, closingIndex-1));
						i = closingIndex;
						break;
					}
					case '/': {
						opStack.push(opStack.pop() / evaluateRecur(opIndex, closingIndex-1));
						i = closingIndex;
						break;
					}
				}
				i += closeAdj;
			} else if (Character.isDigit(c)) {
				String s = String.valueOf(c);
				while (i != end && Character.isDigit(expr.charAt(i+1))) {
					s += expr.charAt(i+1);
					i++;
				}
				opStack.push(Float.valueOf(s));
			} else {
				StringTokenizer st = new StringTokenizer(expr.substring(i, end+1), delims, true);
				String tempSym = st.nextToken();
				if (st.hasMoreTokens() && st.nextToken().equals("[")) {
					ArraySymbol asym = new ArraySymbol(String.valueOf(tempSym));
					int opIndex = 0;
					for (int j = i; j < expr.length(); j++) {
						if (expr.charAt(j) == '[') {
							opIndex = j;
							break;
						}
					}
					int closeIndex = getClosing(opIndex);
					int arrIndex = (int) evaluateRecur(opIndex+1, closeIndex-1);
					float val = arrays.get(arrays.indexOf(asym)).values[arrIndex];
					opStack.push(val);
					i = closeIndex;
				} else {
					ScalarSymbol scal = new ScalarSymbol(tempSym);
					float val = scalars.get(scalars.indexOf(scal)).value;
					opStack.push(val);
					// move i to end of variable
					i += tempSym.length()-1;
				}
			}
				
		}
		
		pmStack = reverseStack(pmStack);
		opStack = reverseStack(opStack);
		
		while (!pmStack.isEmpty()) {
			char op = pmStack.pop();
			switch (op) {
				case '+': {
					opStack.push(opStack.pop() + opStack.pop());
					break;
				}
				case '-': {
					opStack.push(opStack.pop() - opStack.pop());
					break;
				}
			}
		}
    		return opStack.pop();
    }
    
    private int getClosing(int start) {
    		char c = expr.charAt(start);
    		char[] arr = expr.toCharArray();
    		Stack<Character> cstack = new Stack<Character>();
    		
    		if (c == '[') {
    			for (int i = start; i < arr.length; i++) {
        			if (arr[i] == '[')
        				cstack.push(arr[i]);
        			else if (arr[i] == ']')
        				cstack.pop();
        			if (cstack.isEmpty())
        				return i;
        		}
    		} else if (c == '(') {
    			for (int i = start; i < arr.length; i++) {
        			if (arr[i] == '(')
        				cstack.push(arr[i]);
        			else if (arr[i] == ')')
        				cstack.pop();
        			if (cstack.isEmpty())
        				return i;
        		}
    		}
    		return start+1;
    }
    
    private static <T> Stack<T> reverseStack(Stack<T> s) {
    		 Stack<T> temp = new Stack<T>();
    		 while (!s.isEmpty()) {
    			 temp.push(s.pop());
    		 }
    		 return temp;
    }
    /**
     * Utility method, prints the symbols in the scalars list
     */
    public void printScalars() {
        for (ScalarSymbol ss: scalars) {
            System.out.println(ss);
        }
    }
    
    /**
     * Utility method, prints the symbols in the arrays list
     */
    public void printArrays() {
    		for (ArraySymbol as: arrays) {
    			System.out.println(as);
    		}
    }

}

package math;

/**
 * This class encapsulates a BigInteger, i.e. a positive or negative integer with 
 * any number of digits, which overcomes the computer storage length limitation of 
 * an integer.
 * 
 */
public class BigInteger {

	/**
	 * True if this is a negative integer
	 */
	boolean negative;
	
	/**
	 * Number of digits in this integer
	 */
	int numDigits;
	
	/**
	 * Reference to the first node of this integer's linked list representation
	 * NOTE: The linked list stores the Least Significant Digit in the FIRST node.
	 * For instance, the integer 235 would be stored as:
	 *    5 --> 3  --> 2
	 */
	DigitNode front;
	
	/**
	 * Initializes this integer to a positive number with zero digits, in other
	 * words this is the 0 (zero) valued integer.
	 */
	public BigInteger() {
		negative = false;
		numDigits = 0;
		front = null;
	}
	
	/**
	 * Parses an input integer string into a corresponding BigInteger instance.
	 * A correctly formatted integer would have an optional sign as the first 
	 * character (no sign means positive), and at least one digit character
	 * (including zero). 
	 * Examples of correct format, with corresponding values
	 *      Format     Value
	 *       +0            0
	 *       -0            0
	 *       +123        123
	 *       1023       1023
	 *       0012         12  
	 *       0             0
	 *       -123       -123
	 *       -001         -1
	 *       +000          0
	 *       
	 * 
	 * @param integer Integer string that is to be parsed
	 * @return BigInteger instance that stores the input integer
	 * @throws IllegalArgumentException If input is incorrectly formatted
	 */
	public static BigInteger parse(String integer) 
	throws IllegalArgumentException {
		
		// error check first character
//		if (!Character.isDigit(digit) && (digit != '-' && digit != '+'))
//			throw new IllegalArgumentException();
		
		// check if String contains 1 invalid character
		if (integer.length() == 1 && !Character.isDigit(integer.charAt(0)))
			throw new IllegalArgumentException();
		
		integer = processString(integer);
		
		// check rest of String
		for (int i = 1; i < integer.length(); i++) {
			if(!Character.isDigit(integer.charAt(i)) || integer.charAt(i) == ' ')
				throw new IllegalArgumentException();
		}
		
		// digit equals first character in String
				int index = 0;
				char digit = integer.charAt(index);
				
		// create new BigInteger and check if negative
		BigInteger b = new BigInteger();
		if (digit == '-') {
			b.negative = true;
			digit = integer.charAt(index++);
		} else if (digit == '+'){
			digit = integer.charAt(index++);
		} else if (!Character.isDigit(digit))	// check if illegal first character
			throw new IllegalArgumentException();
		
		// skip through leading zeroes
		while (integer.charAt(index) == '0' && index != integer.length()-1)
			index++;
		
		// negative 0 correction
		if (integer.charAt(index) == '0')
			b.negative = false;
			
		// set head for BigInteger b
		b.front = new DigitNode(Character.getNumericValue(integer.charAt(index)), null);
		b.numDigits++;
		index++;
		
		// create DigitNode for each remaining character
		while(index < integer.length()) {
			DigitNode newDN = new DigitNode(Character.getNumericValue(integer.charAt(index)), b.front);
			b.front = newDN;
			b.numDigits++;
			index++;
		}
		
		return b;
	}
	
	private static String processString(String s) {
		int index = 0;
		while(s.charAt(index) == ' ' && index != s.length()) {
			index++;
		}
		if (index != s.length())
			s = s.substring(index);
		
		index = s.length() - 1;
		while(s.charAt(index) == ' ' && index >= 0) {
			index--;
		}
		
		if (index != 0)
			s = s.substring(0, index+1);
				
		return s;
	}
	
	/**
	 * Adds an integer to this integer, and returns the result in a NEW BigInteger object. 
	 * DOES NOT MODIFY this integer.
	 * NOTE that either or both of the integers involved could be negative.
	 * (Which means this method can effectively subtract as well.)
	 * 
	 * @param other Other integer to be added to this integer
	 * @return Result integer
	 */
	public BigInteger add(BigInteger other) {
		
		// test if subtract method needed
		if (!(this.negative == true && other.negative == true) && (this.negative == true || other.negative == true))
			return this.subtract(other);
		
		BigInteger sum = new BigInteger();
		DigitNode bigOp, lilOp;
		DigitNode sumLast = null;
		boolean needCarry = false;
		
		// determine which BigInteger has more digits
		if (this.numDigits >= other.numDigits) {
			bigOp = this.front;
			lilOp = other.front;
		} else {
			bigOp = other.front;
			lilOp = this.front;
		}
		
		while(lilOp != null) {
			
			int digitSum = bigOp.digit + lilOp.digit;
			
			// resolve carry
			if (needCarry) 
				digitSum++;
			if (digitSum > 9) {
				digitSum %= 10;
				needCarry = true;
			} else
				needCarry = false;
			
			// create new DigitNode
			DigitNode d = new DigitNode(digitSum, null);
			
				
			if (sum.front == null) {
				sum.front = d;
				sumLast = sum.front;
			}
			else {
				sumLast.next = d;
				sumLast = d;
			}
			
			sum.numDigits++;
			bigOp = bigOp.next;
			lilOp = lilOp.next;
			
			if (bigOp == null && lilOp == null && needCarry)
				sumLast.next = new DigitNode(1, null);
		}
		while(bigOp != null) {
			int digit = bigOp.digit;
			
			// resolve carry
			if (needCarry) 
				digit++;
			if (digit > 9) {
				digit %= 10;
				needCarry = true;
			} else
				needCarry = false;
			
			DigitNode d = new DigitNode((digit), null);
			sumLast.next = d;
			sum.numDigits++;
			sumLast = sumLast.next;
			bigOp = bigOp.next;
			
			if (bigOp == null && needCarry)
				sumLast.next = new DigitNode(1, null);
		}
		
		if(this.negative == true && other.negative == true)
			sum.negative = true;
		
		return sum;
	}
	
	private BigInteger subtract(BigInteger other) {
		boolean needNegative = false;
		DigitNode topOp, botOp; 
//		if (this.negative == true && this.absLargerThan(other) == 1) {
//			topOp = this.front;
//			botOp = other.front;
//			needNegative = true;
//		} else if (this.negative == true && this.absLargerThan(other) == -1) {
//			topOp = other.front;
//			botOp = this.front;
//			needNegative = true;
//		} else if (other.negative == true && this.absLargerThan(other) == -1) {
//			topOp = other.front;
//			botOp = this.front;
//		}
		
		if (this.absLargerThan(other) == 1) {
			topOp = this.front;
			botOp = other.front;
			if (this.negative == true)
				needNegative = true;
		} else if (this.absLargerThan(other) == -1) {
			topOp = other.front;
			botOp = this.front;
			if (other.negative == true)
				needNegative = true;
		} else {
			return BigInteger.parse("0");
		}
		
		boolean needCarry = false;
		
		BigInteger diff = new BigInteger();
		DigitNode diffLast = null;
		
		while (botOp != null) {
			int topDigit = topOp.digit;
			int botDigit = botOp.digit;
			
			if (needCarry) {
				topDigit--;
				needCarry = false;
			}
			
			if (topDigit < botDigit) {
				topDigit += 10;
				needCarry = true;
			}
			
			DigitNode d = new DigitNode(topDigit - botDigit, null);
			topOp = topOp.next;
			botOp = botOp.next;
			
			if (diff.front == null) {
				diff.front = d;
				diffLast = diff.front;
			}
			else {
				if (topOp == null && botOp == null && d.digit == 0) {
					
					break;
				}
					
				diffLast.next = d;
				diffLast = d;
			}
			
			diff.numDigits++;
		}
		
		while (topOp != null) {
			int digit = topOp.digit;
			
			if (needCarry) {
				digit--;
				needCarry = false;
			}
			
			if (digit < 0) {
				digit += 10;
				needCarry = true;
			}
			topOp = topOp.next;
			
			if (topOp == null && digit == 0) {
				break;
			}
			
			DigitNode d = new DigitNode(digit, null);
			diffLast.next = d;
			diffLast = d;
			diff.numDigits++;
		}
		
		if (needNegative)
			diff.negative = true;
		
		return diff;
	}
	
	private int absLargerThan(BigInteger other) {
		if (this.numDigits > other.numDigits)
			return 1;
		else if (this.numDigits < other.numDigits)
			return -1;
		
		int thisValue = Math.abs(Integer.parseInt(this.toString()));
		int otherValue = Math.abs(Integer.parseInt(other.toString()));
		
		if (thisValue > otherValue)
			return 1;
		else if (thisValue < otherValue)
			return -1;
		else
			return 0;
		
	}

	/**
	 * Returns the BigInteger obtained by multiplying the given BigInteger
	 * with this BigInteger - DOES NOT MODIFY this BigInteger
	 * 
	 * @param other BigInteger to be multiplied
	 * @return A new BigInteger which is the product of this BigInteger and other.
	 */
	public BigInteger multiply(BigInteger other) {
		// THE FOLLOWING LINE IS A PLACEHOLDER SO THE PROGRAM COMPILES
		// YOU WILL NEED TO CHANGE IT TO RETURN THE APPROPRIATE BigInteger
		
		boolean needNegative = false;
		
		if (this.negative || other.negative && !(this.negative && other.negative))
			needNegative = true;
		
		DigitNode topOp, botOp, firstNode;
		int loopNum = 0;
		
		if (this.numDigits >= other.numDigits) {
			topOp = this.front;
			firstNode = topOp;
			botOp = other.front;
			loopNum = other.numDigits;
		} else {
			topOp = other.front;
			firstNode = topOp;
			botOp = this.front;
			loopNum = this.numDigits;
		}
		
		int carry = 0;
		int subProd = 0;
		BigInteger product = new BigInteger();
		product = BigInteger.parse("0");
		
		DigitNode prodLast = null;
		for (int i = 0; i < loopNum; i++) {
			BigInteger tempProd = new BigInteger();
			topOp = firstNode;
			// add 0s for product addition
			for (int j = 0; j < i; j++) {
				DigitNode d = new DigitNode(0, null);
				if (prodLast == null) {
					tempProd.front = d;
					prodLast = d;
					tempProd.numDigits++;
				}
				else {
					prodLast.next = d;
					prodLast = prodLast.next;
					tempProd.numDigits++;
				}
			}
			
			while (topOp != null) {
				subProd = topOp.digit * botOp.digit + carry;
				carry = subProd / 10;
				DigitNode d = new DigitNode(subProd % 10, null);
				if (prodLast == null) {
					tempProd.front = d;
					prodLast = d;
					tempProd.numDigits++;
				}
				else {
					prodLast.next = d;
					prodLast = prodLast.next;
					tempProd.numDigits++;
				}
				topOp = topOp.next;
				if (topOp == null && carry > 0) {
					prodLast.next = new DigitNode(carry, null);
					tempProd.numDigits++;
				}
					
			}
			product = product.add(tempProd);
		}
		if (needNegative)
			product.negative = true;
		
		return product;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (front == null) {
			return "0";
		}
		
		String retval = front.digit + "";
		for (DigitNode curr = front.next; curr != null; curr = curr.next) {
				retval = curr.digit + retval;
		}
		
		if (negative) {
			retval = '-' + retval;
		}
		
		return retval;
	}
	
}

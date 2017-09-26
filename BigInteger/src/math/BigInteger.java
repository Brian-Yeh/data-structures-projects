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
		
		// check if String contains 1 invalid character
		if (integer.length() == 1 && !Character.isDigit(integer.charAt(0)))
			throw new IllegalArgumentException();
		
		integer = processString(integer); // remove spaces before and after string
		
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
	
	// removes spaces at beginning and end of string
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
		DigitNode a1 = this.front;
		DigitNode a2 = other.front;
		DigitNode sumLast = null; // pointer to last node of sum
		
		int tempSum = 0;
		while (a1 != null || a2 != null) {
			tempSum /= 10;
			if (a1 != null) {
				tempSum += a1.digit;
				a1 = a1.next;
			}
			if (a2 != null) {
				tempSum += a2.digit;
				a2 = a2.next;
			}
			if (sum.front == null) {
				sum.front = new DigitNode(tempSum % 10, null);
				sumLast = sum.front;
			} else {
				sumLast.next = new DigitNode(tempSum % 10, null);
				sumLast = sumLast.next;
			}
		}
		if (tempSum / 10 == 1)
			sumLast.next = new DigitNode(1, null);
		
		// resolve negative
		if(this.negative == true && other.negative == true)
			sum.negative = true;
		
		return sum;
	}
	
	// subtract function for add
	private BigInteger subtract(BigInteger other) {
		boolean needNegative = false;
		DigitNode topOp, botOp; 
		
		// set topOp to BigInteger with largest absolute value
		int absValueReturn = this.absLargerThan(other);
		if (absValueReturn == 1) {
			topOp = this.front;
			botOp = other.front;
			if (this.negative == true)
				needNegative = true;
		} else if (absValueReturn == -1) {
			topOp = other.front;
			botOp = this.front;
			if (other.negative == true)
				needNegative = true;
		} else {
			return BigInteger.parse("0");
		}
		
		// carry flag
		boolean needCarry = false;
		
		BigInteger diff = new BigInteger();
		DigitNode diffLast = null; // pointer for last node in diff
		int numZeros = 0;
		boolean needZeros = false;
		
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
			
			// account for zeros in front
			if (topDigit - botDigit != 0) {
				if (needZeros == true) {
					for (int i = 0; i < numZeros; i++) {
						if (diff.front == null) {
							diff.front = new DigitNode(0, null);
							diffLast = diff.front;
						}
						else {
							diffLast.next = new DigitNode(0, null);
							diffLast = diffLast.next;
						}
						diff.numDigits++;
					}
					needZeros = false;
					numZeros = 0;
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
			else {
				needZeros = true;
				numZeros++;
				topOp = topOp.next;
				botOp = botOp.next;
				continue;
			}
		}
		
		// iterate through rest of top operand
		while (topOp != null) {
			int digit = topOp.digit;
			
			// resolve carry
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
			
			// account for zeros in front while adding nodes to diff
			if (digit != 0) {
				// add held zeros
				if (needZeros == true) {
					for (int i = 0; i < numZeros; i++) {
						diffLast.next = new DigitNode(0, null);
						diffLast = diffLast.next;
						diff.numDigits++;
					}
					needZeros = false;
					numZeros = 0;
				}
				DigitNode d = new DigitNode(digit, null);
				diffLast.next = d;
				diffLast = d;
				diff.numDigits++;
			} else {
				needZeros = true;
				numZeros++;
				continue;
			}
		}
		
		if (needNegative)
			diff.negative = true;
		
		return diff;
	}
	
	/** determines which BigInteger has greater absolute value
	 *  BigInteger greater than argument returns 1
	 *  BigInteger greater than argument returns -1
	 *  BigInteger equal to argument returns 0
	 */
	private int absLargerThan(BigInteger other) {
		if (this.numDigits > other.numDigits)
			return 1;
		else if (this.numDigits < other.numDigits)
			return -1;
		
		// In case where numDigits equal, find greater BigInteger by
		// storing BigIntegers in greatest to least significant order and comparing
		BigInteger thisReverse = new BigInteger();
		BigInteger otherReverse = new BigInteger();
		
		thisReverse.front = new DigitNode(this.front.digit, null);
		thisReverse.numDigits++;
		for (DigitNode ptr = this.front.next; ptr != null; ptr = ptr.next) {
			DigitNode tempNode = new DigitNode(ptr.digit, thisReverse.front);
			thisReverse.front = tempNode;
			thisReverse.numDigits++;
		}
		
		otherReverse.front = new DigitNode(other.front.digit, null);
		otherReverse.numDigits++;
		for (DigitNode ptr = other.front.next; ptr != null; ptr = ptr.next) {
			DigitNode tempNode = new DigitNode(ptr.digit, otherReverse.front);
			otherReverse.front = tempNode;
			otherReverse.numDigits++;
		}
		
		DigitNode thisPtr = thisReverse.front;
		DigitNode otherPtr = otherReverse.front;
		
		while (thisPtr != null) {
			if (thisPtr.digit > otherPtr.digit)
				return 1;
			else if (otherPtr.digit > thisPtr.digit)
				return -1;
			thisPtr = thisPtr.next;
			otherPtr = otherPtr.next;
		}
		
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
		
		// if any multiplicand is 0, return 0
		if ((this.numDigits == 1 && this.front.digit == 0) || (other.numDigits == 1 && other.front.digit == 0))
			return BigInteger.parse("0");
		
		boolean needNegative = false;
		
		// needsNegative if only 1 multiplicand is negative
		if (this.negative || other.negative && !(this.negative && other.negative))
			needNegative = true;
		
		DigitNode topOp, botOp, firstNode;
		int loopNum = 0;
		
		// set largest multiplicand to top operator
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
		BigInteger product = null;
		DigitNode prodLast = null; // pointer for last node
		
		// loop through and add addend to product
		for (int i = 0; i < loopNum; i++) {
			BigInteger addend = new BigInteger();
			prodLast = null;
			
			topOp = firstNode; // set topOp to least significant digit of top multiplicand
			
			// add 0s depending on addend
			for (int j = 0; j < i; j++) {
				DigitNode d = new DigitNode(0, null);
				if (prodLast == null) {
					addend.front = d;
					prodLast = d;
					addend.numDigits++;
				}
				else {
					prodLast.next = d;
					prodLast = prodLast.next;
					addend.numDigits++;
				}
			}
			
			while (topOp != null) {
				subProd = topOp.digit * botOp.digit + carry;
				carry = subProd / 10;
				DigitNode d = new DigitNode(subProd % 10, null);
				if (prodLast == null) {
					addend.front = d;
					prodLast = d;
					addend.numDigits++;
				}
				else {
					prodLast.next = d;
					prodLast = prodLast.next;
					addend.numDigits++;
				}
				topOp = topOp.next;
				if (topOp == null && carry > 0) {
					prodLast.next = new DigitNode(carry, null);
					addend.numDigits++;
				}
					
			}
			if (product == null)
				product = addend;
			else
				product = product.add(addend);
			
			carry = 0;
			botOp = botOp.next;
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
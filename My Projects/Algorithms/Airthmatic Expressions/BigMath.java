import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

/*
 * ---------------Project 2-------------------
 * BigMath: CS6301- Implementation of Algorithms and datastructures
 * 
 * Purpose: Arbitrary precision arthimetic
 * 
 * Authors : Harsha Teja Kanna ( hxk132730@utdallas.edu)
 * 	         Venkata Vinay Kumar Maripina (vxm133430@utdallas.edu)
 * 
 * Note : This class uses List,Stack implementations from java library
 * 
 */

public class BigMath {

	public static final long BASE = 1000000000L;
	public static final String DECIMAL_FORMAT = "%09d";
	public static final int NUMBER_OF_DECIMAL_DIGITS_PER_WORD = 9;

	public static void main(String[] args) {

		System.out
				.println("---------Please enter arithmetic expressions line by line.----------");
		System.out
				.println("-----------      (*,+,-,^) operators are allowed       ----------------");
		System.out
				.println("------------------------------------------------------------------------------");
		Scanner scanner = new Scanner(System.in);
		while (scanner.hasNextLine()) {

			String expr = scanner.nextLine();

			if (expr == null || (expr != null && expr.equals(""))) {
				continue;
			}
			// Exit if zero
			if (expr.equals("0")) {
				System.out.println("Bye");
				scanner.close();
				return;
			}
			// Parse expression to postfix
			List<Element> tokens = null;
			try {
				tokens = Operators.postFix(expr);
				// Evaluate postfix expression
				System.out
						.println("---Result---------------------------------------------------------------------");
				String result = eval(tokens);
				System.out.println(result);
				System.out
						.println("------------------------------------------------------------------------------");

			} catch (InvalidExpressionException e) {
				System.out.println("syntax error:" + e.getMessage());
				System.out
				.println("------------------------------------------------------------------------------");
			} catch (Exception e) {
				System.out
						.println("Program error occurred exiting..............................");
			}
		}
		scanner.close();
	}

	/*
	 * Expression evaluation stack routine
	 */
	public static String eval(List<Element> expr)
			throws InvalidExpressionException {

		Stack<String> evalStack = new Stack<String>();
		for (Element elem : expr) {
			// Single character operators
			if (elem.type.equals(Element.Type.NUMBER)) {
				evalStack.push(elem.value);
			} else {
				String op1 = evalStack.pop();
				String op2 = evalStack.pop();
				if (elem.value.equals(Operators.OPERATOR_ADD)) {
					evalStack.push(toString(add(op1, op2)));
				} else if (elem.value.equals(Operators.OPERATOR_SUB)) {

					if (compare(op2, op1) == -1) {
						throw new InvalidExpressionException(
								"Negative numbers are not supported");
					}
					evalStack.push(toString(substract(op1, op2)));

				} else if (elem.value.equals(Operators.OPERATOR_MUL)) {
					evalStack.push(multiply(op1, op2));

				} else if (elem.value.equals(Operators.OPERATOR_EXP)) {
					evalStack.push(power(op2, op1));
				}
			}
		}
		return evalStack.pop();
	}

	private static List<String> add(String a, String b) {
		List<Long> big = parseBigInteger(a);
		List<Long> otherBig = parseBigInteger(b);
		return add(big, otherBig);

	}

	private static List<String> add(List<Long> big, List<Long> otherBig) {

		List<String> bigStr = new LinkedList<String>();

		if (big.size() < otherBig.size()) {
			List<Long> tmp = big;
			big = otherBig;
			otherBig = tmp;
		}

		Long carry = 0L;
		// i int or long?
		int i = 0;
		for (; i < otherBig.size(); i++) {

			Long bDigit = otherBig.get(i);
			Long aDigit = big.get(i);

			aDigit = aDigit + bDigit + carry;
			carry = aDigit / BASE;

			aDigit = aDigit % BASE;
			// Padding with zeroes in front

			big.set(i, aDigit);
			bigStr.add(String.format(DECIMAL_FORMAT, aDigit));
		}

		// Add carry to the last digit
		if (carry == 1L) {
			if (i == big.size()) {
				// big.add(new Long(carry));
				bigStr.add("1");
			} else if (i < big.size()) {
				big.set(i, big.get(i) + carry);
				bigStr.add(String.format(DECIMAL_FORMAT, big.get(i)));
				for (int j = i + 1; j < big.size(); j++) {
					bigStr.add(String.format(DECIMAL_FORMAT, big.get(j)));
				}
			}
		} else {
			if (i < big.size()) {
				for (int j = i; j < big.size(); j++) {
					bigStr.add(String.format(DECIMAL_FORMAT, big.get(j)));
				}
			}
		}
		return bigStr;
	}

	private static List<String> substract(String a, String b) {

		List<Long> big = parseBigInteger(a);
		List<Long> otherBig = parseBigInteger(b);
		return substract(big, otherBig);

	}

	private static List<String> substract(List<Long> big, List<Long> otherBig) {

		List<String> bigStr = new LinkedList<String>();

		// b is smaller than a
		if (big.size() < otherBig.size()
				|| (big.size() == otherBig.size() && big.get(big.size() - 1) < otherBig
						.get(big.size() - 1))) { // Comparing most significant
													// digit

			List<Long> tmp = big;
			big = otherBig;
			otherBig = tmp;
		}

		Long borrow = 0L;
		// i int or long?
		int i = 0;
		for (; i < otherBig.size(); i++) {

			Long bDigit = otherBig.get(i);
			Long aDigit = big.get(i);

			if (aDigit < bDigit) {
				aDigit = BASE + aDigit - borrow - bDigit;
				borrow = 1L;
			} else {
				aDigit = aDigit - borrow - bDigit;
				borrow = 0L;
			}
			// aDigit = aDigit % BASE;
			// Padding with zeroes in front

			big.set(i, aDigit);
			bigStr.add(String.format(DECIMAL_FORMAT, aDigit));
		}

		// copy remaining digits
		if (borrow == 1L) {
			if (i < big.size()) {
				big.set(i, big.get(i) - borrow);
				if (i < big.size() - 1) {
					bigStr.add(String.format(DECIMAL_FORMAT, big.get(i)));
				} else if ((i == big.size() - 1) && (big.get(i) != 0)) {
					bigStr.add(String.format(DECIMAL_FORMAT, big.get(i)));
				}
				for (int j = i + 1; j < big.size(); j++) {
					bigStr.add(String.format(DECIMAL_FORMAT, big.get(j)));
				}
			}
		} else {
			if (i < big.size()) {
				for (int j = i; j < big.size(); j++) {
					bigStr.add(String.format(DECIMAL_FORMAT, big.get(j)));
				}
			}
		}

		return bigStr;
	}

	private static String multiply(String a, String b) {
		List<Long> multiplicand = parseBigInteger(a);
		List<Long> multiplier = parseBigInteger(b);

		if (multiplicand.size() < multiplier.size()) {
			List<Long> tmp = multiplicand;
			multiplicand = multiplier;
			multiplier = tmp;
		}
		return convertListToString(convertToString(karatsuba(multiplicand,
				multiplier)));
	}

	/*
	 * Utility functions to convert between formats
	 */
	private static List<String> convertToString(List<Long> big) {

		List<String> bigStr = new LinkedList<String>();
		for (int i = big.size() - 1; i >= 0; i--) {
			bigStr.add(String.format(DECIMAL_FORMAT, big.get(i)));
		}
		return bigStr;
	}

	private static List<Long> convertToLong(List<String> bigStr) {
		List<Long> big = new LinkedList<Long>();
		for (int i = 0; i < bigStr.size(); i++) {
			big.add(Long.parseLong(bigStr.get(i)));
		}
		return big;
	}

	public static String convertListToString(List<String> bigStr) {
		if (bigStr.isEmpty()) {
			return "0";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bigStr.size(); i++) {
			sb.append(bigStr.get(i));
		}
		if (sb.toString().matches("0+"))
			return "0";
		// Strip leading zeroes added by formatting
		return sb.toString().replaceFirst("^0+", "");
	}

	private static void appendZeroes(List<Long> big, int n) {
		for (int i = 0; i < n; i++) {
			big.add(0, new Long(0));
		}
	}

	private static List<Long> subList(List<Long> big, int fromIndex, int toIndex) {

		if (fromIndex == toIndex) {
			List<Long> oneList = new LinkedList<Long>();
			oneList.add(big.get(fromIndex - 1));
			return oneList;
		}
		return big.subList(fromIndex, toIndex);
	}

	private static List<Long> cloneList(List<Long> src) {
		List<Long> list = new LinkedList<Long>();
		for (Long l : src) {
			list.add(new Long(l));
		}
		return list;
	}

	/*
	 * Karatsuba multiplication algorithm implementation
	 */
	private static List<Long> karatsuba(List<Long> multiplicand,
			List<Long> multiplier) {

		if (multiplicand.size() < multiplier.size()) {
			List<Long> tmp = multiplicand;
			multiplicand = multiplier;
			multiplier = tmp;
		}

		// if (multiplier.size() == 1 && multiplier.get(0).equals(0L))
		// return multiplicand;

		if (multiplicand.size() == 1 && multiplier.size() == 1) {
			Long result = multiplicand.get(0) * multiplier.get(0);
			return parseBigInteger(Long.toString(result));
		}

		int mhalf = Math.max(multiplicand.size(), multiplier.size()) / 2;

		List<Long> x1 = subList(multiplicand, mhalf, multiplicand.size());
		List<Long> x0 = subList(multiplicand, 0, mhalf);
		List<Long> y0 = null;
		if (mhalf > multiplier.size()) {
			y0 = subList(multiplier, 0, multiplier.size());
		} else {
			y0 = subList(multiplier, 0, mhalf);
		}

		List<Long> y1 = null;
		if (mhalf >= multiplier.size()) {
			y1 = new LinkedList<Long>();
			y1.add(new Long(0));
		} else {
			y1 = subList(multiplier, mhalf, multiplier.size());
		}

		List<Long> z0 = karatsuba(x0, y0);
		List<Long> z2 = karatsuba(x1, y1);

		List<Long> z1 = karatsuba(
				convertToLong(add(cloneList(x1), cloneList(x0))),
				convertToLong(add(cloneList(y1), cloneList(y0))));

		z1 = convertToLong(substract(z1, z0));
		z1 = convertToLong(substract(z1, z2));

		appendZeroes(z1, mhalf);
		appendZeroes(z2, mhalf * 2);

		List<Long> result = convertToLong(add(z2, z1));
		result = convertToLong(add(result, z0));
		return result;
	}

	/*
	 * Base can be large integer Exponent is limited to java long.
	 */
	private static String power(String a, String b) {

		List<Long> base = parseBigInteger(a);
		List<Long> exponent = parseBigInteger(b);

		List<Long> prod = new LinkedList<Long>();
		prod.add(1L);

		while (exponent.get(0) > 0) {

			if (exponent.get(0) == 1L && prod.get(0) == 1L) {
				return convertListToString(convertToString(base));
			}

			if ((exponent.get(0).longValue() & 1) != 0) {
				prod = karatsuba(prod, base);
			}

			if (exponent.get(0).longValue() == 1) {
				break;
			}

			base = karatsuba(base, base);
			long exp = exponent.get(0).longValue();
			exp >>= 1;
			exponent.set(0, exp);

		}
		return convertListToString(convertToString(prod));
	}

	private static int compare(String a, String b) {
		if (a.length() < b.length())
			return -1;
		if (a.length() > b.length())
			return 1;

		if (a.length() == b.length()) {
			for (int i = 0; i < a.length(); i++) {
				if (a.charAt(i) < b.charAt(i)) {
					return -1;
				}
				if (a.charAt(i) > b.charAt(i)) {
					return 1;
				}
				if (a.charAt(i) == a.charAt(i))
					continue;
			}
		}
		return 0;
	}

	/*
	 * Postcondition:Least significant digit is at starting of the list
	 */
	private static List<Long> parseBigInteger(String bigInteger) {
		List<Long> big = new LinkedList<Long>();

		if (bigInteger.equals("0")) {
			big.add(new Long(0));
			return big;
		}
		bigInteger = bigInteger.replaceFirst("^0+", "");
		int i = bigInteger.length();
		int j = 0;
		while (i > 0) {
			j = i - NUMBER_OF_DECIMAL_DIGITS_PER_WORD;
			if (j < 0) {
				j = 0;
			}
			String number = bigInteger.substring(j, i);
			i = j;
			// Losing leading zeroes here!!!
			big.add(Long.parseLong(number));
		}
		return big;
	}

	/*
	 * Precondition:Least significant digit is at starting of the list
	 */
	public static String toString(List<String> bigInteger) {
		if (bigInteger == null) {
			throw new IllegalArgumentException("Null string");
		}
		if (bigInteger.isEmpty()) {
			return "0";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = bigInteger.size() - 1; i >= 0; i--) {
			sb.append(bigInteger.get(i));
		}
		if (sb.toString().matches("0+"))
			return "0";
		// Strip leading zeroes
		return sb.toString().replaceFirst("^0+", "");
	}

}

class Element {
	public static enum Type {
		NUMBER, OPERATOR
	};

	public final String value;
	public final Type type;

	public Element(String value, Type type) {
		this.value = value;
		this.type = type;
	}
}

/*
 * Class to parse arithmetic expressions
 */
class Operators {

	public static final String OPERATOR_OPP = "(";
	public static final String OPERATOR_CLP = ")";
	public static final String OPERATOR_ADD = "+";
	public static final String OPERATOR_SUB = "-";
	public static final String OPERATOR_MUL = "*";
	public static final String OPERATOR_EXP = "^";

	public static final String[] operatorString = new String[128];

	static {
		operatorString['('] = OPERATOR_OPP;
		operatorString[')'] = OPERATOR_CLP;
		operatorString['+'] = OPERATOR_ADD;
		operatorString['-'] = OPERATOR_SUB;
		operatorString['*'] = OPERATOR_MUL;
		operatorString['^'] = OPERATOR_EXP;

	}

	public static final int[] operators = new int[128];

	static {
		operators['^'] = 1;
		operators['*'] = 2;
		operators['+'] = 3;
		operators['-'] = 3;
		operators['('] = 4;
		operators[')'] = 4;
	}

	public static List<Element> postFix(String expr)
			throws InvalidExpressionException {

		if (expr == null || expr.isEmpty()) {
			throw new IllegalArgumentException("Null String");
		}
		Stack<Character> pending = new Stack<Character>();
		List<Element> postFix = new LinkedList<Element>();
		StringBuilder token = new StringBuilder();

		if (expr.charAt(0) != '(' && !Character.isDigit(expr.charAt(0))) {
			throw new InvalidExpressionException(
					"Expression should start with digits");
		}

		char prev = 0;
		char curr = 0;
		for (int i = 0; i < expr.length(); i++) {

			curr = expr.charAt(i);

			if (Character.isSpaceChar(curr))
				continue;

			if (Character.isDigit(curr)) {
				token.append(curr);

			} else {

				if (operators[curr] == 0) {
					throw new InvalidExpressionException("Invalid expression");
				}

				if (operators[prev] != 0 && curr == '-') {
					throw new InvalidExpressionException(
							"Negative numbers are not supported");
				}

				if (!pending.isEmpty() && pending.peek() != '(' && curr != '('
						&& curr != ')' && prev != '(' && prev != ')'
						&& token.length() == 0) {
					throw new InvalidExpressionException("Invalid expression");
				}

				if (prev == '(' && curr != '(') {
					throw new InvalidExpressionException("Invalid expression");
				}

				if (token.length() != 0) {
					postFix.add(new Element(token.toString(),
							Element.Type.NUMBER));
					token.delete(0, token.length());
				}

				if (pending.isEmpty()) {

					if (curr == ')') {
						throw new InvalidExpressionException(
								"Invalid expression");
					}
					if (operators[curr] != 0) {
						pending.push(curr);
					} else {
						throw new InvalidExpressionException(
								"Illegal character");
					}

				} else {

					if (curr == ')') {
						while (pending.peek() != '(') {
							postFix.add(new Element(operatorString[pending
									.pop()], Element.Type.OPERATOR));
						}
						pending.pop();
					} else {
						char top = pending.peek();

						if (operators[top] <= operators[curr]) {

							if (operators[top] == operators[curr]
									&& curr == '^') {
								pending.push(curr);
							} else {
								while (operators[top] <= operators[curr]
										&& top != '(' && curr != '('
										&& !pending.isEmpty()) {

									if (!pending.isEmpty()) {
										postFix.add(new Element(
												operatorString[pending.pop()],
												Element.Type.OPERATOR));
									}
									if (!pending.isEmpty()) {
										top = pending.peek();
									}

								}
								pending.push(curr);
							}

						} else {

							pending.push(curr);

						}
					}

				}
			}
			prev = curr;
		}

		if (curr != ')' && !Character.isDigit(curr)) {
			throw new InvalidExpressionException("Invalid expression");
		}

		if (!pending.isEmpty()) {

			if (token.length() != 0) {
				postFix.add(new Element(token.toString(), Element.Type.NUMBER));
				token.delete(0, token.length());
			}

			while (!pending.isEmpty()) {
				if (pending.peek() == '(') {
					throw new InvalidExpressionException("Invalid expression");
				}
				postFix.add(new Element(operatorString[pending.pop()],
						Element.Type.OPERATOR));
			}
		}

		if (postFix.size() == 0) {
			throw new InvalidExpressionException("Invalid expression");
		}

		if (postFix.size() == 0) {
			throw new InvalidExpressionException("Invalid expression");
		}

		return postFix;
	}
}

/*
 * Exception indicating syntax errors, invalid expressions,unimplemented
 * operators
 */
class InvalidExpressionException extends Exception {

	private static final long serialVersionUID = -3729659022581315134L;

	public InvalidExpressionException(String msg) {
		super(msg);
	}
}

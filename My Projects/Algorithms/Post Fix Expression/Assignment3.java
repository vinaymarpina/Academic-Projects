import java.util.ConcurrentModificationException;
import java.util.Iterator;

public class Assignment3 {

	public static void main(String[] args) {

		String expr = "2 4 ^ 4 * 36 + 100 -";
		
		try {
			String result = eval(expr);
			assert result.equals("0");
			System.out.println(result);
		} catch (InvalidExpressionException e) {
			e.printStackTrace();
		}

	}

	static class Operators {
		public static final String OPERATOR_ADD = "+";
		public static final String OPERATOR_SUB = "-";
		public static final String OPERATOR_MUL = "*";
		public static final String OPERATOR_DIV = "/";
		public static final String OPERATOR_EXP = "^";
	}

	static class Element {
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
	 * evaluate the postfix expression using stack.
	 * throws invalid expression exception
	 * 
	 */
	public static String eval(String postfix) throws InvalidExpressionException {

		LinkedList<Element> expr = postFix(postfix);
		LinkedListStack<String> evalStack = new LinkedListStack<String>();
		for (Element elem : expr) {
			// Single character operators
			if (elem.type.equals(Element.Type.NUMBER)) {
				evalStack.push(elem.value);
			} else {
				
				if(evalStack.isEmpty())
					throw new InvalidExpressionException("Invalid expression");				
				String op1 = evalStack.pop();				
				if(evalStack.isEmpty())
					throw new InvalidExpressionException("Invalid expression");			
				String op2 = evalStack.pop();
				
				if (elem.value.equals(Operators.OPERATOR_ADD)) {
					evalStack.push(Integer.toString(Integer.parseInt(op1)
							+ Integer.parseInt(op2)));
				} else if (elem.value.equals(Operators.OPERATOR_SUB)) {
					evalStack.push(Integer.toString(Integer.parseInt(op1)
							- Integer.parseInt(op2)));
				} else if (elem.value.equals(Operators.OPERATOR_MUL)) {
					evalStack.push(Integer.toString(Integer.parseInt(op1)
							* Integer.parseInt(op2)));
				} else if (elem.value.equals(Operators.OPERATOR_DIV)) {
					evalStack.push(Integer.toString(Integer.parseInt(op2)
							/ Integer.parseInt(op1)));
				} else if (elem.value.equals(Operators.OPERATOR_EXP)) {
					evalStack.push(Integer.toString((int) Math.pow(
							Integer.parseInt(op2), Integer.parseInt(op1))));
				}
			}
		}

		String result = Integer.toString((int) Double.parseDouble(evalStack
				.pop()));
		if (!evalStack.isEmpty()) {
			throw new InvalidExpressionException("Invalid expression");
		}
		return result;
	}
	
	
	/*
	 * Parsing the postfix expression into a list of token elements.
	 * Assuming tokens separated by space
	 * throws Invalid Exceptions if non numerical data occurs 
	 * or invalid operator occur
	 * 
	 */
	public static LinkedList<Element> postFix(String expr)
			throws InvalidExpressionException {

		LinkedList<Element> postFix = new LinkedList<Element>();

		String[] tokens = expr.split(" ");

		for (String token : tokens) {

			if (token.length() == 1) {
				if (token.equals(Operators.OPERATOR_ADD)) {
					postFix.add(new Element(Operators.OPERATOR_ADD,
							Element.Type.OPERATOR));
				} else if (token.equals(Operators.OPERATOR_SUB)) {
					postFix.add(new Element(Operators.OPERATOR_SUB,
							Element.Type.OPERATOR));
				} else if (token.equals(Operators.OPERATOR_MUL)) {
					postFix.add(new Element(Operators.OPERATOR_MUL,
							Element.Type.OPERATOR));
				} else if (token.equals(Operators.OPERATOR_DIV)) {
					postFix.add(new Element(Operators.OPERATOR_DIV,
							Element.Type.OPERATOR));
				} else if (token.equals(Operators.OPERATOR_EXP)) {
					postFix.add(new Element(Operators.OPERATOR_EXP,
							Element.Type.OPERATOR));
				} else if (token.matches("\\d+")) {
					try {
						Integer number = Integer.parseInt(token);
						postFix.add(new Element(number.toString(),
								Element.Type.NUMBER));
					} catch (Exception e) {
						throw new InvalidExpressionException("Invalid integer");
					}
				} else {
					throw new InvalidExpressionException("Invalid expression");
				}

			} else {
				try {
					Integer number = Integer.parseInt(token);
					postFix.add(new Element(number.toString(),
							Element.Type.NUMBER));
				} catch (Exception e) {
					throw new InvalidExpressionException("Invalid integer");
				}
			}
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

/*
* Stack implementation
*/
class LinkedListStack<T> implements Iterable<T> {

	private int top = -1;
	private LinkedList<T> stack;

	public LinkedListStack() {
		doClear();
	}

	public void clear() {
		doClear();
	}

	private void doClear() {
		stack = new LinkedList<T>();
		top = -1;
	}

	@Override
	public T pop() {
		if (isEmpty())
			throw new IllegalStateException("Empty stack");
		T value = stack.remove(top);
		top--;
		return value;
	}

	@Override
	public T top() {
		if (isEmpty())
			throw new IllegalStateException("Empty stack");
		return stack.get(top);
	}

	@Override
	public void push(T value) {
		if (top == Integer.MAX_VALUE)
			throw new IllegalStateException("Full stack");
		stack.add(value);
		top++;
	}

	@Override
	public boolean isEmpty() {
		return top == -1;
	}

	@Override
	public int size() {
		return top + 1;
	}

	@Override
	public Iterator<T> iterator() {
		// TODO Auto-generated method stub
		return stack.iterator();
	}
	
	@Override
	public String toString() {
		StringBuilder sbuf = new StringBuilder();
		for (T value : stack) {
			sbuf.append(value).append(",");
		}
		return "LinkedListStack-[" + sbuf.toString() + "]";
	}

}

/*
* LinkedList implementation
*/
class LinkedList<T> implements Iterable<T> {

	private Node<T> head;
	private int size = 0;
	private int modCount = 0;

	private static class Node<T> {
		T value;
		Node<T> next;

		public Node(T itm, Node<T> next) {
			this.value = itm;
			this.next = next;
		}
	}

	private class LinkedlistIterator implements Iterator<T> {

		private Node<T> current = head;
		private int expectedModCount = modCount;
		private boolean canRemove = false;

		@Override
		public boolean hasNext() {
			return current.next != null;
		}

		@Override
		public T next() {
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();
			if (!hasNext())
				throw new java.util.NoSuchElementException();
			current = current.next;
			T value = current.value;
			canRemove = true;
			return value;
		}

		@Override
		public void remove() {
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();
			if (!canRemove)
				throw new IllegalStateException();
			LinkedList.this.remove(current);
			expectedModCount++;
			canRemove = false;
		}
	}

	public LinkedList() {
		doClear();
	}

	public boolean add(T value) {
		if (isEmpty()) {
			addAfter(head, value);
		} else {
			addAfter(getNode(size() - 1), value);
		}
		return true;
	}

	public void add(int index, T value) {
		addAfter(getNode(index), value);
	}

	public T get(int index) {
		return getNode(index).value;
	}

	public T set(int index, T value) {
		Node<T> node = getNode(index);
		T returnValue = node.value;
		node.value = value;
		return returnValue;
	}

	public T remove(int index) {
		if (index < 0 || index > size - 1)
			throw new IndexOutOfBoundsException();

		Node<T> prev = head;
		Node<T> curr = head.next;
		int i = 0;
		while (curr.next != null) {
			if (i == index) {
				prev.next = curr.next;
				size--;
				modCount++;
				break;
			}
			prev = curr;
			curr = curr.next;
			i++;
		}
		if (curr.next == null) {
			prev.next = null;
			size--;
			modCount++;
			return curr.value;
		}
		return curr.value;
	}

	public boolean contains(T value) {
		for (T v : this) {
			if (v.equals(v))
				return true;
		}
		return false;
	}

	public boolean addIfNotPresent(T value) {
		if (!contains(value)) {
			return add(value);
		}
		return false;
	}

	public void removeIfPresent(T value) {
		Iterator<T> itr = this.iterator();
		while (itr.hasNext()) {
			T v = itr.next();
			if (v.equals(value)) {
				itr.remove();
			}
		}
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public boolean clear() {
		doClear();
		return true;
	}

	private void doClear() {
		head = new Node<T>(null, null);
		size = 0;
		modCount++;
	}

	private void addAfter(Node<T> node, T value) {
		node.next = new Node<T>(value, node.next);
		size++;
		modCount++;
	}

	private Node<T> getNode(int index) {
		if (index < 0 || index > size - 1)
			throw new IndexOutOfBoundsException();
		Node<T> node = head.next;
		for (int i = 0; i < index; i++) {
			node = node.next;
		}
		return node;
	}

	private T remove(Node<T> node) {
		if (node == null)
			throw new IllegalStateException();
		T nodeValue = node.value;
		T nextNodevalue = node.next.value;
		node.next = node.next.next;
		node.value = nextNodevalue;
		size--;
		modCount++;
		return nodeValue;
	}

	@Override
	public Iterator<T> iterator() {
		return new LinkedlistIterator();
	}

	@Override
	public String toString() {
		StringBuilder sbuf = new StringBuilder();
		for (T value : this) {
			sbuf.append(value).append(",");
		}
		return "LinkedList-[" + sbuf.toString() + "]";
	}

}


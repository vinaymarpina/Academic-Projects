import java.util.ConcurrentModificationException;
import java.util.Iterator;

public class DLinkedList<T> implements Iterable<T> {

	public static void main(String[] args) {

		System.out.println("1) Swap(2,3)");
		DLinkedList<Integer> list = new DLinkedList<Integer>();
		list.add(4);
		list.add(5);
		list.add(6);
		list.add(1);
		System.out.println("Given list: " + list.toString());
		list.swap(2, 3);
		System.out.println("Result list: " + list.toString());
		System.out.println("----------------------------------");
		System.out.println("2) Reverse()");
		DLinkedList<Integer> rlist = list.reverse();
		System.out.println("Given list: " + list.toString());
		System.out.println("Result list: " + rlist.toString());
		System.out.println("----------------------------------");
		System.out.println("3) Erase(1,2)");
		list.add(11);
		list.add(12);
		System.out.println("Given list: " + list.toString());
		list.erase(1, 2);
		System.out.println("Result list: " + list.toString());
		System.out.println("----------------------------------");
		System.out.println("4) InsertList(list,3)");
		System.out.println("Given list: " + list.toString());
		list.insertList(rlist, 3);
		System.out.println("Result list: " + list.toString());

	}

	private static class Node<T> {
		public Node(T itm, Node<T> prev, Node<T> next) {
			this.value = itm;
			this.prev = prev;
			this.next = next;
		}

		T value;
		Node<T> next;
		Node<T> prev;
	}

	private class DlinkedlistIterator implements Iterator<T> {

		private Node<T> current = head.next;
		private int expectedModCount = modCount;
		private boolean canRemove = false;

		@Override
		public boolean hasNext() {
			return current != tail;
		}

		@Override
		public T next() {
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();

			if (!hasNext())
				throw new java.util.NoSuchElementException();

			T value = current.value;
			current = current.next;
			canRemove = true;
			return value;
		}

		@Override
		public void remove() {
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();

			if (!canRemove)
				throw new IllegalStateException();

			DLinkedList.this.remove(current.prev);
			expectedModCount++;
			canRemove = false;
		}
	}

	private class ReverseDlinkedlistIterator implements Iterator<T> {

		private Node<T> current = tail.prev;
		private int expectedModCount = modCount;
		private boolean canRemove = false;

		@Override
		public boolean hasNext() {
			return current != head;
		}

		@Override
		public T next() {
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();

			if (!hasNext())
				throw new java.util.NoSuchElementException();

			T value = current.value;
			current = current.prev;
			canRemove = true;
			return value;
		}

		@Override
		public void remove() {
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();

			if (!canRemove)
				throw new IllegalStateException();

			DLinkedList.this.remove(current.next);
			expectedModCount++;
			canRemove = false;
		}
	}

	private Node<T> head;
	private Node<T> tail;
	private int size = 0;
	private int modCount = 0;

	public DLinkedList() {
		doClear();
	}

	public void clear() {
		doClear();
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public boolean add(T itm) {
		add(size(), itm);
		return true;
	}

	public boolean add(int index, T itm) {
		addBefore(getNode(index, 0, size()), itm);
		return true;
	}

	public T get(int index) {
		return getNode(index).value;
	}

	public T set(int index, T itm) {
		Node<T> node = getNode(index);
		T value = node.value;
		node.value = itm;
		return value;
	}

	public T remove(int index) {
		return remove(getNode(index));
	}

	/*
	 * swaps elements at given indices.
	 * 
	 * Given list :1,2,3,4 swap(1,2) Result list : 1,3,2,4 Given list :1,2,3,4
	 * swap(3,0) Result list : 4,2,3,1
	 */
	public void swap(int i, int j) {
		if (i < 0 || i > size() - 1 || j < 0 || j > size() - 1)
			throw new IndexOutOfBoundsException();

		if (i == j)
			return;

		if (i > j) {
			int tmp = i;
			i = j;
			j = tmp;
		}

		Node<T> n1 = null;
		Node<T> n2 = null;
		if (i < size() / 2) {
			Node<T> n = head.next;
			for (int k = 0; k <= j; k++) {
				if (k == i)
					n1 = n;
				if (k == j) {
					n2 = n;
					break;
				}
				n = n.next;
			}
		} else {
			Node<T> n = tail.prev;
			for (int k = size() - 1; k >= i; k--) {
				if (k == j)
					n1 = n;
				if (k == i) {
					n2 = n;
					break;
				}
				n = n.prev;
			}
		}
		T temp = n2.value;
		n2.value = n1.value;
		n1.value = temp;

	}

	/*
	 * Gives list in reverse order
	 * 
	 * Given list :1,2,3,4 reverse() Result list : 4,3,2,1
	 */
	public DLinkedList<T> reverse() {

		DLinkedList<T> revList = new DLinkedList<T>();
		Iterator<T> rItr = this.reverseIterator();
		while (rItr.hasNext()) {
			revList.add(rItr.next());
		}
		return revList;
	}

	/*
	 * Removes the elements beginning at i and plus the count of elements.
	 * 
	 * erase(0,0) : Given list :1,2,3,4,5, Result list : 2,3,4,5 erase(0,1) :
	 * Given list :1,2,3,4,5, Result list : 3,4,5 erase(1,1) : Given list
	 * :1,2,3,4,5, Result list : 1,4,5
	 */
	public void erase(int i, int count) {
		int j = i + count;
		if (i < 0 || i > size() - 1 || j < 0 || j > size() - 1 || i > j)
			throw new IndexOutOfBoundsException();

		if (i < size() / 2) {
			Node<T> n = head.next;
			for (int k = 0; k <= j; k++) {
				if (k >= i)
					remove(n);
				n = n.next;
			}
		} else {
			Node<T> n = tail.prev;
			for (int k = size() - 1; k >= i; k--) {
				if (k <= j)
					remove(n);
				n = n.prev;
			}
		}
	}

	/*
	 * inserts the list beginning at index.
	 * 
	 * Given list :1,2 Inserted list: 3,4 - insertList(list,1) Result list :
	 * 1,3,4,2
	 */
	public void insertList(DLinkedList<T> list, int index) {
		if (index < 0 || index > size())
			throw new IndexOutOfBoundsException();
		Node<T> node = null;

		if (index < size() / 2) {
			node = head.next;
			for (int i = 0; i < index; i++) {
				node = node.next;
			}
			for (T value : list) {
				addBefore(node, value);
			}
		} else {
			node = tail;
			for (int i = size(); i > index; i--) {
				node = node.prev;
			}
			for (T value : list) {
				addBefore(node, value);
			}
		}

	}

	private void doClear() {
		head = new Node<T>(null, null, tail);
		tail = new Node<T>(null, head, null);
		head.next = tail;
		size = 0;
		modCount++;
	}

	private void addBefore(Node<T> node, T itm) {
		node.prev.next = node.prev = new Node<T>(itm, node.prev, node);
		size++;
		modCount++;
	}

	private void addAfter(Node<T> node, T itm) {
		node.next.prev = node.next = new Node<T>(itm, node, node.next);
		size++;
		modCount++;
	}

	private T remove(Node<T> node) {
		node.prev.next = node.next;
		node.next.prev = node.prev;
		size--;
		modCount++;
		return node.value;
	}

	private Node<T> getNode(int index) {
		return getNode(index, 0, size());
	}

	private Node<T> getNode(int index, int lower, int upper) {

		Node<T> node = null;
		if (index < lower || index > upper)
			throw new IndexOutOfBoundsException();

		if (index < size() / 2) {
			node = head.next;
			for (int i = 0; i < index; i++) {
				node = node.next;
			}
		} else {
			node = tail;
			for (int i = size(); i > index; i--) {
				node = node.prev;
			}
		}
		return node;
	}

	@Override
	public Iterator<T> iterator() {
		// TODO Auto-generated method stub
		return new DlinkedlistIterator();
	}

	public Iterator<T> reverseIterator() {
		// TODO Auto-generated method stub
		return new ReverseDlinkedlistIterator();
	}

	@Override
	public String toString() {
		StringBuilder sbuf = new StringBuilder();
		for (T value : this) {
			sbuf.append(value).append(",");
		}
		return "DLinkedList-[" + sbuf.toString() + "]";
	}
}

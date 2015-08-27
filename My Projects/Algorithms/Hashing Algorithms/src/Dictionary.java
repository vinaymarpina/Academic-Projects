


public interface Dictionary<K extends Comparable<K>, V extends Comparable<V>> {

	/*
	 * insert a new entry with key k, value v. If a key with key k already
	 * exists, its value is replaced by v.
	 */
	public void insert(K k, V v);

	/*
	 * return the value associated with key k. If there is no element with key
	 * k, it returns null (or 0).
	 */
	public V find(K k);

	/*
	 * return (k,v) corresponding to the current smallest key
	 */
	public Entry<K, V> findMin();

	/*
	 * 
	 * return (k,v) corresponding to the current largest key
	 */
	public Entry<K, V> findMax();

	/*
	 * remove element with key k. Returns value of deleted element (null if such
	 * a key does not exist).
	 */
	public V remove(K k);

	/*
	 * remove all elements whose value is v. Returns number of elements deleted.
	 */
	public int removeValue(V v);

	/*
	 * return the number of elements currently stored.
	 */
	public int size();

	/*
	 * returns boolean indicating whether the current store is empty.
	 */
	public boolean isEmpty();
}

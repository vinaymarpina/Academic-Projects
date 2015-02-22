
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Harsha Teja Kanna
 */
public class SeparateChainLinkedHashTable<K extends Comparable<K>, V extends Comparable<V>> implements Dictionary<K, V>, Iterable<Entry<K, V>> {

    private int size;
    private int tableSize;
    private LinkedList<Entry<K, V>>[] table;
    //List to maintain insertion order and make FindMin,FindMax faster.
    private LinkedList<Entry<K, V>> entryList;
    private final double MAX_LOAD_FACTOR = 0.75;
    private int rehashes = 0;

    // Change to dynamic array implementation
    public SeparateChainLinkedHashTable(int m) {
        if (m < 0 || m == Integer.MAX_VALUE) {
            throw new IllegalArgumentException();
        }
        BigInteger big = new BigInteger(m + "");
        this.tableSize = big.nextProbablePrime().intValue();
        this.table = (LinkedList<Entry<K, V>>[]) Array.newInstance(LinkedList.class, tableSize);
        this.entryList = new LinkedList<Entry<K, V>>();
    }

    private void rehash() {
        rehashes++;
        LinkedList<Entry<K, V>>[] prevTable = table;
        BigInteger big = new BigInteger((int) (tableSize / MAX_LOAD_FACTOR) + "");
        this.tableSize = big.nextProbablePrime().intValue();
        System.out.println("Rehashing to length:" + tableSize + " Rehashes till now: " + rehashes);
        this.table = (LinkedList<Entry<K, V>>[]) Array.newInstance(LinkedList.class, tableSize);
        entryList.clear();
        size = 0;
        for (LinkedList<Entry<K, V>> list : prevTable) {
            if (list != null) {
                for (Entry<K, V> entry : list) {
                    if (entry != null) {
                        insert(entry);
                    }
                }
            }
        }
    }

    @Override
    public void insert(K k, V v) {
        //If load factor is reached
        if (size >= table.length * MAX_LOAD_FACTOR) {
            rehash();
        }
        if (k != null && v != null) {
            Entry<K, V> entry = new Entry(k, v);
            insert(entry);
        }
    }

    private void insert(Entry<K, V> entry) {
        int index = (entry.getKey().hashCode() & 0x7fffffff) % tableSize;
        LinkedList<Entry<K, V>> list = table[index];
        if (list == null) {
            table[index] = new LinkedList<Entry<K, V>>();
        }
        table[index].add(entry);
        //Insertion order list
        entryList.add(entry);
        size++;
    }

    @Override
    public V find(K k) {
        V value = null;
        LinkedList<Entry<K, V>> list = table[(k.hashCode() & 0x7fffffff) % tableSize];
        if (list != null) {
            for (Entry<K, V> n : list) {
                if (n.getKey().equals(k)) {
                    value = n.getValue();
                }
            }
        }
        return value;
    }

    @Override
    public Entry<K, V> findMax() {
        Entry<K, V> max = null;
        Iterator<Entry<K, V>> itr = entryList.iterator();
        if (itr.hasNext()) {
            max = itr.next();
        } else {
            return null;
        }
        while (itr.hasNext()) {
            Entry<K, V> entry = itr.next();
            if (entry.getKey().compareTo(max.getKey()) > 0) {
                max = entry;
            }
        }
        return max;
    }

    @Override
    public Entry<K, V> findMin() {
        Entry<K, V> min = null;
        Iterator<Entry<K, V>> itr = entryList.iterator();
        if (itr.hasNext()) {
            min = itr.next();
        } else {
            return null;
        }
        while (itr.hasNext()) {
            Entry<K, V> entry = itr.next();
            if (entry.getKey().compareTo(min.getKey()) < 0) {
                min = entry;
            }
        }
        return min;
    }

    @Override
    public V remove(K k) {
        V value = null;
        LinkedList<Entry<K, V>> list = table[(k.hashCode() & 0x7fffffff) % tableSize];
        if (list != null && !list.isEmpty()) {
            Iterator<Entry<K, V>> itr = list.iterator();
            while (itr.hasNext()) {
                Entry<K, V> entry = itr.next();
                value = entry.getValue();
                if (entry.getKey().equals(k)) {
                    itr.remove();
                }
            }
            //Insertion order list
            entryList.remove(k);
            size--;
        }
        return value;
    }

    @Override
    public int removeValue(V v) {
        if (v == null) {
            return 0;
        }
        int count = 0;
        for (LinkedList<Entry<K, V>> list : table) {
            if (list != null && !list.isEmpty()) {
                Iterator<Entry<K, V>> itr = list.iterator();
                while (itr.hasNext()) {
                    Entry<K, V> entry = itr.next();
                    if (entry.getValue().equals(v)) {
                        entryList.remove(entry);
                        itr.remove();
                        count++;
                    }
                }
            }
        }
        size -= count;
        return count;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    public Iterator<Entry<K, V>> iterator() {
        return new HashTableIterator();
    }

    private class HashTableIterator implements Iterator<Entry<K, V>> {

        Iterator<Entry<K, V>> listIterator = entryList.iterator();

        @Override
        public boolean hasNext() {
            return listIterator.hasNext();
        }

        @Override
        public Entry<K, V> next() {
            return listIterator.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not implemented");
        }
    }
}

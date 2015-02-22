
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.Iterator;

/**
 *
 * @author Harsha Teja Kanna
 */
public class CuckooHashTable<K extends Comparable<K>, V extends Comparable<V>> implements Dictionary<K, V>, Iterable<Entry<K, V>> {

    private int size;
    private int tableSize;
    private HashFunction<K> hashFunction;
    private int numberOfTables;
    private Entry<K, V>[] table;
    private int[] tableStart;
    private final int MAX_TRIES_TO_INSERT = 100;
    private final int MAX_REHASHES_BEFORE_EXPAND = 10;
    private final double MAX_LOAD_FACTOR = 0.49;
    private int rehashes = 0;

    // Change to dynamic array implementation
    public CuckooHashTable(HashFunction<K> hashFunction, int m) {
        this.hashFunction = hashFunction;
        this.numberOfTables = hashFunction.number();
        intializeTable(m);
    }

    private void rehash() {
        //System.out.println( "NEW HASH FUNCTIONS " + array.length );
        hashFunction.generateNew();
        rehash(table.length);
    }

    private void intializeTable(int m) {
        BigInteger big = new BigInteger(Integer.toString(m / hashFunction.number()));
        this.tableSize = big.nextProbablePrime().intValue();
        this.table = (Entry<K, V>[]) Array.newInstance(Entry.class, tableSize * hashFunction.number());
        this.tableStart = new int[hashFunction.number()];
        for (int i = 0; i < hashFunction.number(); i++) {
            tableStart[i] = i * tableSize;
        }
    }

    private void rehash(int toLength) {
        rehashes++;
        System.out.println("Rehashing to length:" + toLength + " Rehashes till now: " + rehashes);

        Entry<K, V>[] prevTable = table;
        if (toLength != table.length) {
            intializeTable(toLength);
        }
        size = 0;
        // Copy table over
        for (Entry<K, V> entry : prevTable) {
            if (entry != null) {
                insert(entry);
            }
        }
    }

    @Override
    public void insert(K k, V v) {
        if (k != null && v != null) {
            if (find(k) != null) {
                return;
            }
            //If load factor is reached
            if (size >= table.length * MAX_LOAD_FACTOR) {
                rehash((int) (table.length / MAX_LOAD_FACTOR));
            }

            Entry<K, V> entry = new Entry(k, v);
            insert(entry);
        }
    }

    private void insert(Entry<K, V> entry) {
        //Find index from hashcode and entry
        int t = 0;
        for (int j = 0; t < MAX_TRIES_TO_INSERT; t++, j++) {
            if (j == numberOfTables - 1) {
                j = 0;
            }
            for (int i = 0; i < numberOfTables; i++) {
                //System.out.print(" Hash function." + (i + 1) + " ");
                int index = (hashFunction.hashCode(entry.getKey(), i + 1) & 0x7fffffff) % tableSize + tableStart[i];
                entry = insert(entry, index);
                if (entry == null) {
                    return;
                }
            }
        }
        // Insert failed
        if (rehashes <= MAX_REHASHES_BEFORE_EXPAND) {
            rehash((int) (table.length / MAX_LOAD_FACTOR));
        } else {
            rehash();
        }
    }

    /*
     * insert and return kicked entry
     */
    private Entry<K, V> insert(Entry<K, V> entry, int index) {
        Entry<K, V> kicked = null;
        if (table[index] == null) {
            table[index] = entry;
            size++;
            //System.out.println("Inserted in Table" + tableIndex + "... at index: " + index + "  " + entry.toString() + " Table size:" + size);
        } else {
            kicked = table[index];
            table[index] = entry;
            //System.out.println("Inserted in Table" + tableIndex + "... at index: " + index + "  " + entry.toString() + "....... Kicked " + kicked.toString());
        }
        return kicked;
    }

    @Override
    public V find(K k) {
        if (k == null) {
            return null;
        }
        V value = null;
        for (int i = 0; i < numberOfTables; i++) {
            //Find index from hashcode and entry
            int index = (hashFunction.hashCode(k, i + 1) & 0x7fffffff) % tableSize + tableStart[i];
            if (table[ index] != null && table[index].getKey().equals(k)) {
                value = table[ index].getValue();
                break;
            }
        }
        return value;
    }

    @Override
    public V remove(K k) {
        if (k == null) {
            return null;
        }
        V value = null;
        for (int i = 0; i < numberOfTables; i++) {
            //Find index from hashcode and entry
            int index = (hashFunction.hashCode(k, i + 1) & 0x7fffffff) % tableSize + tableStart[i];
            if (table[ index] != null && table[ index].getKey().equals(k)) {
                value = table[ index].getValue();
                table[ index] = null;
                size--;
                break;
            }
        }
        return value;
    }

    @Override
    public int removeValue(V v) {
        if (v == null) {
            return 0;
        }
        int count = 0;
        Iterator<Entry<K, V>> itr = iterator();
        while (itr.hasNext()) {
            Entry<K, V> entry = itr.next();
            if (entry.getValue().compareTo(v) == 0) {
                itr.remove();
                count++;
            }
        }
        return count;
    }

    @Override
    public Entry<K, V> findMax() {
        Entry<K, V> max = null;
        Iterator<Entry<K, V>> itr = iterator();
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
        Iterator<Entry<K, V>> itr = iterator();
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

        private int index = 0;
        private boolean valid = false;
        private boolean canRemove = false;

        @Override
        public boolean hasNext() {
            if (size == 0) {
                return false;
            }
            if (index >= table.length - 1) {
                return false;
            }
            while (table[index] == null && index != table.length - 1) {
                index++;
            }
            if (table[index] == null && index == table.length - 1) {
                return false;
            }
            valid = true;
            return true;
        }

        @Override
        public Entry<K, V> next() {
            if (!valid) {
                throw new IllegalStateException();
            }
            valid = false;
            canRemove = true;
            return table[index++];
        }

        @Override
        public void remove() {
            if (!canRemove) {
                throw new IllegalStateException();
            }
            table[index - 1] = null;
            canRemove = false;
            size--;
        }
    }
}

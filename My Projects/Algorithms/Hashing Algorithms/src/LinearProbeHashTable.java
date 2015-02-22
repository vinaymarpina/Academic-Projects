
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.Iterator;

/**
 *
 * @author Harsha Teja Kanna
 */
public class LinearProbeHashTable<K extends Comparable<K>, V extends Comparable<V>> implements Dictionary<K, V>, Iterable<ProbeTableEntry<K, V>> {

    private int size;
    private int tableSize;
    private ProbeTableEntry<K, V>[] table;
    private final double MAX_LOAD_FACTOR = 0.75;
    private int rehashes = 0;

    // Change to dynamic array implementation
    public LinearProbeHashTable(int m) {
        if (m < 0 || m == Integer.MAX_VALUE) {
            throw new IllegalArgumentException();
        }
        BigInteger big = new BigInteger(m + "");
        this.tableSize = big.nextProbablePrime().intValue();
        this.table = (ProbeTableEntry<K, V>[]) Array.newInstance(ProbeTableEntry.class, tableSize);
    }

    private void rehash() {
        rehashes++;
        ProbeTableEntry<K, V>[] prevTable = table;
        BigInteger big = new BigInteger((int) (tableSize / MAX_LOAD_FACTOR) + "");
        this.tableSize = big.nextProbablePrime().intValue();
        System.out.println("Rehashing to length:" + tableSize + " Rehashes till now: " + rehashes);
        this.table = (ProbeTableEntry<K, V>[]) Array.newInstance(ProbeTableEntry.class, tableSize);
        size = 0;
        // Copy table over
        for (ProbeTableEntry<K, V> entry : prevTable) {
            if (entry != null && !entry.isDeleted()) {
                insert(entry);
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
            ProbeTableEntry<K, V> entry = new ProbeTableEntry(k, v);
            insert(entry);
        }
    }

    private void insert(ProbeTableEntry<K, V> entry) {
        //Find index from hashcode and entry
        int index = (entry.getKey().hashCode() & 0x7fffffff) % tableSize;
        ProbeTableEntry<K, V> find = table[index];
        if (find != null) {
            do {
                find = table[++index % tableSize];
            } while (find != null && !find.isDeleted());
        }
        table[index % tableSize] = entry;
        size++;
    }

    @Override
    public V find(K k) {
        if (k == null) {
            return null;
        }
        //Find index from hashcode and entry
        int index = (k.hashCode() & 0x7fffffff) % tableSize;
        ProbeTableEntry<K, V> find = table[index];
        //If null return
        if (find == null) {
            return null;
        }
        // If found return
        if (find.getKey().equals(k) && !find.isDeleted()) {
            return find.getValue();
        } else if (find.getKey().equals(k) && find.isDeleted()) {
            return null;
        }

        V value = null;
        // key not equals then probe
        do {
            find = table[++index % tableSize];
            if (find != null && find.getKey().equals(k)) {
                if (!find.isDeleted()) {
                    value = find.getValue();
                }
                break;
            }
        } while (find != null);

        return value;
    }

    @Override
    public V remove(K k) {
        if (k == null) {
            return null;
        }
        //Find index from hashcode and entry
        int index = (k.hashCode() & 0x7fffffff) % tableSize;
        ProbeTableEntry<K, V> find = table[index];

        //If null return
        if (find == null) {
            return null;
        }
        // If found delete
        if (find.getKey().equals(k)) {
            if (!find.isDeleted()) {
                find.setDeleted(true);
                size--;
                return find.getValue();
            } else {
                return null;
            }
        }

        V value = null;
        do {
            find = table[++index % tableSize];
            if (find != null && find.getKey().equals(k)) {
                if (!find.isDeleted()) {
                    find.setDeleted(true);
                    value = find.getValue();
                    size--;
                    break;
                }
            }
        } while (find != null);

        return value;
    }

    @Override
    public int removeValue(V v) {
        if (v == null) {
            return 0;
        }
        int count = 0;
        Iterator<ProbeTableEntry<K, V>> itr = iterator();
        while (itr.hasNext()) {
            ProbeTableEntry<K, V> entry = itr.next();
            if (entry.getValue().compareTo(v) == 0) {
                itr.remove();
                count++;
            }
        }
        return count;
    }

    @Override
    public ProbeTableEntry<K, V> findMax() {
        ProbeTableEntry<K, V> max = null;
        Iterator<ProbeTableEntry<K, V>> itr = iterator();
        if (itr.hasNext()) {
            max = itr.next();
        } else {
            return null;
        }
        while (itr.hasNext()) {
            ProbeTableEntry<K, V> entry = itr.next();
            if (entry.getKey().compareTo(max.getKey()) > 0) {
                max = entry;
            }
        }
        return max;
    }

    @Override
    public ProbeTableEntry<K, V> findMin() {
        ProbeTableEntry<K, V> min = null;
        Iterator<ProbeTableEntry<K, V>> itr = iterator();
        if (itr.hasNext()) {
            min = itr.next();
        } else {
            return null;
        }
        while (itr.hasNext()) {
            ProbeTableEntry<K, V> entry = itr.next();
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

    public Iterator<ProbeTableEntry<K, V>> iterator() {
        return new HashTableIterator();
    }

    private class HashTableIterator implements Iterator<ProbeTableEntry<K, V>> {

        private int index = 0;
        private boolean valid = false;
        ProbeTableEntry<K, V> current;
        private boolean canRemove = false;

        @Override
        public boolean hasNext() {
            if (size == 0) {
                return false;
            }
            if (index >= table.length - 1) {
                return false;
            }

            ProbeTableEntry<K, V> find = null;
            do {
                find = table[index++ % tableSize];
                if (find != null && !find.isDeleted()) {
                    current = find;
                    break;
                }
            } while (index != table.length - 1);

            if ((find == null || find.isDeleted()) && index == table.length - 1) {
                return false;
            }

            valid = true;
            return true;
        }

        @Override
        public ProbeTableEntry<K, V> next() {
            if (!valid) {
                throw new IllegalStateException();
            }
            valid = false;
            canRemove = true;
            return current;
        }

        @Override
        public void remove() {
            if (!canRemove) {
                throw new IllegalStateException();
            }
            current.setDeleted(true);
            canRemove = false;
            size--;
        }
    }
}

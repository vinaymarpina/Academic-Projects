

import java.lang.reflect.Array;
import java.util.Random;
import java.util.Iterator;

public class SkipList<K extends Comparable<K>, V extends Comparable<V>>
        implements Dictionary<K, V>, Iterable<Entry<K, V>> {

    private static final int DEFAULT_HEIGHT = 32;
    private Node<K, V> head;
    private Random rand;
    private int height = 1;
    private int size = 0;

    public SkipList() {
        head = new Node<K, V>(null, DEFAULT_HEIGHT, 0);
        rand = new Random();
    }

    @Override
    public void insert(K k, V v) {

        int level = 1;
        // Coin flips : 0 bit-tail,1 bit-head
        int randInt = rand.nextInt();
        while ((randInt & 1) == 1) {
            level++;
            // Flip coin
            randInt >>= 1;
        }

        if (level > height) {
            height = level;
        }

        Entry<K, V> entry = new Entry<K, V>(k, v);
        Node<K, V> node = new Node<K, V>(entry, level, 0);

        Node<K, V> current = head;

        for (int i = height - 1; i >= 0; i--) {

            while (current.next[i] != null) {
                if (current.next[i].entry.getKey().compareTo(k) > 0) {
                    break;
                }
                current = current.next[i];
            }

            if (i <= level - 1) {
                node.next[i] = current.next[i];
                current.next[i] = node;
            }
        }

        size++;
    }

    @Override
    public V find(K k) {
        Node<K, V> current = head;
        for (int i = height - 1; i >= 0; i--) {

            while (current.next[i] != null) {
                if (current.next[i].entry.getKey().compareTo(k) > 0) {
                    break;
                }

                if (current.next[i].entry.getKey().compareTo(k) == 0) {
                    return current.next[i].entry.getValue();
                }
                current = current.next[i];
            }
        }
        return null;
    }

    @Override
    public Entry<K, V> findMin() {
        return head.next[0].entry;
    }

    @Override
    public Entry<K, V> findMax() {
        Node<K, V> current = head.next[0];
        Node<K, V> prev = head;
        while (current != null) {
            prev = current;
            current = current.next[0];
        }
        return prev.entry;
    }

    @Override
    public V remove(K k) {
        Node<K, V> current = head;
        V result = null;
        for (int i = height - 1; i >= 0; i--) {
            while (current.next[i] != null) {
                if (current.next[i].entry.getKey().compareTo(k) > 0) {
                    break;
                }
                if (current.next[i].entry.getKey().compareTo(k) == 0) {
                    if (result == null) {
                        result = current.next[i].entry.getValue();
                    }
                    current.next[i] = current.next[i].next[i];
                    break;
                }
                current = current.next[i];
            }
        }
        size--;
        return result;
    }

    @Override
    public int removeValue(V v) {
        int count = 0;
        Node<K, V> current = head;
        while (current.next[0] != null) {
            if (current.next[0].entry.getValue().compareTo(v) == 0) {
                for (int i = 0; i < current.next.length; i++) {
                    if (current.next[i] != null) {
                        current.next[i] = current.next[i].next[i];
                    }
                }
                count++;
            } else {
                current = current.next[0];
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

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("SkipList head=").append(head).append(", height=").append(height).append("]");
        sb.append("Keys");
        sb.append("[");
        Node<K, V> current = head;
        while (current.next[0] != null) {
            current = current.next[0];
            sb.append(current.entry.getKey().toString()).append("-").append(current.next.length).append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public Iterator<Entry<K, V>> iterator() {
        return new SkiplistIterator();
    }

    private class SkiplistIterator implements Iterator<Entry<K, V>> {

        private Node<K, V> current = head;

        @Override
        public boolean hasNext() {
            return current.next[0] != null;
        }

        @Override
        public Entry<K, V> next() {
            current = current.next[0];
            if (current != null) {
                return current.entry;
            } else {
                return null;
            }
        }

        @Override
        public void remove() {
            SkipList.this.remove(current.entry.getKey());
        }
    }
}

class Node<K extends Comparable<K>, V extends Comparable<V>> {

    protected Entry<K, V> entry;
    protected Node<K, V>[] next;
    protected int count;
    protected int level;

    @SuppressWarnings("unchecked")
    public Node(Entry<K, V> entry, int level, int count) {
        this.entry = entry;
        this.next = (Node<K, V>[]) Array.newInstance(Node.class, level);
        this.count = count;
        this.level = level;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Node [entry=").append(entry).append(", next=");

        sb.append("[");
        if (next != null) {
            for (Node<K, V> node : next) {
                if (node != null && node.entry != null) {
                    sb.append(node.entry.getKey().toString()).append(",");
                }
            }
        }
        sb.append(", count=").append(count).append(", level=").append(level).append("]");
        return sb.toString();
    }
}

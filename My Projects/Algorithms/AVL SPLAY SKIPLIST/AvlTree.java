

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

public class AvlTree<K extends Comparable<K>, V extends Comparable<V>>
        implements Tree<K, V>, Dictionary<K, V>, Iterable<Entry<K, V>> {

    protected static class AvlNode<K extends Comparable<K>, V extends Comparable<V>> {

        protected Entry<K, V> entry;
        protected AvlNode<K, V> left;
        protected AvlNode<K, V> right;
        protected int count;
        protected int height;

        public AvlNode(Entry<K, V> itm, AvlNode<K, V> parent, AvlNode<K, V> left,
                AvlNode<K, V> right, int count, int height) {
            this.entry = itm;
            this.left = left;
            this.right = right;
            this.count = count;
            this.height = height;
        }
    }
    protected AvlNode<K, V> root;
    protected int size = 0;

    @Override
    public V find(K k) {
        AvlNode<K, V> result = findNode(k);
        return result != null ? result.entry.getValue() : null;
    }

    protected AvlNode<K, V> findNode(K k) {
        AvlNode<K, V> x = root;
        while (x != null) {
            if (x.entry.getKey().compareTo(k) == 0) {
                return x;
            } else if (x.entry.getKey().compareTo(k) > 0) {
                x = x.left;
            } else if (x.entry.getKey().compareTo(k) < 0) {
                x = x.right;
            }
        }
        return null;
    }

    @Override
    public Entry<K, V> findMin() {
        AvlNode<K, V> result = findMin(root);
        return result != null ? findMin(root).entry : null;
    }

    /*
     * Find mininum value node in the subtree rooted at node
     */
    public AvlNode<K, V> findMin(AvlNode<K, V> node) {
        AvlNode<K, V> result = null;
        while (node != null) {
            result = node;
            node = node.left;
        }
        return result;
    }

    @Override
    public Entry<K, V> findMax() {
        AvlNode<K, V> result = findMax(root);
        return result != null ? findMax(root).entry : null;
    }

    /*
     * Find maximum value node in the subtree rooted at node
     */
    public AvlNode<K, V> findMax(AvlNode<K, V> node) {
        AvlNode<K, V> result = null;
        while (node != null) {
            result = node;
            node = node.right;
        }
        return result;
    }

    @Override
    public void insert(K k, V v) {
        Entry<K, V> entry = new Entry<K, V>(k, v);
        AvlNode<K, V> node = new AvlNode<K, V>(entry, null, null, null, 0, 0);
        Stack<AvlNode<K, V>> backTrack = new Stack<AvlNode<K, V>>();
        insert(node, backTrack);
    }

    protected void insert(AvlNode<K, V> node, Stack<AvlNode<K, V>> backTrack) {

        if (root == null) {
            root = node;
            size++;
            return;
        }

        AvlNode<K, V> p = root;

        while (true) {
            if (node.entry.getKey().compareTo(p.entry.getKey()) == 0) {
                node.entry.setKey(p.entry.getKey());
                node.entry.setValue(p.entry.getValue());
                return;
            }

            backTrack.push(p);

            if (node.entry.getKey().compareTo(p.entry.getKey()) < 0) {
                if (p.left == null) {
                    p.left = node;
                    break;
                }
                p = p.left;
            } else {
                if (p.right == null) {
                    p.right = node;
                    break;
                }
                p = p.right;
            }
        }
        root = balance(root, backTrack);
        size++;
    }

    @Override
    public V remove(K k) {
        if (k == null) {
            return null;
        }
        Stack<AvlNode<K, V>> backTrack = new Stack<AvlNode<K, V>>();
        Entry<K, V> result = remove(k, backTrack);
        if (result != null) {
            return result.getValue();
        } else {
            return null;
        }
    }

    /*
     * Removes the node passed and returns the node which replaced the node.
     */
    protected void replace(AvlNode<K, V> parent, AvlNode<K, V> x, AvlNode<K, V> y) {
        if (parent == null) {
            root = y;
        } else if (x == parent.left) {
            parent.left = y;
        } else if (x == parent.right) {
            parent.right = y;
        }
    }

    /*
     * Find mininum value node in the subtree rooted at node
     */
    public AvlNode<K, V> findMin(AvlNode<K, V> node, Stack<AvlNode<K, V>> backTrack) {
        AvlNode<K, V> result = null;
        while (node != null) {
            result = node;
            backTrack.push(node);
            node = node.left;
        }
        return result;
    }

    protected Entry<K, V> remove(K k, Stack<AvlNode<K, V>> backTrack) {

        if (root == null) {
            return null;
        }
        Entry<K, V> result = null;
        AvlNode<K, V> x = root;

        while (true) {
            if (k.compareTo(x.entry.getKey()) == 0) {
                result = x.entry;

                AvlNode<K, V> parent = null;
                if (!backTrack.isEmpty()) {
                    parent = backTrack.peek();
                }

                if (x.left == null) {
                    replace(parent, x, x.right);
                } else if (x.right == null) {
                    replace(parent, x, x.left);
                } else {
                    AvlNode<K, V> successor = findMin(x.right, backTrack);
                    if (x.right != successor) {
                        backTrack.pop();
                        replace(backTrack.peek(), successor, successor.right);
                        x.entry = successor.entry;
                    } else {
                        replace(parent, x, successor);
                        successor.left = x.left;
                    }
                }
                size--;
                break;
            }
            backTrack.push(x);

            if (k.compareTo(x.entry.getKey()) < 0) {
                if (x.left == null) {
                    break;
                }
                x = x.left;
            } else {
                if (x.right == null) {
                    break;
                }
                x = x.right;
            }
        }
        if (result == null) {
            return null;
        }
        root = balance(root, backTrack);
        return result;
    }

    @Override
    public int removeValue(V v) {
        List<K> list = new LinkedList<K>();
        matchToList(root, v, list);
        int count = list.size();
        for (K k : list) {
            remove(k);
        }
        return count;
    }

    protected void matchToList(AvlNode<K, V> node, V v, List<K> entryList) {
        if (node != null) {
            matchToList(node.left, v, entryList);
            if (node.entry.getValue().equals(v)) {
                entryList.add(node.entry.getKey());
            }
            matchToList(node.right, v, entryList);
        }
    }

    private AvlNode<K, V> balance(AvlNode<K, V> tree, Stack<AvlNode<K, V>> backTrack) {

        while (!backTrack.isEmpty()) {
            AvlNode<K, V> parent = backTrack.pop();

            int balance = height(parent.right) - height(parent.left);
            if (balance < -1) {
                AvlNode<K, V> oldParent = parent;
                if (height(parent.left.left) < height(parent.left.right)) {
                    parent.left = rotateLeft(parent.left);
                }
                parent = rotateRight(parent);
                if (backTrack.empty()) {
                    tree = parent;
                } else if (oldParent == backTrack.peek().right) {
                    backTrack.peek().right = parent;
                } else {
                    backTrack.peek().left = parent;
                }
            } else if (balance > 1) {
                AvlNode<K, V> oldParent = parent;
                if (height(parent.right.left) > height(parent.right.right)) {
                    parent.right = rotateRight(parent.right);
                }
                parent = rotateLeft(parent);
                if (backTrack.empty()) {
                    tree = parent;
                } else if (oldParent == backTrack.peek().right) {
                    backTrack.peek().right = parent;
                } else {
                    backTrack.peek().left = parent;
                }
            }
            parent.height = Math.max(height(parent.left), height(parent.right)) + 1;
        }
        return tree;
    }

    private AvlNode<K, V> rotateRight(AvlNode<K, V> k2) {
        AvlNode<K, V> k1 = k2.left;
        k2.left = k1.right;
        k1.right = k2;
        k2.height = Math.max(height(k2.left), height(k2.right)) + 1;
        k1.height = Math.max(height(k1.left), k2.height) + 1;
        return k1;
    }

    private AvlNode<K, V> rotateLeft(AvlNode<K, V> k1) {
        AvlNode<K, V> k2 = k1.right;
        k1.right = k2.left;
        k2.left = k1;
        k1.height = Math.max(height(k1.left), height(k1.right)) + 1;
        k2.height = Math.max(height(k2.right), k1.height) + 1;
        return k2;
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
        StringBuilder sbuf = new StringBuilder();
        return "BinarySearchTree-[" + sbuf.toString() + "]";
    }

    @Override
    public void print(Tree.TRAVERSAL type) {

        if (type == Tree.TRAVERSAL.IN_ORDER) {
            printInOrder(root);
        } else if (type == Tree.TRAVERSAL.PRE_ORDER) {
            printPreOrder(root);
        } else if (type == Tree.TRAVERSAL.POST_ORDER) {
            printPostOrder(root);
        } else if (type == Tree.TRAVERSAL.LEVEL_ORDER) {
            printLevelOrder(root);
        }
        System.out.println();

    }

    protected void printInOrder(AvlNode<K, V> k) {
        if (k != null) {
            printInOrder(k.left);
            System.out.print(k.entry.getKey() + " ");
            printInOrder(k.right);
        }
    }

    protected void printPreOrder(AvlNode<K, V> k) {
        if (k != null) {
            System.out.print(k.entry.getKey() + " ");
            printPreOrder(k.left);
            printPreOrder(k.right);
        }
    }

    protected void printPostOrder(AvlNode<K, V> k) {
        if (k != null) {
            printPostOrder(k.left);
            printPostOrder(k.right);
            System.out.print(k.entry.getKey() + " ");
        }

    }

    public void printLevelOrder(AvlNode<K, V> node) {

        Queue<AvlNode<K, V>> queue = new LinkedList<AvlNode<K, V>>();
        if (node != null) {
            queue.offer(node);
            printNodeQueue(queue);
        }
    }

    /*
     * Assuming queue.size() is O(1)
     */
    protected void printNodeQueue(Queue<AvlNode<K, V>> queue) {
        if (queue.isEmpty()) {
            return;
        }

        int levelSize = queue.size();
        int i = 0;
        StringBuilder sb = new StringBuilder();

        while (i < levelSize) {
            AvlNode<K, V> node = queue.poll();
            if (node.left != null) {
                queue.offer(node.left);
            }

            if (node.right != null) {
                queue.offer(node.right);
            }

            if (node != null) {
                sb.append(node.entry.getKey());
                sb.append(" ");
            }
            i++;
        }
        sb.append(System.getProperty("line.separator"));
        System.out.println(sb.toString());
        printNodeQueue(queue);
    }

    @Override
    public int height() {
        return height(root);
    }

    protected int height(AvlNode<K, V> node) {
        if (node == null) {
            return -1;
        } else {
            return node.height;
        }
    }

    @Override
    public List<Entry<K, V>> toList(Tree.TRAVERSAL type) {
        List<Entry<K, V>> entryList = new ArrayList<Entry<K, V>>();
        if (type == Tree.TRAVERSAL.IN_ORDER) {
            inOrderToList(root, entryList);
        } else if (type == Tree.TRAVERSAL.PRE_ORDER) {
            preOrderToList(root, entryList);
        } else if (type == Tree.TRAVERSAL.POST_ORDER) {
            postOrderToList(root, entryList);
        } else if (type == Tree.TRAVERSAL.LEVEL_ORDER) {
            levelOrderToList(root, entryList);
        }
        return entryList;
    }

    protected void inOrderToList(AvlNode<K, V> k, List<Entry<K, V>> entryList) {
        if (k != null) {
            inOrderToList(k.left, entryList);
            entryList.add(k.entry);
            inOrderToList(k.right, entryList);
        }
    }

    protected void preOrderToList(AvlNode<K, V> k, List<Entry<K, V>> entryList) {
        if (k != null) {
            entryList.add(k.entry);
            preOrderToList(k.left, entryList);
            preOrderToList(k.right, entryList);
        }
    }

    protected void postOrderToList(AvlNode<K, V> k, List<Entry<K, V>> entryList) {
        if (k != null) {
            postOrderToList(k.left, entryList);
            postOrderToList(k.right, entryList);
            entryList.add(k.entry);
        }
    }

    protected void levelOrderToList(AvlNode<K, V> node, List<Entry<K, V>> entryList) {
        Queue<AvlNode<K, V>> queue = new LinkedList<AvlNode<K, V>>();
        queue.offer(node);
        entryList.add(node.entry);
        printNodeQueue(queue, entryList);
    }

    /*
     * Assuming queue.size() is O(1)
     */
    protected void printNodeQueue(Queue<AvlNode<K, V>> queue,
            List<Entry<K, V>> entryList) {
        if (queue.isEmpty()) {
            return;
        }

        int levelSize = queue.size();
        int i = 0;

        while (i < levelSize) {
            AvlNode<K, V> node = queue.poll();
            if (node.left != null) {
                queue.offer(node.left);
                entryList.add(node.left.entry);
            }

            if (node.right != null) {
                queue.offer(node.right);
                entryList.add(node.right.entry);
            }
            i++;
        }
        printNodeQueue(queue, entryList);
    }

    public Dictionary<K, V> copy() {
        AvlTree<K, V> copy = new AvlTree<K, V>();

        List<Entry<K, V>> entries = this.toList(TRAVERSAL.PRE_ORDER);

        for (Entry<K, V> entry : entries) {
            copy.insert(entry.getKey(), entry.getValue());
        }

        return copy;
    }

    /*
     *
     */
    public int[] count() {
        int[] counts = new int[]{0, 0, 0};
        countNodes(counts, root);
        return counts;
    }

    protected void countNodes(int[] counts, AvlNode<K, V> node) {

        if (node != null) {
            counts[0]++;
        }

        if (node.right == null && node.left == null && node != root) {
            counts[1]++;
        }

        if (node.left != null && node.right != null) {
            counts[2]++;
        }

        if (node.left != null) {
            countNodes(counts, node.left);
        }

        if (node.right != null) {
            countNodes(counts, node.right);
        }

    }

    public Iterator<Entry<K, V>> iterator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

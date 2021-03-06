

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.ArrayList;

public class SplayTree<K extends Comparable<K>, V extends Comparable<V>>
        implements Tree<K, V>, Dictionary<K, V> {

    protected static class Node<K extends Comparable<K>, V extends Comparable<V>> {

        protected Entry<K, V> entry;
        protected Node<K, V> parent;
        protected Node<K, V> left;
        protected Node<K, V> right;

        public Node(Entry<K, V> itm, Node<K, V> parent, Node<K, V> left,
                Node<K, V> right, int count, int height) {
            this.entry = itm;
            this.parent = parent;
            this.left = left;
            this.right = right;
        }

        public void setParent(Node<K, V> node) {
            this.parent = node;
        }

        public void setLeft(Node<K, V> node) {
            this.left = node;
            if (node != null) {
                node.parent = this;
            }
        }

        public void setRight(Node<K, V> node) {
            this.right = node;
            if (node != null) {
                node.parent = this;
            }
        }

        public boolean isLeftChild(Node<K, V> node) {
            if (this.left != null && node != null) {
                return this.left == node;
            } else {
                return false;
            }
        }

        public boolean isRightChild(Node<K, V> node) {
            if (this.right != null && node != null) {
                return this.right == node;
            } else {
                return false;
            }
        }

        public boolean isLeftChild() {
            if (parent != null) {
                return parent.left == this;
            } else {
                return false;
            }
        }

        public boolean isRightChild() {
            if (parent != null) {
                return parent.right == this;
            } else {
                return false;
            }
        }
    }
    protected Node<K, V> root;
    protected int size = 0;

    @Override
    public void insert(K k, V v) {
        Entry<K, V> entry = new Entry<K, V>(k, v);
        Node<K, V> node = new Node<K, V>(entry, null, null, null, 0, -1);
        insert(node);

    }

    protected void insert(Node<K, V> node) {

        Node<K, V> y = null;
        Node<K, V> x = root;

        while (x != null) {
            y = x;
            if (x.entry.getKey().compareTo(node.entry.getKey()) > 0) {
                x = x.left;
            } else if (x.entry.getKey().compareTo(node.entry.getKey()) < 0) {
                x = x.right;
            } else {
                x.entry.setKey(node.entry.getKey());
                x.entry.setValue(node.entry.getValue());
                root = splay(x);
                return;
            }
        }
        node.parent = y;

        if (y == null) {
            root = node;
        } else {
            if (y.entry.getKey().compareTo(node.entry.getKey()) > 0) {
                y.left = node;
            } else if (y.entry.getKey().compareTo(node.entry.getKey()) < 0) {
                y.right = node;
            }
        }

        root = splay(node);
        size++;// increase size
    }

    @Override
    public V find(K k) {
        Node<K, V> result = findNode(k);
        return result != null ? result.entry.getValue() : null;
    }

    protected Node<K, V> findNode(K k) {
        Node<K, V> x = root;
        Node<K, V> parent = null;
        while (x != null) {
            parent = x;
            if (x.entry.getKey().compareTo(k) == 0) {
                break;
            } else if (x.entry.getKey().compareTo(k) > 0) {
                x = x.left;
            } else if (x.entry.getKey().compareTo(k) < 0) {
                x = x.right;
            }
        }
        if (x != null) {
            root = splay(x);
        } else if (parent != null) {
            root = splay(parent);
        }
        return x;
    }

    @Override
    public Entry<K, V> findMin() {
        Node<K, V> result = findMin(root);
        return result != null ? result.entry : null;
    }

    /*
     * Find mininum value node in the subtree rooted at node
     */
    public Node<K, V> findMin(Node<K, V> node) {
        Node<K, V> result = null;
        while (node != null) {
            result = node;
            node = node.left;
        }
        if (result != null) {
            root = splay(result);
        }
        return result;
    }

    @Override
    public Entry<K, V> findMax() {
        Node<K, V> result = findMax(root);
        return result != null ? result.entry : null;
    }

    /*
     * Find maximum value node in the subtree rooted at node
     */
    public Node<K, V> findMax(Node<K, V> node) {
        Node<K, V> result = null;
        while (node != null) {
            result = node;
            node = node.right;
        }
        if (result != null) {
            root = splay(result);
        }
        return result;
    }

    @Override
    public V remove(K k) {

        if (k == null) {
            return null;
        }
        Node<K, V> x = root;
        while (x != null) {
            if (x.entry.getKey().compareTo(k) == 0) {
                break;
            } else if (x.entry.getKey().compareTo(k) > 0) {
                x = x.left;
            } else if (x.entry.getKey().compareTo(k) < 0) {
                x = x.right;
            }
        }
        if (x != null) {
            remove(x);
            return x.entry.getValue();
        } else {
            return null;
        }
    }

    protected Node<K, V> remove(Node<K, V> node) {

        Node<K, V> parent = node.parent;
        Node<K, V> result = null;
        if (node.left == null) {
            replace(node, node.right);
            result = node.right;
        } else if (node.right == null) {
            replace(node, node.left);
            result = node.left;
        } else {

            Node<K, V> successor = null;
            Node<K, V> x = node.right;
            while (x != null) {
                successor = x;
                x = x.left;
            }
            if (successor.parent != node) {
                replace(successor, successor.right);
                successor.right = node.right;
                successor.right.parent = successor;
            }
            replace(node, successor);
            successor.left = node.left;
            successor.left.parent = successor;
            result = successor;
        }
        if (parent != null) {
            root = splay(parent);
        }
        size--;
        return result;
    }

    /*
     * replace a subtree rooted at x with subtree rooted a y
     */
    protected void replace(Node<K, V> x, Node<K, V> y) {
        if (x.parent == null) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else if (x == x.parent.right) {
            x.parent.right = y;
        }
        if (y != null) {
            y.parent = x.parent;
        }
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

    protected void matchToList(Node<K, V> node, V v, List<K> entryList) {
        if (node != null) {
            matchToList(node.left, v, entryList);
            if (node.entry.getValue().equals(v)) {
                entryList.add(node.entry.getKey());
            }
            matchToList(node.right, v, entryList);
        }
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

    protected void printInOrder(Node<K, V> k) {
        if (k != null) {
            printInOrder(k.left);
            System.out.print(k.entry.getKey() + " ");
            printInOrder(k.right);
        }
    }

    protected void printPreOrder(Node<K, V> k) {
        if (k != null) {
            System.out.print(k.entry.getKey() + " ");
            printPreOrder(k.left);
            printPreOrder(k.right);
        }
    }

    protected void printPostOrder(Node<K, V> k) {
        if (k != null) {
            printPostOrder(k.left);
            printPostOrder(k.right);
            System.out.print(k.entry.getKey() + " ");
        }

    }

    public void printLevelOrder(Node<K, V> node) {

        Queue<Node<K, V>> queue = new LinkedList<Node<K, V>>();
        if (node != null) {
            queue.offer(node);
            printNodeQueue(queue);
        }
    }

    /*
     * Assuming queue.size() is O(1)
     */
    protected void printNodeQueue(Queue<Node<K, V>> queue) {
        if (queue.isEmpty()) {
            return;
        }

        int size = queue.size();
        int i = 0;
        StringBuilder sb = new StringBuilder();

        while (i < size) {
            Node<K, V> node = queue.poll();
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

    protected int height(Node<K, V> node) {
        if (node == null) {
            return -1;
        } else {
            return 1 + Math.max(height(node.left), height(node.right));
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

    protected void inOrderToList(Node<K, V> k, List<Entry<K, V>> entryList) {
        if (k != null) {
            inOrderToList(k.left, entryList);
            entryList.add(k.entry);
            inOrderToList(k.right, entryList);
        }
    }

    protected void preOrderToList(Node<K, V> k, List<Entry<K, V>> entryList) {
        if (k != null) {
            entryList.add(k.entry);
            preOrderToList(k.left, entryList);
            preOrderToList(k.right, entryList);
        }
    }

    protected void postOrderToList(Node<K, V> k, List<Entry<K, V>> entryList) {
        if (k != null) {
            postOrderToList(k.left, entryList);
            postOrderToList(k.right, entryList);
            entryList.add(k.entry);
        }
    }

    protected void levelOrderToList(Node<K, V> node, List<Entry<K, V>> entryList) {
        Queue<Node<K, V>> queue = new LinkedList<Node<K, V>>();
        queue.offer(node);
        entryList.add(node.entry);
        printNodeQueue(queue, entryList);
    }

    /*
     * Assuming queue.size() is O(1)
     */
    protected void printNodeQueue(Queue<Node<K, V>> queue,
            List<Entry<K, V>> entryList) {
        if (queue.isEmpty()) {
            return;
        }

        int size = queue.size();
        int i = 0;

        while (i < size) {
            Node<K, V> node = queue.poll();
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
        SplayTree<K, V> copy = new SplayTree<K, V>();

        List<Entry<K, V>> entries = this.toList(TRAVERSAL.PRE_ORDER);

        for (Entry<K, V> entry : entries) {
            copy.insert(entry.getKey(), entry.getValue());
        }

        return copy;
    }

    public void zigzig(K k) {
        Node<K, V> c = findNode(k);
        root = zigzig(c);
    }

    private Node<K, V> zigzig(Node<K, V> c) {

        //when c is root
        if (c == root) {
            return c;
        }

        Node<K, V> p = c.parent;
        Node<K, V> g = c.parent.parent;
        if (g != null) {
            if (g.isLeftChild(p)) {
                g.setLeft(c);
                p.setLeft(c.right);
                c.setRight(p);
                //g is not root
                if (g != root) {
                    if (g.isLeftChild()) {
                        g.parent.setLeft(c);
                    } else {
                        g.parent.setRight(c);
                    }
                } else {
                    c.setParent(null);
                }
                g.setLeft(p.right);
                p.setRight(g);
            } else {
                g.setRight(c);
                p.setRight(c.left);
                c.setLeft(p);
                //g is not root
                if (g != root) {
                    if (g.isLeftChild()) {
                        g.parent.setLeft(c);
                    } else {
                        g.parent.setRight(c);
                    }
                } else {
                    c.setParent(null);
                }
                g.setRight(p.left);
                p.setLeft(g);
            }

        } else {
            //when p is root
            if (p.isLeftChild(c)) {
                c.setParent(g);
                p.setLeft(c.right);
                c.setRight(p);
            } else {
                c.setParent(g);
                p.setRight(c.left);
                c.setLeft(p);
            }
            c.setParent(null);
        }
        return c;
    }

    public void zigzag(K k) {
        Node<K, V> c = findNode(k);
        root = zigzag(c);
    }

    private Node<K, V> zigzag(Node<K, V> c) {
        //when c is root
        if (c == root) {
            return c;
        }

        Node<K, V> p = c.parent;
        Node<K, V> g = c.parent.parent;

        if (g.parent != null) {

            if (g.isLeftChild(p) && p.isRightChild(c)) {

                if (g.isLeftChild()) {
                    g.parent.setLeft(c);
                } else {
                    g.parent.setRight(c);
                }

                p.setRight(c.left);
                g.setLeft(c.right);

                c.setLeft(p);
                c.setRight(g);


            } else if (g.isRightChild(p) && p.isLeftChild(c)) {
                if (g.isLeftChild()) {
                    g.parent.setLeft(c);
                } else {
                    g.parent.setRight(c);
                }
                p.setLeft(c.right);
                g.setRight(c.left);

                c.setRight(p);
                c.setLeft(g);
            }
        } else {
            //g is root
            if (g.isLeftChild(p) && p.isRightChild(c)) {
                p.setRight(c.left);
                g.setLeft(c.right);
                c.setLeft(p);
                c.setRight(g);
            } else if (g.isRightChild(p) && p.isLeftChild(c)) {
                p.setLeft(c.right);
                g.setRight(c.left);
                c.setRight(p);
                c.setLeft(g);
            }
            c.setParent(null);
        }
        return c;
    }

    public void splay(K k) {
        Node<K, V> c = findNode(k);
        root = splay(c);
    }

    private Node<K, V> splay(Node<K, V> c) {
        //when c is root
        if (c == root) {
            return c;
        }
        Node<K, V> p = c.parent;
        Node<K, V> g = c.parent.parent;
        while (true) {
            if (g != null && p != null) {
                if (g.isLeftChild(p) && p.isLeftChild(c) || g.isRightChild(p) && p.isRightChild(c)) {
                    c = zigzig(c);
                } else if (g.isLeftChild(p) && p.isRightChild(c) || g.isRightChild(p) && p.isLeftChild(c)) {
                    c = zigzag(c);
                }
            } else {
                c = zigzig(c);
            }
            if (c.parent == null) {
                break;
            } else {
                p = c.parent;
                if (p.parent != null) {
                    g = c.parent.parent;
                } else {
                    g = null;
                }
            }
        }
        return c;
    }
}

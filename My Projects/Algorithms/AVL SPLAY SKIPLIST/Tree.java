

import java.util.List;

public interface Tree<K extends Comparable<K>, V extends Comparable<V>> {

    public enum TRAVERSAL {

        PRE_ORDER, IN_ORDER, POST_ORDER, LEVEL_ORDER
    }

    public void print(TRAVERSAL type);

    public int height();

    public List<Entry<K, V>> toList(TRAVERSAL type);
}


public class ProbeTableEntry<K extends Comparable<K>, V extends Comparable<V>> extends Entry<K, V> {

    private boolean deleted;

    public ProbeTableEntry(K k, V v) {
        super(k, v);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "ProbeTableEntry [entry=" + super.toString() + ", deleted=" + deleted + "]";
    }
}

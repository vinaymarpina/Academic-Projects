
public class Command<K extends Comparable<K>, V extends Comparable<V>> {

    public enum Type {

        INSERT, REMOVE, REMOVE_VALUE, FIND, FIND_MIN, FIND_MAX, SIZE, IS_EMPTY, ZIG, ZAG, SPLAY
    }
    private final Type type;
    private final K key;
    private final V value;

    public Command(Type type, K key, V value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "[" + type + ", key=" + key + ", value=" + value + "]";
    }

    public static Command<Long, Long> parseCommand(String str) {

        String[] tokens = str.split(" ");
        if (tokens.length == 3) {
            if (tokens[0].equals("Insert")) {
                return new Command<Long, Long>(Command.Type.INSERT, Long.parseLong(tokens[1]), Long.parseLong(tokens[2]));
            }
        }

        if (tokens.length == 2) {
            if (tokens[0].equals("Find")) {
                return new Command<Long, Long>(Command.Type.FIND, Long.parseLong(tokens[1]), null);
            }

            if (tokens[0].equals("Remove")) {
                return new Command<Long, Long>(Command.Type.REMOVE, Long.parseLong(tokens[1]), null);
            }

            if (tokens[0].equals("RemoveValue")) {
                return new Command<Long, Long>(Command.Type.REMOVE_VALUE, null, Long.parseLong(tokens[1]));
            }

        }

        if (tokens.length == 1) {
            if (tokens[0].equals("FindMax")) {
                return new Command<Long, Long>(Command.Type.FIND_MAX, null, null);
            }
            if (tokens[0].equals("FindMin")) {
                return new Command<Long, Long>(Command.Type.FIND_MIN, null, null);
            }
            if (tokens[0].equals("Size")) {
                return new Command<Long, Long>(Command.Type.SIZE, null, null);
            }

        }
        return null;
    }
}


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author hkanna
 */
public class SkipListRun {

    public static void main(String[] args) {
        System.out.println("-----------------------Running SkipList benchmark---------------------------");
        if (args == null || !(args.length > 0)) {
            System.out.println("Run as : java full_input_file_path full_output_file_path");
            System.exit(-1);
        }
        System.out.println("Input file: " + args[0]);
        if (args.length == 2) {
            System.out.println("Output file: " + args[1]);
        }
        System.out.println("---------------------------------------------------------------------------");
        FileReader fr = null;
        BufferedReader br = null;

        Map<Command.Type, Integer> commandCount = new HashMap<Command.Type, Integer>();
        commandCount.put(Command.Type.INSERT, 0);
        commandCount.put(Command.Type.FIND, 0);
        commandCount.put(Command.Type.REMOVE, 0);
        commandCount.put(Command.Type.REMOVE_VALUE, 0);
        commandCount.put(Command.Type.FIND_MIN, 0);
        commandCount.put(Command.Type.FIND_MAX, 0);
        commandCount.put(Command.Type.SIZE, 0);

        SkipList<Long, Long> skipList = new SkipList<Long, Long>();

        try {
            fr = new FileReader(args[0]);
            br = new BufferedReader(fr);
            // StringBuilder sb = new StringBuilder();
            long sum = 0;
            String line = null;

            StopWatch stopWatch = StopWatch.getInstance();
            while ((line = br.readLine()) != null) {
                Command<Long, Long> command = Command.parseCommand(line);
                if (command != null) {
                    if (Command.Type.INSERT.equals(command.getType())) {
                        skipList.insert(command.getKey(), command.getValue());
                        commandCount.put(Command.Type.INSERT, commandCount.get(Command.Type.INSERT) + 1);
                    } else if (Command.Type.FIND.equals(command.getType())) {
                        commandCount.put(Command.Type.FIND, commandCount.get(Command.Type.FIND) + 1);
                        skipList.find(command.getKey());
                    } else if (Command.Type.REMOVE.equals(command.getType())) {
                        commandCount.put(Command.Type.REMOVE, commandCount.get(Command.Type.REMOVE) + 1);
                        skipList.remove(command.getKey());
                    } else if (Command.Type.REMOVE_VALUE.equals(command.getType())) {
                        commandCount.put(Command.Type.REMOVE_VALUE, commandCount.get(Command.Type.REMOVE_VALUE) + 1);
                        sum += skipList.removeValue(command.getValue());
                    } else if (Command.Type.FIND_MIN.equals(command.getType())) {
                        commandCount.put(Command.Type.FIND_MIN, commandCount.get(Command.Type.FIND_MIN) + 1);
                        sum += skipList.findMin().getValue();
                    } else if (Command.Type.FIND_MAX.equals(command.getType())) {
                        commandCount.put(Command.Type.FIND_MAX, commandCount.get(Command.Type.FIND_MAX) + 1);
                        sum += skipList.findMax().getValue();
                    } else if (Command.Type.SIZE.equals(command.getType())) {
                        commandCount.put(Command.Type.SIZE, commandCount.get(Command.Type.SIZE) + 1);
                        sum += skipList.size();
                    }
                }
            }
            System.out.println("Time taken: " + stopWatch.elapsedTime() + " secs");
            System.out.println("Insert: " + commandCount.get(Command.Type.INSERT));
            System.out.println("Find: " + commandCount.get(Command.Type.FIND));
            System.out.println("Remove: " + commandCount.get(Command.Type.REMOVE));
            System.out.println("RemoveValue: " + commandCount.get(Command.Type.REMOVE_VALUE));
            System.out.println("FindMin: " + commandCount.get(Command.Type.FIND_MIN));
            System.out.println("FindMax: " + commandCount.get(Command.Type.FIND_MAX));
            System.out.println("Size: " + commandCount.get(Command.Type.SIZE));

            System.out.println("Total:" + (commandCount.get(Command.Type.INSERT) + commandCount.get(Command.Type.FIND) + commandCount.get(Command.Type.REMOVE) + commandCount.get(Command.Type.REMOVE_VALUE) + commandCount.get(Command.Type.FIND_MIN) + commandCount.get(Command.Type.FIND_MAX) + commandCount.get(Command.Type.SIZE)));
            System.out.println("Sum: " + sum);
            fr.close();
            fr = null;

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {

            try {
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        System.out.println("---------------------------------------------------------------------------");
        System.out.println("Min:" + skipList.findMin().getKey());
        System.out.println("Max:" + skipList.findMax().getKey());
        System.out.println("Total size:" + skipList.size());

        System.out.println("-------------Writing output file and checking the sort order---------------");

        FileWriter fw = null;
        BufferedWriter bw = null;
        try {

            fw = new FileWriter(args[1]);
            bw = new BufferedWriter(fw);

            Iterator<Entry<Long, Long>> itr = skipList.iterator();
            Entry<Long, Long> prev = itr.next();
            boolean isValid = true;
            while (itr.hasNext()) {
                Entry<Long, Long> curr = itr.next();
                if (curr != null) {
                    bw.append(curr.getKey().toString() + " " + curr.getValue().toString());
                    bw.append(System.getProperty("line.separator"));
                    if (prev.getKey().compareTo(curr.getKey()) > 0 || prev.getKey().compareTo(curr.getKey()) == 0) {
                        isValid = false;
                    }
                    prev = curr;
                }
            }
            if (isValid) {
                System.out.println("Done. List is in sorted order");
            } else {
                System.out.println("Done. List not in sorted order");
            }

            fw.flush();
            fw.close();
            fw = null;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {

            try {
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        System.out.println("-------------------------------End-----------------------------------------");
    }
}

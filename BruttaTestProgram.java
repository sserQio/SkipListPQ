import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

// CLASS MyEntry
class MyEntry {
    private Integer key;
    private String value;
    public MyEntry(Integer key, String value) {
        this.key = key;
        this.value = value;
    }
    public Integer getKey() {
        return key;
    }
    public String getValue() {
        return value;
    }
    @Override
    public String toString() {
        return key + " " + value;
    }
}

// CLASS Node
class Node {
    MyEntry entry;
    Node[] forward;

    public Node(MyEntry entry, int level) {
        this.entry = entry;
        this.forward = new Node[level + 1];
    }
}

// CLASS SkipListPQ
class SkipListPQ {

    private int maxLevel;
    private int level;
    private Node head;
    private double alpha;
    private Random rand = new Random();
    private int entryNumber;


    public SkipListPQ(double alpha) {
        this.alpha = alpha;
        maxLevel = 10;
        level = 0;
        head = new Node(new MyEntry(Integer.MIN_VALUE, null), maxLevel); // MIN_VALUE to represent -infinity
        rand = new Random();
        entryNumber = 0;
    }

    // Returns the number of entries in S
    public int size() {
        return entryNumber;
    }

    // Returns an entry of S with minimum key and prints the key and the value of the entry separated by space
    public MyEntry min() {
        if (head.forward[0] == null) return null; // if the entry of the next node is null (the next entry is the tail) then return null
        return head.forward[0].entry;
    }

    // inserts a new entry e = (key, string) in S
    public int insert(int key, String value) {
        int visited = 0;
        Node current = head;

        // Array of nodes that keeps track of the node that need to have its forward pointers updated
        // during the insertion. Size is due to the fact that skip lists use levels indexed from 0 to maxLevel
        Node[] update = new Node[maxLevel + 1];

        // No need to check if there is already an item with the same key, in that case it must be added regardless

        for (int i = level; i >= 0; i--){
            while (current.forward[i] != null && current.forward[i].entry.getKey() < key){
                current = current.forward[i];
                visited++;
            }
            update[i] = current;
        }
        visited++;
        current = current.forward[0]; // Bring the node current to level 0

        int level2 = 0;
        level2 = generateEll(alpha, key);
        if (level2 > level){
            for (int i = level + 1; i <= level2; i++){
                update[i] = head;
            }
            level = level2;
        }
        
        Node node2 = new Node(new MyEntry(key, value), level2);
        for (int i = 0; i <= level2; i++){
            // node2 now points forward to the node that was previously pointed to by the update node 
            node2.forward[i] = update[i].forward[i]; 
            update[i].forward[i] = node2; // update now points forward to node2
            // node2 has been inserted after the update node
        }

        entryNumber++; // A node has been added
        return visited;
    }

    private int generateEll(double alpha_ , int key) {
        int level = 0;
        if (alpha_ >= 0. && alpha_< 1) {
            while (rand.nextDouble() < alpha_) {
                level += 1;
            }
        } else {
            while (key != 0 && key % 2 == 0){
                key = key / 2;
                level += 1;
            }
        }
        return level;
    }

    public MyEntry removeMin() {
        // Check if the skipList is empty
        if (head.forward[0] == null) return null;
        Node min = head.forward[0];
        for (int i = 0; i < level; i++) min.forward[i] = head.forward[i];
        entryNumber--;
        while (min.forward[level] == null && level > 0) level--;

        return min.entry;
    }

    public void print() {
        Node n = head.forward[0];
        while (n != null){
            int lvl_height = n.forward.length; // Size of the vertical list corresponding to the forward entry
            System.out.println(n.entry.getKey() + " " + n.entry.getValue() + lvl_height); 
            n = n.forward[0]; // Goes to the next entry
        }
    }
}

//TestProgram

public class BruttaTestProgram {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java TestProgram <file_path>");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
            String[] firstLine = br.readLine().split(" ");
            int N = Integer.parseInt(firstLine[0]);
            double alpha = Double.parseDouble(firstLine[1]);
            System.out.println(N + " " + alpha);

            SkipListPQ skipList = new SkipListPQ(alpha);

            for (int i = 0; i < N; i++) {
                String[] line = br.readLine().split(" ");
                int operation = Integer.parseInt(line[0]);

                switch (operation) {
                    case 0: // min 
                        MyEntry min = skipList.min();
                        System.out.println(min);
                        break;
                    case 1: // removeMin
                        MyEntry rmin = skipList.removeMin();
                        System.out.println(rmin);
                        break;
                    case 2: // insert 

                        break;
                    case 3: // print
                        skipList.print();
                        break;
                    default:
                        System.out.println("Invalid operation code");
                        return;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}

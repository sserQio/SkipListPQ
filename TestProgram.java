import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

//Class my entry
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

// Class Node
class Node {
    MyEntry entry;
    Node forward, backward, above, below;

    // Creates an empty node
    public Node(MyEntry entry){
        this.entry = entry;
        forward = null;
        backward = null;
        above = null;
        below = null;
    }
}

//Class SkipListPQ
class SkipListPQ {

    private double alpha;
    private Random rand;
    int entry_number;
    int level;
    int max_level;
    Node head, tail;

    /* 
     * Creates an empty Skip List with only two nodes with null value and
     * +- infinity keys on level 0
     */
    public SkipListPQ(double alpha) {
        entry_number = 0;
        max_level = 16;
        level = 0;      // Using row indexes, the lowest one would be S_0
        this.alpha = alpha;
        this.rand = new Random();
        head = new Node(new MyEntry(Integer.MIN_VALUE, null));
        tail = new Node(new MyEntry(Integer.MAX_VALUE, null));
    }

    /* 
     * Search for a node in the Skip List
     * 
     * Search for a node with given key
     * @param key The key to search for
     * @return The node with the matching key or null
     */
    public Node SkipSearch(int key) {
    Node current = head;

    while (current.below != null) {
        current = current.below; // Move down one level
        while (current.forward != null && current.forward.entry.getKey() <= key){
            current = current.forward; // Move forward
        }
    }

    // Return the node with the largest key less than or equal to the given key
    return current;
    }


    /* 
     * Adds a new level to the Skip List
     * 
     * Adds a new level on the top of the Skip List, this level consists
     * of only a +/- infinity keys nodes
     */
    private void addNewLevel() {
        Node newHead = new Node(new MyEntry(Integer.MIN_VALUE, null));
        Node newTail = new Node(new MyEntry(Integer.MAX_VALUE, null));
        newHead.forward = newTail;
        newTail.backward = newHead;

        newHead.below = head;
        head.above = newHead;
        newTail.below = tail;
        tail.above = newTail;

        // Update head and tall to point to the new level
        head = newHead;
        tail = newTail;
        level++;
    }

    /* 
     * Returns the current number of entries in the Skip List 
     * 
     * @return Number of entries
     */
    public int size() {
	    return entry_number;      
    }

    /* 
     * Returns an entry of S with minimum key and prints the key 
     * and the value of the entry separated by space
     * 
     * @return Entry with the lowest key
     */
    public MyEntry min() {
        Node current;
        current = head;
        while (current.below != null){
            current = current.below;
        }
        if (current.forward != null)  return current.forward.entry;
        else    return null;
    }

    /* 
     * Inserts a new entry in the Skip List
     * 
     * @param key Key of the new entry
     * @param value Value of the new entry
     * @return Number of nods traversed to insert the new entry
     */
    public int insert(int key, String value) {
        // Find the insertion point
        Node p = SkipSearch(key);
        
        // Initialize variables for tracking the new entry's tower
        Node q = null;
        int i = -1;
        int traversedNodes = 0;
        
        do {
            // Increase the height of the new entry
            i++;
            
            // If the current max height, add a new level
            if (i >= level) {
                // Add a new level to the skip list
                addNewLevel();
                
                // Grow the sentinels
                Node hf = head.forward;
                head = insertAfterAbove(null, head, new MyEntry(Integer.MIN_VALUE, null));
                insertAfterAbove(head, hf, new MyEntry(Integer.MAX_VALUE, null));
            }
            
            // Add node
            q = insertAfterAbove(p, q, new MyEntry(key, value));
            while (p.above == null) p = p.backward;
            
            // Move up one
            p = p.above;
            traversedNodes++;
        } while (generateEll(alpha, key) >= i);
        
        // Increase the number of entries
        entry_number++;
        return traversedNodes;
    }

    /*
     * Function to insert a node after another node on the same level or above
     *
     * @param p Node after the new node (same level)
     * @param q Node below the new node
     * @param entry Entry for the new node
     * @return The new node
     */
    private Node insertAfterAbove(Node p, Node q, MyEntry entry){
        Node newNode = new Node(entry);
        
        if (p != null) {
            newNode.forward = p.forward;
            newNode.backward = p;
            if (p.forward != null) p.forward.backward = newNode;
            p.forward = newNode;
        }
        
        // Link verticaly if a previous node exists
        if (q != null){
            newNode.below = q;
            q.above = newNode;
        }
        return newNode;
    }

    private int generateEll(double alpha_ , int key){
        int level = 0;
        if (alpha_ >= 0. && alpha_< 1) {
          while (rand.nextDouble() < alpha_){
              level += 1;
          }
        }
        else{
          while (key != 0 && key % 2 == 0){
            key = key / 2;
            level += 1;
          }
        }
        return level;
    }

    /* 
     * Removes the minimum entry
     * 
     * Removes and returns an entry of S with minimum key. After removing the
     * entry also remove upper useless orempty levels.
     * 
     * @return entry with lowest key
     */
    public MyEntry removeMin(){
        if (entry_number == 0) return null;

        // Find the minimum entry: the first on the right after the lowest head
        Node current = head;
        while (current.below != null) current = current.below;
        Node minNode = current.forward;

        // If there are no actual entries (only sentinels)
        if (minNode == null || minNode == tail) return null;

        MyEntry minEntry = minNode.entry;

        // Remove the node from all levels
        while (minNode != null){
            if (minNode.backward != null){
                minNode.backward.forward = minNode.forward;
            }
            if (minNode.forward != null){
                minNode.forward.backward = minNode.backward;
            }
            // Move up to the next level
            minNode = minNode.above;
        }
        entry_number--;
        // Remove empty levels
        while (level > 0 && head.forward == tail){
            head = head.below;
            tail = tail.below;
            level--;
            if (head != null) {
                head.above = null;
                tail.above = null;
            }
        }
        return minEntry;
    }

    /* 
     * Prit all the entries
     * 
     * Prints all the entries that appean in the bottom list in increasing
     * key order. The function prints following this scheme:
     * key, entry, height
     * So for an entry with key 15, value car and that appears on 8 levels
     * the output will be:
     * 15 car 8
     */
    public void print(){
        if (head == null){
            System.out.println("Skip list is empty");
            return;
        }
        // Go to the bottom level
        Node current = head;
        while (current.below != null) current = current.below;

        // Start from the first element after sentinel
        current = current.forward;
        
        // Check: are there any elements?
        if (current == null || current == tail){
            System.out.println("No elements to print");
            return;
        }

        boolean first = true;
        while (current != null && current != tail){
            // Calculate height by going up
            int height = 0; 
            Node temp = current;
            while (temp.above != null){
                height++;
                temp = temp.above;
            }

            if (!first) System.out.print(", ");
            System.out.print(current.entry.getKey() + " " + current.entry.getValue() + " " + height);
            
            first = false;
            current = current.forward;
        }
        System.out.println();
    }
}

//TestProgram

public class TestProgram {
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
            int totalTraversed = 0;
            int totalInserts = 0;

            for (int i = 0; i < N; i++) {
                String[] line = br.readLine().split(" ");
                int operation = Integer.parseInt(line[0]);

                switch (operation) {
                    case 0: // min
                        MyEntry min = skipList.min();
                        System.out.println(min);
                        break;
                    case 1: // removeMin
                        MyEntry rm = skipList.removeMin();
                        break;
                    case 2: // insert
                        int key = Integer.parseInt(line[1]);
                        String value = line[2];
                        totalTraversed += skipList.insert(key, value);
                        totalInserts++;
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

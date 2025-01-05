import java.util.*;

class Node {
    int value;
    Node[] forward; // Array to hold references to different levels

    public Node(int value, int level) {
        this.value = value;
        forward = new Node[level + 1];
    }
}

public class SkipList {
    private Node head; 
    private int maxLevel;
    private int level;
    private Random random;
    
    public SkipList() {
        maxLevel = 16; // Maximum number of levels
        level = 0; // Current level
        head = new Node(Integer.MIN_VALUE, maxLevel); // Head of the skip list, value is supposed to be -infinity
        random = new Random();
    }


    public void insert(int value) {
        // --- VARIABLES ---

        // Array of nodes that keeps track of the nodes that need to have their forward pointers updated during
        // the insertion. Size is due to the fact that skip lists use levels indexed from 0 to maxLevel
        Node[] update = new Node[maxLevel + 1]; 
        
        // Pointer used to traverse the skip list starting from the head node
        Node current = this.head;
        
        // --- TRAVERSE THE SKIP LIST ---

        // Traverse the skip list from the topmost level down to level 0.
        // The goal is to find the correct insertion position for the new node at each level
        for (int i = level; i >= 0; i--) {
            // First condition ensures we don't go past the end of the list at level i.
            // Second condition moves forward while the next node's value is bigger than is being inserted
            while (current.forward[i] != null && current.forward[i].value < value) { 
                current = current.forward[i]; // Traverse horizontally
            } 
            update[i] = current; // current points to the last node before the insertion point at level i.
                                 // This node (last before insertion) must update its forward pointer to point to the new node
        }
        
        // --- CHECK IF THE VALUE IS ALREADY PRESENT ---

        // Move current to the next node at level 0, where the node we wanted to insert would be if it exists
        current = current.forward[0];

        // If either
        // reached the end of the list
        // failed at finding a value equal to current
        // we proceed with inserion
        if (current == null || current.value != value) {
            int lvl = randomLevel(); // Generate random level by tossing a coin. If heads, add a level and keep tossing  
        
            
            if (lvl < level) {
                for (int i = level + 1; i <= lvl; i++) {
                    update[i] = head;
                }
                level = lvl;
            }

            Node newNode = new Node(value, lvl);
            for (int i = 0; i <= lvl; i++) {
                newNode.forward[i] = update[i].forward[i];
                update[i].forward[i] = newNode;
            }
        }
    }
}
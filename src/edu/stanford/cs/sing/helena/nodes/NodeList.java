package edu.stanford.cs.sing.helena.nodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.stanford.cs.sing.helena.nodes.Node;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * 
 */
public class NodeList {

    /**
     * An array of sample (dummy) items.
     */
    public static List<Node> ITEMS = new ArrayList<Node>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, Node> ITEM_MAP = new HashMap<String, Node>();

    static {
        // Add 3 sample items.
        addItem(new Node("1", "Node 1"));
        addItem(new Node("2", "Node 2"));
        addItem(new Node("3", "Node 3"));
    }

    private static void addItem(Node item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }


}

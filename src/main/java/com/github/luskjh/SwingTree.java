package com.github.luskjh;

import java.util.List;
import java.util.ArrayList;

import java.awt.Component;
import java.awt.Container;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.awt.IllegalComponentStateException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/** Generates trees of Java Swing applications
 * 
 * @author      Josh Lusk
 * @version     1.0-SNAPSHOT               
 * @since       2016-3-16
 */
public class SwingTree {
    private final Node<Component> internalRepresentation;
    private long primaryKeyCount = 0;
    
    // Node implementation used in internalRepresentation's tree 
    public static class Node<T> {
        public T data;
        // since swing components can have any number of children
        // hold them in a list
        public List<Node<T>> children;
        
        public Node(T data) {
            this.data = data;
            this.children = null;
        }
    }   
    
    public SwingTree(Component component) {
        Node<Component> node = new Node<>(component);
        this.internalRepresentation = constructTree(node);
    }
    
     /** constructs a tree with component at the root 
      *  modified from http://stackoverflow.com/a/6495800
      * 
      * @param component the root component
      */
    public static Node<Component> constructTree(Node<Component> parent) {      
        if (Container.class.isAssignableFrom(parent.data.getClass())) {
            Component[] children = ((Container)parent.data).getComponents();
            parent.children = new ArrayList<>();
            
            for (Component c : children)
                parent.children.add(constructTree(new Node<>(c)));
            
        }
        return parent;
    }
    
    /** Prints out the tree to stdout. Primarily for debugging purposes.
     * 
     */
    public void printTree() {
        printTree(this.internalRepresentation, 0);
    }
    
    private static void printTree(Node<Component> parent, int levels) {
        String treeStr = "";
        for (int i = 0; i < levels - 1; i++)
            treeStr += "   ";
        treeStr += "|-- ";
        
        Object text = "";
        try {
            Method textMethod = 
                parent.data.getClass().getMethod("getText", (Class<?>[]) null);
            try {
                String tmpText = 
                    (String)textMethod.invoke(parent.data, (Object[]) null);
                
                text = tmpText.equals("") ? "": "--> [" +  tmpText + "]";
            } catch (IllegalAccessException |
                     IllegalArgumentException |
                     InvocationTargetException e) {
            }
        } catch (NoSuchMethodException e) {
            // it's fine if the method doesn't exist - same for the catch above
        }
        
        System.out.println(treeStr + parent.data.getClass() + text);
        if (parent.children != null && !parent.children.isEmpty()) {
            parent.children.forEach(n -> {
                printTree(n, levels + 1);
            });
        }
    }
    
    /** Convert the in-memory tree to a JSON representation.
     * 
     * @return a JSONObject of the object's internal tree
     */
    public JSONObject toJSON() {
        primaryKeyCount = 0;
        return toJSON(internalRepresentation);
    }
    
    private JSONObject toJSON(Node<Component> node) {
        JSONObject obj = new JSONObject();
        obj.put("name", node.data.getClass().getSimpleName());
        obj.put("pk", Long.toString(++primaryKeyCount));
        try {
            obj.put("x", node.data.getLocationOnScreen().getX());
            obj.put("y", node.data.getLocationOnScreen().getY());
        } catch (IllegalComponentStateException e) {
            // supress
        }
        obj.put("width", node.data.getWidth());
        obj.put("height", node.data.getHeight());
        try {
            Method textMethod = node.data.getClass().getMethod("getText", (Class<?>[]) null);
            try {
                String getText = (String)textMethod.invoke(node.data, (Object[]) null);
                if (!getText.equals(""))
                    obj.put("label", getText);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                // supress
            }
        } catch (NoSuchMethodException e) {
        }
        if (node.children != null && !node.children.isEmpty()) {
            JSONArray arr = new JSONArray();
            node.children.forEach(n -> {
                arr.add(toJSON(n));
            });
            obj.put("children", arr);
        }
       return obj;
    } 
}

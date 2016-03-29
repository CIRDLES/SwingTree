/*
 * Copyright 2016 Joshua Lusk.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.luskjh;


import java.util.List;
import java.util.ArrayList;

import java.awt.Component;
import java.awt.Container;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import com.athaydes.automaton.Swinger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Josh Lusk
 */
public class SwingTree {
    private static Swinger swinger;
    private final Node<Component> internalRepresentation;
    
    public static class Node<T> {
        public T data;
        //private Node<T> parent;
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
    
    /* modified from http://stackoverflow.com/a/6495800 */
    public static Node<Component> constructTree(Node<Component> parent) {
                        
        if (Container.class.isAssignableFrom(parent.data.getClass())) {
            Component[] children = ((Container)parent.data).getComponents();
            parent.children = new ArrayList<>();
            
            for (Component c : children)
                parent.children.add(constructTree(new Node<>(c)));
            
        }
        return parent;
    }
    
    public void printTree() {
        printTree(this.internalRepresentation);
    }
    
    private static void printTree(Node<Component> parent) {
        printTree(parent, 0);
    }
    
    private static void printTree(Node<Component> parent, int levels) {
        String treeStr = "";
        for (int i = 0; i < levels - 1; i++)
            treeStr += "   ";
        treeStr += "|-- ";
        
        Object text = "";
        try {
            Method textMethod = parent.data.getClass().getMethod("getText", (Class<?>[]) null);
            try {
                String tmpText = (String)textMethod.invoke(parent.data, (Object[]) null);
                text = tmpText.equals("") ? "": "--> [" +  tmpText + "]";
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            }
        } catch (NoSuchMethodException e) {
        }
        
        
        System.out.println(treeStr + parent.data.getClass() + text);
        if (parent.children != null && !parent.children.isEmpty()) {
            parent.children.forEach(n -> {
                printTree(n, levels + 1);
            });
        }
    }
    
    public JSONObject toJSON() {
        return toJSON(internalRepresentation);
    }
    
    private JSONObject toJSON(Node<Component> node) {
        JSONObject obj = new JSONObject();
        obj.put("name", node.data.getClass().getSimpleName());
//        obj.put("x", node.data.getLocationOnScreen().getX());
//        obj.put("y", node.data.getLocationOnScreen().getY());
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.pubsubnode;

import edu.rutgers.winlab.mfpubsub.common.structure.Address;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import edu.rutgers.winlab.mfpubsub.common.structure.Vertice;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author zoe
 */
public class DijkstraTree {

    private final HashMap<NA, HashMap<NA, Vertice>> djikGraph = new HashMap<>();

    private final HashMap<NA, HashMap<NA, Integer>> weightGraph;

//    private final ArrayList<NA> visited;
    private final HashMap<NA, Vertice> distance = new HashMap<>();

    public DijkstraTree(HashMap<NA, HashMap<NA, Integer>> graph) {
        this.weightGraph = graph;
//        this.visited = new ArrayList<>();
        initial(graph.keySet());
        UpdateDistance();
        printDjik();
    }

    private void initial(Set<NA> addresses) {
        for (NA from : addresses) {
            HashMap<NA, Vertice> tmp = new HashMap<>();
            for (NA to : addresses) {
                distance.put(to, new Vertice(to));
                tmp.put(to, new Vertice(to));
            }
            djikGraph.put(from, tmp);
        }
    }

    private void UpdateDistance() {
        for (NA na : weightGraph.keySet()) {
            tree(na, new ArrayDeque<Address>());
            djikGraph.put(na, (HashMap<NA, Vertice>) distance.clone());
            distance.clear();
            for (NA i : weightGraph.keySet()) {
                distance.put(i, new Vertice(i));
            }
        }
    }

    private void tree(NA s, ArrayDeque<Address> queue) {
        queue.add(s);
        distance.get(s).setWeight(0);
        while (!queue.isEmpty()) {
            Vertice from = distance.get(queue.poll());
            HashMap<NA, Integer> edges = weightGraph.get(from.getSelf());

            for (Map.Entry<NA, Integer> edge : edges.entrySet()) {
                Vertice to = distance.get(edge.getKey());
                int tmp = distance.get(from.getSelf()).getWeight() + edge.getValue();
                if (to.getWeight() > tmp) {
                    to.setWeight(tmp);
                    to.setPrev(from.getSelf());
//                    from.addNext(to.getSelf());
                    if (!queue.contains(edge.getKey())) {
                        queue.add(edge.getKey());
                    }
                }
            }
        }
    }

//    private HashMap<NA, ArrayList<Address>> buildtree(NA RP, List<Address> receivers) {
//        HashMap<NA, ArrayList<Address>> ret = new HashMap<>();
//        for (Address addr : receivers) {
//            NA prev = distance.get(addr).getPrev();
//            treeAdd(ret, prev, addr);
//        }
//
//        for (NA na : ret.keySet()) {
//
//        }
//
//        return ret;
//    }

    private void treeAdd(HashMap<NA, ArrayList<NA>> tree, NA key, NA value) {
        ArrayList tmp = tree.get(key);
        if (tmp == null) {
            tree.put(key, tmp = new ArrayList<>());
        }
        tmp.add(value);
    }

    private void printDjik() {
        System.out.println("\n\n************************Dijkstra graph****************************");
        for (Map.Entry<NA, HashMap<NA, Vertice>> node : djikGraph.entrySet()) {
            node.getKey().print(System.out.printf("\nvertex ")).printf("to ");
            for (Map.Entry<NA, Vertice> edge : node.getValue().entrySet()) {
                edge.getKey().print(System.out).printf("-" + edge.getValue().getWeight());
                edge.getValue().getPrev().print(System.out.printf("(")).printf(")");
            }
        }
    }
    
    //need a translator to change the ArrayList<GUID>(subscribers) to ArrayList<NA>(connected edge routers)
    //Then add guid at the list<Address> of NA at the mapping of each NA of receivers
    public HashMap<NA, ArrayList<NA>> getTree(NA RP, ArrayList<NA> receivers){
        HashMap<NA, ArrayList<NA>> ret = new HashMap<>();
        ArrayList<NA> to = new ArrayList<>();
        for(NA receiver : receivers){
            NA prev = djikGraph.get(receiver).get(RP).getPrev();
            NA now = receiver;
            if(prev == receiver){
                treeAdd(ret, RP, now);
            }
            //do recursive to build tree trace
            while (djikGraph.get(receiver).get(prev).getWeight() != 0){
                treeAdd(ret, now, prev);
//                receiver = prev;
                now = prev;
                prev = djikGraph.get(receiver).get(prev).getPrev();
            }
            
        }
        return ret;
    }
}

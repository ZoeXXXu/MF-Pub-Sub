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
public class SPFA {

    private final HashMap<NA, HashMap<NA, Vertice>> djikGraph = new HashMap<>();

    private final HashMap<NA, HashMap<NA, Integer>> weightGraph;

    private final ArrayList<NA> visited;
    
    private final HashMap<NA, Vertice> distance = new HashMap<>();

    public SPFA(HashMap<NA, HashMap<NA, Integer>> graph, NA RP) {
        this.weightGraph = graph;
        this.visited = new ArrayList<>();
        initial(graph.keySet());
        UpdateDistance();
    }

    private void initial(Set<NA> addresses) {
        for (NA from : addresses) {
            HashMap<NA, Vertice> tmp = new HashMap<>();
            for (NA to : addresses) {
                tmp.put(to, new Vertice(to));
            }
            djikGraph.put(from, tmp);
        }
    }

    private void UpdateDistance() {
        for(NA na : weightGraph.keySet()){
            tree(na, new ArrayDeque<Address>());
            djikGraph.put(na, (HashMap<NA, Vertice>) distance.clone());
            distance.clear();
        }        
    }

    private void tree(NA s, ArrayDeque<Address> queue) {
        queue.add(s);
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

    private HashMap<NA, ArrayList<Address>> buildtree(NA RP, List<Address> receivers) {
        HashMap<NA, ArrayList<Address>> ret = new HashMap<>();
        for (Address addr : receivers) {
            NA prev = distance.get(addr).getPrev();
            treeAdd(ret, prev, addr);
        }

        for (NA na : ret.keySet()) {

        }

        return ret;
    }

    private void treeAdd(HashMap<NA, ArrayList<Address>> tree, NA key, Address value) {
        ArrayList tmp = tree.get(key);
        if (tmp == null) {
            tree.put(key, tmp = new ArrayList<>());
        }
        tmp.add(value);
    }
}

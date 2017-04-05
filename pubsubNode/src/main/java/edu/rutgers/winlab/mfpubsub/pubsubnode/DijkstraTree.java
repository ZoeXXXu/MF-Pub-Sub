/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.pubsubnode;

import edu.rutgers.winlab.mfpubsub.common.structure.Address;
import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import edu.rutgers.winlab.mfpubsub.common.structure.Vertice;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author zoe
 */
public class DijkstraTree {

    private final HashMap<NA, HashMap<NA, Vertice>> djikGraph = new HashMap<>();

    private HashMap<NA, HashMap<NA, Integer>> weightGraph;

//    private final ArrayList<NA> visited;
    private final HashMap<NA, Vertice> distance = new HashMap<>();

    public DijkstraTree(HashMap<NA, HashMap<NA, Integer>> graph) {
        this.weightGraph = graph;
        initial(graph.keySet());
        UpdateDistance();
//        printDjik();
    }

    public void Renew(HashMap<NA, HashMap<NA, Integer>> graph) {
        this.weightGraph = graph;
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
    private void treeAdd(HashMap<NA, ArrayList<Address>> tree, NA key, NA value) {
        ArrayList<Address> tmp = tree.get(key);
        if (tmp == null) {
            tree.put(key, tmp = new ArrayList<>());
        }
        if (!tmp.contains(value)) {
            tmp.add(value);
        }
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
    public HashMap<NA, ArrayList<Address>> getTree(NA RP, ArrayList<NA> receivers) {
        HashMap<NA, ArrayList<Address>> ret = new HashMap<>();
//        ArrayList<NA> to = new ArrayList<>();
        for (NA receiver : receivers) {
            NA prev = djikGraph.get(RP).get(receiver).getPrev();
            NA now = receiver;
            if (prev == RP) {
                treeAdd(ret, RP, now);
            }
            //do recursive to build tree trace
            while (djikGraph.get(now).get(RP).getWeight() != 0) {
                treeAdd(ret, prev, now);
                now = prev;
                prev = djikGraph.get(RP).get(prev).getPrev();
            }
        }
        return ret;
    }

    public HashMap<NA, ArrayList<Address>> getBranch(NA receiver, NA RP, HashMap<NA, ArrayList<Address>> ret) {
        if (ret == null) {
            ret = new HashMap<>();
        }
        NA prev = djikGraph.get(RP).get(receiver).getPrev();
        NA now = receiver;
        if (prev == RP) {
            treeAdd(ret, RP, now);
        }
        //do recursive to build tree trace
        while (djikGraph.get(now).get(RP).getWeight() != 0) {
            treeAdd(ret, prev, now);
            now = prev;
            prev = djikGraph.get(RP).get(prev).getPrev();
        }
        return ret;
    }

    public void deleteBranch(NA receiver, NA RP, HashMap<NA, ArrayList<Address>> ret) {
        NA prev = djikGraph.get(RP).get(receiver).getPrev();
        NA now = receiver;
        if (prev == RP) {
            treeDelete(ret, RP, now);
        }
        //if this is empty after the treeDelete above run
        if(ret.isEmpty()){
            return;
        }
        //do recursive to build tree trace
        while (djikGraph.get(now).get(RP).getWeight() != 0) {
            treeDelete(ret, prev, now);
            now = prev;
            prev = djikGraph.get(RP).get(prev).getPrev();
        }
//        return ret;
    }

    public void treeDelete(HashMap<NA, ArrayList<Address>> tree, NA key, Address value) {
        ArrayList<Address> tmp = tree.get(key);
        if (tmp == null) {
            System.out.println("something wrongzsxdcfvgbhnjmk,l;,mknjbhvgcfxdsz");
            key.print(System.out.printf("key: "));
            value.print(System.out.printf("  value: "));
            return;
        }
        if (value instanceof GUID) {
            tmp.remove(value);
        } else if (value instanceof NA) {
            if (!tree.containsKey((NA) value)) {
                tmp.remove(value);
            }
        }

        if (tmp.isEmpty()) {
            tree.remove(key);
        }
    }
}

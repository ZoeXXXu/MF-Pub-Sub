/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.pubsubnode;

import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zoe
 */
public class SPFATest {

    public SPFATest() {
    }

    @org.junit.Test
    public void testSomeMethod() {
        NA na1 = new NA(1);
        NA na2 = new NA(2);
        NA na3 = new NA(3);
        NA na4 = new NA(4);
        NA na5 = new NA(5);
        NA na6 = new NA(6);

        HashMap<NA, HashMap<NA, Integer>> weight = new HashMap<>();
        HashMap<NA, Integer> edges = new HashMap<>();

        edges.put(na2, 1);
        edges.put(na5, 1);
        weight.put(na1, (HashMap<NA, Integer>) edges.clone());

        edges.clear();
        edges.put(na1, 1);
        edges.put(na3, 1);
        edges.put(na4, 1);
        weight.put(na2, (HashMap<NA, Integer>) edges.clone());

        edges.clear();
        edges.put(na2, 1);
        edges.put(na4, 1);
        weight.put(na3, (HashMap<NA, Integer>) edges.clone());

        edges.clear();
        edges.put(na2, 1);
        edges.put(na3, 1);
        edges.put(na5, 1);
        edges.put(na6, 1);
        weight.put(na4, (HashMap<NA, Integer>) edges.clone());

        edges.clear();
        edges.put(na1, 1);
        edges.put(na4, 1);
        weight.put(na5, (HashMap<NA, Integer>) edges.clone());

        edges.clear();
        edges.put(na4, 1);
        weight.put(na6, (HashMap<NA, Integer>) edges.clone());

        printWeight(weight);

        DijkstraTree dijkstraTree = new DijkstraTree(weight);
    }

    private void printWeight(HashMap<NA, HashMap<NA, Integer>> weight) {
        System.out.println("************************weight graph****************************");
        for (Map.Entry<NA, HashMap<NA, Integer>> node : weight.entrySet()) {
            node.getKey().print(System.out.printf("\nnode ")).printf("to: ");
            for (Map.Entry<NA, Integer> edge : node.getValue().entrySet()) {
                edge.getKey().print(System.out).printf("(" + edge.getValue().toString() + ")");
            }
        }
    }

}

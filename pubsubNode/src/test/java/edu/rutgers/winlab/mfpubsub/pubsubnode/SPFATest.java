/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.pubsubnode;

import edu.rutgers.winlab.mfpubsub.common.structure.Address;
import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import java.util.ArrayList;
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

        ArrayList<NA> receivers = new ArrayList<>();
//        receivers.add(na6);
        receivers.add(na3);
        HashMap<NA, ArrayList<Address>> tree = dijkstraTree.getTree(na1, receivers);
        printTree(tree);
        dijkstraTree.getBranch(na6, na1, tree);        
//        byte[] dstGuidBuf = new byte[GUID.GUID_LENGTH];
//        dstGuidBuf[GUID.GUID_LENGTH - 1] = 0x2;
//        GUID Guid = new GUID(dstGuidBuf);
//        ArrayList<Address> tmp = tree.get(na1);
//        tmp.add(Guid);
        printTree(tree);
        dijkstraTree.deleteBranch(na6, na1, tree);
        printTree(tree);
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

    private void printTree(HashMap<NA, ArrayList<Address>> tree) {
        for (Map.Entry<NA, ArrayList<Address>> branch : tree.entrySet()) {
            branch.getKey().print(System.out.printf("\n")).printf(" : ");
            for (Address addr : branch.getValue()) {
                addr.print(System.out).printf(" ");
            }
        }
        System.out.println();
    }

}

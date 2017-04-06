/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.pubsubnode;

import edu.rutgers.winlab.mfpubsub.common.elements.NetworkInterface;
import edu.rutgers.winlab.mfpubsub.common.elements.PacketProcessor;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacket;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketData;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketDataPayloadSub;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketDataPayloadUnsub;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketGNRS;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketGNRSPayloadAssoc;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketGNRSPayloadQuery;
import edu.rutgers.winlab.mfpubsub.common.structure.Address;
import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zoe
 */
public class PacketProcessorPubSub extends PacketProcessor {

    private final GUID PubSubGUID;

    private final NA GNRS;

    private final HashMap<NA, HashMap<NA, Integer>> coreWeightGraph;

    private final DijkstraTree dijkstraGraph;

    //this two should be updated synchronization
    private final HashMap<GUID, HashMap<NA, ArrayList<Address>>> multiTree = new HashMap<>();

//    private final HashMap<GUID, HashMap<NA, ArrayList<NA>>> coreTree = new HashMap<>();
    //this NA is the NA of connected router, not hosts' NA
    private final HashMap<GUID, NA> RoutingTable;

    private final HashMap<GUID, ArrayList<GUID>> GraphTable;

    //guid - na table/process
    public PacketProcessorPubSub(NA GNRS, GUID PubSub, HashMap<NA, HashMap<NA, Integer>> weightGraph, HashMap<GUID, NA> RoutingTable, HashMap<GUID, ArrayList<GUID>> GraphTable, NA myNA, HashMap<NA, NetworkInterface> neighbors) {
        super(myNA, neighbors);
        this.PubSubGUID = PubSub;
        this.GNRS = GNRS;
        this.coreWeightGraph = weightGraph;
        this.RoutingTable = RoutingTable;
        this.GraphTable = GraphTable;
        this.dijkstraGraph = new DijkstraTree(weightGraph);
    }

    @Override
    protected void handlePacket(MFPacket packet) throws IOException {
        switch (packet.getType()) {
            case MFPacketData.MF_PACKET_TYPE_DATA:
                MFPacketData pkt = (MFPacketData) packet;
                switch (pkt.getSID()) {
                    case MFPacketDataPayloadSub.MF_PACKET_DATA_SID_SUBSCRIPTION:
                        AddBranch(((MFPacketDataPayloadSub) pkt.getPayload()).getTopicGUID(), pkt.getsrcGuid());
                        printGraph();
//                        printMulti();
                        break;
                    case MFPacketDataPayloadUnsub.MF_PACKET_DATA_SID_UNSUBSCRIPTION:
//                        DeleteBranch(((MFPacketDataPayloadUnsub) pkt.getPayload()).getTopicGUID(), pkt.getsrcGuid());
                        break;
                    default:
//                        packet.print(System.err);
//                        System.out.println(packet.serialize(System.out));
                        System.err.println(String.format("receive a wrong packet type which shouldn't receive actually. %s", pkt.getSID()));
                }
                break;
            case MFPacketGNRS.MF_PACKET_TYPE_GNRS:
                if (((MFPacketGNRS) packet).getPayload().getType() == MFPacketGNRSPayloadQuery.MF_GNRS_PACKET_PAYLOAD_TYPE_QUERY) {
                    //build tree and send assoc msg
                    build(((MFPacketGNRSPayloadQuery) ((MFPacketGNRS) packet).getPayload()).getQuery());
                    printMulti();
                }
                break;
            default:
                System.err.println("receive a wrong packet type which shouldn't receive actually.");
        }
    }

    public void AddBranch(GUID topic, GUID src) throws IOException {
        GraphAdd(topic, src);
        NA rp = getRP(topic);
        NA router = RoutingTable.get(src);
        if (multiTree.containsKey(topic)) {
            multiTree.put(topic, dijkstraGraph.getBranch(router, rp, multiTree.get(topic)));
            ArrayList<Address> branch = multiTree.get(topic).get(router);
            if (branch == null) {
                multiTree.get(topic).put(router, branch = new ArrayList<>());
            }
            branch.add(src);
            sendToNeighbor(null, new MFPacketGNRS(getNa(), GNRS, new MFPacketGNRSPayloadAssoc(topic, rp, MFPacketGNRSPayloadAssoc.MF_GNRS_PACKET_PAYLOAD_TYPE_ASSOC_SUB, src, multiTree.get(topic))));
        }
        GUID parent = GraphTable.get(topic).get(0);
        ArrayList<GUID> parents = GraphTable.get(parent);
        //recursively add branch to its parent trees if the tree exist and user didn't sub to that parent topic
        if (!parents.isEmpty()) {
            for (GUID parentTopic : parents) {
                if (multiTree.containsKey(parentTopic) && !GraphTable.get(parentTopic).contains(src)) {
                    AddBranch(parentTopic, src, rp, router);
                }
            }
        }
    }

    private void AddBranch(GUID topic, GUID src, NA rp, NA router) throws IOException {
        multiTree.put(topic, dijkstraGraph.getBranch(router, rp, multiTree.get(topic)));
        ArrayList<Address> branch = multiTree.get(topic).get(router);
        if (branch == null) {
            multiTree.get(topic).put(router, branch = new ArrayList<>());
        }
        branch.add(src);
        sendToNeighbor(null, new MFPacketGNRS(getNa(), GNRS, new MFPacketGNRSPayloadAssoc(topic, rp, MFPacketGNRSPayloadAssoc.MF_GNRS_PACKET_PAYLOAD_TYPE_ASSOC_SUB, GUID.GUID_NULL, multiTree.get(topic))));
        GUID parent = GraphTable.get(topic).get(0);
        ArrayList<GUID> parents = GraphTable.get(parent);
        //recursively add branch to its parent trees if the tree exist and user didn't sub to that parent topic
        if (!parents.isEmpty()) {
            for (GUID parentTopic : parents) {
                if (multiTree.containsKey(parentTopic)) {
                    if (!GraphTable.get(parentTopic).contains(src)) {
                        AddBranch(parentTopic, src, rp, router);
                    }
                }
            }
        }
    }

    public void RenewTrees() throws IOException {
        for (Map.Entry<GUID, HashMap<NA, ArrayList<Address>>> topic : multiTree.entrySet()) {
//            System.out.println("(((((((((((((((((( renew tree ))))))))))))))))))");
            RenewTree(topic.getKey());
        }
    }

    private void RenewTree(GUID topic) throws IOException {
        //different - Store/send to multiTree/GNRS
        HashMap<NA, ArrayList<Address>> tree = buildTree(topic);
        if (istreeChanged(topic, tree)) {
            multiTree.put(topic, tree);
            sendToNeighbor(NA.NA_NULL, new MFPacketGNRS(getNa(), GNRS, new MFPacketGNRSPayloadAssoc(topic, RoutingTable.get(topic), MFPacketGNRSPayloadAssoc.MF_GNRS_PACKET_PAYLOAD_TYPE_ASSOC_SUB, GUID.GUID_NULL, tree)));
        } 
//        else {
//            System.out.println("(((((((((((((((((( tree is same ))))))))))))))))))");
//        }
    }

    public void build(GUID topic) throws IOException {
        //Store/send to multiTree/GNRS
        multiTree.put(topic, buildTree(topic));
        sendToNeighbor(NA.NA_NULL, new MFPacketGNRS(getNa(), GNRS, new MFPacketGNRSPayloadAssoc(topic, RoutingTable.get(topic), MFPacketGNRSPayloadAssoc.MF_GNRS_PACKET_PAYLOAD_TYPE_ASSOC_SUB, GUID.GUID_NULL, multiTree.get(topic))));
    }

    private HashMap<NA, ArrayList<GUID>> GUIDtoNA(GUID topic) {
        HashMap<NA, ArrayList<GUID>> ret = new HashMap<>();
        //do recursive look up
        ArrayList<GUID> receivers = RecursiveLookUp(topic);
        //do guid to na transformation
        for (GUID user : receivers) {
//            user.print(System.out);
            ArrayList<GUID> tmp = ret.get(RoutingTable.get(user));
            if (tmp == null) {
                ret.put(RoutingTable.get(user), tmp = new ArrayList<>());
            }
            tmp.add(user);
        }
        return ret;
    }

    private NA getRP(GUID topic) {
        NA rp = RoutingTable.get(topic);
        if (rp.getVal() != 0) {
            return rp;
        }
        throw new IllegalStateException(String.format("PubSub Node cannot find RP of topic %s", topic.print(System.out)));
    }

    public void GraphAdd(GUID key, GUID value) {
        ArrayList<GUID> tmp = GraphTable.get(key);
        if (tmp == null) {
            GraphTable.put(key, tmp = new ArrayList<>());
        }
        tmp.add(value);
    }

//    ArrayList<GUID> receivers = new ArrayList<>();
    private ArrayList<GUID> RecursiveLookUp(GUID topic) {
        ArrayList<GUID> tmp = (ArrayList<GUID>) GraphTable.get(topic).clone();
        tmp.remove(0);
        ArrayList<GUID> ret = new ArrayList<>();
        for (GUID i : tmp) {
            if (GraphTable.containsKey(i)) {
                ret.addAll(RecursiveLookUp(i));
//                int length = tmp.size();
//                tmp.addAll((ArrayList<GUID>) GraphTable.get(i).clone());
//                tmp.remove(length);
            } else {
                ret.add(i);
            }
        }
        return ret;
    }

    private void DeleteBranch(GUID topic, GUID src) throws IOException {
        GraphDelete(topic, src);
        NA rp = getRP(topic);
        NA router = RoutingTable.get(src);
        if (multiTree.containsKey(topic)) {
            dijkstraGraph.treeDelete(multiTree.get(topic), router, src);
            dijkstraGraph.deleteBranch(router, rp, multiTree.get(topic));
            if (multiTree.get(topic).isEmpty()) {
                multiTree.remove(topic);
            }
            sendToNeighbor(NA.NA_NULL, new MFPacketGNRS(getNa(), GNRS, new MFPacketGNRSPayloadAssoc(topic, rp, MFPacketGNRSPayloadAssoc.MF_GNRS_PACKET_PAYLOAD_TYPE_ASSOC_UNSUB, src, multiTree.get(topic))));
        }
        GUID parent = GraphTable.get(topic).get(0);
        ArrayList<GUID> parents = GraphTable.get(parent);
        //recursively add branch to its parent trees if the tree exist and user didn't sub to that parent topic
        if (!parents.isEmpty()) {
            for (GUID parentTopic : parents) {
                if (multiTree.containsKey(parentTopic)) {
                    if (!GraphTable.get(parentTopic).contains(src)) {
                        DeleteBranch(parentTopic, src, rp, router);
                    }
                }
            }
        }
    }

    private void GraphDelete(GUID key, GUID value) {
        ArrayList<GUID> tmp = GraphTable.get(key);
        if (tmp != null) {
            tmp.remove(value);
        }
    }

    private void DeleteBranch(GUID topic, GUID src, NA rp, NA router) throws IOException {
        printMulti();
        dijkstraGraph.treeDelete(multiTree.get(topic), router, src);
        printMulti();
        dijkstraGraph.deleteBranch(router, rp, multiTree.get(topic));
        sendToNeighbor(NA.NA_NULL, new MFPacketGNRS(getNa(), GNRS, new MFPacketGNRSPayloadAssoc(topic, rp, MFPacketGNRSPayloadAssoc.MF_GNRS_PACKET_PAYLOAD_TYPE_ASSOC_UNSUB, GUID.GUID_NULL, multiTree.get(topic))));
        if (multiTree.get(topic).isEmpty()) {
            multiTree.remove(topic);
        }
        printGraph();
        GUID parent = GraphTable.get(topic).get(0);
        ArrayList<GUID> parents = GraphTable.get(parent);
        //recursively add branch to its parent trees if the tree exist and user didn't sub to that parent topic

        if (!parents.isEmpty()) {
            for (GUID parentTopic : parents) {
                if (multiTree.containsKey(parentTopic)) {
                    if (!GraphTable.get(parentTopic).contains(src)) {
                        DeleteBranch(parentTopic, src, rp, router);
                    }
                }
            }
        }
    }

    public void printGraph() {
        System.out.printf("\n\n************************graph****************************");
        for (Map.Entry<GUID, ArrayList<GUID>> node : GraphTable.entrySet()) {
            node.getKey().print(System.out.printf("\n ")).printf(" : ");
            for (GUID edge : node.getValue()) {
                edge.print(System.out);
            }
        }
        System.out.println();
    }

    public void printMulti() {
        System.out.printf("\n\n************************ multicast tree ****************************");
        for (Map.Entry<GUID, HashMap<NA, ArrayList<Address>>> tree : multiTree.entrySet()) {
            tree.getKey().print(System.out.printf("\ntopic ")).printf(" tree:");
            for (Map.Entry<NA, ArrayList<Address>> branch : tree.getValue().entrySet()) {
                branch.getKey().print(System.out.printf("\n")).printf(" : ");
                for (Address addr : branch.getValue()) {
                    addr.print(System.out).printf(" ");
                }
            }
        }
        System.out.println();
    }

    private boolean istreeChanged(GUID topic, HashMap<NA, ArrayList<Address>> tree) {
        HashMap<NA, ArrayList<Address>> compared = multiTree.get(topic);
        if (tree.size() == compared.size()) {
            for (Map.Entry<NA, ArrayList<Address>> entry : tree.entrySet()) {
                ArrayList<Address> comparedEntry = compared.get(entry.getKey());
                if (comparedEntry != null && comparedEntry.size() == entry.getValue().size()) {
                    for (Address addr : entry.getValue()) {
//                        addr.print(System.out.printf("new"));
                        if (!comparedEntry.contains(addr)) {
                            return true;
                        }
                    }
                } else {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    private HashMap<NA, ArrayList<Address>> buildTree(GUID topic) {
        //arraylist<GUID> receiver = do recursive look up
        //get connected arraylist<NA> NAs from receiver
        HashMap<NA, ArrayList<GUID>> receivers = GUIDtoNA(topic);
        //build tree though NAs
        HashMap<NA, ArrayList<Address>> tree = dijkstraGraph.getTree(RoutingTable.get(topic), new ArrayList<>(receivers.keySet()));
        //add GUID follow the NAs in the tree
        for (NA router : receivers.keySet()) {
            ArrayList<Address> tmp = tree.get(router);
            if (tmp == null) {
                tree.put(router, tmp = new ArrayList<>());
            }
            tmp.addAll(receivers.get(router));
        }
        return tree;
    }
}

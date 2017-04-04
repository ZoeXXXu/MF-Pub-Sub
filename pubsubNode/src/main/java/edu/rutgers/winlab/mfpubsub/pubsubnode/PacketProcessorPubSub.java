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
import java.util.Collection;
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
                        break;
                    case MFPacketDataPayloadUnsub.MF_PACKET_DATA_SID_UNSUBSCRIPTION:
                        DeleteBranch(((MFPacketDataPayloadSub) pkt.getPayload()).getTopicGUID(), pkt.getsrcGuid());
                        break;
                    default:
                        System.err.println("receive a wrong packet type which shouldn't receive actually.");
                }
                break;
            case MFPacketGNRS.MF_PACKET_TYPE_GNRS:
                if (((MFPacketGNRS) packet).getPayload().getType() == MFPacketGNRSPayloadQuery.MF_GNRS_PACKET_PAYLOAD_TYPE_QUERY) {
                    //build tree and send assoc msg
                    TreeBuild((MFPacketGNRSPayloadQuery) ((MFPacketGNRS) packet).getPayload());
                }
                break;
            default:
                System.err.println("receive a wrong packet type which shouldn't receive actually.");
        }
    }

    private void AddBranch(GUID topic, GUID src) throws IOException {
        GraphAdd(topic, src);
        NA rp = getRP(topic);
        NA router = RoutingTable.get(src);
        multiTree.put(topic, dijkstraGraph.getBranch(router, rp, multiTree.get(topic)));
        ArrayList<Address> branch = multiTree.get(topic).get(router);
        if (branch == null) {
            multiTree.get(topic).put(router, branch = new ArrayList<>());
        }
        branch.add(src);
        sendToNeighbor(GNRS, new MFPacketGNRS(getNa(), GNRS, new MFPacketGNRSPayloadAssoc(topic, rp, MFPacketGNRSPayloadAssoc.MF_GNRS_PACKET_PAYLOAD_TYPE_ASSOC_SUB, src, multiTree.get(topic))));
        GUID parent = GraphTable.get(topic).get(0);
        //recursively add branch to its parent trees if the tree exist and user didn't sub to that parent topic
        if (!parent.isNULL()) {
            for (GUID parentTopic : GraphTable.get(parent)) {
                if (multiTree.containsKey(parentTopic)) {
                    if (!GraphTable.get(parentTopic).contains(src)) {
                        AddBranch(parentTopic, src, rp, router);
                    }
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
        sendToNeighbor(GNRS, new MFPacketGNRS(getNa(), GNRS, new MFPacketGNRSPayloadAssoc(topic, rp, MFPacketGNRSPayloadAssoc.MF_GNRS_PACKET_PAYLOAD_TYPE_ASSOC_SUB, GUID.GUID_NULL, multiTree.get(topic))));
        GUID parent = GraphTable.get(topic).get(0);
        //recursively add branch to its parent trees if the tree exist and user didn't sub to that parent topic
        if (!parent.isNULL()) {
            for (GUID parentTopic : GraphTable.get(parent)) {
                if (multiTree.containsKey(parentTopic)) {
                    if (!GraphTable.get(parentTopic).contains(src)) {
                        AddBranch(parentTopic, src, rp, router);
                    }
                }
            }
        }
    }

    //may not need it any more
    private void RenewTree(GUID topicGUID) {

    }

    public void TreeBuild(MFPacketGNRSPayloadQuery query) {
        //arraylist<GUID> receiver = do recursive look up
        //get connected arraylist<NA> NAs from receiver
        //build tree though NAs
        //add GUID follow the NAs in the tree
        //Store/send to multiTree/GNRS
    }

    private ArrayList<NA> GUIDtoNA(GUID topic) {
        //do recursive look up
        //do guid to na transformation
        return null;
    }

    private NA getRP(GUID topic) {
        NA rp = RoutingTable.get(topic);
        if (rp.getVal() != 0) {
            return rp;
        }
        throw new IllegalStateException(String.format("PubSub Node cannot find RP of topic %s", topic.print(System.out)));
    }

    private void GraphAdd(GUID key, GUID value) {
        ArrayList<GUID> tmp = GraphTable.get(key);
        if (tmp == null) {
            GraphTable.put(key, tmp = new ArrayList<>());
        }
        tmp.add(value);
    }

//    ArrayList<GUID> receivers = new ArrayList<>();
    private ArrayList RecursiveLookUp(GUID topic) {
        ArrayList<GUID> receivers = GraphTable.get(topic);
        receivers.remove(0);
        ArrayList<GUID> tmp;
        for (GUID i : receivers) {
            if (GraphTable.containsKey(i)) {
                receivers.remove(i);
                tmp = GraphTable.get(i);
                tmp.remove(0);
                receivers.addAll((Collection<? extends GUID>) tmp.clone());
                tmp.clear();
            }
        }
        return receivers;
    }

    private void DeleteBranch(GUID topic, GUID src) throws IOException {
        GraphDelete(topic, src);
        NA rp = getRP(topic);
        NA router = RoutingTable.get(src);
        dijkstraGraph.treeDelete(multiTree.get(topic), router, src);
        multiTree.put(topic, dijkstraGraph.deleteBranch(router, rp, multiTree.get(topic)));
        sendToNeighbor(GNRS, new MFPacketGNRS(getNa(), GNRS, new MFPacketGNRSPayloadAssoc(topic, rp, MFPacketGNRSPayloadAssoc.MF_GNRS_PACKET_PAYLOAD_TYPE_ASSOC_UNSUB, src, multiTree.get(topic))));
        GUID parent = GraphTable.get(topic).get(0);
        //recursively add branch to its parent trees if the tree exist and user didn't sub to that parent topic
        if (!parent.isNULL()) {
            for (GUID parentTopic : GraphTable.get(parent)) {
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
//        ArrayList<Address> branch = multiTree.get(topic).get(router);
//        branch.remove(src);
//        if(branch.isEmpty()){
//            multiTree.get(topic).remove(router);
//        }
        dijkstraGraph.treeDelete(multiTree.get(topic), router, src);
        multiTree.put(topic, dijkstraGraph.deleteBranch(router, rp, multiTree.get(topic)));
        sendToNeighbor(GNRS, new MFPacketGNRS(getNa(), GNRS, new MFPacketGNRSPayloadAssoc(topic, rp, MFPacketGNRSPayloadAssoc.MF_GNRS_PACKET_PAYLOAD_TYPE_ASSOC_UNSUB, GUID.GUID_NULL, multiTree.get(topic))));
        GUID parent = GraphTable.get(topic).get(0);
        //recursively add branch to its parent trees if the tree exist and user didn't sub to that parent topic
        if (!parent.isNULL()) {
            for (GUID parentTopic : GraphTable.get(parent)) {
                if (multiTree.containsKey(parentTopic)) {
                    if (!GraphTable.get(parentTopic).contains(src)) {
                        DeleteBranch(parentTopic, src, rp, router);
                    }
                }
            }
        }
    }

    public void printGraph() {
        System.out.println("************************graph****************************");
        for (Map.Entry<GUID, ArrayList<GUID>> node : GraphTable.entrySet()) {
            node.getKey().print(System.out.printf("\nguid ")).printf("to: ");
            for (GUID edge : node.getValue()) {
                edge.print(System.out);
            }
        }
    }
}

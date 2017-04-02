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
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketGNRS;
import edu.rutgers.winlab.mfpubsub.common.structure.Address;
import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author zoe
 */
public class PacketProcessorPubSub extends PacketProcessor {

    private final GUID PubSubGUID;

    private final HashMap<NA, HashMap<NA, Integer>> coreWeightGraph;

    private final DijkstraTree dijkstraGraph;

    //this two should be updated synchronization
    private final HashMap<GUID, HashMap<NA, ArrayList<Address>>> multiTree = new HashMap<>();

    //this NA is the NA of connected router, not hosts' NA
    private final HashMap<GUID, NA> RoutingTable;

    private final HashMap<GUID, NA> RPs = new HashMap<>();

    private final HashMap<GUID, ArrayList<GUID>> GraphTable;

    //guid - na table/process
    public PacketProcessorPubSub(GUID PubSub, HashMap<NA, HashMap<NA, Integer>> weightGraph, HashMap<GUID, NA> RoutingTable, HashMap<GUID, ArrayList<GUID>> GraphTable, NA myNA, HashMap<NA, NetworkInterface> neighbors) {
        super(myNA, neighbors);
        this.PubSubGUID = PubSub;
        this.coreWeightGraph = weightGraph;
        this.RoutingTable = RoutingTable;
        this.GraphTable = GraphTable;
        this.dijkstraGraph = new DijkstraTree(this.coreWeightGraph);
    }

    @Override
    protected void handlePacket(MFPacket packet) throws IOException {
        if (packet.getType() == MFPacketData.MF_PACKET_TYPE_DATA) {
            MFPacketData pkt = (MFPacketData) packet;
            if (pkt.getSID() == MFPacketDataPayloadSub.MF_PACKET_DATA_SID_SUBSCRIPTION) {
                //update the related tree
                AddBranch(pkt);
            } else {
                System.err.println("receive a wrong packet type which shouldn't receive actually.");
            }
        } else {
            System.err.println("receive a wrong packet type which shouldn't receive actually.");
        }
    }

    private void AddBranch(MFPacketData pkt) {
        MFPacketDataPayloadSub sub = (MFPacketDataPayloadSub) pkt.getPayload();
        NA rp = getRP(sub);
        GraphAdd(sub.getTopicGUID(), pkt.getsrcGuid());
        NA router = RoutingTable.get(pkt.getsrcGuid());
    }

    private void RenewTree(GUID topicGUID) {

    }

    private void NotifyGNRS(MFPacketData pkt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void TreeBuild(GUID topicGUID) {

    }
    
    private ArrayList<NA> GUIDtoNA(GUID topic){
        return null;
    }
    
    private NA getRP(MFPacketDataPayloadSub sub) {
        NA rp = RPs.get(sub.getTopicGUID());
        if (rp != null) {
            return rp;
        }
        throw new IllegalStateException(String.format("PubSub Node cannot find RP of topic %s", sub.getTopicGUID().print(System.out)));
    }
    
    private void GraphAdd(GUID key, GUID value){
        ArrayList<GUID> tmp = GraphTable.get(key);
        if(tmp == null){
            GraphTable.put(key, tmp = new ArrayList<>());
        }
        tmp.add(value);
    }
}

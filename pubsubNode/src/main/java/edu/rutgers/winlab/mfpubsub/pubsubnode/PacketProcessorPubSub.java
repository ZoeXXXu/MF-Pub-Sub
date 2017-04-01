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

    private final HashMap<NA, HashMap<NA, Integer>> weightGraph;

    private final DijkstraTree dijkstraGraph;

    //this two should be updated synchronization
    private final HashMap<GUID, HashMap<NA, ArrayList<Address>>> multiTree = new HashMap<>();

    private final HashMap<GUID, NA> RPs = new HashMap<>();

    //guid - na table/process
    public PacketProcessorPubSub(HashMap<NA, HashMap<NA, Integer>> weightGraph, NA myNA, HashMap<NA, NetworkInterface> neighbors) {
        super(myNA, neighbors);
        this.weightGraph = weightGraph;
        this.dijkstraGraph = new DijkstraTree(this.weightGraph);
    }

    @Override
    protected void handlePacket(MFPacket packet) throws IOException {
        switch (packet.getType()) {
            case MFPacketGNRS.MF_PACKET_TYPE_GNRS:
                break;
            case MFPacketData.MF_PACKET_TYPE_DATA:
                break;
            default:
                System.err.println("receive a wrong packet type which shouldn't receive actually.");
        }
    }
}

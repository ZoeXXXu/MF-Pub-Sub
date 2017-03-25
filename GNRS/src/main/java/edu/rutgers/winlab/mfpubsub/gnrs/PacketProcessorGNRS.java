/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.gnrs;

import edu.rutgers.winlab.mfpubsub.common.elements.NetworkInterface;
import edu.rutgers.winlab.mfpubsub.common.elements.PacketProcessor;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacket;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketGNRS;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketGNRSPayloadAssoc;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketGNRSPayloadQuery;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketGNRSPayloadResponse;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketNetworkRenew;
import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author zoe
 */
public class PacketProcessorGNRS extends PacketProcessor {

    private final HashMap<GUID, NA> AddrTable;
    private final HashMap<GUID, ArrayList<GUID>> GraphTable;

    public PacketProcessorGNRS(HashMap<GUID, ArrayList<NA>> AddrTable, HashMap<GUID, ArrayList<GUID>> GraphTable, NA myNA, HashMap<NA, NetworkInterface> neighbors) {
        super(myNA, neighbors);
        this.AddrTable = new HashMap<>();
        this.GraphTable = GraphTable;
    }

    @Override
    protected void handlePacket(MFPacket packet) throws IOException {
        if (packet.getType() == MFPacketGNRS.MF_PACKET_TYPE_GNRS) {
            MFPacketGNRS pkt = (MFPacketGNRS) packet;
            if(pkt.getPayload().getType() == MFPacketGNRSPayloadQuery.MF_GNRS_PACKET_PAYLOAD_TYPE_QUERY){
                //look up the AddrTable and send response
                NA rsp = AddrTable.get(((MFPacketGNRSPayloadQuery) pkt.getPayload()).getQuery());
                if(rsp != null){
                    sendToNeighbor(pkt.getSrcNa(), new MFPacketGNRS(pkt.getDstNA(), pkt.getSrcNa(), new MFPacketGNRSPayloadResponse(rsp)));
                }
            }
            else if(pkt.getPayload().getType() == MFPacketGNRSPayloadAssoc.MF_GNRS_PACKET_PAYLOAD_TYPE_ASSOC){
                //TODO: add the subscriber GUID to the mapping of topic GUID if existed
                
                //TODO: renew the multicast tree and multicast GNRS sync
            }
        } else if(packet.getType() == MFPacketNetworkRenew.MF_PACKET_TYPE_NETWORK_RENEW){
            //TODO: renew the AddrTable
        }
        else {
            System.out.println("This is not the correct packet type that GNRS should receive.");
        }
    }

}

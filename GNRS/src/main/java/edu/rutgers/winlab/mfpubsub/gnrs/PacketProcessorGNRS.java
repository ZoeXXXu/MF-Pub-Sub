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
import edu.rutgers.winlab.mfpubsub.common.structure.Address;
import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author zoe
 */
public class PacketProcessorGNRS extends PacketProcessor {

    private HashMap<GUID, List<Address>> Table;

    public PacketProcessorGNRS(NA myNA, HashMap<NA, NetworkInterface> neighbors) {
        super(myNA, neighbors);
    }

    @Override
    protected void handlePacket(MFPacket packet) throws IOException {
        if (packet.getType() == MFPacketGNRS.MF_PACKET_TYPE_GNRS) {
            MFPacketGNRS pkt = (MFPacketGNRS) packet;
            if(pkt.getPayload().getType() == MFPacketGNRSPayloadQuery.MF_GNRS_PACKET_PAYLOAD_TYPE_QUERY){
                //TODO: look up the GNRS table and send response
            }
            else if(pkt.getPayload().getType() == MFPacketGNRSPayloadAssoc.MF_GNRS_PACKET_PAYLOAD_TYPE_ASSOC){
                //TODO: add the subscriber GUID to the mapping of topic GUID if existed
                //TODO: renew the multicast tree and multicast GNRS sync
            }
        } else {
            System.out.println("This is not the correct packet type that GNRS should receive.");
        }
    }

}

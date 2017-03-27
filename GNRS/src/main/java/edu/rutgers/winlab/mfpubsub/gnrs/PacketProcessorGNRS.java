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
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketGNRSPayloadSync;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketNetworkRenew;
import edu.rutgers.winlab.mfpubsub.common.structure.Address;
import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            if (pkt.getPayload().getType() == MFPacketGNRSPayloadQuery.MF_GNRS_PACKET_PAYLOAD_TYPE_QUERY) {
                response(pkt);
            } else if (pkt.getPayload().getType() == MFPacketGNRSPayloadAssoc.MF_GNRS_PACKET_PAYLOAD_TYPE_ASSOC) {
                //TODO: add the subscriber GUID to the mapping of topic GUID in GraohTable if existed
                //TODO: renew the multicast tree and multicast GNRS sync
                renewMulticast((MFPacketGNRSPayloadAssoc) pkt.getPayload());
            }
        } else if (packet.getType() == MFPacketNetworkRenew.MF_PACKET_TYPE_NETWORK_RENEW) {
            System.out.println("TODO: renew the AddrTable");
        } else {
            System.out.println("This is not the correct packet type that GNRS should receive.");
        }
    }

    private void response(MFPacketGNRS query) throws IOException {
        NA rsp = AddrTable.get(((MFPacketGNRSPayloadQuery) query.getPayload()).getQuery());
        if (rsp != null) {
            sendToNeighbor(query.getSrcNa(), new MFPacketGNRS(query.getDstNA(), query.getSrcNa(), new MFPacketGNRSPayloadResponse(rsp)));
        }
    }

    private void renewMulticast(MFPacketGNRSPayloadAssoc assoc) throws IOException {
        GraphAdd(assoc.getTopicGUID(), assoc.getSubscriber());
        AddrTable.put(assoc.getTopicGUID(), assoc.getRP());
        HashMap<NA, List<Address>> tree = assoc.getTree();
        for(Map.Entry<NA, List<Address>> entry : tree.entrySet()){
            sendToNeighbor(entry.getKey(), new MFPacketGNRS(getNa(), entry.getKey(), new MFPacketGNRSPayloadSync(assoc.getTopicGUID(), entry.getValue())));
        }
    }

    private void GraphAdd(GUID key, GUID value) {
        ArrayList<GUID> tmp = GraphTable.get(key);
        if (tmp == null) {
            GraphTable.put(key, tmp = new ArrayList<>());
        }
        tmp.add(value);
    }

}

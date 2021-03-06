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
public class PacketProcessorGNRS extends PacketProcessor {

    private final GUID PUBSUB;

    private final HashMap<GUID, NA> AddrTable;
    //stored in computation node
//    private final HashMap<GUID, ArrayList<GUID>> GraphTable;

    private final HashMap<GUID, ArrayList<MFPacketGNRS>> pendingTable = new HashMap<>();

//    public PacketProcessorGNRS(HashMap<GUID, ArrayList<GUID>> GraphTable, NA myNA, HashMap<NA, NetworkInterface> neighbors) {
//        super(myNA, neighbors);
//        this.AddrTable = new HashMap<>();
//        this.GraphTable = GraphTable;
//    }
    public PacketProcessorGNRS(GUID PUBSUB, HashMap<GUID, NA> AddrTable, NA myNA, HashMap<NA, NetworkInterface> neighbors) {
        super(myNA, neighbors);
        this.PUBSUB = PUBSUB;
        this.AddrTable = AddrTable;
//        this.GraphTable = GraphTable;
    }

    @Override
    protected void handlePacket(MFPacket packet) throws IOException {
        if (packet.getType() == MFPacketGNRS.MF_PACKET_TYPE_GNRS) {
            MFPacketGNRS pkt = (MFPacketGNRS) packet;
            switch (pkt.getPayload().getType()) {
                case MFPacketGNRSPayloadQuery.MF_GNRS_PACKET_PAYLOAD_TYPE_QUERY:
                    response(pkt);
                    break;
                case MFPacketGNRSPayloadAssoc.MF_GNRS_PACKET_PAYLOAD_TYPE_ASSOC:
                    renewMulticast((MFPacketGNRSPayloadAssoc) pkt.getPayload());
                    break;
                default:
                    System.err.println("receive a wrong packet type which shouldn't receive actually.");
            }
        } else {
            System.out.println("This is not the correct packet type that GNRS should receive.");
        }
    }

    private void response(MFPacketGNRS query) throws IOException {
        GUID queriedGUID = ((MFPacketGNRSPayloadQuery) query.getPayload()).getQuery();
        NA rsp = AddrTable.get(queriedGUID);
        if (rsp != null) {
            getNa().print(System.out).printf("responsing the query").println();
            sendToNeighbor(null, new MFPacketGNRS(query.getDstNA(), query.getSrcNa(), new MFPacketGNRSPayloadResponse(queriedGUID, rsp)));
        } else {
            //query pubsub for multicast tree since it isn't stored in GNRS
            getNa().print(System.out).printf("receive the query, ask the pubsub first, will respnse later").println();
            PTadd(queriedGUID, query);
            sendToNeighbor(AddrTable.get(PUBSUB), new MFPacketGNRS(getNa(), AddrTable.get(PUBSUB), new MFPacketGNRSPayloadQuery(queriedGUID)));
        }
    }

    private void renewMulticast(MFPacketGNRSPayloadAssoc assoc) throws IOException {
//        System.out.println("numberofbranches " + assoc.getNumofbranches());
        if (assoc.getNumofbranches() != 0) {//pubsub-assoc msg
            switch (assoc.getAdd()) {
                case MFPacketGNRSPayloadAssoc.MF_GNRS_PACKET_PAYLOAD_TYPE_ASSOC_SUB:
//                    System.out.println("sub");
                    SubTrees(assoc);
                    break;
                case MFPacketGNRSPayloadAssoc.MF_GNRS_PACKET_PAYLOAD_TYPE_ASSOC_UNSUB:
//                    System.out.println("unsub");
//                    UnsubTree(assoc);
                    break;
                default:
                    System.err.println("do not know what this packet want to do, sub or unsub?");
            }
        } else {//the normal GNRS update msg
            AddrTable.put(assoc.getTopicGUID(), assoc.getRP());
        }
        AddrTable.put(assoc.getTopicGUID(), assoc.getRP());
        if (pendingTable.containsKey(assoc.getTopicGUID())) {
            ArrayList<MFPacketGNRS> packets = pendingTable.get(assoc.getTopicGUID());
            for (MFPacketGNRS pkt : packets) {
                response(pkt);
            }
        } else {
            System.out.println("query packet is not stored in the pendingtable");
        }
    }

//    private void GraphAdd(GUID key, GUID value) {
//        ArrayList<GUID> tmp = GraphTable.get(key);
//        if (tmp == null) {
//            GraphTable.put(key, tmp = new ArrayList<>());
//        }
//        tmp.add(value);
//    }
    private void SubTrees(MFPacketGNRSPayloadAssoc assoc) throws IOException {
//        if (assoc.getNumofsub() != 0) {
//            GraphAdd(assoc.getTopicGUID(), assoc.getSubscriber());
//        }
//        if (!assoc.getSubscriber().isNULL()) {
//            GraphAdd(assoc.getTopicGUID(), assoc.getSubscriber());
//        }
        AddrTable.put(assoc.getTopicGUID(), assoc.getRP());
        
        HashMap<NA, ArrayList<Address>> tree = assoc.getTree();
        for (Map.Entry<NA, ArrayList<Address>> entry : tree.entrySet()) {
//            MFPacketGNRS p = new MFPacketGNRS(getNa(), entry.getKey(), new MFPacketGNRSPayloadSync(assoc.getTopicGUID(), entry.getValue()));
//            p.print(System.out).println();
            sendToNeighbor(NA.NA_NULL, new MFPacketGNRS(getNa(), entry.getKey(), new MFPacketGNRSPayloadSync(assoc.getTopicGUID(), entry.getValue())));
        }
    }

//    private void UnsubTree(MFPacketGNRSPayloadAssoc assoc) throws IOException {
////        if (assoc.getNumofsub() != 0) {
////            GraphDelete(assoc.getTopicGUID(), assoc.getSubscriber());
////        }
//        if (!assoc.getSubscriber().isNULL()) {
//            GraphDelete(assoc.getTopicGUID(), assoc.getSubscriber());
//        }
//        if(assoc.getTree().isEmpty()){
//            
//        }
//        AddrTable.put(assoc.getTopicGUID(), assoc.getRP());
//        HashMap<NA, ArrayList<Address>> tree = assoc.getTree();
//        for (Map.Entry<NA, ArrayList<Address>> entry : tree.entrySet()) {
//            sendToNeighbor(entry.getKey(), new MFPacketGNRS(getNa(), entry.getKey(), new MFPacketGNRSPayloadSync(assoc.getTopicGUID(), entry.getValue())));
//        }
//    }
//    private void GraphDelete(GUID key, GUID value) {
//        ArrayList<GUID> tmp = GraphTable.get(key);
//        if (tmp != null) {
//            tmp.remove(value);
//        }
//    }
//    private void GraphDelete(GUID key, List<GUID> value) {
//        ArrayList<GUID> tmp = GraphTable.get(key);
//        if (tmp != null) {
//            for (GUID guid : value) {
//                tmp.remove(guid);
//            }
//        }
//    }
    public void PTadd(GUID dst, MFPacketGNRS packet) {
        ArrayList<MFPacketGNRS> pendings = pendingTable.get(dst);
        if (pendings == null) {
            pendingTable.put(dst, pendings = new ArrayList<>());
        }
        pendings.add(packet);
    }
}

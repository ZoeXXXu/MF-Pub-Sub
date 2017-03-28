package edu.rutgers.winlab.mfpubsub.router;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import edu.rutgers.winlab.mfpubsub.common.elements.NetworkInterface;
import edu.rutgers.winlab.mfpubsub.common.elements.PacketProcessor;
import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacket;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketData;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketDataPublish;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketDataUnicast;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketGNRS;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketGNRSPayloadResponse;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketGNRSPayloadSync;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketNetworkRenew;
import edu.rutgers.winlab.mfpubsub.common.structure.Address;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author zoe
 */
public class PacketProcessorRouter extends PacketProcessor {

    private final HashMap<GUID, ArrayList<MFPacketData>> pendingTable;
    private final HashMap<NA, NA> routingTable;
    private final HashMap<GUID, List<Address>> multicastTable;
    private final HashMap<GUID, NA> localGUIDTable;
    private final GUID GNRS_GUID;

    public PacketProcessorRouter(GUID GNRS, HashMap<GUID, NA> localGUIDTable, HashMap<NA, NA> RoutingTable, NA myNA, HashMap<NA, NetworkInterface> neighbors) {
        super(myNA, neighbors);
        this.multicastTable = new HashMap<>();
        this.pendingTable = new HashMap<>();
        this.routingTable = RoutingTable;
        this.localGUIDTable = localGUIDTable;
        this.GNRS_GUID = GNRS;
    }

    @Override
    public PrintStream print(PrintStream ps) throws IOException {
        return super.print(ps.printf("\nRouter")).printf("\n");
    }

    /**
     * router logic (implement the router flowchart in doc)
     *
     * @param packet
     * @throws IOException
     */
    @Override
    protected void handlePacket(MFPacket packet) throws IOException {
        if (packet.getDstNA().getVal() == 0) {//validate
            MFPacketData pkt = (MFPacketData) packet;
            GUID key = pkt.getdstGuid();
            QueryGNRS(key, pkt);
            InvokePacket(new MFPacketGNRSPayloadResponse(pkt.getdstGuid(), new NA(4)));
        } else if (packet.getDstNA().equals(getNa())) { // the packet is sent to myself
            switch (packet.getType()) {
                case MFPacketData.MF_PACKET_TYPE_DATA:
                    packet.print(getNa().print(System.out.printf("receive packet at ")).printf("packet = ")).println();
                    MFPacketData pkt = (MFPacketData) packet;
                    switch (pkt.getSID()) {//TODO: if it is unicast, send by looking up LT; if it is publish: send by looking up MT
                        case MFPacketDataPublish.MF_PACKET_DATA_SID_PUBLISH:
                            PublishToMulticastGroup(pkt);
                            break;
                        case MFPacketDataUnicast.MF_PACKET_DATA_SID_UNICAST:
                            SendToGUID(pkt, pkt.getdstGuid());
                            break;
                        default:
                            System.err.println("The packet SID number is not exist.");
                    }
                    break;
                case MFPacketGNRS.MF_PACKET_TYPE_GNRS:
                    switch ((((MFPacketGNRS) packet).getPayload()).getType()) {
                        case MFPacketGNRSPayloadSync.MF_GNRS_PACKET_PAYLOAD_TYPE_SYNC:
                            updateMT((MFPacketGNRSPayloadSync) ((MFPacketGNRS) packet).getPayload());
                            break;
                        case MFPacketGNRSPayloadResponse.MF_GNRS_PACKET_PAYLOAD_TYPE_RESPONSE:
                            InvokePacket((MFPacketGNRSPayloadResponse) ((MFPacketGNRS) packet).getPayload());
                            break;
                        default:
                            System.err.println("The GNRS packet type is out of service.");
                    }
                default:
                    System.err.println("PacketProcessorRouter.handlePacket(): shouldn't have such types.");
            }
        } else { // i know where to forward the packet
            Routing(packet);
        }
    }

    private void updateMT(MFPacketGNRSPayloadSync sync) {
        multicastTable.put(sync.getTopicGUID(), sync.getMulticast());
    }

    //this may need to be created at another class in mysql
    private void Routing(MFPacket packet) throws IOException {
        NA i = routingTable.get(packet.getDstNA());
        if (i == null) {
            throw new IOException(String.format("Cannot find next hop: %s on %s", packet.getDstNA(), this));
        }
        getNa().print(System.out.printf("transmist by ")).println();
        sendToNeighbor(i, packet);
    }

    public void send(NA neighbor, MFPacket packet) throws IOException {
        packet.print(System.out.printf("")).println();
        sendToNeighbor(neighbor, packet);
    }

    public PrintStream printRoutingTable(PrintStream ps) throws IOException {
        getNa().print(ps.printf("Node NA=")).println();
        for (Map.Entry<NA, NA> entry : routingTable.entrySet()) {
            NA destna = entry.getKey();
            NA face = entry.getValue();
            destna.print(ps).printf("->");
            face.print(ps).println();
        }
        return ps;
    }

    public PrintStream printMulticastTable(PrintStream ps) throws IOException {
        getNa().print(ps.printf("Node NA=")).println();
        for (Map.Entry<GUID, List<Address>> entry : multicastTable.entrySet()) {
            entry.getKey().print(ps).printf(" -> ");
            for (Address a : entry.getValue()) {
                a.print(ps).printf("; ");
            }
        }
        return ps;
    }

    /**
     * send the query to GNRS and add the pending packet into pending table
     *
     * @param dstGuid
     * @param packet
     */
    private void QueryGNRS(GUID dstGuid, MFPacketData packet) {
        getNa().print(System.out.printf("TODO: query GNRS with provided GUID: ")).println();
        PTadd(dstGuid, packet);
    }

    private void InvokePacket(MFPacketGNRSPayloadResponse response) throws IOException {
        getNa().print(System.out.printf("")).printf("invoke the paket with dstGUID: ");
        response.getQueriedGUID().print(System.out.printf("")).println();
        ArrayList<MFPacketData> packets = pendingTable.remove(response.getQueriedGUID());
        NA na = response.getNa();
        if (packets != null) {
            for (MFPacketData packet : packets) {
                Routing(new MFPacketDataPublish(packet.getsrcGuid(), packet.getdstGuid(), na, packet.getPayload()));
            }
        }
    }

    private void RenewNAandSend(MFPacketData packet, NA na) throws IOException {
//        getNa().print(System.out.printf("transmist by ")).println();
        Routing(new MFPacketDataPublish(packet.getsrcGuid(), packet.getdstGuid(), na, packet.getPayload()));
    }

    private void PublishToMulticastGroup(MFPacketData packet) throws IOException {
        List<Address> multicast = (List<Address>) multicastTable.get(packet.getdstGuid());
        if (multicast != null) {
            for (Address address : multicast) {
                if (address instanceof GUID) {
                    SendToGUID(packet, (GUID) address);
                } else if (address instanceof NA) {
                    NA na = (NA) address;
                    RenewNAandSend(packet, na);
                } else {
                    System.out.println("Something wrong this multicast table ");
                }
            }
        } else {
            //Or just print the notification?
            throw new IOException(String.format("no GUID %s exist in NA %s", packet.getdstGuid(), getNa().getVal()));
        }
    }

    private void SendToGUID(MFPacketData packet, GUID key) throws IOException {
        NA dstna = localGUIDTable.get(key);
        getNa().print(System.out.printf("transmist by ")).println();
        sendToNeighbor(dstna, new MFPacketDataUnicast(packet.getsrcGuid(), key, dstna, packet));
    }

    public void RTadd(NA dst, NA neighbor) {
        routingTable.put(dst, neighbor);
    }

    public void RTdelete(NA delete) {
        routingTable.remove(delete);
    }

    public void PTadd(GUID dst, MFPacketData packet) {
        ArrayList<MFPacketData> pendings = pendingTable.get(dst);
        if (pendings == null) {
            pendingTable.put(dst, pendings = new ArrayList<>());
        }
        pendings.add(packet);
    }

    public void MTadd(GUID topic, Address addr) {
        List<Address> multicast = multicastTable.get(topic);
        if (multicast == null) {
            multicastTable.put(topic, multicast = new ArrayList<>());
        }
        multicast.add(addr);
    }
}

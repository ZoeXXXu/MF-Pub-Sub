/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.router;

import edu.rutgers.winlab.mfpubsub.common.elements.NetworkInterface;
import edu.rutgers.winlab.mfpubsub.common.elements.PacketProcessor;
import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacket;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketData;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketGNRS;
import edu.rutgers.winlab.mfpubsub.common.structure.Address;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import edu.rutgers.winlab.mfpubsub.common.structure.TreeBranch;
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

    private final HashMap<GUID, ArrayList<MFPacket>> pendingTable;
    private final HashMap<NA, NA> routingTable;
    private final TreeBranch multicastTable;
    private final HashMap<GUID, NA> localGUIDTable;
    private final GUID GNRS_GUID;

    public PacketProcessorRouter(GUID GNRS, HashMap<GUID, NA> localGUIDTable, TreeBranch MulticastTable, HashMap<NA, NA> RoutingTable, NA myNA, HashMap<NA, NetworkInterface> neighbors) {
        super(myNA, neighbors);
        this.multicastTable = MulticastTable;
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
        if (packet.getDstNA().getVal() == 0) {
            GUID key = ((MFPacketData) packet).getdstGuid();
            QueryGNRS(key, packet);
            InvokePacket(key, (NA) multicastTable.getTree(key).get(0));
        } else if (packet.getDstNA().equals(getNa())) {
            if (packet.getType() == MFPacketData.MF_PACKET_TYPE_DATA) {
                //look up MT
//                getNa().print(System.out.printf("TODO: MT look up function, return true if found it...")).println();
                packet.print(getNa().print(System.out.printf("receive packet from ")).printf("packet = ")).println();
                //only na 4 will do this
                LookUpMulticastTable((MFPacketData) packet);
            } else if (packet.getType() == MFPacketGNRS.MF_PACKET_TYPE_GNRS) {
                getNa().print(System.out.printf("GNRS packet process haven't been done, temperally skip!"));
            } else {
                getNa().print(System.out.printf("PacketProcessorRouter.handlePacket(): shouldn't have such types."));
            }
        } else {
            MFPacketData pkt = (MFPacketData) packet;
            NA dstna = LookUpRoutingTable(pkt.getDstNA());
            getNa().print(System.out.printf("transmist by ")).println();
            sendToNeighbor(dstna, new MFPacketData(pkt.getsrcGuid(), pkt.getdstGuid(), pkt.getDstNA(), pkt.getPayload()));
        }
    }

    //this may need to be created at another class in mysql
    private NA LookUpRoutingTable(NA na) throws IOException {
        NA i = routingTable.get(na);
        if (i == null) {
            throw new IOException(String.format("Cannot find next hop: %s on %s", na, this));
        }
        return i;
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
        for (Map.Entry<GUID, ArrayList<Address>> entry : multicastTable.getTree().entrySet()) {
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
    private void QueryGNRS(GUID dstGuid, MFPacket packet) {
        getNa().print(System.out.printf("TODO: query GNRS with provided GUID: ")).println();
        PTadd(dstGuid, packet);
    }

    private void InvokePacket(GUID key, NA na) throws IOException {
        getNa().print(System.out.printf("")).printf("invoke the paket with dstGUID: ");
        key.print(System.out.printf("")).println();
        ArrayList<MFPacket> packets = pendingTable.get(key);
        if (packets == null) {
            throw new IOException(String.format("Cannot find packet: %s in %s", key, this));
        }
        pendingTable.remove(key);
        for (MFPacket p : packets) {
            PacketUpdate((MFPacketData) p, na);
        }
    }

    private void PacketUpdate(MFPacketData packet, NA na) throws IOException {
        NA dstna = LookUpRoutingTable(na);
        getNa().print(System.out.printf("transmist by ")).println();
        sendToNeighbor(dstna, new MFPacketData(packet.getsrcGuid(), packet.getdstGuid(), na, packet.getPayload()));
    }

    private void RTadd(NA dst, NA neighbor) {
        routingTable.put(dst, neighbor);
    }

    private void RTdelete(NA delete) {
        routingTable.remove(delete);
    }

    private void PTadd(GUID dst, MFPacket packet) {
        if (pendingTable.containsKey(dst)) {
            pendingTable.get(dst).add(packet);
        } else {
            pendingTable.put(dst, new ArrayList<MFPacket>());
            pendingTable.get(dst).add(packet);
        }
    }

    private void LookUpMulticastTable(MFPacketData packet) throws IOException {
        List<Address> nextHops = (List<Address>) multicastTable.getTree(packet.getdstGuid());
        if(nextHops == null){
            throw new IOException(String.format("no GUID %s exist in NA %s", packet.getdstGuid(), getNa().getVal()));
        }
        for (Address address : nextHops) {
            if (address instanceof GUID) {
                LoopUpLocalGUIDTable(packet, (GUID) address);
            } else if (address instanceof NA) {
                NA na = (NA) address;
                PacketUpdate(packet, na);
            } else {
                System.out.println("Something wrong this multicast table ");
            }
        }
    }

    private void LoopUpLocalGUIDTable(MFPacketData packet, GUID key) throws IOException {
        NA dstna = localGUIDTable.get(key);
        getNa().print(System.out.printf("transmist by ")).println();
        sendToNeighbor(dstna, new MFPacketData(packet.getsrcGuid(), key, dstna, new MFPacketData(packet.getsrcGuid(), packet.getdstGuid(), new NA(0), packet.getPayload())));
    }
}

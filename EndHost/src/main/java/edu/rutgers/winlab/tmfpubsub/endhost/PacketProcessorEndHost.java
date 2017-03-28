/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.tmfpubsub.endhost;

import edu.rutgers.winlab.mfpubsub.common.elements.NetworkInterface;
import edu.rutgers.winlab.mfpubsub.common.elements.PacketProcessor;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacket;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketData;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketFactory;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketGNRS;
import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

/**
 *
 * @author zoe
 */
public class PacketProcessorEndHost extends PacketProcessor {

    private final GUID myGUID;

    public PacketProcessorEndHost(GUID myGUID, NA myNA, HashMap<NA, NetworkInterface> neighbors) {
        super(myNA, neighbors);
        this.myGUID = myGUID;
    }

    @Override
    public PrintStream print(PrintStream ps) throws IOException {
        return super.print(ps.printf("\nRouter")).printf("\n");
    }

    public void send(NA neighbor, MFPacket packet) throws IOException {
        packet.print(System.out.printf("")).println();
        sendToNeighbor(neighbor, packet);
    }

    @Override
    protected void handlePacket(MFPacket packet) throws IOException {
        getNa().print(System.out.printf("NA ")).println("receive packet");
//        ((MFPacketData) packet).getsrcGuid().print(System.out.printf("receive from the publisher ")).println();
        switch (packet.getType()) {
            case MFPacketData.MF_PACKET_TYPE_DATA:
                try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
                    ((MFPacketData) packet).getPayload().serialize(stream);
                    MFPacket pkt = MFPacketFactory.createPacket(stream.toByteArray(), new int[]{0});
                    pkt.print(System.out).println();
                }
                break;
            case MFPacketGNRS.MF_PACKET_TYPE_GNRS:
                getNa().print(System.err.printf("The end host should not receive the GNRS message. Please check the logic.")).println();
            default:
                System.err.println("not a correct packet type");
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.transmissionshared.endhost;

import edu.rutgers.winlab.mfpubsub.common.elements.NetworkInterface;
import edu.rutgers.winlab.mfpubsub.common.elements.PacketProcessor;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacket;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketData;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketGNRS;
import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

/**
 *
 * @author zoe
 */
public class PacketProcessorSubscriber extends PacketProcessor {

    private final GUID myGUID;

    public PacketProcessorSubscriber(GUID myGUID, NA myNA, HashMap<NA, NetworkInterface> neighbors) {
        super(myNA, neighbors);
        this.myGUID = myGUID;
    }

    @Override
    public PrintStream print(PrintStream ps) throws IOException{
        return super.print(ps.printf("\nRouter")).printf("\n");
    }

    @Override
    protected void handlePacket(MFPacket packet) throws IOException {
        if (packet.getType() == MFPacketData.MF_PACKET_TYPE_DATA) {
            MFPacketData pkt = (MFPacketData) ((MFPacketData) ((MFPacketData) packet).getPayload()).getPayload();
            pkt.getPayload().print(System.out.printf("receive an published content: "));
            pkt.getsrcGuid().print(System.out.printf(" from the publisher ")).println();
        } else if (packet.getType() == MFPacketGNRS.MF_PACKET_TYPE_GNRS) {
            getNa().print(System.err.printf("The end host should not receive the GNRS message. Please check the logic.")).println();
        }
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.common.packets;

import java.io.IOException;

/**
 *
 * @author ubuntu
 */
public class MFPacketFactory {
  

    public static MFPacket createPacket(byte[] packet, int[] pos) throws IOException {
        byte type = packet[pos[0]++];
        switch (type) {
            case 0:
                return MFPacketData.createDatapacket(packet, pos);
            case 6:
                return MFPacketGNRS.createGNRSpacket(packet, pos);
            case 9:
                return MFPacketNetworkRenew.createUpdatePacket(packet, pos);
            default:
                throw new IllegalArgumentException("Invalid packet type: " + type);
        }
    }
}

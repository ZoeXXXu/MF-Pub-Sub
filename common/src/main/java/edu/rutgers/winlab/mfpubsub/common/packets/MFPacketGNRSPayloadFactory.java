/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.common.packets;

/**
 *
 * @author ubuntu
 */
public class MFPacketGNRSPayloadFactory {
    public static MFPacketGNRSPayload createPayload(byte[] packet, int[] pos) {
        byte type = packet[pos[0]];
        pos[0]++;
        switch(type) {
            case MFPacketGNRSPayloadQuery.MF_GNRS_PACKET_PAYLOAD_TYPE_QUERY:
                return MFPacketGNRSPayloadQuery.createMFGNRSPacketPayloadQuery(packet, pos);
        }
        return null;
    }
    
}

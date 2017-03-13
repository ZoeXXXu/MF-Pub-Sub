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
public class MFPacketDataPayloadFactory {
    public static ISerializable createPayload(byte[] packet, int[] pos) {
        return MFPacketDataPayloadRandom.createRandomPayload(packet, pos[0]);
    }
}

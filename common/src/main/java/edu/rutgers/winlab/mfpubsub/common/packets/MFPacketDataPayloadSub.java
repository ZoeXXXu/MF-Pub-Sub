/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.common.packets;

import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 *
 * @author zoe
 */
public class MFPacketDataPayloadSub implements ISerializable {

    public static final byte MF_PACKET_DATA_SID_SUBSCRIPTION = 2;

    private final GUID topicGUID;

    public GUID getTopicGUID() {
        return topicGUID;
    }

    public static ISerializable createSubPayload(byte[] packet, int[] pos) {
        GUID topic = GUID.create(packet, pos);
        return new MFPacketDataPayloadSub(topic);
    }

    public MFPacketDataPayloadSub(GUID topicGUID) {
        this.topicGUID = topicGUID;
    }

    @Override
    public OutputStream serialize(OutputStream stream) throws IOException {
        return topicGUID.serialize(stream);
    }

    @Override
    public PrintStream print(PrintStream ps) {
        return topicGUID.print(ps.printf("[topicGUID=")).printf("]");
    }

}

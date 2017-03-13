/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.common.packets;

import edu.rutgers.winlab.mfpubsub.common.Helper;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 *
 * @author ubuntu
 */
public class MFPacketDataPayloadRandom implements ISerializable {

    private final byte[] payload;

    public MFPacketDataPayloadRandom(byte[] payload) {
        this.payload = payload;
    }

    @Override
    public OutputStream serialize(OutputStream stream) throws IOException {
        stream.write(payload);
        return stream;
    }

    public static MFPacketDataPayloadRandom createRandomPayload(byte[] packet, int pos) {
        byte[] payload = new byte[packet.length - pos];
        System.arraycopy(packet, pos, payload, 0, payload.length);
        return new MFPacketDataPayloadRandom(payload);
    }

    @Override
    public PrintStream print(PrintStream ps) {
        return Helper.printBuf(ps.printf("RndPld["), payload, 0, payload.length).printf("]");
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.common.packets;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 *
 * @author ubuntu
 */
public class MFPacketGNRSPayload implements ISerializable {

    private final byte type;

    public MFPacketGNRSPayload(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }

    @Override
    public OutputStream serialize(OutputStream stream) throws IOException {
        stream.write(type);
        return stream;
    }

    @Override
    public PrintStream print(PrintStream ps) {
        return ps.printf("Type=%02x", type);
    }

}

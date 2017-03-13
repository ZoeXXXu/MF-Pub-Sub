/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.common.packets;

import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 *
 * @author ubuntu
 */
public class MFPacket implements ISerializable, Comparable<MFPacket> {

    private final byte type;
    private final NA dstNA;
    private final boolean prioritized;

    public MFPacket(byte type, NA dstNA, boolean prioritized) {
        this.type = type;
        this.dstNA = dstNA;
        this.prioritized = prioritized;
    }

    public byte getType() {
        return type;
    }

    public NA getDstNA() {
        return dstNA;
    }
    
    public boolean isPrioritized() {
        return prioritized;
    }

    @Override
    public OutputStream serialize(OutputStream stream) throws IOException {
        stream.write(type);
        dstNA.serialize(stream);
        return stream;
    }

    @Override
    public PrintStream print(PrintStream ps) {
        ps.printf("Type=%02x", type);
        return dstNA.print(ps.printf(", dstNA="));
    }

    @Override
    public int compareTo(MFPacket o) {
        return prioritized == o.prioritized ? 0 : (prioritized ? -1 : 1);
    }
}

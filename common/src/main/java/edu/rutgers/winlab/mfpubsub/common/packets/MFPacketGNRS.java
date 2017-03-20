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
public class MFPacketGNRS extends MFPacket {

    public static final byte MF_PACKET_TYPE_GNRS = 6;

    private final NA srcNa;//, dstNa;
    private final MFPacketGNRSPayload payload;

    public NA getSrcNa() {
        return srcNa;
    }

//    public NA getDstNa() {
//        return dstNa;
//    }
    public MFPacketGNRSPayload getPayload() {
        return payload;
    }

    public MFPacketGNRS(NA srcNa, NA dstNa, MFPacketGNRSPayload payload) {
        super(MF_PACKET_TYPE_GNRS, dstNa, true);
        this.srcNa = srcNa;
//        this.dstNa = dstNa;
        this.payload = payload;
    }

    @Override
    public OutputStream serialize(OutputStream stream) throws IOException {
        super.serialize(stream);
        srcNa.serialize(stream);
//        dstNa.serialize(stream);
        payload.serialize(stream);
        return stream;
    }

    @Override
    public PrintStream print(PrintStream ps) {
        super.print(ps.printf("GNRS["));
        srcNa.print(ps.printf(", srcNa="));
//        dstNa.print(ps.printf(", dstNa="));
        payload.print(ps.printf(", pld=")).println("]");
        return ps;
    }

    public static MFPacketGNRS createGNRSpacket(byte[] packet, int[] pos) throws IOException {
        NA dstNa = NA.create(packet, pos);
        NA srcNa = NA.create(packet, pos);
        MFPacketGNRSPayload payload = MFPacketGNRSPayloadFactory.createPayload(packet, pos);
        return new MFPacketGNRS(srcNa, dstNa, payload);
    }

}

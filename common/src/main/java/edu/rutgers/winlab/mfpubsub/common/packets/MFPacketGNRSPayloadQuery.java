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
 * @author ubuntu
 */
public class MFPacketGNRSPayloadQuery extends MFPacketGNRSPayload {

    public static final byte MF_GNRS_PACKET_PAYLOAD_TYPE_QUERY = 0;

    private final GUID guid;

    public MFPacketGNRSPayloadQuery(GUID guid) {
        super(MF_GNRS_PACKET_PAYLOAD_TYPE_QUERY);
        this.guid = guid;
    }

    @Override
    public OutputStream serialize(OutputStream stream) throws IOException {
        super.serialize(stream);
        guid.serialize(stream);
        return stream;
    }

    @Override
    public PrintStream print(PrintStream ps) {
        super.print(ps.printf("LKP["));
        guid.print(ps.printf(", guid=")).printf("]");
        return ps;
    }

    public static MFPacketGNRSPayloadQuery createMFGNRSPacketPayloadQuery(byte[] buf, int[] pos) {
        GUID guid = GUID.create(buf, pos);
        return new MFPacketGNRSPayloadQuery(guid);
    }

}

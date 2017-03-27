/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.common.packets;

import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 *
 * @author zoe
 */
public class MFPacketGNRSPayloadResponse extends MFPacketGNRSPayload {

    public static final byte MF_GNRS_PACKET_PAYLOAD_TYPE_RESPONSE = 1;

    private final GUID queriedGUID;
    
    private final NA na;

    public MFPacketGNRSPayloadResponse(GUID queriedGUID, NA na) {
        super(MF_GNRS_PACKET_PAYLOAD_TYPE_RESPONSE);
        this.queriedGUID = queriedGUID;
        this.na = na;
    }

    public static MFPacketGNRSPayload createMFGNRSPacketPayloadResponse(byte[] buf, int[] pos) throws IOException {
        GUID query = GUID.create(buf, pos);
        NA nextBranch = NA.create(buf, pos);
        return new MFPacketGNRSPayloadResponse(query, nextBranch);
    }
    
    @Override
    public OutputStream serialize(OutputStream stream) throws IOException{
        super.serialize(stream);
        queriedGUID.serialize(stream);
        na.serialize(stream);
        return stream;
    }
    
    @Override
    public PrintStream print(PrintStream ps){
        super.print(ps.printf("LKP["));
        queriedGUID.print(ps.printf(", GUID="));
        na.print(ps.printf(", na=")).printf("]");
        return ps;
    }

    public NA getNa() {
        return na;
    }

    public GUID getQueriedGUID() {
        return queriedGUID;
    }
}

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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zoe
 */
public class MFPacketGNRSPayloadResponse extends MFPacketGNRSPayload {

    public static final byte MF_GNRS_PACKET_PAYLOAD_TYPE_RESPONSE = 1;

    private final NA na;
    
    public MFPacketGNRSPayloadResponse(NA nextBranch) {
        super(MF_GNRS_PACKET_PAYLOAD_TYPE_RESPONSE);
        this.na = nextBranch;
    }

    public static MFPacketGNRSPayload createMFGNRSPacketPayloadResponse(byte[] buf, int[] pos) throws IOException {
        NA nextBranch = NA.create(buf, pos);
        return new MFPacketGNRSPayloadResponse(nextBranch);
    }
    
    @Override
    public OutputStream serialize(OutputStream stream) throws IOException{
        super.serialize(stream);
        na.serialize(stream);
        return stream;
    }
    
    @Override
    public PrintStream print(PrintStream ps){
        super.print(ps.printf("LKP["));
        na.print(ps.printf(", na=")).printf("]");
        return ps;
    }
}

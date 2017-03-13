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
 * @author ubuntu
 */
public class MFPacketData extends MFPacket {

    public static final byte MF_PACKET_TYPE_DATA = 0;

    private final GUID srcGUID, dstGUID;
//    private final NA na;
    private final ISerializable payload;

    public MFPacketData(GUID srcGUID, GUID dstGUID, NA na, ISerializable payload) {
        super(MF_PACKET_TYPE_DATA, na, false);
        this.srcGUID = srcGUID;
        this.dstGUID = dstGUID;
//        this.na = na;
        this.payload = payload;
    }
    
    public GUID getdstGuid(){
        return dstGUID;
    }
    
    public GUID getsrcGuid(){
        return srcGUID;
    }

    public ISerializable getPayload() {
        return payload;
    }
            
    @Override
    public OutputStream serialize(OutputStream stream) throws IOException {
        super.serialize(stream);
//        na.serialize(stream);
        srcGUID.serialize(stream);
        dstGUID.serialize(stream);
        payload.serialize(stream);
        return stream;
    }

    public static MFPacketData createDatapacket(byte[] packet, int[] pos) throws IOException {
        NA na = NA.create(packet, pos);
        GUID srcGUID = GUID.create(packet, pos);
        GUID dstGUID = GUID.create(packet, pos);
        ISerializable payload = MFPacketDataPayloadFactory.createPayload(packet, pos);
        return new MFPacketData(srcGUID, dstGUID, na, payload);
    }

    @Override
    public PrintStream print(PrintStream ps) {
        super.print(ps.printf("Data["));
//        na.print(ps.printf(", NA="));
        srcGUID.print(ps.printf(", src="));
        dstGUID.print(ps.printf(", dst="));
        payload.print(ps.printf(", pld=")).printf("]");
        return ps;
    }

}

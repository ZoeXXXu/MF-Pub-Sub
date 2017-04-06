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

    private final byte SID;

    private final ISerializable payload;

    public MFPacketData(GUID srcGUID, GUID dstGUID, NA na, byte SID, ISerializable payload) {
        super(MF_PACKET_TYPE_DATA, na, false);
        this.srcGUID = srcGUID;
        this.dstGUID = dstGUID;
        this.SID = SID;
        this.payload = payload;
    }

    public GUID getdstGuid() {
        return dstGUID;
    }

    public byte getSID() {
        return SID;
    }

    public GUID getsrcGuid() {
        return srcGUID;
    }

    public ISerializable getPayload() {
        return payload;
    }

    @Override
    public OutputStream serialize(OutputStream stream) throws IOException {
        super.serialize(stream);
        stream.write(SID);
        srcGUID.serialize(stream);
        dstGUID.serialize(stream);
        payload.serialize(stream);
        return stream;
    }

    public static MFPacketData createDatapacket(byte[] packet, int[] pos) throws IOException {
        NA na = NA.create(packet, pos);
        byte SID = packet[pos[0]++];
        GUID srcGUID = GUID.create(packet, pos);
        GUID dstGUID = GUID.create(packet, pos);
//        ISerializable payload = MFPacketDataPayloadFactory.createPayload(SID, packet, pos);
        switch (SID) {
            case MFPacketDataPublish.MF_PACKET_DATA_SID_PUBLISH:
                return new MFPacketDataPublish(srcGUID, dstGUID, na, MFPacketDataPayloadFactory.createPayload(packet, pos));
            case MFPacketDataUnicast.MF_PACKET_DATA_SID_UNICAST:
                return new MFPacketDataUnicast(srcGUID, dstGUID, na, MFPacketDataPayloadFactory.createPayload(packet, pos));
            case MFPacketDataPayloadSub.MF_PACKET_DATA_SID_SUBSCRIPTION:
                return new MFPacketData(srcGUID, dstGUID, na, MFPacketDataPayloadSub.MF_PACKET_DATA_SID_SUBSCRIPTION, MFPacketDataPayloadSub.createSubPayload(packet, pos));
            case MFPacketDataPayloadUnsub.MF_PACKET_DATA_SID_UNSUBSCRIPTION:
                return new MFPacketData(srcGUID, dstGUID, na, MFPacketDataPayloadUnsub.MF_PACKET_DATA_SID_UNSUBSCRIPTION, MFPacketDataPayloadUnsub.createSubPayload(packet, pos));
            default:
                throw new IllegalArgumentException("Invalid data packet server ID: " + SID);
        }
    }

    @Override
    public PrintStream print(PrintStream ps) {
        super.print(ps.printf("Data["));
        ps.printf(",SID=%02x", SID);
        srcGUID.print(ps.printf(", src="));
        dstGUID.print(ps.printf(", dst="));
        payload.print(ps.printf(", pld=")).printf("]");
        return ps;
    }

}

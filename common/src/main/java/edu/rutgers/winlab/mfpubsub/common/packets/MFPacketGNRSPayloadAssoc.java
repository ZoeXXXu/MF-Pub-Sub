/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.common.packets;

import edu.rutgers.winlab.mfpubsub.common.Helper;
import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

/**
 *
 * @author zoe
 */
public class MFPacketGNRSPayloadAssoc extends MFPacketGNRSPayload {

    public static final byte MF_GNRS_PACKET_PAYLOAD_TYPE_ASSOC = 3;

    private final GUID subscriber;

    private final GUID topicGUID;

    private final int numofbranch;

    private final int totalnumGUID;

    private final byte[] numofGUID;

    private final byte[] numofNA;

    private final byte[] NAtree;

    private final byte[] GUIDtree;

    public MFPacketGNRSPayloadAssoc(GUID subscriber, GUID topicGUID, int numofBranch, int totalnumGUID, byte[] numofGUID, byte[] numofNA, byte[] NAtree, byte[] GUIDtree) {
        super(MF_GNRS_PACKET_PAYLOAD_TYPE_ASSOC);
        this.subscriber = subscriber;
        this.topicGUID = topicGUID;
        this.numofbranch = numofBranch;
        this.totalnumGUID = totalnumGUID;
        this.numofGUID = numofGUID;
        this.numofNA = numofNA;
        this.NAtree = NAtree;
        this.GUIDtree = GUIDtree;
    }

    public MFPacketGNRSPayloadAssoc(GUID subscriber, GUID topicGUID, int numofBranch, int totalnumGUID, byte[] numofGUID, byte[] numofNA, List<NA> NAtree, List<GUID> GUIDtree) throws IOException {
        super(MF_GNRS_PACKET_PAYLOAD_TYPE_ASSOC);
        this.subscriber = subscriber;
        this.topicGUID = topicGUID;
        this.numofbranch = numofBranch;
        this.totalnumGUID = totalnumGUID;
        this.numofGUID = numofGUID;
        this.numofNA = numofNA;
        this.NAtree = NAListToByte(NAtree);
        this.GUIDtree = GUIDListToByte(GUIDtree);
    }

    public static MFPacketGNRSPayload createMFPacketGNRSPayloadAssoc(byte[] buf, int[] pos) {
//        Helper.printBuf(System.out, buf, pos[0], buf.length);
        GUID subscriber = GUID.create(buf, pos);
        GUID topic = GUID.create(buf, pos);
        topic.print(System.out.printf("")).println();
        int numofBranch = Helper.readInt(buf, pos);
        int totalnumGUID = Helper.readInt(buf, pos);
        byte[] numofGUID = new byte[numofBranch];
        System.arraycopy(buf, pos[0], numofGUID, 0, numofGUID.length);
        pos[0] += numofBranch;
        byte[] numofNA = new byte[numofBranch];
        System.arraycopy(buf, pos[0], numofNA, 0, numofNA.length);
        pos[0] += numofBranch;
        System.out.println("buf length: " + buf.length + ", pos[0]: " + pos[0] + ", total: " + totalnumGUID);
        byte[] NAtree = new byte[buf.length - pos[0] - totalnumGUID * GUID.GUID_LENGTH];
        System.arraycopy(buf, pos[0], NAtree, 0, NAtree.length);
        byte[] GUIDtree = new byte[totalnumGUID * GUID.GUID_LENGTH];
        System.arraycopy(buf, pos[0] + NAtree.length, GUIDtree, 0, GUIDtree.length);
        return new MFPacketGNRSPayloadAssoc(subscriber, topic, numofBranch, totalnumGUID, numofGUID, numofNA, NAtree, GUIDtree);
    }

    @Override
    public PrintStream print(PrintStream ps) {
        super.print(ps.printf("LKP["));
        subscriber.print(ps.printf(", subscriber GUID="));
        topicGUID.print(ps.printf(", topic GUID="));
        Helper.printBuf(ps.printf("NAnumBranch["), numofNA, 0, numofNA.length).printf("]");
        Helper.printBuf(ps.printf("GUIDnumBranch["), numofGUID, 0, numofGUID.length).printf("]");
        Helper.printBuf(ps.printf("NAtree["), NAtree, 0, NAtree.length).printf("]");
        return Helper.printBuf(ps.printf("GUIDtree["), GUIDtree, 0, GUIDtree.length).printf("]");
    }

    @Override
    public OutputStream serialize(OutputStream stream) throws IOException {
        super.serialize(stream);
        subscriber.serialize(stream);
        topicGUID.serialize(stream);
        Helper.writeInt(stream, numofbranch);
        Helper.writeInt(stream, totalnumGUID);
        stream.write(numofGUID);
        stream.write(numofNA);
        stream.write(NAtree);
        stream.write(GUIDtree);
        return stream;
    }

    private byte[] GUIDListToByte(List<GUID> na) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        for (GUID addr : na) {
            addr.serialize(stream);
        }
        return stream.toByteArray();
    }

    private byte[] NAListToByte(List<NA> na) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        for (NA addr : na) {
            addr.serialize(stream);
        }
        return stream.toByteArray();
    }
}

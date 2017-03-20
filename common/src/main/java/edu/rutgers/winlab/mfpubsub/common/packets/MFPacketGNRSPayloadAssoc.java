/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.common.packets;

import edu.rutgers.winlab.mfpubsub.common.Helper;
import edu.rutgers.winlab.mfpubsub.common.structure.Address;
import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author zoe
 */
public class MFPacketGNRSPayloadAssoc extends MFPacketGNRSPayload {

    public static final byte MF_GNRS_PACKET_PAYLOAD_TYPE_ASSOC = 3;

    private final GUID subscriber;

    private final GUID topicGUID;
    
    private final NA RP;

    private final byte[] tree;

    private final transient HashMap<NA, List<Address>> treeH;

//    private final int numofbranch;
//
//    private final int totalnumGUID;
//
//    private final byte[] numofGUID;
//
//    private final byte[] numofNA;
//
//    private final byte[] NAtree;
//
//    private final byte[] GUIDtree;
    public MFPacketGNRSPayloadAssoc(GUID subscriber, GUID topicGUID, NA RP, HashMap<NA, List<Address>> tree) throws IOException {
        super(MF_GNRS_PACKET_PAYLOAD_TYPE_ASSOC);
        this.subscriber = subscriber;
        this.topicGUID = topicGUID;
        this.RP = RP;
        this.tree = ListToByte(tree);
        this.treeH = tree;
    }

    private byte[] ListToByte(HashMap<NA, List<Address>> tree) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        topic.serialize(stream);
        for (Map.Entry<NA, List<Address>> branch : tree.entrySet()) {
            branch.getKey().serialize(stream);
            List<Address> multicast = branch.getValue();
            stream.write(multicast.size());
            for (Address addr : multicast) {
                if (addr instanceof NA) {
                    stream.write(MF_GNRS_PACKET_PAYLOAD_NA);
                    ((NA) addr).serialize(stream);
                } else if (addr instanceof GUID) {
                    stream.write(MF_GNRS_PACKET_PAYLOAD_GUID);
                    ((GUID) addr).serialize(stream);
                } else {
                    throw new IOException("this address is neither NA or GUID.");
                }
            }
        }
        return stream.toByteArray();
    }

    @Override
    public PrintStream print(PrintStream ps) {
        super.print(ps.printf("LKP["));
        subscriber.print(ps.printf(", subscriber GUID="));
        topicGUID.print(ps.printf(", topic GUID="));
        return Helper.printBuf(ps.printf("MulticastTree["), tree, 0, tree.length).printf("]");
    }

    @Override
    public OutputStream serialize(OutputStream stream) throws IOException {
        super.serialize(stream);
        subscriber.serialize(stream);
        topicGUID.serialize(stream);
//        stream.write((byte) 1);
//        stream.write(MF_GNRS_PACKET_PAYLOAD_NA);
        RP.serialize(stream);
        stream.write(tree);
        return stream;
    }

    public static MFPacketGNRSPayload createMFPacketGNRSPayloadAssoc(byte[] buf, int[] pos) throws IOException {
        GUID subscriber = GUID.create(buf, pos);
        GUID topic = GUID.create(buf, pos);
        NA RP = NA.create(buf, pos);
        HashMap<NA, List<Address>> tree = new HashMap<>();
        while (pos[0] < buf.length) {
            tree.put(NA.create(buf, pos), ByteToBranch(buf, pos));
        }
        return new MFPacketGNRSPayloadAssoc(subscriber, topic, RP, tree);
    }

    private static List<Address> ByteToBranch(byte[] buf, int[] pos) throws IOException {
        int num = buf[pos[0]++];
        List<Address> ret = new ArrayList<>();
        while (num-- > 0) {
            byte type = buf[pos[0]++];
            if (type == MF_GNRS_PACKET_PAYLOAD_NA) {
                ret.add(NA.create(buf, pos));
            } else if (type == MF_GNRS_PACKET_PAYLOAD_GUID) {
                ret.add(GUID.create(buf, pos));
            } else {
                throw new IOException("this address is neither NA or GUID.");
            }
        }
        return ret;
    }
    
    
//    public MFPacketGNRSPayloadAssoc(GUID subscriber, GUID topicGUID, int numofBranch, int totalnumGUID, byte[] numofGUID, byte[] numofNA, List<NA> NAtree, List<GUID> GUIDtree) throws IOException {
//        super(MF_GNRS_PACKET_PAYLOAD_TYPE_ASSOC);
//        this.subscriber = subscriber;
//        this.topicGUID = topicGUID;
//        this.numofbranch = numofBranch;
//        this.totalnumGUID = totalnumGUID;
//        this.numofGUID = numofGUID;
//        this.numofNA = numofNA;
//        this.NAtree = NAListToByte(NAtree);
//        this.GUIDtree = GUIDListToByte(GUIDtree);
//    }
//
//    public static MFPacketGNRSPayload createMFPacketGNRSPayloadAssoc(byte[] buf, int[] pos) {
////        Helper.printBuf(System.out, buf, pos[0], buf.length);
//        GUID subscriber = GUID.create(buf, pos);
//        GUID topic = GUID.create(buf, pos);
//        topic.print(System.out.printf("")).println();
//        int numofBranch = Helper.readInt(buf, pos);
//        int totalnumGUID = Helper.readInt(buf, pos);
//        byte[] numofGUID = new byte[numofBranch];
//        System.arraycopy(buf, pos[0], numofGUID, 0, numofGUID.length);
//        pos[0] += numofBranch;
//        byte[] numofNA = new byte[numofBranch];
//        System.arraycopy(buf, pos[0], numofNA, 0, numofNA.length);
//        pos[0] += numofBranch;
//        System.out.println("buf length: " + buf.length + ", pos[0]: " + pos[0] + ", total: " + totalnumGUID);
//        byte[] NAtree = new byte[buf.length - pos[0] - totalnumGUID * GUID.GUID_LENGTH];
//        System.arraycopy(buf, pos[0], NAtree, 0, NAtree.length);
//        byte[] GUIDtree = new byte[totalnumGUID * GUID.GUID_LENGTH];
//        System.arraycopy(buf, pos[0] + NAtree.length, GUIDtree, 0, GUIDtree.length);
//        return new MFPacketGNRSPayloadAssoc(subscriber, topic, numofBranch, totalnumGUID, numofGUID, numofNA, NAtree, GUIDtree);
//    }
//
//    private byte[] GUIDListToByte(List<GUID> na) throws IOException {
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        for (GUID addr : na) {
//            addr.serialize(stream);
//        }
//        return stream.toByteArray();
//    }
//
//    private byte[] NAListToByte(List<NA> na) throws IOException {
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        for (NA addr : na) {
//            addr.serialize(stream);
//        }
//        return stream.toByteArray();
//    }

    public HashMap<NA, List<Address>> getTree() {
        return treeH;
    }

    public NA getRP() {
        return RP;
    }

    public GUID getSubscriber() {
        return subscriber;
    }

    public GUID getTopicGUID() {
        return topicGUID;
    }
}

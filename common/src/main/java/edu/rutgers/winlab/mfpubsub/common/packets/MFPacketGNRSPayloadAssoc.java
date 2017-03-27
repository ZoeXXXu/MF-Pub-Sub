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

    private final GUID topicGUID;

    private final NA RP;

    //new 
    private final byte add;

    private final short numofsub;

    private final List<GUID> subscriber;

    private final short numofbranches;

    private final transient HashMap<NA, List<Address>> tree;

    public MFPacketGNRSPayloadAssoc(GUID topicGUID, NA RP, byte add, List<GUID> subscriber, HashMap<NA, List<Address>> tree) {
        super(MF_GNRS_PACKET_PAYLOAD_TYPE_ASSOC);
        this.topicGUID = topicGUID;
        this.RP = RP;
        this.add = add;
        this.numofsub = (short) subscriber.size();
        this.subscriber = subscriber;
        this.numofbranches = (short) tree.size();
        this.tree = tree;
    }

    private byte[] ListToByte(HashMap<NA, List<Address>> tree) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        for (Map.Entry<NA, List<Address>> branch : tree.entrySet()) {
            branch.getKey().serialize(stream);
            List<Address> multicast = branch.getValue();
            stream.write(multicast.size());
            for (Address addr : multicast) {
                if (addr instanceof NA) {
                    stream.write(Address.MF_GNRS_PACKET_PAYLOAD_NA);
                    ((NA) addr).serialize(stream);
                } else if (addr instanceof GUID) {
                    stream.write(Address.MF_GNRS_PACKET_PAYLOAD_GUID);
                    ((GUID) addr).serialize(stream);
                } else {
                    throw new IOException("this address is neither NA or GUID.");
                }
            }
        }
        return stream.toByteArray();
    }

    private byte[] ListToByte(List<GUID> multicast) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        for (Address addr : multicast) {
            ((GUID) addr).serialize(stream);
        }
        return stream.toByteArray();
    }

    @Override
    public PrintStream print(PrintStream ps) {
        super.print(ps.printf("LKP["));
        topicGUID.print(ps.printf(", topic GUID="));
        RP.print(ps.printf(", na="));

        return printTree(ps.printf("Tree:"));
    }

    public PrintStream printTree(PrintStream ps) {
        for (Map.Entry<NA, List<Address>> entry : tree.entrySet()) {
            entry.getKey().print(ps).printf(":");
            List<Address> list = entry.getValue();
            for (Address addr : list) {
                addr.print(ps);
            }
            ps.printf("; ");
        }
        return ps;
    }

    @Override
    public OutputStream serialize(OutputStream stream) throws IOException {
        super.serialize(stream);
        topicGUID.serialize(stream);
        RP.serialize(stream);
        stream.write(add);
        Helper.writeShort(stream, numofsub);
        stream.write(ListToByte(subscriber));
        Helper.writeShort(stream, numofbranches);
        stream.write(ListToByte(tree));
        return stream;
    }

    public static MFPacketGNRSPayload createMFPacketGNRSPayloadAssoc(byte[] buf, int[] pos) throws IOException {
        GUID topic = GUID.create(buf, pos);
        NA RP = NA.create(buf, pos);
        byte add = buf[pos[0]++];
        short numofbranch = Helper.readShort(buf, pos);
        ArrayList<GUID> sub = new ArrayList<>();
        
        HashMap<NA, List<Address>> tree = new HashMap<>();
        while (numofbranch-- > 0) {
            tree.put(NA.create(buf, pos), ByteToBranch(buf, pos));
        }
        
    }

    private static List<Address> ByteToBranch(byte[] buf, int[] pos) throws IOException {
        int num = buf[pos[0]++];
        List<Address> ret = new ArrayList<>();
        while (num-- > 0) {
            byte type = buf[pos[0]++];
            switch (type) {
                case Address.MF_GNRS_PACKET_PAYLOAD_NA:
                    ret.add(NA.create(buf, pos));
                    break;
                case Address.MF_GNRS_PACKET_PAYLOAD_GUID:
                    ret.add(GUID.create(buf, pos));
                    break;
                default:
                    throw new IOException("this address is neither NA or GUID.");
            }
        }
        return ret;
    }

    public HashMap<NA, List<Address>> getTree() {
        return tree;
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

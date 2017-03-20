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
import java.util.List;

/**
 *
 * @author zoe
 */
public class MFPacketGNRSPayloadSync extends MFPacketGNRSPayload {

    public static final byte MF_GNRS_PACKET_PAYLOAD_TYPE_SYNC = 2;

    private final transient GUID topicGUID;

//    private final int numofGUID;
//    private final int numofNA;
    private final byte[] multicast;

    private final transient List<Address> multicastL;

    public MFPacketGNRSPayloadSync(GUID topicGUID, List<Address> multicast) throws IOException {
        super(MF_GNRS_PACKET_PAYLOAD_TYPE_SYNC);
        this.topicGUID = topicGUID;
        this.multicastL = multicast;
        this.multicast = ListToByte(topicGUID, multicast);
    }

    private byte[] ListToByte(GUID topic, List<Address> multicast) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        topic.serialize(stream);
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
        return stream.toByteArray();
    }

    public static MFPacketGNRSPayload createMFPacketGNRSPayloadAssociation(byte[] buf, int[] pos) throws IOException {
        GUID topicGUID = GUID.create(buf, pos);
        ArrayList<Address> multicast = new ArrayList<>();
        while (pos[0] < buf.length) {
            multicast.add(ByteToAddress(buf, pos));
        }
        return new MFPacketGNRSPayloadSync(topicGUID, multicast);
    }

    private static Address ByteToAddress(byte[] buf, int[] pos) throws IOException {
        byte type = buf[pos[0]++];
        switch (type) {
            case MF_GNRS_PACKET_PAYLOAD_NA:
                return NA.create(buf, pos);
            case MF_GNRS_PACKET_PAYLOAD_GUID:
                return GUID.create(buf, pos);
            default:
                throw new IOException("this address is neither NA or GUID.");
        }
    }

    @Override
    public PrintStream print(PrintStream ps
    ) {
        super.print(ps.printf("LKP["));
        topicGUID.print(ps.printf(", topic GUID="));
//        return ps.printf("multicast list " + multicastL);
        return Helper.printBuf(ps.printf("multicast["), multicast, GUID.GUID_LENGTH, multicast.length - GUID.GUID_LENGTH).printf("]");
    }

    @Override
    public OutputStream serialize(OutputStream stream) throws IOException {
        super.serialize(stream);
        stream.write(multicast);
        return stream;
    }

//    public MFPacketGNRSPayloadSync(GUID topicGUID, int numofGUID, byte[] GUIDs, byte[] NAs) {
//        super(MF_GNRS_PACKET_PAYLOAD_TYPE_SYNC);
//        this.topicGUID = topicGUID;
//        this.numofGUID = numofGUID;
//        this.GUIDs = GUIDs;
//        this.NAs = NAs;
//    }
//
//    public static MFPacketGNRSPayload createMFGNRSPacketPayloadAssociation(byte[] buf, int[] pos) throws IOException {
//        GUID topicGUID = GUID.create(buf, pos);
//        int numofGUID = Helper.readInt(buf, pos);
//        byte[] guids = new byte[numofGUID * GUID.GUID_LENGTH];
//        System.arraycopy(buf, pos[0], guids, 0, guids.length);
//        byte[] nas = new byte[buf.length - pos[0] - guids.length];
//        System.arraycopy(buf, pos[0] + guids.length, nas, 0, nas.length);
//        return new MFPacketGNRSPayloadSync(topicGUID, numofGUID, guids, nas);
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
//
//    public List<NA> getNAs() throws IOException {
//        ArrayList<NA> ret = new ArrayList<>();
//        int[] pos = new int[]{0};
//        while (pos[0] < NAs.length) {
//            ret.add(NA.create(NAs, pos));
//        }
//        return ret;
//    }
//
//    public GUID getTopicGUID() {
//        return topicGUID;
//    }
//
//    public List<GUID> getGUIDs() {
//        ArrayList<GUID> ret = new ArrayList<>();
//        int[] pos = new int[]{0};
//        while (pos[0] < GUIDs.length) {
//            ret.add(GUID.create(GUIDs, pos));
//        }
//        return ret;
//    }
}

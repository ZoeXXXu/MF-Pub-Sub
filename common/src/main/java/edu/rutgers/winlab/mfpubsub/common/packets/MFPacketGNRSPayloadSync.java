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

    private final GUID topicGUID;

    private final transient List<Address> multicast;

    public MFPacketGNRSPayloadSync(GUID topicGUID, List<Address> multicast) throws IOException {
        super(MF_GNRS_PACKET_PAYLOAD_TYPE_SYNC);
        this.topicGUID = topicGUID;
        this.multicast = multicast;
    }

    private byte[] ListToByte(List<Address> multicast) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
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
            case Address.MF_GNRS_PACKET_PAYLOAD_NA:
                return NA.create(buf, pos);
            case Address.MF_GNRS_PACKET_PAYLOAD_GUID:
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
        return printMulticast(ps.printf("multicast address:"));
    }

    public PrintStream printMulticast(PrintStream ps) {
        for (Address addr : multicast) {
            addr.print(ps);
        }
        return ps;
    }

    @Override
    public OutputStream serialize(OutputStream stream) throws IOException {
        super.serialize(stream);
        topicGUID.serialize(stream);
        stream.write(ListToByte(multicast));
        return stream;
    }

    public GUID getTopicGUID() {
        return topicGUID;
    }

    public List<Address> getMulticast() {
        return multicast;
    }
}

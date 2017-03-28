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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author zoe
 */
public class MFPacketGNRSPayloadAssocTest {

    public MFPacketGNRSPayloadAssocTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getTopicGUID method, of class MFPacketGNRSPayloadAssoc.
     */
    @Test
    public void testGetTopicGUID() throws IOException {
        byte[] guidBuf = new byte[GUID.GUID_LENGTH];
        Helper.getRandomBytes(guidBuf, 0, GUID.GUID_LENGTH);
        GUID guid = new GUID(guidBuf);
        byte[] l1guidBuf = new byte[GUID.GUID_LENGTH];
        Helper.getRandomBytes(l1guidBuf, 0, GUID.GUID_LENGTH);
        GUID l1guid = new GUID(l1guidBuf);
        byte[] l2guidBuf = new byte[GUID.GUID_LENGTH];
        Helper.getRandomBytes(l2guidBuf, 0, GUID.GUID_LENGTH);
        GUID l2guid = new GUID(l2guidBuf);
        guid.print(System.out.printf("guid=")).println();
        l1guid.print(System.out.printf("l1guid=")).println();
        l2guid.print(System.out.printf("l2guid=")).println();
        NA srcNa = new NA(Helper.getRandomInt());
        srcNa.print(System.out.printf("srcNa=")).println();
        NA dstNa = new NA(Helper.getRandomInt());
        dstNa.print(System.out.printf("dstNa=")).println();

        HashMap<NA, List<Address>> tree = new HashMap<>();
        ArrayList<GUID> subs = new ArrayList<>();
        ArrayList<Address> multicast = new ArrayList<>();
        ArrayList<Address> multicast2 = new ArrayList<>();

        multicast2.add(new NA(4));
        multicast2.add(l1guid);
        tree.put(new NA(2), multicast2);

        multicast.add(new NA(2));
        multicast.add(new NA(3));
        tree.put(new NA(1), multicast);

        subs.add(l2guid);
        MFPacketGNRS pkt = new MFPacketGNRS(srcNa, dstNa,
                new MFPacketGNRSPayloadAssoc(guid, new NA(1), MFPacketGNRSPayloadAssoc.MF_GNRS_PACKET_PAYLOAD_TYPE_ASSOC_SUB, subs, tree));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pkt.serialize(baos);
        byte[] pktBuf = baos.toByteArray();
        Helper.printBuf(System.out, pktBuf, 0, pktBuf.length);
        System.out.println();

        int[] pos = {0};
        MFPacket packet = MFPacketFactory.createPacket(pktBuf, pos);
        packet.print(System.out);
        ((MFPacketGNRSPayloadAssoc) ((MFPacketGNRS) packet).getPayload()).printTree(System.out);
//        System.out.println("NA list: " + ((MFPacketGNRSPayloadSync)((MFPacketGNRS)packet).getPayload()).getNAs());
//        System.out.println("GUID list: " + ((MFPacketGNRSPayloadSync)((MFPacketGNRS)packet).getPayload()).getGUIDs());
        System.out.println();

        
    }

}

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
import static org.junit.Assert.*;

/**
 *
 * @author zoe
 */
public class MFPacketGNRSPayloadSyncTest {

    public MFPacketGNRSPayloadSyncTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of createMFPacketGNRSPayloadAssociation method, of class
     * MFPacketGNRSPayloadSync.
     */
    @Test
    public void test() throws IOException {
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
        ArrayList<Address> multicast = new ArrayList<>();

        multicast.add(new NA(1));
        multicast.add(new NA(2));
        multicast.add(new NA(3));
        multicast.add(new NA(2));
        multicast.add(new NA(4));
        multicast.add(new NA(3));
        multicast.add(new NA(5));
        multicast.add(new NA(4));
        multicast.add(new NA(5));
        multicast.add(l1guid);
        multicast.add(l2guid);

//        MFPacketGNRS pkt = new MFPacketGNRS(srcNa, dstNa, new MFPacketGNRSPayloadQuery(guid));
//        MFPacketGNRS pkt = new MFPacketGNRS(srcNa, dstNa, new MFPacketGNRSPayloadResponse(new NA(4)));
        MFPacketGNRS pkt = new MFPacketGNRS(srcNa, dstNa, new MFPacketGNRSPayloadSync(guid, multicast));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pkt.serialize(baos);
        byte[] pktBuf = baos.toByteArray();
        Helper.printBuf(System.out, pktBuf, 0, pktBuf.length);
        System.out.println();

        int[] pos = {0};
        MFPacket packet = MFPacketFactory.createPacket(pktBuf, pos);
        packet.print(System.out);
        System.out.println();
    }

}

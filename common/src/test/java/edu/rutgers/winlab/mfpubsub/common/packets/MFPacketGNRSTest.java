/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.common.packets;

import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import edu.rutgers.winlab.mfpubsub.common.Helper;
import edu.rutgers.winlab.mfpubsub.common.structure.Address;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ubuntu
 */
public class MFPacketGNRSTest {

    public MFPacketGNRSTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void test1() throws Exception {
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
        ArrayList<Address> multicast2 = new ArrayList<>();
//        ArrayList<GUID> GUIDs = new ArrayList<>();

        multicast2.add(new NA(4));
        multicast2.add(l1guid);
        tree.put(new NA(2), multicast2);

        multicast.add(new NA(2));
        multicast.add(new NA(3));
        tree.put(new NA(1), multicast);

        print(tree);

//        MFPacketGNRS pkt = new MFPacketGNRS(srcNa, dstNa, new MFPacketGNRSPayloadQuery(guid));
//        MFPacketGNRS pkt = new MFPacketGNRS(srcNa, dstNa, new MFPacketGNRSPayloadResponse(new NA(4)));
//        MFPacketGNRS pkt = new MFPacketGNRS(srcNa, dstNa, new MFPacketGNRSPayloadSync(guid, multicast));
//        byte[] numofGUID = new byte[]{0, 0, 0, 1, 100};
//        byte[] numofNA = new byte[]{3, 2, 2, 1, 1};
//        Helper.printBuf(System.out.printf("numofGUID"), numofGUID, 0, numofGUID.length);
//        multicast.add(new NA(1));
//        multicast.add(new NA(2));
//        multicast.add(new NA(3));
//        multicast.add(new NA(2));
//        multicast.add(new NA(4));
//        multicast.add(new NA(3));
//        multicast.add(new NA(5));
//        multicast.add(new NA(4));
//        multicast.add(new NA(5));
//        GUIDs.add(l1guid);
//        GUIDs.add(l2guid);
        MFPacketGNRS pkt = new MFPacketGNRS(srcNa, dstNa,
                new MFPacketGNRSPayloadAssoc(guid, guid, new NA(1), tree));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pkt.serialize(baos);
        byte[] pktBuf = baos.toByteArray();
        Helper.printBuf(System.out, pktBuf, 0, pktBuf.length);
        System.out.println();

        int[] pos = {0};
        MFPacket packet = MFPacketFactory.createPacket(pktBuf, pos);
        packet.print(System.out);
        print(((MFPacketGNRSPayloadAssoc) ((MFPacketGNRS) packet).getPayload()).getTree());
//        System.out.println("NA list: " + ((MFPacketGNRSPayloadSync)((MFPacketGNRS)packet).getPayload()).getNAs());
//        System.out.println("GUID list: " + ((MFPacketGNRSPayloadSync)((MFPacketGNRS)packet).getPayload()).getGUIDs());
        System.out.println();

    }

    private void print(HashMap<NA, List<Address>> tree) {
        for (Map.Entry<NA, List<Address>> branch : tree.entrySet()) {
            branch.getKey().print(System.out);
            List<Address> multicast = branch.getValue();
            for (Address addr : multicast) {
                if (addr instanceof NA) {
                    ((NA) addr).print(System.out);
                } else if (addr instanceof GUID) {
                    ((GUID) addr).print(System.out);
                }
            }
            System.out.println();
        }
    }

}

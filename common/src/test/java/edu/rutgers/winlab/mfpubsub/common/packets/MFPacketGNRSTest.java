/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.common.packets;

import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import edu.rutgers.winlab.mfpubsub.common.Helper;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
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
        NA srcNa = new NA(Helper.getRandomInt());
        srcNa.print(System.out.printf("srcNa=")).println();
        NA dstNa = new NA(Helper.getRandomInt());
        dstNa.print(System.out.printf("dstNa=")).println();

        ArrayList<NA> NAs = new ArrayList<>();
        ArrayList<GUID> GUIDs = new ArrayList<>();

        NAs.add(new NA(1));
        NAs.add(new NA(2));
        NAs.add(new NA(3));
        NAs.add(new NA(4));
        NAs.add(new NA(5));

        GUIDs.add(l1guid);
        GUIDs.add(l2guid);
//        MFPacketGNRS pkt = new MFPacketGNRS(srcNa, dstNa, new MFPacketGNRSPayloadQuery(guid));
//        MFPacketGNRS pkt = new MFPacketGNRS(srcNa, dstNa, new MFPacketGNRSPayloadResponse(new NA(4)));
        System.out.println("numofGUID " + GUIDs.size());
        MFPacketGNRS pkt = new MFPacketGNRS(srcNa, dstNa, new MFPacketGNRSPayloadAssociation(guid, GUIDs.size(), GUIDs, NAs));

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

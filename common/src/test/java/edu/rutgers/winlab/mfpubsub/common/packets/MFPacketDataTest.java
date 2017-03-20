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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ubuntu
 */
public class MFPacketDataTest {

    public MFPacketDataTest() {
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

    /**
     * Test of print method, of class DataPacket.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void test1() throws Exception {
        byte[] srcGUIDBuf = new byte[GUID.GUID_LENGTH];
        Helper.getRandomBytes(srcGUIDBuf, 0, GUID.GUID_LENGTH);
        GUID srcGUID = new GUID(srcGUIDBuf);
        Helper.printBuf(System.out, srcGUIDBuf, 0, GUID.GUID_LENGTH);
        System.out.println();

        srcGUID.print(System.out);
        System.out.println();
    }

    /**
     * Test of print method, of class DataPacket.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void test2() throws Exception {
        byte[] srcGUIDBuf = new byte[GUID.GUID_LENGTH];
        Helper.getRandomBytes(srcGUIDBuf, 0, GUID.GUID_LENGTH);
        GUID srcGUID = new GUID(srcGUIDBuf);
        srcGUID.print(System.out.printf("srcGUID: ")).println();

        byte[] dstGUIDBuf = new byte[GUID.GUID_LENGTH];
        Helper.getRandomBytes(dstGUIDBuf, 0, GUID.GUID_LENGTH);
        GUID dstGUID = new GUID(dstGUIDBuf);
        dstGUID.print(System.out.printf("dstGUID: ")).println();

        NA na = new NA(Helper.getRandomInt());
        na.print(System.out.printf("na: ")).println();

        byte[] payloadBuf = new byte[35];
        Helper.getRandomBytes(payloadBuf, 0, payloadBuf.length);
        MFPacketDataPayloadRandom payload = new MFPacketDataPayloadRandom(payloadBuf);
        payload.print(System.out.printf("payload: ")).println();

        MFPacketData dp = new MFPacketDataPublish(srcGUID, dstGUID, na, payload);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dp.serialize(baos);
        byte[] packetBuf = baos.toByteArray();
        Helper.printBuf(System.out.printf("packet: "), packetBuf, 0, packetBuf.length).println();

        int[] pos = {0};

        MFPacket p = MFPacketFactory.createPacket(packetBuf, pos);
        p.print(System.out.printf("from factory: ")).println();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.common.packets;

import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import edu.rutgers.winlab.mfpubsub.common.FIFOEntry;
import java.io.IOException;
import java.util.concurrent.PriorityBlockingQueue;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ubuntu
 */
public class MFPacketTest {

    public MFPacketTest() {
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
     * Test of getType method, of class MFPacket.
     *
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void test1() throws IOException, InterruptedException {
        GUID srcGUID = new GUID(new byte[GUID.GUID_LENGTH]);
        GUID dstGUID = new GUID(new byte[GUID.GUID_LENGTH]);

        PriorityBlockingQueue<FIFOEntry<MFPacket>> packets1 = new PriorityBlockingQueue<>();
        PriorityBlockingQueue<MFPacket> packets2 = new PriorityBlockingQueue<>();

        for (int i = 0; i < 10; i++) {
            byte[] buf = new byte[]{(byte) i};
            MFPacketDataPayloadRandom pay = new MFPacketDataPayloadRandom(buf);
            MFPacketData data = new MFPacketData(srcGUID, dstGUID, new NA(i), pay);
            packets1.add(new FIFOEntry<MFPacket>(data));
            packets2.add(data);
        }
        for (int i = 10; i < 15; i++) {
            MFPacketGNRS gnrs = new MFPacketGNRS(new NA(i + 1), new NA(i), new MFPacketGNRSPayloadQuery(dstGUID));
            packets1.add(new FIFOEntry<MFPacket>(gnrs));
            packets2.add(gnrs);
        }

        System.out.println("Packets1");
        while (!packets1.isEmpty()) {
            packets1.take().getEntry().print(System.out).println();
        }
        System.out.println("Packets2");
        while (!packets2.isEmpty()) {
            packets2.take().print(System.out).println();
        }

    }

}

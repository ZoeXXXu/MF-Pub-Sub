/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.common.packets;

import edu.rutgers.winlab.mfpubsub.common.Helper;
import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author zoe
 */
public class MFPacketNetworkRenewTest {

    public MFPacketNetworkRenewTest() throws IOException {
    }

    /**
     * Test of serialize method, of class MFPacketNetworkRenew.
     */
    @Test
    public void testSerialize() throws Exception {
    }

    /**
     * Test of print method, of class MFPacketNetworkRenew.
     * @throws java.io.IOException
     */
    @Test
    public void test() throws IOException {
        byte[] guidBuf = new byte[GUID.GUID_LENGTH];
        Helper.getRandomBytes(guidBuf, 0, GUID.GUID_LENGTH);
        GUID renewedGUID = new GUID(guidBuf);
        MFPacketNetworkRenew pkt = new MFPacketNetworkRenew(renewedGUID, new NA(7), new NA(4));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pkt.serialize(baos);
        byte[] pktBuf = baos.toByteArray();
        Helper.printBuf(System.out.printf("stream: "), pktBuf, 0, pktBuf.length);
        System.out.println();

        int[] pos = {0};
        MFPacket packet = MFPacketFactory.createPacket(pktBuf, pos);
        packet.print(System.out);
        System.out.println();
    }

}

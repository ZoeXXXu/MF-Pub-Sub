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
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author zoe
 */
public class MFPacketDataPayloadSubTest {

    public MFPacketDataPayloadSubTest() {
    }

    @Test
    public void testSomeMethod() throws IOException {
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

        MFPacketData pkt = new MFPacketData(l2guid, l2guid, dstNa, MFPacketDataPayloadSub.MF_PACKET_DATA_SID_SUBSCRIPTION, l1guid);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pkt.serialize(baos);
        byte[] pktBuf = baos.toByteArray();
        Helper.printBuf(System.out, pktBuf, 0, pktBuf.length);
        System.out.println();

        int[] pos = {0};
        MFPacket packet = MFPacketFactory.createPacket(pktBuf, pos);
        packet.print(System.out);
        
        System.out.printf("sid:%02x", MFPacketDataPayloadSub.MF_PACKET_DATA_SID_SUBSCRIPTION);
    }

}

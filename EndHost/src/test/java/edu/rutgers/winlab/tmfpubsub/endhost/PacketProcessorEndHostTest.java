/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.tmfpubsub.endhost;

import edu.rutgers.winlab.mfpubsub.common.elements.NetworkInterface;
import edu.rutgers.winlab.mfpubsub.common.elements.NetworkInterfaceUDP;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketDataPayloadRandom;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketDataPublish;
import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author zoe
 */
public class PacketProcessorEndHostTest {

    public PacketProcessorEndHostTest() {
    }

    @Test
    public void test() throws IOException, InterruptedException {
        NA na1 = new NA(1);
        NA na2 = new NA(2);
        NA na3 = new NA(3);
        NA na4 = new NA(4);
        NA na5 = new NA(5);
        NA na6 = new NA(6);

        NA na7 = new NA(7);
        NA na8 = new NA(8);
        NA na9 = new NA(9);

        SocketAddress l11 = new InetSocketAddress("127.0.0.1", 10010);
        SocketAddress l12 = new InetSocketAddress("127.0.0.1", 10011);

        SocketAddress l13 = new InetSocketAddress("127.0.0.1", 10012);
        SocketAddress l14 = new InetSocketAddress("127.0.0.1", 10013);

        SocketAddress lp1 = new InetSocketAddress("127.0.0.1", 10014);
        SocketAddress lp2 = new InetSocketAddress("127.0.0.1", 10015);

        byte[] dstGuidBuf = new byte[GUID.GUID_LENGTH];
        dstGuidBuf[GUID.GUID_LENGTH - 1] = 0x2;
        GUID topicGuid = new GUID(dstGuidBuf);

        byte[] user1GuidBuf = new byte[GUID.GUID_LENGTH];
        user1GuidBuf[GUID.GUID_LENGTH - 1] = 0x3;
        GUID user1Guid = new GUID(user1GuidBuf);
        byte[] user2GuidBuf = new byte[GUID.GUID_LENGTH];
        user2GuidBuf[GUID.GUID_LENGTH - 1] = 0x4;
        GUID user2Guid = new GUID(user2GuidBuf);
        byte[] pubGuidBuf = new byte[GUID.GUID_LENGTH];
        pubGuidBuf[GUID.GUID_LENGTH - 1] = 0x5;
        GUID pubGuid = new GUID(pubGuidBuf);

        byte[] payloadBuf = new byte[30];
        for (int i = 0; i < payloadBuf.length; i++) {
            payloadBuf[i] = (byte) (i & 0xFF);
        }

        //publisher 
        HashMap<NA, NetworkInterface> publisherNeighbor = new HashMap<>();
        publisherNeighbor.put(na1, new NetworkInterfaceUDP(lp1, lp2));
        PacketProcessorEndHost pub = new PacketProcessorEndHost(pubGuid, na9, publisherNeighbor);
        pub.print(System.out.printf("pub:")).println();
        pub.printNeighbors(System.out.printf("===Neighbors===%n")).printf("==ENDNeighbors===%n");
        pub.start();

        HashMap<NA, NetworkInterface> connectedRouter1 = new HashMap<>();
        connectedRouter1.put(na6, new NetworkInterfaceUDP(l12, l11));
        PacketProcessorEndHost sub1 = new PacketProcessorEndHost(user1Guid, na7, connectedRouter1);
        sub1.print(System.out.printf("user 1:")).println();
        sub1.printNeighbors(System.out.printf("===Neighbors===%n")).printf("==ENDNeighbors===%n");
        sub1.start();

        HashMap<NA, NetworkInterface> connectedRouter2 = new HashMap<>();
        connectedRouter2.put(na6, new NetworkInterfaceUDP(l14, l13));
        PacketProcessorEndHost sub2 = new PacketProcessorEndHost(user2Guid, na8, connectedRouter2);
        sub2.print(System.out.printf("user 2:")).println();
        sub2.printNeighbors(System.out.printf("===Neighbors===%n")).printf("==ENDNeighbors===%n");
        sub2.start();

        MFPacketDataPublish data = new MFPacketDataPublish(pubGuid, topicGuid, new NA(0), new MFPacketDataPayloadRandom(payloadBuf));
//        n1.send(na2, data);
        pub.send(na1, data);

        Thread.sleep(10000);
        System.out.println("Stopping...");
        pub.stop();
        sub1.stop();
        sub2.stop();
    }

}

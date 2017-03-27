/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.router;

import edu.rutgers.winlab.mfpubsub.common.elements.NetworkInterface;
import edu.rutgers.winlab.mfpubsub.common.elements.NetworkInterfaceUDP;
import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketData;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketDataPayloadRandom;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketDataPublish;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author zoe
 */
public class RouterTest {

    public RouterTest() {
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
     * Test of getProcessor method, of class Router.
     *
     */
    @Test
    public void testGetProcessor() throws SocketException, IOException, InterruptedException {
        NA na1 = new NA(1);
        NA na2 = new NA(2);
        NA na3 = new NA(3);
        NA na4 = new NA(4);
        NA na5 = new NA(5);
        NA na6 = new NA(6);

        NA na7 = new NA(7);
        NA na8 = new NA(8);
        NA na9 = new NA(9);

        SocketAddress l1 = new InetSocketAddress("127.0.0.1", 10000);
        SocketAddress l2 = new InetSocketAddress("127.0.0.1", 10002);
        SocketAddress l3 = new InetSocketAddress("127.0.0.1", 10001);
        SocketAddress l4 = new InetSocketAddress("127.0.0.1", 10003);
        SocketAddress l5 = new InetSocketAddress("127.0.0.1", 10004);
        SocketAddress l6 = new InetSocketAddress("127.0.0.1", 10005);
        SocketAddress l7 = new InetSocketAddress("127.0.0.1", 10006);
        SocketAddress l8 = new InetSocketAddress("127.0.0.1", 10007);
        SocketAddress l9 = new InetSocketAddress("127.0.0.1", 10008);
        SocketAddress l10 = new InetSocketAddress("127.0.0.1", 10009);

        SocketAddress l11 = new InetSocketAddress("127.0.0.1", 10010);
        SocketAddress l12 = new InetSocketAddress("127.0.0.1", 10011);

        SocketAddress l13 = new InetSocketAddress("127.0.0.1", 10012);
        SocketAddress l14 = new InetSocketAddress("127.0.0.1", 10013);

        SocketAddress lp1 = new InetSocketAddress("127.0.0.1", 10014);
        SocketAddress lp2 = new InetSocketAddress("127.0.0.1", 10015);

//        byte[] srcGuidBuf = new byte[GUID.GUID_LENGTH];
//        srcGuidBuf[GUID.GUID_LENGTH - 1] = 0x1;
//        GUID srcGuid = new GUID(srcGuidBuf);
        byte[] dstGuidBuf = new byte[GUID.GUID_LENGTH];
        dstGuidBuf[GUID.GUID_LENGTH - 1] = 0x2;
        GUID dstGuid = new GUID(dstGuidBuf);
        byte[] GnrsGuidBuf = new byte[GUID.GUID_LENGTH];
        GnrsGuidBuf[GUID.GUID_LENGTH - 1] = (byte) 0xffff;
        GUID gnrs = new GUID(GnrsGuidBuf);

        byte[] user1GuidBuf = new byte[GUID.GUID_LENGTH];
        user1GuidBuf[GUID.GUID_LENGTH - 1] = 0x3;
        GUID user1Guid = new GUID(user1GuidBuf);
        byte[] user2GuidBuf = new byte[GUID.GUID_LENGTH];
        user2GuidBuf[GUID.GUID_LENGTH - 1] = 0x4;
        GUID user2Guid = new GUID(user2GuidBuf);

        byte[] payloadBuf = new byte[30];
        for (int i = 0; i < payloadBuf.length; i++) {
            payloadBuf[i] = (byte) (i & 0xFF);
        }

        //router 1
        HashMap<NA, NetworkInterface> neighbor1 = new HashMap<>();
        HashMap<NA, NA> routingt1 = new HashMap<>();
        neighbor1.put(na2, new NetworkInterfaceUDP(l1, l2));
        neighbor1.put(na9, new NetworkInterfaceUDP(lp2, lp1));
        routingt1.put(na2, na2);
        routingt1.put(na4, na2);
        routingt1.put(na4, na2);
        routingt1.put(na5, na2);
        routingt1.put(na6, na2);
//        multi1.addBranch(dstGuid, na4);
        PacketProcessorRouter n1 = new PacketProcessorRouter(gnrs, new HashMap<GUID, NA>(), routingt1, na1, neighbor1);
        n1.MTadd(dstGuid, na4);
        n1.print(System.out.printf("n1:")).println();
        n1.printNeighbors(System.out.printf("===Neighbors===%n")).printf("==ENDNeighbors===%n");
        n1.printRoutingTable(System.out.printf("===Routing===%n")).printf("==ENDInterface===%n");
        n1.start();

        //router 2
        HashMap<NA, NetworkInterface> neighbor2 = new HashMap<>();
        HashMap<NA, NA> routingt2 = new HashMap<>();
        neighbor2.put(na1, new NetworkInterfaceUDP(l2, l1));
        neighbor2.put(na3, new NetworkInterfaceUDP(l3, l4));
        routingt2.put(na1, na1);
        routingt2.put(na3, na3);
        routingt2.put(na4, na3);
        routingt2.put(na5, na3);
        routingt2.put(na6, na3);
//        multi2.addBranch(dstGuid, na4);
        PacketProcessorRouter n2 = new PacketProcessorRouter(gnrs, new HashMap<GUID, NA>(), routingt2, na2, neighbor2);
        n2.print(System.out.printf("n2:")).println();
        n2.printNeighbors(System.out.printf("===Neighbors===%n")).printf("==ENDNeighbors===%n");
        n2.printRoutingTable(System.out.printf("===Routing===%n")).printf("==ENDInterface===%n");
        n2.start();

        //router 3
        HashMap<NA, NetworkInterface> neighbor3 = new HashMap<>();
        HashMap<NA, NA> routingt3 = new HashMap<>();
        neighbor3.put(na2, new NetworkInterfaceUDP(l4, l3));
        neighbor3.put(na4, new NetworkInterfaceUDP(l5, l6));
        routingt3.put(na2, na2);
        routingt3.put(na1, na2);
        routingt3.put(na4, na4);
        routingt3.put(na5, na4);
        routingt3.put(na6, na4);
        PacketProcessorRouter n3 = new PacketProcessorRouter(gnrs, new HashMap<GUID, NA>(), routingt3, na3, neighbor3);
        n3.print(System.out.printf("n3:")).println();
        n3.printNeighbors(System.out.printf("===Neighbors===%n")).printf("==ENDNeighbors===%n");
        n3.printRoutingTable(System.out.printf("===Routing===%n")).printf("==ENDInterface===%n");
        n3.start();

        //router 4
        HashMap<NA, NetworkInterface> neighbor4 = new HashMap<>();
        HashMap<NA, NA> routingt4 = new HashMap<>();
        neighbor4.put(na3, new NetworkInterfaceUDP(l6, l5));
        neighbor4.put(na5, new NetworkInterfaceUDP(l7, l8));
        neighbor4.put(na6, new NetworkInterfaceUDP(l9, l10));
        routingt4.put(na2, na3);
        routingt4.put(na1, na3);
        routingt4.put(na3, na3);
        routingt4.put(na5, na5);
        routingt4.put(na6, na6);
//        multi4.addBranch(dstGuid, na6);
        PacketProcessorRouter n4 = new PacketProcessorRouter(gnrs, new HashMap<GUID, NA>(), routingt4, na4, neighbor4);
        n4.MTadd(dstGuid, na6);
        n4.print(System.out.printf("n4:")).println();
        n4.printNeighbors(System.out.printf("===Neighbors===%n")).printf("==ENDNeighbors===%n");
        n4.printRoutingTable(System.out.printf("===Routing===%n")).printf("==ENDInterface===%n");
//        n4.printMulticastTable(System.out.printf("===Multicast===%n")).printf("==ENDInterface===%n");
        n4.start();

        //router 5
        HashMap<NA, NetworkInterface> neighbor5 = new HashMap<>();
        HashMap<NA, NA> routingt5 = new HashMap<>();
        neighbor5.put(na4, new NetworkInterfaceUDP(l8, l7));
        routingt5.put(na2, na4);
        routingt5.put(na1, na4);
        routingt5.put(na3, na4);
        routingt5.put(na4, na4);
        routingt5.put(na6, na4);
        PacketProcessorRouter n5 = new PacketProcessorRouter(gnrs, new HashMap<GUID, NA>(), routingt5, na5, neighbor5);
        n5.print(System.out.printf("n5:")).println();
        n5.printNeighbors(System.out.printf("===Neighbors===%n")).printf("==ENDNeighbors===%n");
        n5.printRoutingTable(System.out.printf("===Routing===%n")).printf("==ENDInterface===%n");
        n5.start();

        //router 6
        HashMap<NA, NetworkInterface> neighbor6 = new HashMap<>();
        HashMap<NA, NA> routingt6 = new HashMap<>();
        HashMap<GUID, NA> localGT6 = new HashMap<>();
        neighbor6.put(na4, new NetworkInterfaceUDP(l10, l9));
        neighbor6.put(na7, new NetworkInterfaceUDP(l11, l12));
        neighbor6.put(na8, new NetworkInterfaceUDP(l13, l14));
        routingt6.put(na2, na4);
        routingt6.put(na1, na4);
        routingt6.put(na3, na4);
        routingt6.put(na4, na4);
        routingt6.put(na5, na4);
        localGT6.put(user1Guid, na7);
        localGT6.put(user2Guid, na8);
//        multi6.addBranch(dstGuid, user1Guid);
//        multi6.addBranch(dstGuid, user2Guid);
        PacketProcessorRouter n6 = new PacketProcessorRouter(gnrs, localGT6, routingt6, na6, neighbor6);
        n6.MTadd(dstGuid, user1Guid);
        n6.MTadd(dstGuid, user2Guid);
        n6.print(System.out.printf("n6:")).println();
        n6.printNeighbors(System.out.printf("===Neighbors===%n")).printf("==ENDNeighbors===%n");
        n6.printRoutingTable(System.out.printf("===Routing===%n")).printf("==ENDInterface===%n");
        n6.printMulticastTable(System.out.printf("===Multicast===%n")).printf("==ENDInterface===%n");
//        multi6.print(System.out.printf("===Multicast created===%n")).printf("==ENDInterface===%n");
        n6.start();

        Thread.sleep(10000);
        System.out.println("Stopping...");
        n1.stop();
        n2.stop();
        n3.stop();
        n4.stop();
        n5.stop();
        n6.stop();
//        new TopologyReader().testGetProcessor();
    }

}

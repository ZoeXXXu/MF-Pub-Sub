/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.router;

import edu.rutgers.winlab.mfpubsub.common.elements.NetworkInterface;
import edu.rutgers.winlab.mfpubsub.common.elements.NetworkInterfaceUDP;
import edu.rutgers.winlab.mfpubsub.common.elements.PacketProcessor;
import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import org.junit.Test;

/**
 *
 * @author zoe
 */
public class RouterTest {

    NA na1 = new NA(1);
    NA na2 = new NA(2);
    NA na3 = new NA(3);
    NA na4 = new NA(4);
    NA na5 = new NA(5);
    NA na6 = new NA(6);
    NA na7 = new NA(7);
    NA na8 = new NA(8);
    NA na9 = new NA(9);

    public RouterTest() {
    }

    /**
     * Test of getProcessor method, of class Router.
     *
     * @throws java.net.SocketException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testGetProcessor() throws SocketException, IOException, InterruptedException {
        //gnrs
        NA gnrsNA = new NA(Integer.MAX_VALUE);
        byte[] gnrsGuidBuf = new byte[GUID.GUID_LENGTH];
        gnrsGuidBuf[GUID.GUID_LENGTH - 1] = (byte) 0xff;
        GUID gnrsGuid = new GUID(gnrsGuidBuf);
        //pubsub node
        NA pubsubNA = new NA(Integer.MAX_VALUE - 1);
        byte[] pubsubGuidBuf = new byte[GUID.GUID_LENGTH];
        pubsubGuidBuf[GUID.GUID_LENGTH - 1] = (byte) 0xfe;
        GUID pubsubGuid = new GUID(pubsubGuidBuf);

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
        SocketAddress lg3 = new InetSocketAddress("127.0.0.1", 10016);
        SocketAddress l3g = new InetSocketAddress("127.0.0.1", 10017);
        SocketAddress lp3 = new InetSocketAddress("127.0.0.1", 10018);
        SocketAddress l3p = new InetSocketAddress("127.0.0.1", 10019);

//        byte[] srcGuidBuf = new byte[GUID.GUID_LENGTH];
//        srcGuidBuf[GUID.GUID_LENGTH - 1] = 0x1;
//        GUID srcGuid = new GUID(srcGuidBuf);
        byte[] dstGuidBuf = new byte[GUID.GUID_LENGTH];
        dstGuidBuf[GUID.GUID_LENGTH - 1] = 0x2;
        GUID topicGuid = new GUID(dstGuidBuf);
//        byte[] GnrsGuidBuf = new byte[GUID.GUID_LENGTH];
//        GnrsGuidBuf[GUID.GUID_LENGTH - 1] = (byte) 0xffff;
//        GUID gnrs = new GUID(GnrsGuidBuf);

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

        //router 1
        HashMap<NA, NetworkInterface> neighbor1 = new HashMap<>();
        HashMap<NA, NA> routingt1 = new HashMap<>();
        neighbor1.put(na2, new NetworkInterfaceUDP(l1, l2));
        neighbor1.put(na9, new NetworkInterfaceUDP(lp2, lp1));
        routingt1.put(na2, na2);
        routingt1.put(na3, na2);
        routingt1.put(na4, na2);
        routingt1.put(na5, na2);
        routingt1.put(na6, na2);
        routingt1.put(pubsubNA, na2);
        routingt1.put(gnrsNA, na2);
        PacketProcessorRouter n1 = new PacketProcessorRouter(gnrsNA, new HashMap<GUID, NA>(), routingt1, na1, neighbor1);
//        n1.MTadd(topicGuid, na4);
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
        routingt2.put(gnrsNA, na3);
        routingt2.put(pubsubNA, na3);
//        multi2.addBranch(dstGuid, na4);
        PacketProcessor n2 = new PacketProcessorRouter(gnrsNA, new HashMap<GUID, NA>(), routingt2, na2, neighbor2);
        n2.print(System.out.printf("n2:")).println();
        n2.printNeighbors(System.out.printf("===Neighbors===%n")).printf("==ENDNeighbors===%n");
        ((PacketProcessorRouter) n2).printRoutingTable(System.out.printf("===Routing===%n")).printf("==ENDInterface===%n");
        n2.start();

        //router 3
        HashMap<NA, NetworkInterface> neighbor3 = new HashMap<>();
        HashMap<NA, NA> routingt3 = new HashMap<>();
        neighbor3.put(na2, new NetworkInterfaceUDP(l4, l3));
        neighbor3.put(na4, new NetworkInterfaceUDP(l5, l6));
        neighbor3.put(gnrsNA, new NetworkInterfaceUDP(l3g, lg3));
        neighbor3.put(pubsubNA, new NetworkInterfaceUDP(l3p, lp3));
        routingt3.put(na2, na2);
        routingt3.put(na1, na2);
        routingt3.put(na4, na4);
        routingt3.put(na5, na4);
        routingt3.put(na6, na4);
        routingt3.put(gnrsNA, gnrsNA);
        routingt3.put(pubsubNA, pubsubNA);
        PacketProcessorRouter n3 = new PacketProcessorRouter(gnrsNA, new HashMap<GUID, NA>(), routingt3, na3, neighbor3);
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
        routingt4.put(gnrsNA, na3);
        routingt4.put(pubsubNA, na3);
        PacketProcessorRouter n4 = new PacketProcessorRouter(gnrsNA, new HashMap<GUID, NA>(), routingt4, na4, neighbor4);
//        n4.MTadd(topicGuid, na6);
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
        routingt5.put(gnrsNA, na4);
        routingt5.put(pubsubNA, na4);
        PacketProcessorRouter n5 = new PacketProcessorRouter(gnrsNA, new HashMap<GUID, NA>(), routingt5, na5, neighbor5);
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
        routingt6.put(gnrsNA, na4);
        routingt6.put(pubsubNA, na4);
        localGT6.put(user1Guid, na7);
        localGT6.put(user2Guid, na8);
        PacketProcessorRouter n6 = new PacketProcessorRouter(gnrsNA, localGT6, routingt6, na6, neighbor6);
//        n6.MTadd(topicGuid, user1Guid);
//        n6.MTadd(topicGuid, user2Guid);
        n6.print(System.out.printf("n6:")).println();
        n6.printNeighbors(System.out.printf("===Neighbors===%n")).printf("==ENDNeighbors===%n");
        n6.printRoutingTable(System.out.printf("===Routing===%n")).printf("==ENDInterface===%n");
        n6.printMulticastTable(System.out.printf("===Multicast===%n")).printf("==ENDInterface===%n");
        n6.start();
/////////////////////////////////////////////////////////////////////
//        HashMap<NA, NetworkInterface> GNRSneighbor = new HashMap<>();
//        GNRSneighbor.put(new NA(3), new NetworkInterfaceUDP(lg3, l3g));
//
//        HashMap<GUID, NA> addrT = new HashMap<>();
//        HashMap<GUID, ArrayList<GUID>> graphT = new HashMap<>();
//        ArrayList<GUID> subs = new ArrayList<>();
//
//        addrT.put(pubGuid, new NA(9));
//        addrT.put(user1Guid, new NA(7));
//        addrT.put(user2Guid, new NA(8));
//        addrT.put(topicGuid, new NA(4));
//
//        subs.add(user1Guid);
//        subs.add(user2Guid);
//
//        graphT.put(topicGuid, subs);
//
//        PacketProcessorGNRS gnrs = new PacketProcessorGNRS(addrT, graphT, gnrsNA, GNRSneighbor);
//        gnrs.print(System.out.printf("GNRS:")).println();
//        gnrs.printNeighbors(System.out.printf("===Neighbors===%n")).printf("==ENDNeighbors===%n");
//        gnrs.start();
/////////////////////////////////////////////////////////////
////publisher 
//        HashMap<NA, NetworkInterface> publisherNeighbor = new HashMap<>();
//        publisherNeighbor.put(na1, new NetworkInterfaceUDP(lp1, lp2));
//        PacketProcessorEndHost pub = new PacketProcessorEndHost(pubGuid, na9, publisherNeighbor);
//        pub.print(System.out.printf("pub:")).println();
//        pub.printNeighbors(System.out.printf("===Neighbors===%n")).printf("==ENDNeighbors===%n");
//        pub.start();
//
//        HashMap<NA, NetworkInterface> connectedRouter1 = new HashMap<>();
//        connectedRouter1.put(na6, new NetworkInterfaceUDP(l12, l11));
//        PacketProcessorEndHost sub1 = new PacketProcessorEndHost(user1Guid, na7, connectedRouter1);
//        sub1.print(System.out.printf("user 1:")).println();
//        sub1.printNeighbors(System.out.printf("===Neighbors===%n")).printf("==ENDNeighbors===%n");
//        sub1.start();
//
//        HashMap<NA, NetworkInterface> connectedRouter2 = new HashMap<>();
//        connectedRouter2.put(na6, new NetworkInterfaceUDP(l14, l13));
//        PacketProcessorEndHost sub2 = new PacketProcessorEndHost(user2Guid, na8, connectedRouter2);
//        sub2.print(System.out.printf("user 2:")).println();
//        sub2.printNeighbors(System.out.printf("===Neighbors===%n")).printf("==ENDNeighbors===%n");
//        sub2.start();
//
//        MFPacketDataPublish data = new MFPacketDataPublish(pubGuid, topicGuid, new NA(0), new MFPacketDataPayloadRandom(payloadBuf));
//        pub.send(na1, data);

        Thread.sleep(30000);
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

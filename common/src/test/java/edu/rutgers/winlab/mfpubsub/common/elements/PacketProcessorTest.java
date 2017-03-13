/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.common.elements;

import edu.rutgers.winlab.mfpubsub.common.Helper;
import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacket;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketGNRS;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketGNRSPayloadQuery;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ubuntu
 */
public class PacketProcessorTest {

    public PacketProcessorTest() {
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
     * Test of printNeighbors method, of class PacketProcessor.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void test1() throws Exception {
        NA na1 = new NA(1);
        NA na2 = new NA(2);
        NA na3 = new NA(3);

        SocketAddress l12_1 = new InetSocketAddress("127.0.0.1", 10000);
        SocketAddress l12_2 = new InetSocketAddress("127.0.0.1", 10002);

        SocketAddress l13_1 = new InetSocketAddress("127.0.0.1", 10001);
        SocketAddress l13_3 = new InetSocketAddress("127.0.0.1", 10003);

        HashMap<NA, NetworkInterface> neighbor1 = new HashMap<>();
        neighbor1.put(na2, new NetworkInterfaceUDP(l12_1, l12_2));
        neighbor1.put(na3, new NetworkInterfaceUDP(l13_1, l13_3));
        PacketProcessorInitiator n1 = new PacketProcessorInitiator(na1, neighbor1);
        n1.print(System.out.printf("n1:")).println();
        n1.printNeighbors(System.out.printf("===Neighbors===%n")).printf("==ENDNeighbors===%n");
        n1.start();

        HashMap<NA, NetworkInterface> neighbor2 = new HashMap<>();
        neighbor2.put(na1, new NetworkInterfaceUDP(l12_2, l12_1));
        PacketProcessorEcho n2 = new PacketProcessorEcho(na2, neighbor2);
        n2.print(System.out.printf("n2:")).println();
        n2.printNeighbors(System.out.printf("===Neighbors===%n")).printf("==ENDNeighbors===%n");
        n2.start();

        HashMap<NA, NetworkInterface> neighbor3 = new HashMap<>();
        neighbor3.put(na1, new NetworkInterfaceUDP(l13_3, l13_1));
        PacketProcessorEcho n3 = new PacketProcessorEcho(na3, neighbor3);
        n3.print(System.out.printf("n3:")).println();
        n3.printNeighbors(System.out.printf("===Neighbors===%n")).printf("==ENDNeighbors===%n");
        n3.start();

        byte[] queryGUIDBuf1 = new byte[GUID.GUID_LENGTH];
        Helper.getRandomBytes(queryGUIDBuf1, 0, GUID.GUID_LENGTH);
        byte[] queryGUIDBuf2 = new byte[GUID.GUID_LENGTH];
        Helper.getRandomBytes(queryGUIDBuf2, 0, GUID.GUID_LENGTH);

        MFPacketGNRS pkt = new MFPacketGNRS(na1, na3, new MFPacketGNRSPayloadQuery(new GUID(queryGUIDBuf1)));
        n1.getNa().print(System.out.printf("Send from NA "));
        pkt.print(System.out.printf(", packet="));
        n1.send(pkt.getDstNA(), pkt);

//        pkt = new MFPacketGNRS(na1, na2, new MFPacketGNRSPayloadQuery(new GUID(queryGUIDBuf2)));
//        n1.send(na2, pkt);

        Thread.sleep(1000);
        System.out.println("Stopping...");
        n1.stop();
        n2.stop();
        n3.stop();
//        echo.send(na3, null);
    }

    public class PacketProcessorEcho extends PacketProcessor {

        public PacketProcessorEcho(NA myNA, HashMap<NA, NetworkInterface> neighbors) {
            super(myNA, neighbors);
        }

        @Override
        public PrintStream print(PrintStream ps) throws IOException {
            return super.print(ps.printf("Echo[")).printf("]");
        }

        @Override
        protected void handlePacket(MFPacket packet) throws IOException {
            getNa().print(System.out.printf("Received on NA="));
            packet.print(System.out.printf(", packet=")).println();
            if (!(packet instanceof MFPacketGNRS)) {
                System.out.println("Invalid packet type, skip!");
            } else {
                MFPacketGNRS gnrs = (MFPacketGNRS) packet;
                sendToNeighbor(gnrs.getSrcNa(), new MFPacketGNRS(gnrs.getDstNA(), gnrs.getSrcNa(), gnrs.getPayload()));
            }
        }
    }

    public class PacketProcessorInitiator extends PacketProcessor {

        public PacketProcessorInitiator(NA myNA, HashMap<NA, NetworkInterface> neighbors) {
            super(myNA, neighbors);
        }

        public void send(NA neighbor, MFPacket packet) throws IOException {
            sendToNeighbor(neighbor, packet);
        }

        @Override
        public PrintStream print(PrintStream ps) throws IOException {
            return super.print(ps.printf("Initiator[")).printf("]");
        }

        @Override
        protected void handlePacket(MFPacket packet) throws IOException {
            getNa().print(System.out.printf("Received on NA="));
            packet.print(System.out.printf(", packet=")).println();
        }

    }

}

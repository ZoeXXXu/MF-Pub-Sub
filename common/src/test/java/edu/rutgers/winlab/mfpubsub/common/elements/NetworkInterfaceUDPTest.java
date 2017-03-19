/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.common.elements;

import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacket;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketDataPayloadRandom;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketGNRS;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketGNRSPayloadQuery;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ubuntu
 */
public class NetworkInterfaceUDPTest {

    private static final Logger LOGGER = Logger.getLogger(NetworkInterfaceUDPTest.class.getName());

    public NetworkInterfaceUDPTest() {
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
    public void test1() throws SocketException, IOException, InterruptedException {
        SocketAddress sa1 = new InetSocketAddress("127.0.0.1", 5000);
        SocketAddress sa2 = new InetSocketAddress("127.0.0.1", 5001);
        
        NetworkInterfaceUDP face1 = new NetworkInterfaceUDP(sa1, sa2);
        face1.start();
        NetworkInterfaceUDP face2 = new NetworkInterfaceUDP(sa2, sa1);

        face2.setPacketReceivedHandler(new IPacketHandler() {
            @Override
            public void handlePacket(MFPacket packet) {
                packet.print(System.out.printf("Face 2 received: ")).println();
            }
        });
        face2.start();

        byte[] srcGuidBuf = new byte[GUID.GUID_LENGTH];
        srcGuidBuf[GUID.GUID_LENGTH - 1] = 0x1;
        GUID srcGuid = new GUID(srcGuidBuf);
        byte[] dstGuidBuf = new byte[GUID.GUID_LENGTH];
        dstGuidBuf[GUID.GUID_LENGTH - 1] = 0x2;
        GUID dstGuid = new GUID(dstGuidBuf);

        byte[] payloadBuf = new byte[30];
        for (int i = 0; i < payloadBuf.length; i++) {
            payloadBuf[i] = (byte) (i & 0xFF);
        }
        
        MFPacketDataPayloadRandom payload = new MFPacketDataPayloadRandom(payloadBuf);

//        for (int i = 0; i < 10; i++) {
//            MFPacketData data = new MFPacketData(srcGuid, dstGuid, new NA(i), payload);
////            data.print(System.out.printf("send from face 1: ")).println();
//            face1.send(data);
//        }

        Thread.sleep(300);
        MFPacketGNRS gnrs = new MFPacketGNRS(new NA(6), new NA(7), new MFPacketGNRSPayloadQuery(dstGuid));
        gnrs.print(System.out.printf("send from face 1: ")).println();
        face1.send(gnrs);

        Thread.sleep(300);
        face1.stop();
        face2.stop();
    }

}

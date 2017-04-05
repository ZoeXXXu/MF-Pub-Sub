/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.gnrs;

import edu.rutgers.winlab.mfpubsub.common.elements.NetworkInterface;
import edu.rutgers.winlab.mfpubsub.common.elements.NetworkInterfaceUDP;
import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Test;

/**
 *
 * @author zoe
 */
public class PacketProcessorGNRSTest {
    NA na1 = new NA(1);
    NA na2 = new NA(2);
    NA na3 = new NA(3);
    NA na4 = new NA(4);
    NA na5 = new NA(5);
    NA na6 = new NA(6);
    NA na7 = new NA(7);
    NA na8 = new NA(8);
    NA na9 = new NA(9);
    public PacketProcessorGNRSTest() {
    }

    @Test
    public void testSomeMethod() throws SocketException, IOException, InterruptedException {

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
        //topic GUID
        byte[] footballGuidBuf = new byte[GUID.GUID_LENGTH];
        footballGuidBuf[GUID.GUID_LENGTH - 2] = 0x2;
        GUID footballGuid = new GUID(footballGuidBuf);
        //user1
        byte[] user1GuidBuf = new byte[GUID.GUID_LENGTH];
        user1GuidBuf[GUID.GUID_LENGTH - 1] = 0x3;
        GUID user1Guid = new GUID(user1GuidBuf);
        //user2
        byte[] user2GuidBuf = new byte[GUID.GUID_LENGTH];
        user2GuidBuf[GUID.GUID_LENGTH - 1] = 0x4;
        GUID user2Guid = new GUID(user2GuidBuf);
        //publisher
        byte[] pubGuidBuf = new byte[GUID.GUID_LENGTH];
        pubGuidBuf[GUID.GUID_LENGTH - 1] = 0x5;
        GUID pubGuid = new GUID(pubGuidBuf);
        //topic parent GUID
        byte[] fprtGuidBuf = new byte[GUID.GUID_LENGTH];
        fprtGuidBuf[GUID.GUID_LENGTH - 2] = (byte) 0x2f;
        GUID fprtGuid = new GUID(fprtGuidBuf);
        byte[] sprtGuidBuf = new byte[GUID.GUID_LENGTH];
        sprtGuidBuf[GUID.GUID_LENGTH - 2] = (byte) 0x3f;
        GUID sprtGuid = new GUID(sprtGuidBuf);
        //sports GUID
        byte[] sportsGuidBuf = new byte[GUID.GUID_LENGTH];
        sportsGuidBuf[GUID.GUID_LENGTH - 2] = 0x6;
        GUID sportsGuid = new GUID(sportsGuidBuf);


        SocketAddress lg3 = new InetSocketAddress("127.0.0.1", 10016);
        SocketAddress l3g = new InetSocketAddress("127.0.0.1", 10017);

        HashMap<NA, NetworkInterface> GNRSneighbor = new HashMap<>();
        GNRSneighbor.put(na3, new NetworkInterfaceUDP(lg3, l3g));

        HashMap<GUID, NA> addrT = new HashMap<>();
//        HashMap<GUID, ArrayList<GUID>> graphT = new HashMap<>();
//        ArrayList<GUID> subs = new ArrayList<>();

        addrT.put(pubGuid, na1);
        addrT.put(user1Guid, na6);
        addrT.put(user2Guid, na6);
        addrT.put(pubsubGuid, pubsubNA);

//        graphT.put(topicGuid, subs);
        PacketProcessorGNRS gnrs = new PacketProcessorGNRS(pubsubGuid, addrT, gnrsNA, GNRSneighbor);
        gnrs.print(System.out.printf("GNRS:")).println();
        gnrs.printNeighbors(System.out.printf("===Neighbors===%n")).printf("==ENDNeighbors===%n");
        gnrs.start();

        Thread.sleep(30000);
        gnrs.stop();
    }

}

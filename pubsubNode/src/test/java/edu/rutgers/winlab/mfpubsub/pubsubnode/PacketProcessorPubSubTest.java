/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.pubsubnode;

import edu.rutgers.winlab.mfpubsub.common.elements.NetworkInterface;
import edu.rutgers.winlab.mfpubsub.common.elements.NetworkInterfaceUDP;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacket;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketData;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketDataPayloadSub;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketDataPayloadUnsub;
import edu.rutgers.winlab.mfpubsub.common.structure.Address;
import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

/**
 *
 * @author zoe
 */
public class PacketProcessorPubSubTest {

    NA na1 = new NA(1);
    NA na2 = new NA(2);
    NA na3 = new NA(3);
    NA na4 = new NA(4);
    NA na5 = new NA(5);
    NA na6 = new NA(6);
    NA na7 = new NA(7);
    NA na8 = new NA(8);
    NA na9 = new NA(9);

    SocketAddress lp3 = new InetSocketAddress("127.0.0.1", 10018);
    SocketAddress l3p = new InetSocketAddress("127.0.0.1", 10019);

//    private final DatagramSocket socket;

    public PacketProcessorPubSubTest() throws SocketException {
//        this.socket = new DatagramSocket(l3p);
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

        HashMap<NA, HashMap<NA, Integer>> weight = new HashMap<>();
        HashMap<GUID, NA> addrT = new HashMap<>();
        HashMap<GUID, ArrayList<GUID>> graphT = new HashMap<>();

        HashMap<NA, Integer> edges = new HashMap<>();

        edges.put(na2, 1);
//        edges.put(na5, 1);
        weight.put(na1, (HashMap<NA, Integer>) edges.clone());

        edges.clear();
        edges.put(na1, 1);
        edges.put(na3, 1);
//        edges.put(na4, 1);
        weight.put(na2, (HashMap<NA, Integer>) edges.clone());

        edges.clear();
        edges.put(na2, 1);
        edges.put(na4, 1);
        weight.put(na3, (HashMap<NA, Integer>) edges.clone());

        edges.clear();
//        edges.put(na2, 1);
        edges.put(na3, 1);
        edges.put(na5, 1);
        edges.put(na6, 1);
        weight.put(na4, (HashMap<NA, Integer>) edges.clone());

        edges.clear();
//        edges.put(na1, 1);
        edges.put(na4, 1);
        weight.put(na5, (HashMap<NA, Integer>) edges.clone());

        edges.clear();
        edges.put(na4, 1);
        weight.put(na6, (HashMap<NA, Integer>) edges.clone());

        printWeight(weight);

        ArrayList<GUID> subs = new ArrayList<>();
        addrT.put(pubGuid, na1);
        addrT.put(user1Guid, na6);
        addrT.put(user2Guid, na6);
        addrT.put(footballGuid, na4);
        addrT.put(sportsGuid, na4);

        subs.add(fprtGuid);
        subs.add(sportsGuid);
        subs.add(user2Guid);
        graphT.put(footballGuid, (ArrayList<GUID>) subs.clone());
        subs.clear();
        graphT.put(fprtGuid, (ArrayList<GUID>) subs.clone());
        subs.add(footballGuid);
        graphT.put(sprtGuid, (ArrayList<GUID>) subs.clone());
        subs.clear();
        subs.add(sprtGuid);
        graphT.put(sportsGuid, (ArrayList<GUID>) subs.clone());

//        printG(graphT);
        HashMap<NA, NetworkInterface> PubSubneighbor = new HashMap<>();
        PubSubneighbor.put(na3, new NetworkInterfaceUDP(lp3, l3p));

//        for (Map.Entry<GUID, ArrayList<GUID>> entry : graphT.entrySet()) {
//            entry.getKey().print(System.out).printf(" : ");
//            for (GUID mapping : entry.getValue()) {
//                mapping.print(System.out).printf(" ");
//            }
//        }
        PacketProcessorPubSub node = new PacketProcessorPubSub(gnrsNA, pubsubGuid, weight, addrT, graphT, pubsubNA, PubSubneighbor);
        node.build(footballGuid);
//        node.AddBranch(sportsGuid, user1Guid);
//        node.printMulti();
        node.start();

//        System.out.println("send a join msg to pubsub");
        //test
//        send(new MFPacketData(user1Guid, pubsubGuid, pubsubNA, MFPacketDataPayloadSub.MF_PACKET_DATA_SID_SUBSCRIPTION, sportsGuid));
        Thread.sleep(30000);
//        System.out.println("send a unjoin msg to pubsub");
//        send(new MFPacketData(user2Guid, pubsubGuid, pubsubNA, MFPacketDataPayloadUnsub.MF_PACKET_DATA_SID_UNSUBSCRIPTION, footballGuid));
//        Thread.sleep(1000);
//        System.out.println("send a unjoin msg to pubsub");
//        send(new MFPacketData(user1Guid, pubsubGuid, pubsubNA, MFPacketDataPayloadUnsub.MF_PACKET_DATA_SID_UNSUBSCRIPTION, sportsGuid));
//        Thread.sleep(1000);
        node.stop();
    }

    private void send(MFPacket packet) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            packet.serialize(baos);
            byte[] buf = baos.toByteArray();
            DatagramPacket dp = new DatagramPacket(buf, buf.length, lp3);
//            socket.send(dp);
        }
    }

    private void printG(HashMap<GUID, ArrayList<GUID>> graphT) {
        System.out.println("\n\n************************graph****************************");
        for (Map.Entry<GUID, ArrayList<GUID>> node : graphT.entrySet()) {
            node.getKey().print(System.out.printf("\n")).printf(" : ");
            for (GUID edge : node.getValue()) {
                edge.print(System.out);
            }
        }
        System.out.println();
    }

    private void printWeight(HashMap<NA, HashMap<NA, Integer>> weight) {
        System.out.println("************************weight graph****************************");
        for (Map.Entry<NA, HashMap<NA, Integer>> node : weight.entrySet()) {
            node.getKey().print(System.out.printf("\nnode ")).printf("to: ");
            for (Map.Entry<NA, Integer> edge : node.getValue().entrySet()) {
                edge.getKey().print(System.out).printf("(" + edge.getValue().toString() + ")");
            }
        }
    }

    private void printTree(HashMap<NA, ArrayList<Address>> tree) {
        System.out.println("************************ tree ****************************");
        for (Map.Entry<NA, ArrayList<Address>> branch : tree.entrySet()) {
            branch.getKey().print(System.out.printf("\n")).printf(" : ");
            for (Address addr : branch.getValue()) {
                addr.print(System.out).printf(" ");
            }
        }
        System.out.println();
    }

}

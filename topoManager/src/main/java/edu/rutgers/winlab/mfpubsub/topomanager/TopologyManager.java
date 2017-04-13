/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.topomanager;

import edu.rutgers.winlab.mfpubsub.common.elements.NetworkInterface;
import edu.rutgers.winlab.mfpubsub.common.elements.NetworkInterfaceUDP;
import edu.rutgers.winlab.mfpubsub.common.elements.PacketProcessor;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacket;
import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import edu.rutgers.winlab.mfpubsub.router.PacketProcessorRouter;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import static sun.jdbc.odbc.JdbcOdbcObject.hexStringToByteArray;

/**
 *
 * @author zoe
 */
public class TopologyManager {

//    private final PacketProcessor processor;
    private final PacketProcessorTopologyManager manager;
    private final NA self;
    private GUID GNRSguid;
    private NA GNRSna;
    private GUID PubSubguid;
    private NA PubSubna;
    private final HashMap<NA, ArrayList<NA>> neighbors = new HashMap<>();

    public TopologyManager(NA self, String filename, String tracefile, String ID, int port, String selfIP, String ctrlIP) throws IOException {
//        PacketProcessor pro = readTopo(self, filename, ID);
        this.self = self;
        HashMap<NA, NetworkInterface> neighbor = new HashMap<>();
        neighbor.put(new NA(999999), new NetworkInterfaceUDP(new InetSocketAddress(selfIP, port), new InetSocketAddress(ctrlIP, port)));
        manager = new PacketProcessorTopologyManager(neighbor, readTopo(filename, ID), tracefile);
    }

    private GUID createGUID(byte[] guid) throws IOException {
//        Helper.printBuf(System.out.printf("GUID byte[]: "), guid, 0, guid.length).println();
        if (guid.length == GUID.GUID_LENGTH) {
            return new GUID(guid);
        } else {
            throw new IOException("GUID length is not fittable");
        }
    }

    private PacketProcessor readTopo(String filename, String ID) throws FileNotFoundException, IOException {
        BufferedReader buf = new BufferedReader(new FileReader(filename));
        String[] address;
        HashMap<String, String> NAIP = new HashMap<>(); // NA - IP
        while ((address = buf.readLine().split(" ")) != null) {
            initial(address, buf);
            switch (ID) {
                case "router":
                    return doRouter(NAIP, buf);
                case "endhost":
                    return doEndHost(NAIP, buf);
                case "GNRS":
                    return doGNRS(NAIP, buf);
                case "PubSub Node":
                    return doPubSubNode(NAIP, buf);
                default:
                    throw new IOException("don't  understand which element you want to create");
            }
        }
        return null;
    }

    private PacketProcessor doRouter(HashMap<String, String> NAIP, BufferedReader buf) throws IOException {
        HashMap<NA, NetworkInterface> neighbor = new HashMap<>();
        HashMap<NA, NA> routing = new HashMap<>();
        HashMap<GUID, NA> localGUIDTable = new HashMap<>();
        String line;
        String[] address;
        while ((line = buf.readLine()) != null) {
            address = line.split(" ");
            switch (address.length) {
                case 2:
                    NAIP.put(address[0], address[1]);
                    break;
                case 4:
                    //build localGUID table here
                    int tmp = Integer.parseInt(address[2], 16);
                    if (self.getVal() == tmp) {
                        localGUIDTable.put(createGUID(hexStringToByteArray(address[0])), new NA(Integer.parseInt(address[1], 16)));
//                        neighbor.put(new NA(Integer.parseInt(address[1], 16)), new NetworkInterfaceUDP(
//                                new InetSocketAddress(NAIP.get(address[2]), Integer.parseInt(address[3], 16)),
//                                new InetSocketAddress(NAIP.get(address[1]), Integer.parseInt(address[3], 16))));
                    }
                    break;
                case 5:
                    //build neighbor table here
                    AddNeighbors(neighbors, new NA(Integer.parseInt(address[2], 16)), new NA(Integer.parseInt(address[0], 16)));
                    if (self.getVal() == Integer.parseInt(address[0], 16)) {
                        //update neighbor/routing/localGUIDTable
                        NA nbr = new NA(Integer.parseInt(address[2], 16));
//                        neighbor.put(nbr, new NetworkInterfaceUDP(
//                                new InetSocketAddress(NAIP.get(address[0]), Integer.parseInt(address[1], 16)),
//                                new InetSocketAddress(NAIP.get(address[2]), Integer.parseInt(address[3], 16))));
                        routing.put(nbr, nbr);
                    }
                    if (self.getVal() == Integer.parseInt(address[2], 16)) {
                        NA nbr = new NA(Integer.parseInt(address[0], 16));
//                        neighbor.put(nbr, new NetworkInterfaceUDP(
//                                new InetSocketAddress(NAIP.get(address[2]), Integer.parseInt(address[3], 16)),
//                                new InetSocketAddress(NAIP.get(address[0]), Integer.parseInt(address[1], 16))));
                        routing.put(nbr, nbr);
                    }
                    break;
                default:
                    throw new IOException("topology file format is incorrect!");
            }
        }
        //build routing table here
        for (NA nbr : routing.keySet()) {
            LookForR(routing, nbr, nbr);
        }
        return new PacketProcessorRouter(GNRSna, localGUIDTable, routing, self, neighbor);
    }

    private PacketProcessor doEndHost(HashMap<String, String> NAIP, BufferedReader buf) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private PacketProcessor doGNRS(HashMap<String, String> NAIP, BufferedReader buf) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private PacketProcessor doPubSubNode(HashMap<String, String> NAIP, BufferedReader buf) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void initial(String[] address, BufferedReader buf) throws IOException {
        GNRSguid = createGUID(hexStringToByteArray(address[0]));
        GNRSna = new NA(Integer.parseInt(address[1], 16));
        address = buf.readLine().split(" ");
        //PubSub node
        PubSubguid = createGUID(hexStringToByteArray(address[0]));
        PubSubna = new NA(Integer.parseInt(address[1], 16));
    }

    private void AddNeighbors(HashMap<NA, ArrayList<NA>> neighbors, NA na1, NA na2) {
        ArrayList<NA> tmp1 = neighbors.get(na1);
        ArrayList<NA> tmp2 = neighbors.get(na2);
        if (tmp1 == null) {
            neighbors.put(na1, tmp1 = new ArrayList<>());
        }
        if (tmp2 == null) {
            neighbors.put(na2, tmp2 = new ArrayList<>());
        }
        tmp1.add(na2);
        tmp2.add(na1);
    }

    private void LookForR(HashMap<NA, NA> routing, NA nbr, NA nextNA) {
        routing.put(nbr, nextNA);
        for (NA i : neighbors.get(nbr)) {
            // if i != self, i haven't added into the routing table
            if (!(routing.containsKey(i) || i.equals(self))) {
                LookForR(routing, i, nextNA);
            }
        }
    }

    private class PacketProcessorTopologyManager extends PacketProcessor {

        private final PacketProcessor processor;

        public PacketProcessorTopologyManager(HashMap<NA, NetworkInterface> neighbor, PacketProcessor processor, String tracefile) throws FileNotFoundException {
            super(processor.getNa(), neighbor);//one neighbor with ctrl center
//            extractSelfTrace(new BufferedReader(new FileReader(tracefile)));
            this.processor = processor;
        }

        @Override
        protected void handlePacket(MFPacket packet) throws IOException {
            //start traceThread when receive the beginning signal
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        private final Thread traceThread = new Thread() {
            @Override
            public void run() {
                //do trace calls

                super.run(); //To change body of generated methods, choose Tools | Templates.
            }
        };

        private void extractSelfTrace(BufferedReader bufferedReader) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public void printInnerElemTables() throws IOException {
            if (processor instanceof PacketProcessorRouter) {
                PacketProcessorRouter tmp = (PacketProcessorRouter) processor;
                tmp.printNeighbors(System.out.printf("\n********neighbor********\n")).println();
                tmp.printRoutingTable(System.out.printf("\n********routing********\n")).println();
            }
        }
    }

    public void printInnerTables() throws IOException {
        manager.printInnerElemTables();
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.topomanager;

import edu.rutgers.winlab.mfpubsub.common.elements.NetworkInterface;
import edu.rutgers.winlab.mfpubsub.common.elements.PacketProcessor;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacket;
import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import edu.rutgers.winlab.mfpubsub.router.PacketProcessorRouter;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author zoe
 */
public class TopologyManager {

    private final PacketProcessor processor;

    public TopologyManager(NA self, String filename, String tracefile, String ID) throws IOException {
        processor = readTopo(self, filename, ID);
    }

    private GUID createGUID(byte[] guid) throws IOException {
        if (guid.length < GUID.GUID_LENGTH) {
            return new GUID(guid);
        } else {
            throw new IOException("GUID length is too long");
        }
    }

    private PacketProcessor readTopo(NA self, String filename, String ID) throws FileNotFoundException, IOException {
//        BufferedReader buf = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
//        int port_ini_num = 10000;
        BufferedReader buf = new BufferedReader(new FileReader(filename));
        switch (ID) {
            case "router":
                return doRouter(self, buf);
            case "endhost":
                return doEndHost(self, buf);
            case "GNRS":
                return doGNRS(self, buf);
            case "PubSub Node":
                return doPubSubNode(self, buf);
            default:
                throw new IOException("don't  understand which element you want to create");
        }
    }

    private PacketProcessorRouter doRouter(NA self, BufferedReader buf) throws IOException {
        HashMap<NA, NetworkInterface> neighbor = new HashMap<>();
        HashMap<NA, NA> routing = new HashMap<>();
        HashMap<GUID, NA> localGUIDTable = new HashMap<>();
        String line;
        String[] address = buf.readLine().split(" ");
        GUID GNRSguid = createGUID(address[0].getBytes());
        NA GNRSna = new NA(Integer.getInteger(address[1]));
        address = buf.readLine().split(" ");
        GUID PubSubguid = createGUID(address[0].getBytes());
        NA PubSubna = new NA(Integer.getInteger(address[1]));
        while ((line = buf.readLine()) != null) {
            address = line.split(" ");
            if (self.getVal() == Integer.getInteger(address[0])) {
                //update neighbor/routing/localGUIDTable
            }
        }
        return new PacketProcessorRouter(GNRSna, localGUIDTable, routing, self, neighbor);
    }

    private PacketProcessor doEndHost(NA self, BufferedReader buf) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private PacketProcessor doGNRS(NA self, BufferedReader buf) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private PacketProcessor doPubSubNode(NA self, BufferedReader buf) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private class PacketProcessorTopologyManager extends PacketProcessor {

        public PacketProcessorTopologyManager(String tracefile, NA myNA, HashMap<NA, NetworkInterface> neighbors) throws FileNotFoundException {
            super(myNA, neighbors);
            extractSelfTrace(new BufferedReader(new FileReader(tracefile)));
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

    }

}

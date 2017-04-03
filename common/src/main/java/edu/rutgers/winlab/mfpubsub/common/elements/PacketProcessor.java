/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.common.elements;

import edu.rutgers.winlab.mfpubsub.common.FIFOEntry;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacket;
import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ubuntu
 */
public abstract class PacketProcessor {

    private final NA na;
//    private final GUID GNRS;
    private final PriorityBlockingQueue<FIFOEntry<MFPacket>> incomingQueue = new PriorityBlockingQueue<>();
    private final HashMap<NA, NetworkInterface> neighbors;
    private boolean running;

    public NA getNa() {
        return na;
    }

//    public GUID getGNRS() {
//        return GNRS;
//    }

    public int getIncomingQueueSize() {
        return incomingQueue.size();
    }

    private final IPacketHandler addToIncomingQueue = new IPacketHandler() {
        @Override
        public void handlePacket(MFPacket packet) {
            incomingQueue.put(new FIFOEntry<>(packet));
        }
    };

    private final Thread processThread = new Thread() {
        @Override
        public void run() {
            while (running) {
                try {
                    FIFOEntry<MFPacket> packet = incomingQueue.poll(1, TimeUnit.MILLISECONDS);
                    if (packet == null) {
                        continue;
                    }
                    handlePacket(packet.getEntry());
                } catch (InterruptedException | IOException ex) {
                    Logger.getLogger(PacketProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    };

    public PacketProcessor(NA myNA, HashMap<NA, NetworkInterface> neighbors) {
        this.na = myNA;
//        this.GNRS = GNRS;
        this.neighbors = neighbors;
        for (NetworkInterface i : neighbors.values()) {
            i.setPacketReceivedHandler(addToIncomingQueue);
        }
    }

    public synchronized void start() {
        if (processThread.getState() != Thread.State.NEW) {
            throw new IllegalStateException("PacketProcessor already started");
        }
        running = true;
        for (NetworkInterface i : neighbors.values()) {
            i.start();
        }
        processThread.start();

    }

    public void stop() throws InterruptedException {
        running = false;
        processThread.join();
        for (NetworkInterface i : neighbors.values()) {
            i.stop();
        }
    }

    protected void sendToNeighbor(NA neighbor, MFPacket packet) throws IOException {
        NetworkInterface i = neighbors.get(neighbor);
        if (i == null) {
            throw new IOException(String.format("Cannot find neighbor: %s on %s", neighbor.getVal(), getNa().getVal()));
        }
        i.send(packet);
    }

    protected abstract void handlePacket(MFPacket packet) throws IOException;

    public PrintStream print(PrintStream ps) throws IOException {
        return na.print(ps.printf("NA="));
    }

    public PrintStream printNeighbors(PrintStream ps) throws IOException {
        na.print(ps.printf("Node NA=")).println();
        for (Map.Entry<NA, NetworkInterface> entry : neighbors.entrySet()) {
            NA neighbor = entry.getKey();
            NetworkInterface face = entry.getValue();
            neighbor.print(ps).printf("->");
            face.print(ps).println();
        }
        return ps;
    }

}

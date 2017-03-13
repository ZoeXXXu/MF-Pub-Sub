/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.common.elements;

import edu.rutgers.winlab.mfpubsub.common.FIFOEntry;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacket;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacketFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ubuntu
 */
public class NetworkInterfaceUDP extends NetworkInterface {

    public static final int MAX_CHUNK_SIZE = 64 * 1024;
    private static final Logger LOGGER = Logger.getLogger(NetworkInterfaceUDP.class.getName());

    private final SocketAddress localAddress, remoteAddress;
    private final PriorityBlockingQueue<FIFOEntry<MFPacket>> outgoingQueue = new PriorityBlockingQueue<>();
    private final DatagramSocket socket;
    private boolean running = false;

    public SocketAddress getLocalAddress() {
        return localAddress;
    }

    public SocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    public int getOutgoingQueueSize() {
        return outgoingQueue.size();
    }

    public NetworkInterfaceUDP(SocketAddress localAddress, SocketAddress remoteAddress) throws SocketException {
        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
        socket = new DatagramSocket(localAddress);
        //This waiting time need to be considered based on what kind of cases expected to be run in this implementation.
        socket.setSoTimeout(1);
    }

    @Override
    public void send(MFPacket packet) throws IOException {
        outgoingQueue.put(new FIFOEntry<>(packet));
    }

    @Override
    public synchronized void start() {
        if (sendThread.getState() != Thread.State.NEW) {
            throw new IllegalStateException("Interface already started.");
        }
        running = true;
        sendThread.start();
        listenThread.start();
    }

    @Override
    public void stop() throws InterruptedException {
        running = false;
        sendThread.join();
        listenThread.join();
        socket.close();
    }

    private final Thread sendThread = new Thread() {
        @Override
        public void run() {
            NetworkInterfaceUDP.this.print(System.out.printf("Interface sender: ")).println(" started.");
            while (running) {
                try {
                    FIFOEntry<MFPacket> toSend = outgoingQueue.poll(1, TimeUnit.MILLISECONDS);
                    if (toSend == null) {
                        continue;
                    }
                    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

                        toSend.getEntry().serialize(baos);
                        byte[] buf = baos.toByteArray();
                        DatagramPacket dp = new DatagramPacket(buf, buf.length, remoteAddress);
                        socket.send(dp);
                    } catch (IOException ex) {
                        LOGGER.log(Level.SEVERE, "Failed in sending packet", ex);
                    }
                } catch (InterruptedException ex) {
                    LOGGER.log(Level.WARNING, "Interrupted while waiting for sending queue", ex);
                }
            }
            NetworkInterfaceUDP.this.print(System.out.printf("Interface sender: ")).println(" stopped.");
        }
    };

    private final Thread listenThread = new Thread() {
        @Override
        public void run() {
            byte[] buf = new byte[MAX_CHUNK_SIZE];
            DatagramPacket packet = new DatagramPacket(buf, MAX_CHUNK_SIZE);
            NetworkInterfaceUDP.this.print(System.out.printf("Interface listener: ")).println(" started.");
            while (running) {
                try {
                    socket.receive(packet);
                    if (!packet.getSocketAddress().equals(remoteAddress)) {
                        LOGGER.log(Level.INFO, String.format("Discarding packets (%s) not from remote (%s)", packet.getSocketAddress(), remoteAddress));
                        continue;
                    }
                    byte[] tmp = new byte[packet.getLength()];
                    System.arraycopy(buf, 0, tmp, 0, tmp.length);
                    int[] pos = new int[]{0};
                    MFPacket pkt = MFPacketFactory.createPacket(tmp, pos);
//                    pkt.print(System.out.printf("received packet: ")).println();
                    firePacketReceived(pkt); // Need to be override in specific class: add it into Inqueue
                } catch (SocketTimeoutException ex) {
                    // timeout, ignore
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
            NetworkInterfaceUDP.this.print(System.out.printf("Interface listener: ")).println(" stopped.");
        }
    };

    @Override
    public PrintStream print(PrintStream ps) {
        return ps.printf("Interface[%s -> %s]", localAddress, remoteAddress);
    }

}

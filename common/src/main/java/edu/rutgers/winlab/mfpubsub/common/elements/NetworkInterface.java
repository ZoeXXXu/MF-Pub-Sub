/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.common.elements;

import edu.rutgers.winlab.mfpubsub.common.packets.MFPacket;
import java.io.IOException;
import java.io.PrintStream;

/**
 *
 * @author ubuntu
 */
public abstract class NetworkInterface {

    private IPacketHandler packetReceivedHandler = null;

    public IPacketHandler getPacketReceivedHandler() {
        return packetReceivedHandler;
    }

    public void setPacketReceivedHandler(IPacketHandler packetReceivedHandler) {
        this.packetReceivedHandler = packetReceivedHandler;
    }

    protected void firePacketReceived(MFPacket packet) {
        if (packetReceivedHandler != null) {
            packetReceivedHandler.handlePacket(packet);
        }
    }

    public synchronized void start() {
    }

    public void stop() throws InterruptedException {
    }

    public abstract void send(MFPacket packet) throws IOException;

    public abstract PrintStream print(PrintStream ps);
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.pubsubnode;

import edu.rutgers.winlab.mfpubsub.common.elements.NetworkInterface;
import edu.rutgers.winlab.mfpubsub.common.elements.PacketProcessor;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacket;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author zoe
 */
public class PacketProcessorPubSub extends PacketProcessor{

    public PacketProcessorPubSub(NA myNA, HashMap<NA, NetworkInterface> neighbors) {
        super(myNA, neighbors);
    }

    @Override
    protected void handlePacket(MFPacket packet) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

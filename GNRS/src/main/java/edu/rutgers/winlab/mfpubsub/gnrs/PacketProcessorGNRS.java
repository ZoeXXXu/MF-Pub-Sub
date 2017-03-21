/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.gnrs;

import edu.rutgers.winlab.mfpubsub.common.elements.NetworkInterface;
import edu.rutgers.winlab.mfpubsub.common.elements.PacketProcessor;
import edu.rutgers.winlab.mfpubsub.common.packets.MFPacket;
import edu.rutgers.winlab.mfpubsub.common.structure.Address;
import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author zoe
 */
public class PacketProcessorGNRS extends PacketProcessor{
    private HashMap<GUID, List<Address>> Table;

    public PacketProcessorGNRS(NA myNA, HashMap<NA, NetworkInterface> neighbors) {
        super(myNA, neighbors);
    }

    @Override
    protected void handlePacket(MFPacket packet) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

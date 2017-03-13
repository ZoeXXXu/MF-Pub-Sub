/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.router;

import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import edu.rutgers.winlab.mfpubsub.common.elements.NetworkInterface;
import java.util.HashMap;

/**
 *
 * @author zoe
 */
public class Router {

    public final HashMap<NA, NetworkInterface> neighbors;
//    public final PacketProcessorRouter processor;

    public Router(NA self, HashMap<NA, NetworkInterface> neighbors) {
        this.neighbors = neighbors;
//        this.processor = new PacketProcessorRouter(self, neighbors);
//        this.processor.start();
    }
}

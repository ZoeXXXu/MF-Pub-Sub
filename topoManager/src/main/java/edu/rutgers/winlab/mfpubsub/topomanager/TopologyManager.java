/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.topomanager;

import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author zoe
 */
public class TopologyManager {
    
    private static final HashMap<NA, ArrayList<NA>> topology = new HashMap<>();

    public TopologyManager() {
        ArrayList<NA> neighbor;
//        topology.put(, value)

    }
    
    private GUID createGUID(byte num){
        byte[] buf = new byte[GUID.GUID_LENGTH];
        buf[GUID.GUID_LENGTH - 1] = num;
        return new GUID(buf);
    }
}

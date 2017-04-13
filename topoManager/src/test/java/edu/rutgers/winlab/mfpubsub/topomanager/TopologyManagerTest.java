/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.topomanager;

import edu.rutgers.winlab.mfpubsub.common.Helper;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import java.io.IOException;
import org.junit.Test;
import static sun.jdbc.odbc.JdbcOdbcObject.hexStringToByteArray;

/**
 *
 * @author zoe
 */
public class TopologyManagerTest {

    public TopologyManagerTest() {
    }

    @Test
    public void testSomeMethod() throws IOException {
        String filename = "/home/zoe/NetBeansProjects/MF-Pub-Sub/topoManager/topology.txt";
        TopologyManager test = new TopologyManager(new NA(2), filename, "", "router", 15000, "127.0.0.1", "127.0.0.2");
        test.printInnerTables();
        
//        String x = "7fffffff";
////        int xx = Integer.getInteger(x);
//        int y = Integer.MAX_VALUE;
////        System.out.println("x =" + x + ", xx=" s+ xx);
//        System.out.println("x =" + Integer.parseInt(x, 16) + ", y=" + Integer.toHexString(y));
    }
}

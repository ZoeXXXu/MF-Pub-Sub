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

/**
 *
 * @author zoe
 */
public class TopologyManagerTest {

    public TopologyManagerTest() {
    }

    @Test
    public void testSomeMethod() throws IOException {
//        String filename = "/home/zoe/NetBeansProjects/MF-Pub-Sub/topoManager/topology.txt";
//        TopologyManager test = new TopologyManager(new NA(1), filename, "", "router", 15000, "127.0.0.1", "127.0.0.2");
//        test.printInnerTables();

        String test = "ffff";
        char[] c = new char[test.length()];
        test.getChars(0, test.length(), c, 0);
        byte[] bytes = new byte[c.length];
        byte[] by = test.getBytes("UTF-8");
        for (int i = 0; i < c.length; i++) {
            System.out.println(Integer.toBinaryString(c[1] & 0xFF));
            bytes[i] = (byte) (c[i]);
//            System.out.println(Integer.toBinaryString(bytes[i] & 0xFF));
            System.out.println(Integer.toBinaryString(by[i] & 0xFF));
        }
        System.out.println("String: " + test);
        Helper.printBuf(System.out.printf("bytes: "), bytes, 0, bytes.length).println();
        Helper.printBuf(System.out.printf("bytes: "), test.getBytes("UTF-8"), 0, test.getBytes("UTF-8").length).println();
        
        int x = 0b11111111;
        System.out.println("x=" + Integer.toString(x));
        System.out.println("");
        
    }

}

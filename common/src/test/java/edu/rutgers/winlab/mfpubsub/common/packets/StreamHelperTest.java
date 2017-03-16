/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.common.packets;

import edu.rutgers.winlab.mfpubsub.common.Helper;
import java.io.ByteArrayOutputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author ubuntu
 */
public class StreamHelperTest {

    public StreamHelperTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @org.junit.Test
    public void test1() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Helper.writeInt(baos, 1);
        byte[] buf = baos.toByteArray();
        Helper.printBuf(System.out, buf, 0, buf.length);
        System.out.println();
        System.out.printf("0x%x%n", Helper.readInt(buf, new int[]{0}));
    }

    @org.junit.Test
    public void test2() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Helper.writeInt(baos, 0x12345678);
        byte[] buf = baos.toByteArray();
        Helper.printBuf(System.out, buf, 0, buf.length);
        System.out.println();
        System.out.printf("0x%x%n", Helper.readInt(buf, new int[]{0}));
    }

    @org.junit.Test
    public void test3() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Helper.writeInt(baos, 0xf2345678);
        byte[] buf = baos.toByteArray();
        Helper.printBuf(System.out, buf, 0, buf.length);
        System.out.println();
        System.out.printf("0x%x%n", Helper.readInt(buf, new int[]{0}));
    }
}

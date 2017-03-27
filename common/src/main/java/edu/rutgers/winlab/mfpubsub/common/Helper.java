/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.common;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Random;

/**
 *
 * @author ubuntu
 */
public class Helper {

    private static final Random RAND = new Random(0);

    public static void writeInt(OutputStream stream, int val) throws IOException {
        stream.write((val >> 24) & 0xFF);
        stream.write((val >> 16) & 0xFF);
        stream.write((val >> 8) & 0xFF);
        stream.write(val & 0xFF);
    }

    public static int readInt(byte[] buf, int[] pos) {
        int ret = ((buf[pos[0]++]) & 0xFF) << 24;
        ret += ((buf[pos[0]++]) & 0xFF) << 16;
        ret += ((buf[pos[0]++]) & 0xFF) << 8;
        ret += (buf[pos[0]++]) & 0xFF;
        return ret;
    }

    public static void writeShort(OutputStream stream, short val) throws IOException {
        stream.write((val >> 8) & 0xFF);
        stream.write(val & 0xFF);
    }

    public static short readShort(byte[] buf, int[] pos) {
        short ret = (short) (((buf[pos[0]++]) & 0xFF) << 8);
        ret += (buf[pos[0]++]) & 0xFF;
        return ret;
    }

    public static PrintStream printBuf(PrintStream ps, byte[] buf, int start, int len) {
        for (int i = 0; i < len - 1; i++) {
            ps.printf("%02x ", buf[start + i]);
        }
        ps.printf("%02x", buf[start + len - 1]);
        return ps;
    }

    public static void getRandomBytes(byte[] payload, int pos, int len) {
        for (int i = 0; i < len; i++) {
            payload[pos + i] = (byte) (RAND.nextInt() & 0xFF);
        }
    }

    public static int getRandomInt() {
        return RAND.nextInt();
    }

}

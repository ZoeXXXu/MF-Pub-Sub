/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.common.structure;

import edu.rutgers.winlab.mfpubsub.common.Helper;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 *
 * @author ubuntu
 */
public class NA extends Address {

    public static transient final int NA_SIZE = 4;

    private final int val;

    public NA(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + this.val;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NA other = (NA) obj;
        return this.val == other.val;
    }

    @Override
    public OutputStream serialize(OutputStream stream) throws IOException {
        Helper.writeInt(stream, val);
        return stream;
    }

    public static NA create(byte[] packet, int[] pos) throws IOException {
        int val = Helper.readInt(packet, pos);
        return new NA(val);
    }

    @Override
    public PrintStream print(PrintStream ps) {
        return ps.printf("[%08x]", val);
    }

}

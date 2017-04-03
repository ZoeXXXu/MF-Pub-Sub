/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.common.structure;

import edu.rutgers.winlab.mfpubsub.common.Helper;
import edu.rutgers.winlab.mfpubsub.common.packets.ISerializable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;

/**
 * the 20 bytes GUID with serializable function
 *
 * @author ubuntu
 */
public class GUID implements Address {

    public static final int GUID_LENGTH = 20;

    private byte[] value;

    public GUID(byte[] value) {
        if (value.length != GUID_LENGTH) {
            throw new IllegalArgumentException("GUID size should be " + GUID_LENGTH);
        }
        this.value = value;
    }

    public byte[] getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Arrays.hashCode(this.value);
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
        final GUID other = (GUID) obj;
        return Arrays.equals(this.value, other.value);
    }

    @Override
    public OutputStream serialize(OutputStream stream) throws IOException {
        stream.write(value, 0, GUID_LENGTH);
        return stream;
    }

    public static GUID create(byte[] packet, int[] pos) {
        byte[] buf = new byte[GUID_LENGTH];
        System.arraycopy(packet, pos[0], buf, 0, GUID_LENGTH);
        pos[0] += GUID_LENGTH;
        return new GUID(buf);
    }

    @Override
    public PrintStream print(PrintStream ps) {
        return Helper.printBuf(ps.printf("["), value, 0, GUID_LENGTH).printf("]");
    }

    public static transient final GUID GUID_NULL = new GUID(new byte[GUID_LENGTH]);
    
    public boolean isNULL(){
        if(this.value.equals(GUID_NULL)){
            return true;
        }
        return false;
    }
}

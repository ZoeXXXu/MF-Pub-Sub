/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.common.packets;

import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 *
 * @author zoe
 */
public class MFPacketNetworkRenew extends MFPacket {

    private final static byte MF_PACKET_TYPE_NETWORK_RENEW = 9;

    private final GUID renewedGUID;

    private final NA newAddress;

    public MFPacketNetworkRenew(GUID renewedGUID, NA newAddress, NA dstNA) {
        super(MF_PACKET_TYPE_NETWORK_RENEW, dstNA, true);
        this.renewedGUID = renewedGUID;
        this.newAddress = newAddress;
    }

    @Override
    public OutputStream serialize(OutputStream stream) throws IOException {
        super.serialize(stream);
        renewedGUID.serialize(stream);
        return newAddress.serialize(stream);
    }

    @Override
    public PrintStream print(PrintStream ps) {
        super.print(ps.printf("Data["));
        renewedGUID.print(ps.printf(", tobe renewed GUID="));
        return newAddress.print(ps.printf(", new address"));
    }

    public static MFPacketNetworkRenew createUpdatePacket(byte[] packet, int[] pos) throws IOException {
        NA na = NA.create(packet, pos);
        GUID renewedGUID = GUID.create(packet, pos);
        NA newAddress = NA.create(packet, pos);
        return new MFPacketNetworkRenew(renewedGUID, newAddress, na);
    }

    public NA getNewAddress() {
        return newAddress;
    }

    public GUID getRenewedGUID() {
        return renewedGUID;
    }
}

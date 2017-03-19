/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.common.packets;

import edu.rutgers.winlab.mfpubsub.common.structure.GUID;
import edu.rutgers.winlab.mfpubsub.common.structure.NA;

/**
 *
 * @author zoe
 */
public class MFPacketDataPublish extends MFPacketData {

    public static final byte MF_PACKET_DATA_SID_PUBLISH = 0;

    public MFPacketDataPublish(GUID srcGUID, GUID dstGUID, NA na, ISerializable payload) {
        super(srcGUID, dstGUID, na, MF_PACKET_DATA_SID_PUBLISH, payload);
    }
}

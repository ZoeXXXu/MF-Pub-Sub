/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.common.structure;

import edu.rutgers.winlab.mfpubsub.common.packets.ISerializable;

/**
 *
 * @author zoe
 */
public interface Address extends ISerializable {

    public static final byte MF_GNRS_PACKET_PAYLOAD_NA = 0;

    public static final byte MF_GNRS_PACKET_PAYLOAD_GUID = 1;

}

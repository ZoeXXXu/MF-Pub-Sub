/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.common.elements;

import edu.rutgers.winlab.mfpubsub.common.packets.MFPacket;

/**
 *
 * @author ubuntu
 */
public interface IPacketHandler {

    public void handlePacket(MFPacket packet);
}

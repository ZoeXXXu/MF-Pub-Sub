/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.common.structure;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import edu.rutgers.winlab.mfpubsub.common.packets.ISerializable;
import static edu.rutgers.winlab.mfpubsub.common.structure.GUID.GUID_LENGTH;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author zoe
 */
public class TreeBranchCopy extends JSONMessage implements ISerializable {

    private final GUID topicGUIDs;
    private final List<Address> trees;

    public TreeBranchCopy(GUID guid) {
        topicGUIDs = guid;
        trees = new ArrayList<>();
        trees.add(new NA(3));
    }

//    public void addBranch(GUID key, NA address) {
//        if (tree.contains(address)) {
//            tree.get(key).add(address);
//        } else {
//            List<Address> list = new ArrayList<>();
//            list.add(address);
//            tree.put(key, list);
//        }
//    }
    
    public List getTree() {
        return trees;
    }

    public GUID getGUID() {
        return topicGUIDs;
    }

    public static TreeBranch createTreeBrach(String buffer) {
        JsonParser parser = new JsonParser();
        JsonElement elem = parser.parse(buffer);
        TreeBranch tree = JSONMessage.fromJSON(elem, TreeBranch.class);
//        TreeBranch tree = (new GsonBuilder().create()).fromJson(buffer, TreeBranch.class);
        return tree;
    }

    @Override
    public OutputStream serialize(OutputStream stream) throws IOException {
        ByteArrayOutputStream bytearray = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bytearray);
        out.writeObject(trees);
        stream.write(bytearray.toByteArray());
        return stream;
    }

    @Override
    public PrintStream print(PrintStream ps) {
        ps.printf("\nTopic " + topicGUIDs + " Branch:");
        for (Address entry : trees) {
            entry.print(ps).printf(" ");
        }
        ps.printf(">\n");
        return ps;
    }
}

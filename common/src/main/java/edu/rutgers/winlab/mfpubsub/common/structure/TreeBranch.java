/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.common.structure;

import edu.rutgers.winlab.mfpubsub.common.packets.ISerializable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author zoe
 */
public class TreeBranch extends JSONMessage implements ISerializable {

//    private final GUID topicGUID;
//    private final List<Address> tree;

    private final HashMap<GUID, ArrayList<Address>> tree;
    
    public TreeBranch() {
        tree = new HashMap<>();
    }

    public void addBranch(GUID key, Address address) {
        if (tree.containsKey(key)) {
            tree.get(key).add(address);
        } else {
            ArrayList<Address> list = new ArrayList<>();
            list.add(address);
            tree.put(key, list);
        }
    }
    
    public List<Address> getTree(GUID key) {
        return (List<Address>) tree.get(key);
    }
    
    public HashMap<GUID, ArrayList<Address>> getTree(){
        return tree;
    }

//    public GUID getGUID() {
//        return topicGUID;
//    }

//    public static TreeBranch createTreeBrach(String buffer) {
//        JsonParser parser = new JsonParser();
//        JsonElement elem = parser.parse(buffer);
//        TreeBranch tree = JSONMessage.fromJSON(elem, TreeBranch.class);
////        TreeBranch tree = (new GsonBuilder().create()).fromJson(buffer, TreeBranch.class);
//        return tree;
//    }

    @Override
    public OutputStream serialize(OutputStream stream) throws IOException {
        ByteArrayOutputStream bytearray = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bytearray);
        out.writeObject(tree);
        stream.write(bytearray.toByteArray());
        return stream;
    }

//    public PrintStream printMulticastTable(PrintStream ps) throws IOException {
//        for (Map.Entry<GUID, ArrayList<Address>> entry : tree.entrySet()) {
////            GUID dst = entry.getKey();
////            ArrayList<Address> addr = entry.getValue();
//            entry.getKey().print(ps).printf(" -> ");
//            for (Address a : entry.getValue()) {
//                a.print(ps).printf("; ");
//            }
//        }
//        return ps;
//    }

    @Override
    public PrintStream print(PrintStream ps) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

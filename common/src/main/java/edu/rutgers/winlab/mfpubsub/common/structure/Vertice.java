/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mfpubsub.common.structure;

import java.util.ArrayList;

/**
 *
 * @author zoe
 */
public class Vertice {

    private NA prev;
//    private ArrayList<Address> next;
    private final NA self;
    private int weight;

    public Vertice(NA self) {
        this.prev = self;
//        this.next = null;
        this.self = self;
        this.weight = Integer.MAX_VALUE;
    }

    public Vertice(NA self, int weight) {
        this.prev = self;
//        this.next = null;
        this.self = self;
        this.weight = weight;
    }

    public void setPrev(NA prev) {
        this.prev = prev;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

//    public void addNext(Address addr){
//        if(this.next == null){
//            this.next = new ArrayList<>();
//        }
//        this.next.add(addr);
//    }
    public NA getSelf() {
        return self;
    }

    public int getWeight() {
        return weight;
    }

    public NA getPrev() {
        return prev;
    }
}

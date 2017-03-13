package edu.rutgers.winlab.mfpubsub.common;

import java.util.concurrent.atomic.AtomicLong;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ubuntu
 * @param <E>
 */
public class FIFOEntry<E extends Comparable<? super E>>
        implements Comparable<FIFOEntry<E>> {

    private final static AtomicLong SEQ = new AtomicLong();
    private final long seqNum;
    private final E entry;

    public FIFOEntry(E entry) {
        seqNum = SEQ.getAndIncrement();
        this.entry = entry;
    }

    public E getEntry() {
        return entry;
    }

    @Override
    public int compareTo(FIFOEntry<E> other) {
        int res = entry.compareTo(other.entry);
        if (res == 0 && other.entry != this.entry) {
            res = (seqNum < other.seqNum ? -1 : 1);
        }
        return res;
    }
}

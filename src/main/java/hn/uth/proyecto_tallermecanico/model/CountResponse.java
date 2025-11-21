package hn.uth.proyecto_tallermecanico.model;

import java.io.Serializable;

public class CountResponse implements Serializable {
    private long count;

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
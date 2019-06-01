package com.deliverycircuit.ehtp.myapplication.model;

public class Stop {
    long idStop;
    String stopName;

    public Stop(long idStop, String stopName) {
        this.idStop = idStop;
        this.stopName = stopName;
    }

    public long getIdStop() {
        return idStop;
    }

    public void setIdStop(long idStop) {
        this.idStop = idStop;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }
}

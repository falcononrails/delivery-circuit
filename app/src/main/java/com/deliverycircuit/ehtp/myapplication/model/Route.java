package com.deliverycircuit.ehtp.myapplication.model;

public class Route {
    long idRoute;
    String routeName;
    String startStop;
    String endStop;

    public Route(int idRoute, String routeName, String startStop, String endStop) {
        this.idRoute = idRoute;
        this.routeName = routeName;
        this.startStop = startStop;
        this.endStop = endStop;
    }

    public long getIdRoute() {
        return idRoute;
    }

    public void setIdRoute(long idRoute) {
        this.idRoute = idRoute;
    }

    public String getNameRoute() {
        return routeName;
    }

    public void setNameRoute(String routeName) {
        this.routeName = routeName;
    }

    public String getStartStop() {
        return startStop;
    }

    public void setStartStop(String startStop) {
        this.startStop = startStop;
    }

    public String getEndStop() {
        return endStop;
    }

    public void setEndStop(String endStop) {
        this.endStop = endStop;
    }
}

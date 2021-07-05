package com.nikshcherbakov.vacanciesfinder.utils;

import java.util.List;

public class GoogleRouteRequest extends RouteRequest {

    private List<GoogleMapsRoute> routes;
    private String status;

    public GoogleRouteRequest(List<GoogleMapsRoute> routes, String status) {
        this.routes = routes;
        this.status = status;
        if (status.equals("OK")) {
            this.setTravelTime(routes.get(0).getLegs().get(0).getDurationInTraffic() == null ?
                    routes.get(0).getLegs().get(0).getDuration().getValue() / 60 :
                    routes.get(0).getLegs().get(0).getDurationInTraffic().getValue() / 60);
        } else {
            this.setTravelTime(-1);
        }
    }

    public List<GoogleMapsRoute> getRoutes() {
        return routes;
    }

    public void setRoutes(List<GoogleMapsRoute> routes) {
        this.routes = routes;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }


}

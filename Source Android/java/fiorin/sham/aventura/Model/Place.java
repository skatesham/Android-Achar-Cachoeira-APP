package fiorin.sham.aventura.Model;

/**
 * Created by SHAMVINICIUSFIORIN on 28/09/2017.
 */

public class Place {
    double lat;
    double lng;

    public Place() {
    }

    public Place(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}

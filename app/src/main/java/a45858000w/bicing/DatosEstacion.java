package a45858000w.bicing;

/**
 * Created by 45858000w on 31/01/17.
 */

public class DatosEstacion {

    private int id;
    private String streetName;
    private int streetNumber;
    private int bikes;
    private int slots;
    private String type;
    private String status;
    private long latitude;
    private String altitude;
    private long longitude;
    private String nearbyStations;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public int getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(int streetNumber) {
        this.streetNumber = streetNumber;
    }

    public int getBikes() {
        return bikes;
    }

    public void setBikes(int bikes) {
        this.bikes = bikes;
    }

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public String getNearbyStations() {
        return nearbyStations;
    }

    public void setNearbyStations(String nearbyStations) {
        this.nearbyStations = nearbyStations;
    }

    @Override
    public String toString() {
        return "DatosEstacion{" +
                "id=" + id +
                ", streetName='" + streetName + '\'' +
                ", streetNumber=" + streetNumber +
                ", bikes=" + bikes +
                ", slots=" + slots +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", latitude=" + latitude +
                ", altitude='" + altitude + '\'' +
                ", longitude=" + longitude +
                ", nearbyStations='" + nearbyStations + '\'' +
                '}';
    }
}

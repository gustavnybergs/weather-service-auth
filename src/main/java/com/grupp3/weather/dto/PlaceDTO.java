package com.grupp3.weather.dto;

public class PlaceDTO {
    private Long id;
    private String displayName;
    private double lat;
    private double lon;
    private boolean favorite;

    public PlaceDTO() {}

    public PlaceDTO(Long id, String displayName, double lat, double lon, boolean favorite) {
        this.id = id;
        this.displayName = displayName;
        this.lat = lat;
        this.lon = lon;
        this.favorite = favorite;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }

    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
}

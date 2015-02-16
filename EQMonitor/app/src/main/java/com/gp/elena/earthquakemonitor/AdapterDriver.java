package com.gp.elena.earthquakemonitor;


public class AdapterDriver {

    private String title;
    private Double magnitud;
    private String localization;
    private Long time;
    private Double longitude;
    private Double latitude;
    private Double depth;
    private int color;

    public AdapterDriver(String title, Double magnitud, String localization, Long time,
                         Double longitude, Double latitude, Double depth, int color){
        this.title = title;
        this.magnitud = magnitud;
        this.localization = localization;
        this.time = time;
        this.longitude = longitude;
        this.latitude = latitude;
        this.depth = depth;
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public Double getMagnitud() {
        return magnitud;
    }

    public String getLocalization() {
        return localization;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Long getTime() {
        return time;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getDepth() {
        return depth;
    }
}

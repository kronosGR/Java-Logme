package me.kandz.logme.Utils;

public class Logs {

    private int ID;
    private String title;
    private String details;
    private String dato;
    private String day;
    private String time;
    private Boolean picture;
    private Boolean Sound;
    private Boolean Video;
    private Boolean Location;

    public Logs(int ID, String title, String details, String dato, String day, String time, Boolean picture,
                Boolean sound, Boolean video, Boolean location) {
        this.ID = ID;
        this.title = title;
        this.details = details;
        this.dato = dato;
        this.day = day;
        this.time = time;
        this.picture = picture;
        Sound = sound;
        Video = video;
        Location = location;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDato() {
        return dato;
    }

    public void setDato(String dato) {
        this.dato = dato;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getPicture() {
        return picture;
    }

    public void setPicture(Boolean picture) {
        this.picture = picture;
    }

    public Boolean getSound() {
        return Sound;
    }

    public void setSound(Boolean sound) {
        Sound = sound;
    }

    public Boolean getVideo() {
        return Video;
    }

    public void setVideo(Boolean video) {
        Video = video;
    }

    public Boolean getLocation() {
        return Location;
    }

    public void setLocation(Boolean location) {
        Location = location;
    }
}

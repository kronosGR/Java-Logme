package me.kandz.logme.Utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Logs implements Parcelable {

    private int ID;
    private String title;
    private String details;
    private String dato;
    private String day;
    private String time;
    private Boolean image;
    private Boolean audio;
    private Boolean video;
    private Boolean location;

    public Logs(int ID, String title, String details, String dato, String day, String time, Boolean image,
                Boolean audio, Boolean video, Boolean location) {
        this.ID = ID;
        this.title = title;
        this.details = details;
        this.dato = dato;
        this.day = day;
        this.time = time;
        this.image = image;
        this.audio = audio;
        this.video = video;
        this.location = location;
    }

    public Logs(Parcel parcel) {
        ID = parcel.readInt();
        title = parcel.readString();
        details = parcel.readString();
        dato = parcel.readString();
        day = parcel.readString();
        time = parcel.readString();
        image = parcel.readString().equals("TRUE");
        audio = parcel.readString().equals("TRUE");
        video = parcel.readString().equals("TRUE");
        location = parcel.readString().equals("TRUE");
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

    public Boolean getImage() {
        return image;
    }

    public void setImage(Boolean image) {
        this.image = image;
    }

    public Boolean getAudio() {
        return audio;
    }

    public void setAudio(Boolean audio) {
        this.audio = audio;
    }

    public Boolean getVideo() {
        return video;
    }

    public void setVideo(Boolean video) {
        this.video = video;
    }

    public Boolean getLocation() {
        return location;
    }

    public void setLocation(Boolean location) {
        this.location = location;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ID);
        parcel.writeString(title);
        parcel.writeString(details);
        parcel.writeString(dato);
        parcel.writeString(day);
        parcel.writeString(time);
        parcel.writeString(image ? "TRUE" : "FALSE");
        parcel.writeString(audio ? "TRUE" : "FALSE");
        parcel.writeString(video ? "TRUE" : "FALSE");
        parcel.writeString(location ? "TRUE" : "FALSE");
    }

    public final static Parcelable.Creator<Logs> CREATOR = new Parcelable.Creator<Logs>(){

        @Override
        public Logs createFromParcel(Parcel parcel) {
            return new Logs(parcel);
        }

        @Override
        public Logs[] newArray(int i) {
            return new Logs[i];
        }
    };
}

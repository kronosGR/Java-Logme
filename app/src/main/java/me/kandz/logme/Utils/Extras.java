package me.kandz.logme.Utils;

public class Extras {

    private int logID;
    private int typeID;
    private String Url;  //Location type will be stored long/langtitude
    private String dato;
    private String time;

    public Extras(int logID, int typeID, String url, String dato, String time) {
        this.logID = logID;
        this.typeID = typeID;
        Url = url;
        this.dato = dato;
        this.time = time;
    }

    public int getLogID() {
        return logID;
    }

    public void setLogID(int logID) {
        this.logID = logID;
    }

    public int getTypeID() {
        return typeID;
    }

    public void setTypeID(int typeID) {
        this.typeID = typeID;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getDato() {
        return dato;
    }

    public void setDato(String dato) {
        this.dato = dato;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

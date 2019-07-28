package me.kandz.logme.Utils;

public class Type {
    private int ID;
    private String name;

    /**
     * Picture
     * Sound
     * Video
     * Location
     */

    public Type(int ID, String name) {
        this.ID = ID;
        this.name = name;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package com.gaurang.doctorover;

public class Group {
    String ID;
    String name;

    public void setName(String name) {
        this.name = name;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public Group(String ID, String name) {
        this.ID = ID;
        this.name = name;
    }
}

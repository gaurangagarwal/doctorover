package com.gaurang.doctorover;

public class PatientProfile {
    private String ID;
    private String name;
    private String gender;
    private String age;
    private String phone;
    private Group group;
    private String regNo;
    private String bedNo;
    private String roomNo;
    private String diag;
    private String consult;
    private String condition; // not in constructor

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    // Constructor
    public PatientProfile(String ID, String name, String gender, String age, String phone, Group group,
                          String regNo, String bedNo, String roomNo, String diag, String consult) {
        this.ID = ID;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.phone = phone;
        this.group = group;
        this.regNo = regNo;
        this.bedNo = bedNo;
        this.roomNo = roomNo;
        this.diag = diag;
        this.consult = consult;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public void setBedNo(String bedNo) {
        this.bedNo = bedNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public void setDiag(String diag) {
        this.diag = diag;
    }

    public void setConsult(String consult) {
        this.consult = consult;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getAge() {
        return age;
    }

    public String getPhone() {
        return phone;
    }

    public Group getGroup() {
        return group;
    }

    public String getRegNo() {
        return regNo;
    }

    public String getBedNo() {
        return bedNo;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public String getDiag() {
        return diag;
    }

    public String getConsult() {
        return consult;
    }
}



package com.gaurang.doctorover;

public class PatientObsv {
    private String ID;
    private String patientID;
    private String cond;
    private String operative;
    private String blood;
    private String radio;
    private String temp;
    private String pulse;
    private String resp;
    private String genExam;
    private String chest;
    private String abdomen;
    private String hernialsGenitals;
    private String advice;
    private String priorOrd;
    private String doctorName;
    private String dateTime;

    public String getOperative() {
        return operative;
    }

    public void setOperative(String operative) {
        this.operative = operative;
    }

    public PatientObsv(String ID, String patientID, String cond,String operative, String blood, String radio,
                       String temp, String pulse, String resp, String genExam, String chest,
                       String abdomen, String hernialsGenitals, String advice, String priorOrd,
                       String doctorName, String dateTime) {
        this.ID = ID;
        this.patientID = patientID;
        this.cond = cond;
        this.operative = operative;
        this.blood = blood;
        this.radio = radio;
        this.temp = temp;
        this.pulse = pulse;
        this.resp = resp;
        this.genExam = genExam;
        this.chest = chest;
        this.abdomen = abdomen;
        this.hernialsGenitals = hernialsGenitals;
        this.advice = advice;
        this.priorOrd = priorOrd;
        this.doctorName = doctorName;
        this.dateTime = dateTime;
    }



    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getCond() {
        return cond;
    }

    public void setCond(String cond) {
        this.cond = cond;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getRadio() {
        return radio;
    }

    public void setRadio(String radio) {
        this.radio = radio;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getPulse() {
        return pulse;
    }

    public void setPulse(String pulse) {
        this.pulse = pulse;
    }

    public String getResp() {
        return resp;
    }

    public void setResp(String resp) {
        this.resp = resp;
    }

    public String getGenExam() {
        return genExam;
    }

    public void setGenExam(String genExam) {
        this.genExam = genExam;
    }

    public String getChest() {
        return chest;
    }

    public void setChest(String chest) {
        this.chest = chest;
    }

    public String getAbdomen() {
        return abdomen;
    }

    public void setAbdomen(String abdomen) {
        this.abdomen = abdomen;
    }

    public String getHernialsGenitals() {
        return hernialsGenitals;
    }

    public void setHernialsGenitals(String hernialsGenitals) {
        this.hernialsGenitals = hernialsGenitals;
    }

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    public String getPriorOrd() {
        return priorOrd;
    }

    public void setPriorOrd(String priorOrd) {
        this.priorOrd = priorOrd;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

}

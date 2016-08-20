package com.gaurang.doctorover;

public class Doctor {
    private int _id;
    private String _name;
    private String _gender;
    private String _email;
    private String _phone;
    private String _pass;


    public Doctor(String _name, String _gender, String _email, String _phone, String _password) {
        this._name = _name;
        this._gender = _gender;
        this._email = _email;
        this._phone = _phone;
        this._pass = _password;
    }
    public Doctor(int _id, String _name, String _gender, String _email, String _phone, String _password) {
        this._id = _id;
        this._name = _name;
        this._gender = _gender;
        this._email = _email;
        this._phone = _phone;
        this._pass = _password;
    }

    public int get_id() {
        return _id;
    }
    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_name() {
        return _name;
    }
    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_gender() {
        return _gender;
    }
    public void set_gender(String _gender) {
        this._gender = _gender;
    }

    public String get_email() {
        return _email;
    }
    public void set_email(String _email) {
        this._email = _email;
    }

    public String get_phone() {
        return _phone;
    }
    public void set_phone(String _phone) {
        this._phone = _phone;
    }

    public String get_pass() {
        return _pass;
    }
    public void set_pass(String _password) {
        this._pass = _password;
    }



}

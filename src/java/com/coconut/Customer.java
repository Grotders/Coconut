/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coconut;

import java.util.List;

/**
 *
 * @author Grotders
 */
public class Customer {

    private int customerId;
    private String ad;
    private String soyad;
    private String telno;
    private String mail;
    private String bio;
    private String okul;
    private List<Kurslar> satinAlinanKurslar;
    
    
    public Customer() {
        
    }

    public Customer(int customerId, String ad, String soyad, String telno, String mail, String bio, String okul, List list) {
        this.customerId = customerId;
        this.ad = ad;
        this.soyad = soyad;
        this.telno = telno;
        this.mail = mail;
        this.bio = bio;
        this.okul = okul;
        this.satinAlinanKurslar = list;
    }
    
    

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getSoyad() {
        return soyad;
    }

    public void setSoyad(String soyad) {
        this.soyad = soyad;
    }

    public String getTelno() {
        return telno;
    }

    public void setTelno(String telno) {
        this.telno = telno;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getOkul() {
        return okul;
    }

    public void setOkul(String okul) {
        this.okul = okul;
    }
    
    
    public List<Kurslar> getSatinAlinanKurslar() {
        return satinAlinanKurslar;
    }

    public void setSatinAlinanKurslar(List<Kurslar> satinAlinanKurslar) {
        this.satinAlinanKurslar = satinAlinanKurslar;
    }
}

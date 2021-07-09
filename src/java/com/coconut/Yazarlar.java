/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coconut;

/**
 *
 * @author Grotders
 */
public class Yazarlar {
    private int yazar_id;
    private String adSoyad;
    private String job;
    private String bio;

    public Yazarlar() {}
    
    public Yazarlar(int yazar_id, String adSoyad, String job, String bio) {
        this.yazar_id = yazar_id;
        this.adSoyad = adSoyad;
        this.job = job;
        this.bio = bio;
    }

    public int getYazar_id() {
        return yazar_id;
    }

    public String getAdSoyad() {
        return adSoyad;
    }

    public String getJob() {
        return job;
    }

    public String getBio() {
        return bio;
    }
    
    
    
}

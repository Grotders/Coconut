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
public class Kurslar {
    private int kurs_id;
    private String baslik;
    private String kAciklama;
    private String kategori;
    private int dersSayisi;
    private String süre;
    private String intro;
    private String playlist;
    private Yazarlar yazar;
    
    public Kurslar() {
    }

    public Kurslar(int kurs_id, String baslik, String kAciklama, String kategori, int dersSayisi, String süre, String intro, String playlist, Yazarlar yazar) {
        this.kurs_id = kurs_id;
        this.baslik = baslik;
        this.kAciklama = kAciklama;
        this.kategori = kategori;
        this.dersSayisi = dersSayisi;
        this.süre = süre;
        this.intro = intro;
        this.playlist = playlist;
        this.yazar = yazar;
    }

    public int getKurs_id() {
        return kurs_id;
    }


    public String getBaslik() {
        return baslik;
    }

    public String getkAciklama() {
        return kAciklama;
    }

    public String getKategori() {
        return kategori;
    }

    public int getDersSayisi() {
        return dersSayisi;
    }

    public String getSüre() {
        return süre;
    }

    public String getIntro() {
        return intro;
    }

    public String getPlaylist() {
        return playlist;
    }

    public Yazarlar getYazar() {
        return yazar;
    }

    

    
    
    
}

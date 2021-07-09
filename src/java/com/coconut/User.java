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
public class User {
    private String username;
    private String password;
    private Customer customer;
    
    
    
    public User(String username, String password, Customer customer){
        this.username = username;
        this.password = password;
        this.customer = customer;
    }
    
    public User() {
        
    }
   
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}

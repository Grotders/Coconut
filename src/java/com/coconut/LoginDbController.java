/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coconut;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;

/**
 *
 * @author Grotders
 */
@ManagedBean
@RequestScoped
public class LoginDbController {

    DataSource dataSource;

    private User tempUser = new User();
    private Customer tempCustomer = new Customer();

    public LoginDbController() {
        try {
            Context ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("jdbc/addressbook");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    // Kullaniciyi Class User a yüklüyor.
    public User userGetir(String usr, String pw) throws SQLException {
        int cId = 0;
        if (dataSource == null) {
            throw new SQLException("Unable to obtain DataSource");
        }
        // obtain a connection from the connection pool
        Connection connection = dataSource.getConnection();

        // check whether connection was successful
        if (connection == null) {
            throw new SQLException("Unable to connect to DataSource");
        }

        try {
            // create a PreparedStatement to insert a new address book entry
            PreparedStatement ps = connection.prepareStatement("SELECT customer_id FROM APP.USERINFO "
                    + "WHERE username = ? AND password = ?");
            ps.setString(1, usr);
            ps.setString(2, pw);
            ResultSet result = ps.executeQuery();

            if (result.next()) {
                cId = result.getInt("customer_id");

            }

            // 1-) gelismis sql sorgu
            ps = connection.prepareStatement("SELECT * FROM APP.USERINFO INNER JOIN APP.CUSTOMERINFO "
                    + "ON USERINFO.CUSTOMER_ID = CUSTOMERINFO.CUSTOMER_ID "
                    + "WHERE USERINFO.customer_id = ? AND CUSTOMERINFO.customer_id = ?"
                    + "ORDER BY USERINFO.username");
            ps.setInt(1, cId);
            ps.setInt(2, cId);
            result = ps.executeQuery();

            if (result.next()) {
                String ad = result.getString("ad");
                String soyad = result.getString("soyad");
                String telno = result.getString("telno");
                String mail = result.getString("mail");
                String bio = result.getString("bio");
                String okul = result.getString("okul");

                tempCustomer = new Customer(cId, ad, soyad, telno, mail, bio, okul, satinAlinanlariBul(cId));
                tempUser = new User(usr, pw, tempCustomer);
            }
        } finally {
            connection.close(); // return this connection to pool
        }
        if (tempCustomer.getCustomerId() == 0) {
            return null;
        }

        return tempUser;
    }

    // Create new User
    public boolean userRegister(String usr, String pw, String mail) throws SQLException {
        int tempId = 0;
        // check whether dataSource was injected by the server
        if (dataSource == null) {
            throw new SQLException("Unable to obtain DataSource");
        }
        // obtain a connection from the connection pool
        Connection connection = dataSource.getConnection();
        // check whether connection was successful
        if (connection == null) {
            throw new SQLException("Unable to connect to DataSource");
        }
        try {
            // username and mail validation  // gelismis sql sorgusu 1
            PreparedStatement ps
                    = connection.prepareStatement("SELECT * FROM APP.USERINFO INNER JOIN APP.CUSTOMERINFO "
                            + "ON USERINFO.CUSTOMER_ID = CUSTOMERINFO.CUSTOMER_ID "
                            + "WHERE (USERINFO.username = ? OR CUSTOMERINFO.mail = ?)");
            ps.setString(1, usr);
            ps.setString(2, mail);
            ResultSet result = ps.executeQuery();

            if (result.next()) {
                String usr2 = result.getString("username");
                String mail2 = result.getString("mail");
                if (usr2.equals(usr) || mail2.equals(mail)) {
                    return false;
                }
            }
            //##################################################################
            // create new customer
            ps = connection.prepareStatement("INSERT INTO APP.CUSTOMERINFO "
                    + "(mail) VALUES (?)");
            ps.setString(1, mail);
            ps.executeUpdate();
            //##################################################################
            // get customer_id from new created customer
            ps = connection.prepareStatement("SELECT customer_id FROM APP.CUSTOMERINFO WHERE mail = ?");
            ps.setString(1, mail);
            result = ps.executeQuery();

            if (result.next()) {
                // until all the rows finish.
                tempId = result.getInt("customer_id");
            }
            //##################################################################
            // create new user
            ps = connection.prepareStatement("INSERT INTO APP.USERINFO (username, password, customer_id)"
                    + "VALUES (?, ?, ?)");
            ps.setString(1, usr);
            ps.setString(2, pw);
            ps.setInt(3, tempId);
            ps.executeUpdate();
        } // end try
        finally {
            connection.close(); // return this connection to pool
        } // end finally
        return true;
    }

    // UPDATE USER
    public boolean customerGuncelle(User inputUser) throws SQLException {
        Customer customer = inputUser.getCustomer();
        // check whether dataSource was injected by the server
        if (dataSource == null) {
            throw new SQLException("Unable to obtain DataSource");
        }
        // obtain a connection from the connection pool
        Connection connection = dataSource.getConnection();
        // check whether connection was successful
        if (connection == null) {
            throw new SQLException("Unable to connect to DataSource");
        }
        try {
            PreparedStatement ps
                    = connection.prepareStatement("SELECT * FROM APP.USERINFO INNER JOIN APP.CUSTOMERINFO "
                            + "ON USERINFO.CUSTOMER_ID = CUSTOMERINFO.CUSTOMER_ID "
                            + "WHERE NOT USERINFO.customer_id = ? AND  CUSTOMERINFO.mail = ?");
            ps.setInt(1, customer.getCustomerId());
            ps.setString(2, customer.getMail());
            ResultSet result = ps.executeQuery();

            
            if (result.next()) {
                String mail2 = result.getString("mail");
                if (mail2.equals(customer.getMail())) {
                    return false;
                }
            }
            
// UPDATE USER
            ps = connection.prepareStatement("UPDATE APP.CUSTOMERINFO "
                            + "SET ad = ?, soyad = ?, telno = ?, "
                            + "mail = ?, bio = ?, okul = ? "
                            + "WHERE customer_id = ?");
            ps.setString(1, customer.getAd());
            ps.setString(2, customer.getSoyad());
            ps.setString(3, customer.getTelno());
            ps.setString(4, customer.getMail());
            ps.setString(5, customer.getBio());
            ps.setString(6, customer.getOkul());
            ps.setInt(7, customer.getCustomerId());

            ps.executeUpdate();
            
            ps = connection.prepareStatement("UPDATE APP.USERINFO "
                            + "SET password = ?"
                            + "WHERE customer_id = ?");
            ps.setString(1, inputUser.getPassword());
            ps.setInt(2, customer.getCustomerId());

            ps.executeUpdate();
            
        } // end try
        finally {
            connection.close(); // return this connection to pool
        } // end finally
        return true;
    }

    public List tumKurslar() throws SQLException {
        List<Kurslar> tempList = new ArrayList<>();
        Kurslar tempKurs;

        if (dataSource == null) {
            throw new SQLException("Unable to obtain DataSource");
        }

        // obtain a connection from the connection pool
        Connection connection = dataSource.getConnection();

        // check whether connection was successful
        if (connection == null) {
            throw new SQLException("Unable to connect to DataSource");
        }

        try {
            // create a PreparedStatement to insert a new address book entry
            PreparedStatement ps
                    = connection.prepareStatement("select * from APP.KURSLAR");
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                {   // until all the rows finish.
                    int id = result.getInt("kurs_id");
                    int yId = result.getInt("yazar_id");
                    String baslik = result.getString("baslik");
                    String aciklama = result.getString("kAciklama");
                    String kategori = result.getString("kategori");
                    int dSayisi = result.getInt("dersSayisi");
                    String süre = result.getString("süre");
                    String intro = result.getString("intro");
                    String playlist = result.getString("playlist");

                    tempKurs = new Kurslar(id, baslik, aciklama, kategori, dSayisi, süre, intro, playlist, yazarBul(yId));
                    tempList.add(tempKurs);
                }
            }
        } // end try
        finally {
            connection.close(); // return this connection to pool
        } // end finally
        return tempList;
    }
  
    public ResultSet ayinBirincisi() throws SQLException{
        CachedRowSet resultSet1;
        if (dataSource == null) {
            throw new SQLException("Unable to obtain DataSource");
        }

        // obtain a connection from the connection pool
        Connection connection = dataSource.getConnection();

        // check whether connection was successful
        if (connection == null) {
            throw new SQLException("Unable to connect to DataSource");
        }
        
         try {
            // create a PreparedStatement to insert a new address book entry
            PreparedStatement ps
                    = connection.prepareStatement("select ad, soyad, siparis from APP.CUSTOMERINFO " +
                     "inner join " +
                     "(select customer_id, count(order_id) AS Siparis from APP.ORDERLIST " +
                     "group by customer_id) AS temp " +
                     "on temp.customer_id=CUSTOMERINFO.CUSTOMER_ID "
                            + "ORDER BY temp.siparis  desc");
            
             resultSet1 = new com.sun.rowset.CachedRowSetImpl();
             resultSet1.populate( ps.executeQuery() );
         }finally {
            connection.close(); // return this connection to pool
        } 
         return resultSet1;
    }
    
    public Kurslar kursBul(int id) throws SQLException {
        Kurslar tempKurs = new Kurslar();

        if (dataSource == null) {
            throw new SQLException("Unable to obtain DataSource");
        }

        // obtain a connection from the connection pool
        Connection connection = dataSource.getConnection();

        // check whether connection was successful
        if (connection == null) {
            throw new SQLException("Unable to connect to DataSource");
        }

        try {
            // create a PreparedStatement to insert a new address book entry
            PreparedStatement ps
                    = connection.prepareStatement("select * from APP.KURSLAR where kurs_id = ?");
            ps.setInt(1, id);
            ResultSet result = ps.executeQuery();

            if (result.next()) {
                {   // until all the rows finish.
                    id = result.getInt("kurs_id");
                    int yId = result.getInt("yazar_id");
                    String baslik = result.getString("baslik");
                    String aciklama = result.getString("kAciklama");
                    String kategori = result.getString("kategori");
                    int dSayisi = result.getInt("dersSayisi");
                    String süre = result.getString("süre");
                    String intro = result.getString("intro");
                    String playlist = result.getString("playlist");

                    tempKurs = new Kurslar(id, baslik, aciklama, kategori, dSayisi, süre, intro, playlist, yazarBul(yId));

                }
            }

        } // end try
        finally {
            connection.close(); // return this connection to pool
        } // end finally
        if (tempKurs.getKurs_id() != id) {
            return null;
        }
        return tempKurs;
    }

    /**
     *
     * @param cId customer id
     * @param kId kurslar id
     */
    public void kursSatinAl(int cId, int kId) throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Unable to obtain DataSource");
        }

        // obtain a connection from the connection pool
        Connection connection = dataSource.getConnection();

        // check whether connection was successful
        if (connection == null) {
            throw new SQLException("Unable to connect to DataSource");
        }

        try {
            // create a PreparedStatement to insert a new address book entry
            PreparedStatement ps
                    = connection.prepareStatement("INSERT INTO APP.ORDERLIST "
                            + "(customer_id, kurs_id)"
                            + "VALUES (?, ?)");
            ps.setInt(1, cId);
            ps.setInt(2, kId);

            ps.executeUpdate();

        } finally {
            connection.close(); // return this connection to pool
        }
    }

    public List satinAlinanlariBul(int id) throws SQLException {
        List<Kurslar> liste = new ArrayList<>();
        Kurslar tempKurs = new Kurslar();

        if (dataSource == null) {
            throw new SQLException("Unable to obtain DataSource");
        }

        // obtain a connection from the connection pool
        Connection connection = dataSource.getConnection();

        // check whether connection was successful
        if (connection == null) {
            throw new SQLException("Unable to connect to DataSource");
        }

        try {
            // create a PreparedStatement to insert a new address book entry
            PreparedStatement ps
                    = connection.prepareStatement("select kurs_id from APP.ORDERLIST where customer_id = ?");
            ps.setInt(1, id);
            ResultSet result = ps.executeQuery();

            while (result.next()) {

                int temp = result.getInt("kurs_id");

                tempKurs = kursBul(temp);

                liste.add(tempKurs);
            }
        } finally {
            connection.close(); // return this connection to pool
        }
        return liste;
    }

    // hesap silinmesi
    public void hesabiSiler(int id) throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Unable to obtain DataSource");
        }

        // obtain a connection from the connection pool
        Connection connection = dataSource.getConnection();

        // check whether connection was successful
        if (connection == null) {
            throw new SQLException("Unable to connect to DataSource");
        }

        try {
            // Delete customer login infos (USERINFO)
            PreparedStatement ps
                    = connection.prepareStatement("delete from APP.USERINFO where customer_id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();

            // Delete customer infos (CUSTOMERINFO)
            ps = connection.prepareStatement("delete from APP.CUSTOMERINFO where customer_id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();

            // delete customer orders (ORDERLIST)
            ps = connection.prepareStatement("delete from APP.ORDERLIST where customer_id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } finally {
            connection.close(); // return this connection to pool
        }
    }

    public Yazarlar yazarBul(int id) throws SQLException {
        Yazarlar tempYazar = new Yazarlar();

        if (dataSource == null) {
            throw new SQLException("Unable to obtain DataSource");
        }

        // obtain a connection from the connection pool
        Connection connection = dataSource.getConnection();

        // check whether connection was successful
        if (connection == null) {
            throw new SQLException("Unable to connect to DataSource");
        }

        try {
            // create a PreparedStatement to insert a new address book entry
            PreparedStatement ps
                    = connection.prepareStatement("select * from APP.YAZARLAR where yazar_id = ?");
            ps.setInt(1, id);
            ResultSet result = ps.executeQuery();

            if (result.next()) {
                // until all the rows finish.
                id = result.getInt("yazar_id");
                String adSoyad = result.getString("adSoyad");
                String job = result.getString("job");
                String bio = result.getString("bio");

                tempYazar = new Yazarlar(id, adSoyad, job, bio);
            }

        } // end try
        finally {
            connection.close(); // return this connection to pool
        } // end finally

        return tempYazar;
    }

}

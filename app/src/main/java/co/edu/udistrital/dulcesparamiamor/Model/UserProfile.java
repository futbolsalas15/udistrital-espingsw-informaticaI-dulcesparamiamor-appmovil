package co.edu.udistrital.dulcesparamiamor.model;

/**
 * Created by Jeison on 23/03/2016.
 */
public class UserProfile {

public String name;
public String email;
public String password;
public String lovename;
public String lovephone;
public String loveemail;
public String lovefacebook;

    public UserProfile(String name, String email, String password, String lovename, String lovephone, String loveemail, String lovefacebook) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.lovename = lovename;
        this.lovephone = lovephone;
        this.loveemail = loveemail;
        this.lovefacebook = lovefacebook;
    }
}

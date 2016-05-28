package co.edu.udistrital.dulcesparamiamor.presenter.messages;

/**
 * Created by futbo on 15/05/2016.
 */
public class MessageContent {

    private String email;
    private String phone;
    private String textOfMsg;
    private String fbusername;

    public String getFbusername() {
        return fbusername;
    }

    public void setFbusername(String fbusername) {
        this.fbusername = fbusername;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTextOfMsg() {
        return textOfMsg;
    }

    public void setTextOfMsg(String textOfMsg) {
        this.textOfMsg = textOfMsg;
    }
}

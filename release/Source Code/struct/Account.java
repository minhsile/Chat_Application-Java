package struct;

/**
 * struct
 * Created by Minh Sĩ Lê
 * Date 12/22/2021 - 7:32 PM
 * Description: ...
 */
public class Account {
    private String userName;
    private String pw;
    private String name;

    public Account(String userName, String pw, String name) {
        this.userName = userName;
        this.pw = pw;
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

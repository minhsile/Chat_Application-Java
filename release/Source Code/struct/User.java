package struct;

import java.util.UUID;

/**
 * server
 * Created by Minh Sĩ Lê
 * Date 12/15/2021 - 12:02 PM
 * Description: Information of User
 */
public class User {
    private String name;
    private String host;
    private int port;
    private String uid;

    public User(String name, String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
        uid = UUID.randomUUID().toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}

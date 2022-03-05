package struct;

import java.io.Serializable;

/**
 * client
 * Created by Minh Sĩ Lê
 * Date 12/17/2021 - 8:56 PM
 * Description: ...
 */
public class DataFile implements Serializable {
    public byte[] data;

    public DataFile() {
        data = new byte[1024000];
    }

    public DataFile(int size) {
        data = new byte[size];
    }
}

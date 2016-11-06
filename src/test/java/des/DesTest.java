package des;


import algorithms.CipherInterface;
import algorithms.des.DesCipherImpl;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class DesTest {

    @Test
    public void DesTest() {
        CipherInterface des = new DesCipherImpl("test");
        String textToEncrypt = "DesTEsst";
        String encrypted = des.encrypt(textToEncrypt);
        assertEquals(encrypted, des.decrypt(encrypted));
    }
}

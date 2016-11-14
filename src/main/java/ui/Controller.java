package ui;


import algorithms.CipherInterface;
import algorithms.des.DesCipherImpl;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;

public class Controller {
    @FXML
    private TextArea des_text_area;

    @FXML
    private TextField des_key;

    @FXML
    private TextField des_text;

    private String key;


    @FXML
    void encryptDes() throws Exception {
        validateInputs();
        String textToEncrypt = des_text.getText();
        key = des_key.getText();
        CipherInterface des = new DesCipherImpl(key);
        String encryptedText = des.encrypt(textToEncrypt);
        des_text_area.setText(encryptedText);
    }

    @FXML
    void decryptDes() throws Exception {
        if (validateInputs(des_key, des_text)) {
            String textToDecrypt = des_text.getText();
            key = des_key.getText();
            CipherInterface des = new DesCipherImpl(key);
            String decryptedText = des.decrypt(textToDecrypt);
            des_text_area.setText(decryptedText);
        }
    }

    // some validation of inputs (check for emptiness)
    private boolean validateInputs(TextField ... fields) {
        boolean flag = false;
        for (TextField fieldToCheck : fields) {
            if (StringUtils.isEmpty(fieldToCheck.getText())) {
                fieldToCheck.getStyleClass().add("error");
                flag = true;
            } else {
                fieldToCheck.getStyleClass().removeAll("error");
            }
        }
        return flag;
    }
}

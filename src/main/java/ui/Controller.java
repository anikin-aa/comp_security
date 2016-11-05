package ui;


import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

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
    }

    @FXML
    void decryptDes() throws Exception {
        validateInputs();
        String textToDecrypt = des_text.getText();
        key = des_key.getText();
    }

    // some validation of inputs (check for emptiness, etc.)
    private void validateInputs() {

    }
}

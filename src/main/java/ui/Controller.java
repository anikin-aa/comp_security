package ui;


import algorithms.CipherInterface;
import algorithms.des.DesCipherImpl;
import algorithms.knapsackproblem.KnapsackProblemImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;

public class Controller {
    private String key;
    private String textToEncrypt;
    private String textToDecrypt;

    @FXML
    private TextArea des_text_area;

    @FXML
    private TextField des_key;

    @FXML
    private TextField des_text;

    @FXML
    private TextArea knapsackArea;

    @FXML
    private TextField knapsackKey;

    @FXML
    private TextField knapsackText;

    @FXML
    void encryptDes() throws Exception {
        if (validateInputs()) {
            textToEncrypt = des_text.getText();
            key = des_key.getText();
            CipherInterface des = new DesCipherImpl(key);
            String encryptedText = des.encrypt(textToEncrypt);
            des_text_area.setText(encryptedText);
        }
    }

    @FXML
    void decryptDes() throws Exception {
        if (validateInputs(des_key, des_text)) {
            textToDecrypt = des_text.getText();
            key = des_key.getText();
            CipherInterface des = new DesCipherImpl(key);
            String decryptedText = des.decrypt(textToDecrypt);
            des_text_area.setText(decryptedText);
        }
    }


    @FXML
    void encryptKnapsack() {
        if (validateInputs(knapsackKey, knapsackText)) {
            key = knapsackKey.getText();
            CipherInterface knapsackProblem = new KnapsackProblemImpl(key);
            textToEncrypt = knapsackText.getText();
            try {
                String res = knapsackProblem.encrypt(textToEncrypt);
                System.out.println(res);
                knapsackArea.setText(res);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Something went wrong!");
                alert.setHeaderText(null);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    void decryptKnapsack() {

    }

    // some validation of inputs (check for emptiness)
    private boolean validateInputs(TextField... fields) {
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

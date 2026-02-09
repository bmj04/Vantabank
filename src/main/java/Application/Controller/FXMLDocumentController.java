package Application.Controller;

import Application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLDocumentController implements Initializable {
    @FXML
    private Text typeOperation;
    @FXML
    private Button depot;
    @FXML
    private Button retrait;
    @FXML
    private Button deconnect;
    @FXML
    private Button profilePage;
    @FXML
    private Button transfert;
    @FXML
    private TextField transfertInput;
    @FXML
    private Text transfertPrompt;
    @FXML
    private Button submitOperation;
    @FXML
    private TextField montantField;
    @FXML
    private Button submitFacture;

    @FXML
    void inputMontant(KeyEvent event) {
    }



    @FXML
    void displayDepot(ActionEvent event){
        transfertInput.setVisible(false);
        transfertPrompt.setVisible(false);
        typeOperation.setText("DÉPÔT");
    }

    @FXML
    void displayRetrait(ActionEvent event){
        transfertInput.setVisible(false);
        transfertPrompt.setVisible(false);
        submitFacture.setVisible(false);
        typeOperation.setText("RETRAIT");
    }


    @FXML
    void displayTransfert(ActionEvent event){
        transfertInput.setVisible(true);
        transfertPrompt.setVisible(true);
        submitFacture.setVisible(false);
        typeOperation.setText("TRANSFERT");
    }

    @FXML
    void displayprofilePage(ActionEvent event){
        transfertInput.setVisible(false);
        transfertPrompt.setVisible(false);
        submitFacture.setVisible(false);
        typeOperation.setText("PROFILE");
    }
    @FXML
    void activerOperation(ActionEvent event){
        System.out.println(montantField.getText());
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

}

package Application.Controller;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateController implements Initializable {


    @FXML
    private Pane accountOverviewClient;

    @FXML
    private Pane caseExtra;

    @FXML
    private Text date;

    @FXML
    private Text extraInfo;

    @FXML
    private Text montant;

    @FXML
    private Text montant1;

    @FXML
    private Text montant11;

    @FXML
    private Text montantDépotRégulier;

    @FXML
    private Text montantRetrait;

    @FXML
    private Text montantTransfert;

    @FXML
    private TextField nipValue;

    @FXML
    private Text numeroCompte;

    @FXML
    private Pane paneInfoExtra;

    @FXML
    private Button retour;

    @FXML
    private TextField retraitMontant;

    @FXML
    private BorderPane rightBorderPane;

    @FXML
    private Button submit;

    @FXML
    private TextField transfertMontant;

    @FXML
    private Text typeAttribut;

    @FXML
    private Text typeCompte;

    @FXML
    private Text typeCompte1;

    @FXML
    private Text typeCompte11;

    @FXML
    private ComboBox<String> typeCompteValue;

    @FXML
    private Text valeurInfo;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        typeCompteValue.getItems().addAll("CHEQUE", "EPARGNE", "HYPOTHECAIRE","MARGE");
        typeCompteValue.setValue("CHEQUE");
    }
}

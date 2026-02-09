package Application.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class AdminController {

    @FXML
    private Button creerCompte;

    @FXML
    private Text date;

    @FXML
    private Button deconnect;

    @FXML
    private Button gererCompte;

    @FXML
    private GridPane gestion;

    @FXML
    private AnchorPane holder;

    @FXML
    private Text info1;

    @FXML
    private Text info2;

    @FXML
    private Pane leftPaneClient;

    @FXML
    private Text montant;

    @FXML
    private Text nip;

    @FXML
    private Text noCompte;

    @FXML
    private Text noText;

    @FXML
    private Button payerHypo;

    @FXML
    private Button payerInteret;

    @FXML
    private TextField rechercheCompteField;

    @FXML
    private TextField rechercheTransactionField;

    @FXML
    private BorderPane rightBorderPane;

    @FXML
    private Button supprimer;

    @FXML
    private ScrollPane transactions;

    @FXML
    private Text type;

    @FXML
    private Text value1;

    @FXML
    private Text value2;

    @FXML
    private Button verouiller;

    @FXML
    private Text welcomeMessage;

}

package Application;

import Application.Controller.FXMLDocumentController;
import Application.Model.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import javafx.stage.StageStyle;
import javafx.scene.layout.AnchorPane;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Objects;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Application extends javafx.application.Application {

    private Stage primaryStage;
    private int tries;
    private Client lastUser;
    private GestionnaireGuichet gestionnaireGuichet;

    @Override
    public void start(Stage primaryStage) throws IOException {

        this.primaryStage = primaryStage;
        this.tries = 0;
        this.lastUser = null;

        //VALEURS INITIALS POUR LES TESTS

        //Creation Banque, clients et admin

        Banque banque = new Banque(0,0,19000.0,500,500);
        Client client1 = new Client(1,"Benkarrouch","Ali","(514) 975-5229","alibenkarrouch@gmail.com",1234);
        Client client2 = new Client(2,"Kadum","Zaid","(514) 523-1114","thomasdupois@gmail.com",4321);
        Client admin = new Client(3,"Ad","Min","(514) 111-0000","admin@gmail.com",1001);
        admin.setAdmin();

        //Creation comptes

        Cheque cheque1 = new Cheque(client1,1234,1,357.11,175.00,600.00,100.00);
        Cheque cheque2 = new Cheque(client1,1234,5,521.47,100.00,750.00,100.00);
        Cheque cheque3 = new Cheque(client2,1234,6,250.07,120.00,250.00,120.00);
        Epargne epargne1 = new Epargne(client1,1234,2,3508.47,900.00,1000.00);
        Hypothecaire hypothecaire1 = new Hypothecaire(client1,1234,3,65344.03,500.00,1375.00);
        Marge marge = new Marge(client1,1234,4,10.05,250.00,100.00);

        //Ajout des comptes pour chaque client
        client1.ajouterCompte(cheque1);
        client1.ajouterCompte(epargne1);
        client1.ajouterCompte(hypothecaire1);
        client1.ajouterCompte(marge);
        client1.ajouterCompte(cheque2);
        client2.ajouterCompte(cheque3);

        //Creation gestionnaireGuichet
        gestionnaireGuichet = new GestionnaireGuichet(banque);
        gestionnaireGuichet.getClients().add(client1);
        gestionnaireGuichet.getClients().add(client2);
        gestionnaireGuichet.getClients().add(admin);

        //Ajout des comptes
        gestionnaireGuichet.getComptesGlobales().add(cheque1);
        gestionnaireGuichet.getComptesGlobales().add(epargne1);
        gestionnaireGuichet.getComptesGlobales().add(hypothecaire1);
        gestionnaireGuichet.getComptesGlobales().add(marge);
        gestionnaireGuichet.getComptesGlobales().add(cheque2);
        gestionnaireGuichet.getComptesGlobales().add(cheque3);

        //Ajout des transactions
        gestionnaireGuichet.creeTransaction(10.5,cheque1,"DÉPÔT");
        gestionnaireGuichet.creeTransaction(125.34,epargne1,"DÉPÔT");
        gestionnaireGuichet.creeTransaction(30.0,cheque3,"RETRAIT");
        gestionnaireGuichet.creeTransaction(28.0,cheque2,"DÉPÔT");
        gestionnaireGuichet.creeTransaction(28.0,cheque2,"TRANSFERT",cheque1);
        primaryStage.initStyle(StageStyle.DECORATED);

        loadLoginView();


    }

    /**
     * Affiche la scène de connexion.
     */
    private void loadLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Application.class.getResource("com/example/javafx_tests/login_view.fxml"));
            loader.setController(new FXMLDocumentController());
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/javafx_tests/login_view.fxml")));

            Scene scene = new Scene(root);
            primaryStage.setTitle("Vanta Bank");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(500);
            primaryStage.show();
            tries = 0;


            ImageView lock = (ImageView) root.lookup("#imageLock");
            lock.setImage(new Image(new FileInputStream("src/main/java/Application/lock.png")));
            //Logique

            Button submit = (Button) root.lookup("#enterButton");
            submit.setOnAction(event -> {

                TextField noCompteField = (TextField) root.lookup("#noclientField");
                PasswordField nipField = (PasswordField ) root.lookup("#nipfield");
                int valeurNoCompte = Integer.parseInt(noCompteField.getText());
                int valeurNip = Integer.parseInt(nipField.getText());
                Text erreur = (Text) root.lookup("#errorMes");

                boolean isUser = gestionnaireGuichet.validerUtilisateur(valeurNoCompte);

                if (isUser) {
                    Client tempClient = null;
                    for (int i = 0; i<gestionnaireGuichet.getClients().size();i++){
                        if (gestionnaireGuichet.getClients().get(i).getCodeClient() == valeurNoCompte){
                            tempClient = gestionnaireGuichet.getClients().get(i);
                            break;
                        }
                    }
                    if (lastUser!=null && !lastUser.equals(tempClient)){
                        tries = 0;
                    }
                    lastUser = tempClient;
                    if (tries>3 && !tempClient.getBloquer()) {
                        erreur.setText("Mot de passe invalide, compte verouiller, veuillez contacter un admin.");
                        tempClient.setBloquer(true);
                    }
                    else if (valeurNip != tempClient.getNumeroNIP() && !tempClient.getBloquer()){
                        erreur.setVisible(true);
                        erreur.setText("Mot de passe invalide, "+(3-tries)+" essaies restants");
                        tries++;
                    }
                    else if (tempClient.getBloquer()){
                        erreur.setVisible(true);
                        erreur.setText("Compte verouillé, veuillez contacter un admin.");
                    }
                    else {
                        gestionnaireGuichet.setClientCourant(tempClient);

                        if (!gestionnaireGuichet.getClientCourant().getAdmin()) {
                            if (!gestionnaireGuichet.getClientCourant().getComptes().isEmpty()){
                                gestionnaireGuichet.setCompteCourant(gestionnaireGuichet.getClientCourant().getComptes().getFirst());
                            }
                            loadMainView();
                        }
                        else{
                            loadAdminView();
                        }
                    }

                }
                else {
                    erreur.setVisible(true);
                    erreur.setText("Numéro de client invalide.");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche la scène principal.
     */
    public void loadMainView() {
        try {
            //Initialization
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Application.class.getResource("com/example/javafx_tests/main_view.fxml"));
            loader.setController(new FXMLDocumentController());
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/javafx_tests/main_view.fxml")));

            //Configuration du Stage

            //Ajout du Nom
            Text bienvenue = (Text) root.lookup("#welcomeMessage");
            bienvenue.setText("BIENVENUE "+gestionnaireGuichet.getClientCourant().getPrenom().toUpperCase());

            //Ajout de la date
            Text dateText = (Text) root.lookup("#date");
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDateTime = currentDateTime.format(formatter);
            dateText.setText(formattedDateTime);

            //Ajout des informations du compte courant
            if (!gestionnaireGuichet.getClientCourant().getComptes().isEmpty()) {
                mettreAJourInformationsCompteCourant(gestionnaireGuichet, root);
            }
            ScrollPane comptesHolder = (ScrollPane) root.lookup("#comptes");
            AnchorPane holder = (AnchorPane) comptesHolder.getContent();
            updateListeComptes(gestionnaireGuichet,root,holder);

            //Parametre de la scène
            Scene scene = new Scene(root);
            primaryStage.setTitle("Vanta Bank");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(500);
            primaryStage.show();

            //Configurations des bouttons

            Button deconnexion = (Button) root.lookup("#deconnect");
            deconnexion.setOnAction(event -> loadLoginView());
            GridPane buttonsPane = (GridPane) root.lookup("#buttonsPane");

            Button depot = (Button) buttonsPane.getChildren().get(0);
            depot.setOnAction(event -> {
                Button submitFacture = (Button) root.lookup("#submitFacture");
                if (gestionnaireGuichet.getCompteCourant() instanceof Cheque){
                    submitFacture.setVisible(true);
                }else{
                    submitFacture.setVisible(false);
                }
                Text operation = (Text) root.lookup("#typeOperation");
                operation.setText("DÉPÔT");
            });
            Button profil = (Button) buttonsPane.getChildren().get(2);
            profil.setOnAction(event -> loadProfilView());

            Button payerFacture = (Button) root.lookup("#submitFacture");
            payerFacture.setOnAction(event -> {
               if (gestionnaireGuichet.getCompteCourant().getClass().getSimpleName().equals("Cheque")){
                   TextField fieldMontant = (TextField) root.lookup("#montantField");
                   Text erreur2 = (Text) root.lookup("#errorMessage2");
                   String montant = fieldMontant.getText();
                   double montantDouble = 0;
                   boolean valid = true;
                   try{
                       montantDouble = Double.parseDouble(montant);
                   }catch (Exception e){
                       valid=false;
                       erreur2.setText("Valeur invalide.");
                       erreur2.setVisible(true);
                   }
                   if(valid) {
                       boolean paiement = ((Cheque) gestionnaireGuichet.getCompteCourant()).paiementFacture(montantDouble);
                       if (!paiement) {
                           erreur2.setText("Le montant excède la limite.");
                           erreur2.setVisible(true);
                       }
                   }
               }
                mettreAJourInformationsCompteCourant(gestionnaireGuichet,root);
                try {
                    updateListeComptes(gestionnaireGuichet,root,holder);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });

            Button submit = (Button) root.lookup("#submitOperation");
            submit.setOnAction(event -> {
                TextField fieldMontant = (TextField) root.lookup("#montantField");
                Text operation = (Text) root.lookup("#typeOperation");
                String montant = fieldMontant.getText();
                String typeOperation = operation.getText();
                Text erreur2 = (Text) root.lookup("#errorMessage2");
                erreur2.setVisible(false);
                Text erreur = (Text) root.lookup("#errorMessage");
                TextField noCompteDestinataire = (TextField) root.lookup("#transfertInput");
                String noCompteDest = noCompteDestinataire.getText();
                double montantDouble = 0;
                boolean valid = true;
                try{
                    montantDouble = Double.parseDouble(montant);
                }catch (NumberFormatException e){
                    valid =false;
                    erreur.setText("Valeur invalide.");
                    erreur.setVisible(true);
                }
                if (valid){
                    switch (typeOperation) {
                        case "DÉPÔT":
                            gestionnaireGuichet.getCompteCourant().depot(montantDouble);
                            gestionnaireGuichet.getBanque().depot(montantDouble);
                            gestionnaireGuichet.creeTransaction(montantDouble, gestionnaireGuichet.getCompteCourant(), "DÉPÔT");
                            erreur.setVisible(false);
                            break;
                        case "RETRAIT":
                            if (!gestionnaireGuichet.multiple10Check(montantDouble)) {
                                erreur.setText("Erreur, le montant doit etre un multiple de 10.");
                                erreur.setVisible(true);
                                return;
                            }
                            if (gestionnaireGuichet.getBanque().getSolde() > montantDouble && gestionnaireGuichet.getCompteCourant().getRetraitMax() >= montantDouble) {
                                boolean retraitMarge = gestionnaireGuichet.getCompteCourant().retrait(montantDouble);
                                gestionnaireGuichet.getBanque().retrait(montantDouble);
                                if (retraitMarge) {
                                    erreur.setText(montantDouble + "$ ont été débité de votre compte marge.");
                                    erreur.setVisible(true);
                                } else {
                                    erreur.setVisible(false);
                                }
                                gestionnaireGuichet.creeTransaction(montantDouble, gestionnaireGuichet.getCompteCourant(), "RETRAIT");
                            } else {
                                if (gestionnaireGuichet.getCompteCourant().getRetraitMax() < montantDouble) {
                                    erreur.setText("Erreur, le montant excéde la limite de retrait.");
                                } else {
                                    erreur.setText("Erreur, solde du GAB est insuffisant.");
                                }

                                erreur.setVisible(true);
                            }
                            break;
                        case "TRANSFERT":

                            if (noCompteDestinataire.isVisible() && montantDouble <= gestionnaireGuichet.getCompteCourant().getMontantTransfertMaximum() && !noCompteDestinataire.getText().isEmpty()) {
                                int intNoCompte = Integer.parseInt(noCompteDest);
                                boolean comptePresent = false;
                                for (int i = 0; i < gestionnaireGuichet.getComptesGlobales().size(); i++) {
                                    if (gestionnaireGuichet.getComptesGlobales().get(i).getNumeroCompte() == intNoCompte) {
                                        gestionnaireGuichet.getCompteCourant().retrait(montantDouble);
                                        gestionnaireGuichet.getComptesGlobales().get(i).depot(montantDouble);
                                        gestionnaireGuichet.creeTransaction(montantDouble, gestionnaireGuichet.getCompteCourant(), "TRANSFERT", gestionnaireGuichet.getComptesGlobales().get(i));
                                        erreur.setVisible(false);
                                        comptePresent = true;
                                        break;
                                    }
                                }
                                if (!comptePresent) {
                                    erreur.setText("Erreur, le numéro de compte est invalide.");
                                    erreur.setVisible(true);
                                } else {
                                    erreur.setVisible(false);
                                }
                            } else {
                                if (noCompteDestinataire.getText().isEmpty()) {
                                    erreur.setText("Erreur, aucun numéro de compte a été saisit.");
                                } else {
                                    erreur.setText("Erreur,le montant excéde la limite de transfert.");
                                }
                                erreur.setVisible(true);
                            }
                            break;
                    }

                }
                mettreAJourInformationsCompteCourant(gestionnaireGuichet,root);
                try {
                    updateListeComptes(gestionnaireGuichet,root,holder);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Affiche la scène du profil du client.
     */
    private void loadProfilView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Application.class.getResource("com/example/javafx_tests/profil_view.fxml"));
            loader.setController(new FXMLDocumentController());
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/javafx_tests/profil_view.fxml")));

            Scene scene = new Scene(root);
            primaryStage.setTitle("Vanta Bank");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(500);
            primaryStage.show();
            tries = 0;

            Text dateText = (Text) root.lookup("#date");
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDateTime = currentDateTime.format(formatter);
            dateText.setText(formattedDateTime);

            //Logique
            Button retour = (Button) root.lookup("#retour");
            retour.setOnAction(event -> loadMainView()
            );
            ScrollPane transactionsScroll = (ScrollPane) root.lookup("#comptes");
            AnchorPane transactionHolder = (AnchorPane) transactionsScroll.getContent();

            updateTransactionClient(gestionnaireGuichet,transactionHolder,gestionnaireGuichet.getClientCourant());

            Text nom = (Text) root.lookup("#nom");
            Text prenom = (Text) root.lookup("#prenom");
            Text courriel = (Text) root.lookup("#courriel");
            Text telephone = (Text) root.lookup("#telephone");
            Text noClient = (Text) root.lookup("#noClient");
            Text nip = (Text) root.lookup("#nip");

            Text nbCompte = (Text) root.lookup("#nbCompte");
            Text soldeGlobal = (Text) root.lookup("#soldeGlobal");
            Text margeGlobal = (Text) root.lookup("#margeGlobal");

            nom.setText(gestionnaireGuichet.getClientCourant().getNom().toUpperCase());
            prenom.setText(gestionnaireGuichet.getClientCourant().getPrenom().toUpperCase());
            courriel.setText(gestionnaireGuichet.getClientCourant().getCourriel());
            telephone.setText(gestionnaireGuichet.getClientCourant().getTelephone());
            noClient.setText(String.valueOf(gestionnaireGuichet.getClientCourant().getCodeClient()));
            nip.setText(createMaskedString(String.valueOf(gestionnaireGuichet.getClientCourant().getNumeroNIP()).length()));
            nbCompte.setText(gestionnaireGuichet.getNbCompte()+" compte(s)");
            soldeGlobal.setText(gestionnaireGuichet.getSoldeGlobal()+"$");
            margeGlobal.setText(gestionnaireGuichet.getMargeGlobal()+"$");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche la scène pour admin.
     */
    private void loadAdminView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Application.class.getResource("com/example/javafx_tests/admin_view.fxml"));
            loader.setController(new FXMLDocumentController());
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/javafx_tests/admin_view.fxml")));

            Scene scene = new Scene(root);
            primaryStage.setTitle("Vanta Bank");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(500);
            primaryStage.show();

            Text dateText = (Text) root.lookup("#date");
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDateTime = currentDateTime.format(formatter);
            dateText.setText(formattedDateTime);

            //Logique
            Button deconnexion = (Button) root.lookup("#deconnect");
            deconnexion.setOnAction(event -> loadLoginView());

            TextField searchField = (TextField) root.lookup("#rechercheField");

            Button gererClient = (Button) root.lookup("#gererClient");
            Button creerClient = (Button) root.lookup("#creerCompte");
            Button gererCompte = (Button) root.lookup("#gererCompte");
            Button remplirGuichet = (Button) root.lookup("#remplirGuichet");
            Button compGlobal = (Button) root.lookup("#payerInteret");
            Button fermerGuichet = (Button) root.lookup("#fermerGuichet");

            ScrollPane clientHolder = (ScrollPane) root.lookup("#clients");
            AnchorPane holder = (AnchorPane) clientHolder.getContent();

            ScrollPane transactionsScroll = (ScrollPane) root.lookup("#transactions");
            AnchorPane transactionHolder = (AnchorPane) transactionsScroll.getContent();

            updateTransaction(gestionnaireGuichet,transactionHolder,"0");
            TextField serachTrans = (TextField) root.lookup("#rechercheTransactionField");
            serachTrans.setOnKeyTyped(keyEvent -> {updateTransaction(gestionnaireGuichet,transactionHolder,serachTrans.getText())
            ;});

            //Boutton gerer Clients
            gererClient.setOnAction(event -> {
                clientHolder.setVisible(true);
                updateListClient(gestionnaireGuichet,root,holder, "0");
                searchField.setOnKeyTyped(secondEvent->{
                    String filter = searchField.getText();
                    updateListClient(gestionnaireGuichet,root,holder, filter);
                });
            });

            //Bouton cree client
            creerClient.setOnAction(event -> afficherCreeClient(root,gestionnaireGuichet));
            //Boutton gerer Comptes
            gererCompte.setOnAction(event -> {
                clientHolder.setVisible(true);
                updateListeCompteAdmin(gestionnaireGuichet,root,holder, "0");
                searchField.setOnKeyTyped(secondEvent->{
                    String filter = searchField.getText();
                    updateListeCompteAdmin(gestionnaireGuichet,root,holder, filter);
                });
            });
            //Boutton remplirGuichet
            remplirGuichet.setOnAction(event -> {
                afficherRemplirGuichet(root,gestionnaireGuichet);
            });
            //Boutton compte GLobal
            compGlobal.setOnAction(event -> {
                afficherComGlob(root,gestionnaireGuichet);
            });
            //Button fermer guichet
            fermerGuichet.setOnAction(event -> gestionnaireGuichet.fermerGuichet());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche la scène de création de requête pour créer un compte.
     */
    private void loadCreateView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Application.class.getResource("com/example/javafx_tests/create_view.fxml"));
            loader.setController(new FXMLDocumentController());
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/javafx_tests/create_view.fxml")));

            Scene scene = new Scene(root);
            primaryStage.setTitle("Vanta Bank");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(500);
            primaryStage.show();

            Text dateText = (Text) root.lookup("#date");
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDateTime = currentDateTime.format(formatter);
            dateText.setText(formattedDateTime);

            //Logique
            Button retour = (Button) root.lookup("#retour");
            retour.setOnAction(event -> loadMainView()
            );

            ComboBox typeCompte = (ComboBox) root.lookup("#typeCompteValue");
            TextField retraitMax = (TextField) root.lookup("#retraitMontant");
            TextField transfertMax = (TextField)  root.lookup("#transfertMontant");
            PasswordField nip = (PasswordField) root.lookup("#nipValue");


            retraitMax.setOnKeyTyped(event->updateCreeComptePane(root,gestionnaireGuichet));
            transfertMax.setOnKeyTyped(event->updateCreeComptePane(root,gestionnaireGuichet));
            nip.setOnKeyTyped(event->updateCreeComptePane(root,gestionnaireGuichet));
            typeCompte.setOnAction(event -> updateCreeComptePane(root,gestionnaireGuichet));


            Button submit = (Button) root.lookup("#submit");
            submit.setOnAction(event -> {

                Text messageOutput = (Text) root.lookup("#messageOutput");

                String typeCompteValeur = (String) typeCompte.getValue();
                double valeurRetrait = 0.0;
                double valeurTransfert = 0.0;
                int valeurNip = 0;
                boolean isValidInput = true;
                if (gestionnaireGuichet.getClientCourant().getRequete()){
                    messageOutput.setText("Une requête de création de compte est déjà en cours.");
                    messageOutput.setFill(Color.rgb(212,99,99));
                    messageOutput.setVisible(true);
                    return;
                }
                try {
                    valeurRetrait = Double.parseDouble(retraitMax.getText());
                    valeurTransfert = Double.parseDouble(transfertMax.getText());
                    valeurNip = Integer.parseInt(nip.getText());
                } catch (NumberFormatException e) {
                    isValidInput = false;
                    messageOutput.setText("Entrée invalide, entrez des valeurs appropriées");
                    messageOutput.setFill(Color.rgb(212,99,99));
                    messageOutput.setVisible(true);
                }
                if (valeurRetrait>1000){
                    isValidInput = false;
                    messageOutput.setText("Le montant de retrait doit être inférieur à 1000$");
                    messageOutput.setFill(Color.rgb(212,99,99));
                    messageOutput.setVisible(true);
                }
                if (isValidInput) {
                    boolean creationDeCompte = gestionnaireGuichet.creerCompte(gestionnaireGuichet.getClientCourant(), typeCompteValeur, valeurNip, valeurRetrait, valeurTransfert);
                    if (creationDeCompte) {
                        gestionnaireGuichet.getClientCourant().setRequete(true);
                        messageOutput.setText("Votre requête de création de compte a été soumise.");
                        messageOutput.setFill(Color.rgb(97,150,94));
                        messageOutput.setVisible(true);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        launch(args);

    }

    /**
     * Cree un Pane qui affiche des informations d'un compte et l'ajoute au ScrollPane
     * @param anchorPane
     * @param compte
     * @param y
     * @param gestionnaireGuichet
     * @param root
     */
    public void creeComptePane(AnchorPane anchorPane, Compte compte, double y, GestionnaireGuichet gestionnaireGuichet, Parent root) {

        //Settings pour Pane
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: rgb(27,27,27,0.75); -fx-background-radius: 7;");
        pane.setPrefSize(210, 100);

        //Settings pour les texts

        //Type de compte
        Text compteText = new Text();
        compteText.setText(compte.getClass().getSimpleName().toUpperCase());
        compteText.setFill(Color.rgb(255, 255, 255, 0.5));
        compteText.setStyle("-fx-font-size: 16px; -fx-font-family: 'Segoe UI', sans-serif;");
        pane.getChildren().add(compteText);
        compteText.setLayoutX(10);
        compteText.setLayoutY(25);

        //Balance
        Text balanceText = new Text();
        balanceText.setText("BALANCE");
        balanceText.setFill(Color.rgb(255, 255, 255, 0.5));
        balanceText.setStyle("-fx-font-size: 13px; -fx-font-family: 'Segoe UI', sans-serif;");
        pane.getChildren().add(balanceText);
        balanceText.setLayoutX(140);
        balanceText.setLayoutY(25);

        //Montant
        Text montantText = new Text();
        double solde = compte.getSolde();
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        String soldeFormatte = decimalFormat.format(solde);
        montantText.setText(soldeFormatte + "$");
        montantText.setFill(Color.rgb(255, 255, 255, 1));
        montantText.setStyle("-fx-font-size: 11px; -fx-font-family: 'Segoe UI', sans-serif;");
        pane.getChildren().add(montantText);
        montantText.setLayoutX(146);
        montantText.setLayoutY(40.5);

        //No compte titre
        Text noCompteText = new Text();
        noCompteText.setText("NO COMPTE");
        noCompteText.setFill(Color.rgb(255, 255, 255, 0.5));
        noCompteText.setStyle("-fx-font-size: 6.5px; -fx-font-family: 'Segoe UI', sans-serif;");
        pane.getChildren().add(noCompteText);
        noCompteText.setLayoutX(10);
        noCompteText.setLayoutY(61.5);

        //NIP titre
        Text nipText = new Text();
        nipText.setText("NIP");
        nipText.setFill(Color.rgb(255, 255, 255, 0.5));
        nipText.setStyle("-fx-font-size: 6.5px; -fx-font-family: 'Segoe UI', sans-serif;");
        pane.getChildren().add(nipText);
        nipText.setLayoutX(10);
        nipText.setLayoutY(77.5);

        //Panes pour placeholder noCompte
        Pane noPane = new Pane();
        noPane.setStyle("-fx-background-color: rgb(45,45,45,1); -fx-background-radius: 15;");
        noPane.setPrefSize(100, 5);
        pane.getChildren().add(noPane);
        noPane.setLayoutX(10);
        noPane.setLayoutY(65);

        //no Compte
        Text noCompteUser = new Text();
        noCompteUser.setText(String.valueOf(compte.getNumeroCompte()));
        noCompteUser.setFill(Color.rgb(255, 255, 255, 0.5));
        noCompteUser.setStyle("-fx-font-size: 5px; -fx-font-family: 'Segoe UI', sans-serif;");
        noPane.getChildren().add(noCompteUser);
        noCompteUser.setLayoutX(2.5);
        noCompteUser.setLayoutY(5);

        //Panes pour placeholder NIP
        Pane nipPane = new Pane();
        nipPane.setStyle("-fx-background-color: rgb(45,45,45,1); -fx-background-radius: 15;");
        nipPane.setPrefSize(25, 5);
        pane.getChildren().add(nipPane);
        nipPane.setLayoutX(10);
        nipPane.setLayoutY(80);

        //nip Compte
        Text nipUser = new Text();
        nipUser.setText(createMaskedString(String.valueOf(compte.getNumeroNip()).length()));
        nipUser.setFill(Color.rgb(255, 255, 255, 0.5));
        nipUser.setStyle("-fx-font-size: 5px; -fx-font-family: 'Segoe UI', sans-serif;");
        nipPane.getChildren().add(nipUser);
        nipUser.setLayoutX(2.5);
        nipUser.setLayoutY(5);

        //panel visuel
        Pane rectangle = new Pane();
        rectangle.setStyle("-fx-background-color: rgb(8,8,8,1); -fx-background-radius: 15;");
        pane.getChildren().add(rectangle);
        rectangle.setPrefSize(206, 4);
        rectangle.setLayoutX(2.5);
        rectangle.setLayoutY(90);

        //Setting multimedia
        pane.setOnMouseEntered(event -> {
            pane.setStyle("-fx-background-color: rgb(55,55,55,0.75); -fx-background-radius: 7;");
            rectangle.setStyle("-fx-background-color: rgb(33,33,33,0.75); -fx-background-radius: 15;");
            noPane.setStyle("-fx-background-color: rgb(40,40,40,1); -fx-background-radius: 15;");
            nipPane.setStyle("-fx-background-color: rgb(40,40,40,1); -fx-background-radius: 15;");

        });
        pane.setOnMouseExited(event -> {
            pane.setStyle("-fx-background-color: rgb(27,27,27,0.75); -fx-background-radius: 7;");
            rectangle.setStyle("-fx-background-color: rgb(8,8,8,1); -fx-background-radius: 15;");
            noPane.setStyle("-fx-background-color: rgb(45,45,45,1); -fx-background-radius: 15;");
            nipPane.setStyle("-fx-background-color: rgb(45,45,45,1); -fx-background-radius: 15;");

        });

        //Boutton et fonctionnalité
        Button actionButton = new Button("Effectuer une action");
        actionButton.setPrefSize(pane.getPrefWidth(),pane.getPrefHeight());
        actionButton.setStyle("-fx-background-color: rgb(1,1,1,0)");
        actionButton.setText("");
        actionButton.setCursor(Cursor.HAND);
        actionButton.setOnAction(event -> {
            gestionnaireGuichet.setCompteCourant(compte);
            mettreAJourInformationsCompteCourant(gestionnaireGuichet,root);
            Text operation = (Text) root.lookup("#typeOperation");
            String operationStirng = operation.getText();
            Button submitFacture = (Button) root.lookup("#submitFacture");
            if (operationStirng.equals("DÉPÔT") && gestionnaireGuichet.getCompteCourant() instanceof Cheque){
                submitFacture.setVisible(true);
            }else{
                submitFacture.setVisible(false);
            }
        });
        pane.getChildren().add(actionButton);
        //Settings pour le AnchorPane
        AnchorPane.setTopAnchor(pane, y);
        AnchorPane.setLeftAnchor(pane, 10.0);
        anchorPane.getChildren().add(pane);
    }

    /**
     * Cree un Pane servant de bouton pour se diriger vers la scene de creation de compte.
     * @param anchorPane
     * @param y
     * @throws FileNotFoundException
     */
    public void createAddPane(AnchorPane anchorPane, double y) throws FileNotFoundException {
        //Settings pour Pane
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: rgb(32,32,32,0.75); -fx-background-radius: 7;");
        pane.setPrefSize(210, 100);
        //image
        Image image1 = new Image(new FileInputStream("src/main/java/Application/add.png"));
        ImageView imageView = new ImageView(image1);
        imageView.setLayoutX(80);
        imageView.setLayoutY(23);
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);
        pane.getChildren().add(imageView);

        //Setting multimedia
        pane.setOnMouseEntered(event -> {
            pane.setStyle("-fx-background-color: rgb(55,55,55,0.75); -fx-background-radius: 7;");
            imageView.setOpacity(1);
        });

        pane.setOnMouseExited(event -> {
            pane.setStyle("-fx-background-color: rgb(27,27,27,0.75); -fx-background-radius: 7;");
            imageView.setOpacity(0.5);
        });

        //Boutton et fonctionnalité
        Button actionButton = new Button("Effectuer une action");
        actionButton.setPrefSize(pane.getPrefWidth(),pane.getPrefHeight());
        actionButton.setStyle("-fx-background-color: rgb(1,1,1,0)");
        actionButton.setText("");
        actionButton.setCursor(Cursor.HAND);
        actionButton.setOnAction(event -> {
            loadCreateView();
        });
        pane.getChildren().add(actionButton);

        AnchorPane.setTopAnchor(pane, y);
        AnchorPane.setLeftAnchor(pane, 10.0);
        anchorPane.getChildren().add(pane);
    }

    /**
     * Appelle la fonction createComptePane sur tous les comptes du client courant et appelle la fonction createAddPane à la fin.
     * @param gestionnaireGuichet
     * @param root
     * @param holder
     * @throws FileNotFoundException
     */
    public void updateListeComptes(GestionnaireGuichet gestionnaireGuichet,Parent root,AnchorPane holder) throws FileNotFoundException {
        holder.getChildren().clear();
        double y = 10.0;
        for(int i = 0; i<gestionnaireGuichet.getClientCourant().getComptes().size();i++){
            creeComptePane(holder,gestionnaireGuichet.getClientCourant().getComptes().get(i),y,gestionnaireGuichet, root);
            y+=105.0;
        }
        createAddPane(holder,y);
    }

    /**
     * Met à jour le pane qui affiche les informations du compte courant.
     * @param gestionnaireGuichet
     * @param root
     */
    public void mettreAJourInformationsCompteCourant(GestionnaireGuichet gestionnaireGuichet, Parent root) {
        // Mettez à jour les éléments d'interface utilisateur avec les nouvelles informations du compte courant
        Text typeDeCompte = (Text) root.lookup("#typeCompte");
        typeDeCompte.setText(gestionnaireGuichet.getCompteCourant().getClass().getSimpleName().toUpperCase());

        Text montantCompte = (Text) root.lookup("#montant");
        double solde = gestionnaireGuichet.getCompteCourant().getSolde();
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        String soldeFormatte = decimalFormat.format(solde);
        montantCompte.setText(soldeFormatte + " $");

        Text retraitMax = (Text) root.lookup("#montantRetrait");
        double retraitMaxValue = gestionnaireGuichet.getCompteCourant().getRetraitMax();
        String retraitMaxFormatted = decimalFormat.format(retraitMaxValue);
        retraitMax.setText(retraitMaxFormatted + " $");

        Text transfertMax = (Text) root.lookup("#montantTransfert");
        double transfertMaxValue = gestionnaireGuichet.getCompteCourant().getMontantTransfertMaximum();
        String transfertMaxFormatted = decimalFormat.format(transfertMaxValue);
        transfertMax.setText(transfertMaxFormatted + " $");

        //Parties Optionels
        if (gestionnaireGuichet.getCompteCourant().getClass().getSimpleName().equals("Epargne")) {

            Text infoExtra = (Text) root.lookup("#extraInfo");
            infoExtra.setText("TAUX INTÉRÊT");
            Text valeurExtra = (Text) root.lookup("#valeurInfo");
            double tauxInteret = ((Epargne) gestionnaireGuichet.getCompteCourant()).getTauxInteret()+1.0;
            valeurExtra.setText(tauxInteret+"%");

            Text attribut = (Text) root.lookup("#typeAttribut");
            attribut.setText("DÉPÔT RÉGULIER");
            Text depotRegulier = (Text) root.lookup("#montantDépotRégulier");
            depotRegulier.setText(decimalFormat.format(gestionnaireGuichet.getCompteCourant().getSolde()) + " $ /mo");

            Pane extrePane = (Pane) root.lookup("#caseExtra");
            extrePane.setVisible(true);
            infoExtra.setVisible(true);
            valeurExtra.setVisible(true);
        }
        else if (gestionnaireGuichet.getCompteCourant().getClass().getSimpleName().equals("Cheque")){



            Text operation = (Text) root.lookup("#typeOperation");
            String operationStirng = operation.getText();
            Button submitFacture = (Button) root.lookup("#submitFacture");
            if (operationStirng.equals("DÉPÔT") && gestionnaireGuichet.getCompteCourant() instanceof Cheque){
                submitFacture.setVisible(true);
            }else{
                submitFacture.setVisible(false);
            }


            Text infoExtra = (Text) root.lookup("#extraInfo");
            infoExtra.setText("FRAIS FACTURE");
            Text valeurExtra = (Text) root.lookup("#valeurInfo");
            double frais = ((Cheque) gestionnaireGuichet.getCompteCourant()).getFraisPaimeentFacture();
            valeurExtra.setText(frais+"$");

            Text attribut = (Text) root.lookup("#typeAttribut");
            attribut.setText("FACTURE MAX");
            Text depotRegulier = (Text) root.lookup("#montantDépotRégulier");
            depotRegulier.setText(String.valueOf(((Cheque) gestionnaireGuichet.getCompteCourant()).getMontantFactureMaximum() + " $"));

            Pane extrePane = (Pane) root.lookup("#caseExtra");
            extrePane.setVisible(true);
            infoExtra.setVisible(true);
            valeurExtra.setVisible(true);
        }
        else if (gestionnaireGuichet.getCompteCourant().getClass().getSimpleName().equals("Marge")){
            Text infoExtra = (Text) root.lookup("#extraInfo");
            infoExtra.setText("TAUX INTÉRÊT");
            Text valeurExtra = (Text) root.lookup("#valeurInfo");
            double tauxInteret = ((Marge) gestionnaireGuichet.getCompteCourant()).getTauxInteret()+1.0;
            valeurExtra.setText(tauxInteret+"%");

            Text attribut = (Text) root.lookup("#typeAttribut");
            attribut.setText("MARGE");
            Text depotRegulier = (Text) root.lookup("#montantDépotRégulier");
            depotRegulier.setText("-"+((Marge) gestionnaireGuichet.getCompteCourant()).getSolde() + " $");

            Pane extrePane = (Pane) root.lookup("#caseExtra");
            extrePane.setVisible(true);
            infoExtra.setVisible(true);
            valeurExtra.setVisible(true);
        }
        else {
            Text infoExtra = (Text) root.lookup("#extraInfo");
            Text valeurExtra = (Text) root.lookup("#valeurInfo");

            Pane extrePane = (Pane) root.lookup("#caseExtra");
            extrePane.setVisible(false);
            infoExtra.setVisible(false);
            valeurExtra.setVisible(false);
        }

        Text numeroCompte = (Text) root.lookup("#numeroCompte");
        numeroCompte.setText(String.valueOf(gestionnaireGuichet.getCompteCourant().getNumeroCompte()));
        Text numeroNIP = (Text) root.lookup("#nip");
        numeroNIP.setText(createMaskedString(numeroNIP.getText().length()));
    }

    /**
     * Met à jour l'aperçu de la creation de compte.
     * @param root
     * @param gestionnaireGuichet
     */
    public void updateCreeComptePane(Parent root,GestionnaireGuichet gestionnaireGuichet){
        //Text a mettre a jour
        Text typeCompte = (Text) root.lookup("#typeCompte");
        Text attribut = (Text) root.lookup("#extraInfo");
        Text attributValue = (Text) root.lookup("#valeurInfo");
        Text montantRetrait = (Text) root.lookup("#montantRetrait");
        Text montantTransfert = (Text) root.lookup("#montantTransfert");
        Pane extraPanel = (Pane) root.lookup("#caseExtra");
        Text paneText =(Text) root.lookup("#typeAttribut");
        Text paneValue = (Text) root.lookup("#montantDépotRégulier");
        Text noCompteValue = (Text) root.lookup("#numeroCompte");
        Text nipValue = (Text) root.lookup("#nip");

        //Valeur courante
        TextField retraitMax = (TextField) root.lookup("#retraitMontant");
        TextField transfertMax = (TextField)  root.lookup("#transfertMontant");
        TextField nip = (TextField) root.lookup("#nipValue");
        ComboBox typeCompteCombo = (ComboBox) root.lookup("#typeCompteValue");
        String typeCompteValeur = (String) typeCompteCombo.getValue();
        double valeurRetrait = 0.0;
        double valeurTransfert = 0.0;
        int valeurNip = 0;
        boolean isValidInput = true;

        typeCompte.setText(typeCompteValeur);
        noCompteValue.setText(String.valueOf(gestionnaireGuichet.genererNouveauNumeroCOmpte()));

        switch (typeCompte.getText()){
            case "CHEQUE":
                //Attribut
                attribut.setText("FRAIS FACTURE");
                attribut.setVisible(true);
                attributValue.setText(1.25+"%");
                attributValue.setVisible(true);
                //Pane extra
                paneText.setText("FACTURE MAX");
                paneValue.setText(0+"$");
                extraPanel.setVisible(true);

                break;
            case "EPARGNE":
                //Attribut
                attribut.setText("TAUX INTÉRÊT");
                attribut.setVisible(true);
                attributValue.setText(1.1+"%");
                attributValue.setVisible(true);
                //Pane extra
                paneText.setText("DÉPÔT RÉGULIER");
                paneValue.setText("montant$ /mo");
                extraPanel.setVisible(true);
                break;
            case "HYPOTHECAIRE":
                //Attribut
                extraPanel.setVisible(false);
                attribut.setVisible(false);
                attributValue.setVisible(false);
                break;
            case "MARGE":
                //Attribut
                attribut.setVisible(false);
                attributValue.setVisible(false);
                //Pane extra
                paneText.setText("MARGE");
                paneValue.setText(0+"$");
                extraPanel.setVisible(true);
                break;
            default:
                break;
        }
        try {
            valeurNip = Integer.parseInt(nip.getText());
            nipValue.setText(createMaskedString(String.valueOf(valeurNip).length()));
        } catch (NumberFormatException ignored) {}
        try {
            valeurRetrait = Double.parseDouble(retraitMax.getText());
            montantRetrait.setText(valeurRetrait+"$");
        } catch (NumberFormatException ignored) {}
        try {
            valeurTransfert = Double.parseDouble(transfertMax.getText());
            montantTransfert.setText(valeurTransfert+"$");
        } catch (NumberFormatException ignored) {}



    }

    /**
     * Cree un Pane qui affiche quelque information d'un client et qui l'ajoute au scroll Pane pour les clients.
     * @param anchorPane
     * @param client
     * @param y
     * @param gestionnaireGuichet
     * @param root
     */
    public void createClientPane(AnchorPane anchorPane, Client client, double y, GestionnaireGuichet gestionnaireGuichet, Parent root){
        //Settings pour Pane
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: rgb(27,27,27,0.75); -fx-background-radius: 7;");
        pane.setPrefSize(180, 75);

        //Nom client
        Text nomClient = new Text();
        nomClient.setText(client.getPrenom().toUpperCase()+" "+client.getNom().toUpperCase());
        nomClient.setFill(Color.rgb(255, 255, 255, 1));
        nomClient.setStyle("-fx-font-size: 12px; -fx-font-family: 'Segoe UI', sans-serif;");
        pane.getChildren().add(nomClient);
        nomClient.setLayoutX(10);
        nomClient.setLayoutY(18);

        //Ajouter image cadena pour compte verouiller
        if (client.getBloquer()){
            Image image1 = null;
            try {
                image1 = new Image(new FileInputStream("src/main/java/Application/lock.png"));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            ImageView imageView = new ImageView(image1);
            imageView.setLayoutX(152);
            imageView.setLayoutY(10);
            imageView.setFitHeight(12);
            imageView.setFitWidth(12);
            pane.getChildren().add(imageView);
        }

        //Addresse
        Text addresseText = new Text();
        addresseText.setText("COURRIEL");
        addresseText.setFill(Color.rgb(255, 255, 255, 0.5));
        addresseText.setStyle("-fx-font-size: 6px; -fx-font-family: 'Segoe UI', sans-serif;");
        pane.getChildren().add(addresseText);
        addresseText.setLayoutX(10);
        addresseText.setLayoutY(30);

        //Addresse Value
        Text addresseValeur = new Text();
        addresseValeur.setText(client.getCourriel());
        addresseValeur.setFill(Color.rgb(255, 255, 255, 1));
        addresseValeur.setStyle("-fx-font-size: 7px; -fx-font-family: 'Segoe UI', sans-serif;");
        pane.getChildren().add(addresseValeur);
        addresseValeur.setLayoutX(10);
        addresseValeur.setLayoutY(39);

        //No client titre
        Text noClientText = new Text();
        noClientText.setText("NO CLIENT");
        noClientText.setFill(Color.rgb(255, 255, 255, 0.5));
        noClientText.setStyle("-fx-font-size: 5px; -fx-font-family: 'Segoe UI', sans-serif;");
        pane.getChildren().add(noClientText);
        noClientText.setLayoutX(10);
        noClientText.setLayoutY(53.5);

        //NIP titre
        Text nipText = new Text();
        nipText.setText("NIP");
        nipText.setFill(Color.rgb(255, 255, 255, 0.5));
        nipText.setStyle("-fx-font-size: 5px; -fx-font-family: 'Segoe UI', sans-serif;");
        pane.getChildren().add(nipText);
        nipText.setLayoutX(10);
        nipText.setLayoutY(64.15);

        //Panes pour placeholder noCompte
        Pane noPane = new Pane();
        noPane.setStyle("-fx-background-color: rgb(45,45,45,1); -fx-background-radius: 15;");
        noPane.setPrefSize(100, 5);
        pane.getChildren().add(noPane);
        noPane.setLayoutX(10);
        noPane.setLayoutY(54.5);

        //no client value
        Text noClientValue = new Text();
        noClientValue.setText(String.valueOf(client.getCodeClient()));
        noClientValue.setFill(Color.rgb(255, 255, 255, 0.5));
        noClientValue.setStyle("-fx-font-size: 5px; -fx-font-family: 'Segoe UI', sans-serif;");
        noPane.getChildren().add(noClientValue);
        noClientValue.setLayoutX(2.5);
        noClientValue.setLayoutY(5);

        //Panes pour placeholder NIP
        Pane nipPane = new Pane();
        nipPane.setStyle("-fx-background-color: rgb(45,45,45,1); -fx-background-radius: 15;");
        nipPane.setPrefSize(25, 5);
        pane.getChildren().add(nipPane);
        nipPane.setLayoutX(10);
        nipPane.setLayoutY(65);

        //nip Compte
        Text nipUser = new Text();
        nipUser.setText(createMaskedString(String.valueOf(client.getNumeroNIP()).length()));
        nipUser.setFill(Color.rgb(255, 255, 255, 0.5));
        nipUser.setStyle("-fx-font-size: 5px; -fx-font-family: 'Segoe UI', sans-serif;");
        nipPane.getChildren().add(nipUser);
        nipUser.setLayoutX(2.5);
        nipUser.setLayoutY(5);

        //panel visuel
        Pane rectangle = new Pane();
        rectangle.setStyle("-fx-background-color: rgb(8,8,8,1); -fx-background-radius: 15;");
        pane.getChildren().add(rectangle);
        rectangle.setPrefSize(4.75, 72);
        rectangle.setLayoutX(174.5);
        rectangle.setLayoutY(2);

        //Setting multimedia
        pane.setOnMouseEntered(event -> {
            pane.setStyle("-fx-background-color: rgb(55,55,55,0.75); -fx-background-radius: 7;");
            rectangle.setStyle("-fx-background-color: rgb(33,33,33,0.75); -fx-background-radius: 15;");
            noPane.setStyle("-fx-background-color: rgb(40,40,40,1); -fx-background-radius: 15;");
            nipPane.setStyle("-fx-background-color: rgb(40,40,40,1); -fx-background-radius: 15;");

        });
        pane.setOnMouseExited(event -> {
            pane.setStyle("-fx-background-color: rgb(27,27,27,0.75); -fx-background-radius: 7;");
            rectangle.setStyle("-fx-background-color: rgb(8,8,8,1); -fx-background-radius: 15;");
            noPane.setStyle("-fx-background-color: rgb(45,45,45,1); -fx-background-radius: 15;");
            nipPane.setStyle("-fx-background-color: rgb(45,45,45,1); -fx-background-radius: 15;");

        });

        //Boutton et fonctionnalité
        Button actionButton = new Button("Effectuer une action");
        actionButton.setPrefSize(pane.getPrefWidth(),pane.getPrefHeight());
        actionButton.setStyle("-fx-background-color: rgb(1,1,1,0)");
        actionButton.setText("");
        actionButton.setCursor(Cursor.HAND);
        actionButton.setOnAction(event -> {
            afficherClientPane(root,client,gestionnaireGuichet);
        });
        pane.getChildren().add(actionButton);
        //Settings pour le AnchorPane
        AnchorPane.setTopAnchor(pane, y);
        AnchorPane.setLeftAnchor(pane, 10.0);
        anchorPane.getChildren().add(pane);
    }

    /**
     * Appelle la fonction createClientPane pour tous les clients.
     * @param gestionnaireGuichet
     * @param root
     * @param holder
     * @param filter
     */
    public void updateListClient(GestionnaireGuichet gestionnaireGuichet,Parent root,AnchorPane holder,String filter){
        Pane guichetPane = (Pane) root.lookup("#guichetPane");
        Pane creeClientPane = (Pane) root.lookup("#createPane");
        Pane comGlobPane = (Pane) root.lookup("#comGlobalPane");
        Text msgErreur = (Text) root.lookup("#msgErreurPane");
        msgErreur.setVisible(false);
        Pane infoPane = (Pane) root.lookup("#info");
        Button supprimer = (Button) root.lookup("#supprimer");
        Button verouiller = (Button) root.lookup("#verouiller");
        ScrollPane clientHolder = (ScrollPane) root.lookup("#clients");
        TextField montantField = (TextField) root.lookup("#montantHypo");
        comGlobPane.setVisible(false);
        montantField.setVisible(false);
        creeClientPane.setVisible(false);
        clientHolder.setVisible(true );
        supprimer.setVisible(false);
        verouiller.setVisible(false);
        infoPane.setVisible(false);
        guichetPane.setVisible(false);
        holder.getChildren().clear();
        double y = 10.0;
        int valeurFiltreInt = 0;
        try {
            valeurFiltreInt = Integer.parseInt(filter);
        } catch (NumberFormatException ignored) {}
        for(int i = 0; i<gestionnaireGuichet.getClients().size();i++){
            if (valeurFiltreInt != 0){
                if (gestionnaireGuichet.getClients().get(i).getCodeClient() == valeurFiltreInt){
                    createClientPane(holder,gestionnaireGuichet.getClients().get(i),y,gestionnaireGuichet, root);
                    y+=80;
                }
            }
            else{
                createClientPane(holder,gestionnaireGuichet.getClients().get(i),y,gestionnaireGuichet, root);
                y+=80;
            }

        }
    }

    /**
     * Affiche les informations d'un client dans un Pane.
     * @param root
     * @param client
     * @param gestionnaireGuichet
     */
    public void afficherClientPane(Parent root,Client client,GestionnaireGuichet gestionnaireGuichet){

        ScrollPane clientHolder = (ScrollPane) root.lookup("#clients");
        Pane comGlobPane = (Pane) root.lookup("#comGlobalPane");
        Pane guichetPane = (Pane) root.lookup("#guichetPane");
        Pane creeClientPane = (Pane) root.lookup("#createPane");
        Pane infoPane = (Pane) root.lookup("#info");
        Text type = (Text) root.lookup("#type");
        Text balanceText = (Text) root.lookup("#balanceText");
        Text valeur0 = (Text) root.lookup("#valeur0");
        Text info1 = (Text) root.lookup("#info1");
        Text valeur1 = (Text) root.lookup("#value1");
        Text info2 = (Text) root.lookup("#info2");
        Text valeur2 = (Text) root.lookup("#value2");
        Text noText = (Text) root.lookup("#noText");
        Text noTextValue = (Text) root.lookup("#noTextValue");
        Text nip = (Text) root.lookup("#nip");
        Text msgErreur = (Text) root.lookup("#msgErreurPane");
        ImageView image = (ImageView) root.lookup("#image");
        Button supprimer = (Button) root.lookup("#supprimer");
        Button verouiller = (Button) root.lookup("#verouiller");

        comGlobPane.setVisible(false);
        guichetPane.setVisible(false);
        creeClientPane.setVisible(false);
        msgErreur.setVisible(false);
        supprimer.setVisible(true);
        verouiller.setVisible(true);
        clientHolder.setVisible(false);
        infoPane.setVisible(true);
        balanceText.setVisible(false);
        valeur0.setVisible(false);
        supprimer.setText("SUPPRIMER");

        type.setText(client.getPrenom().toUpperCase()+" "+client.getNom().toUpperCase());
        info1.setText("COURRIEL");
        valeur1.setText(client.getCourriel());
        info2.setText("TÉLÉPHONE");
        valeur2.setText(client.getTelephone());
        noText.setText("NO CLIENT");
        noTextValue.setText(String.valueOf(client.getCodeClient()));
        nip.setText(createMaskedString(String.valueOf(client.getNumeroNIP()).length()));
        verouiller.setText("VÉROUILLER");

        try {
            image.setImage(new Image(new FileInputStream("src/main/java/Application/lock.png")));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (client.getBloquer()){
            image.setVisible(true);
            verouiller.setText("DÉVERROUILLER");
        }
        else{
            image.setVisible(false);
            verouiller.setText("VÉROUILLER");
        }
        verouiller.setOnAction(event -> {
            if (!client.getBloquer()){
                if (client.getAdmin()){
                    msgErreur.setText("Impossible de vérouillier un compte admin.");
                    msgErreur.setFill(Color.rgb(234,78,78));
                    msgErreur.setVisible(true);
                }else{
                    verouiller.setText("DÉVERROUILLER");
                    client.setBloquer(true);
                    image.setVisible(true);
                }
            }
            else {
                verouiller.setText("VÉROUILLER");
                client.setBloquer(false);
                image.setVisible(false);
            }
        });
        supprimer.setOnAction(event -> {
            if (client.getAdmin()){
                msgErreur.setText("Impossible de supprimer un compte admin.");
                msgErreur.setFill(Color.rgb(234,78,78));
                msgErreur.setVisible(true);
            }else{
                gestionnaireGuichet.getClients().remove(client);
                msgErreur.setText("Le compte client a été supprimé.");
                msgErreur.setFill(Color.rgb(91,215,104));
                msgErreur.setVisible(true);
            }
        });

    }

    /**
     * Cree un Pane qui affiche les informations d'une transaction et l'ajoute à un ScrollPane pour les transactions.
     * @param anchorPane
     * @param transaction
     * @param y
     */
    public void createTransactionPane(AnchorPane anchorPane, Transaction transaction,double y){
        //Settings pour Pane
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: rgb(27,27,27,0.75); -fx-background-radius: 7;");
        pane.setPrefSize(195, 60);

        //Settings pour les texts

        //Type de Transaction
        Text transactionText = new Text();
        transactionText.setText(transaction.getTypeTransaction());
        transactionText.setFill(Color.rgb(255, 255, 255, 0.5));
        transactionText.setStyle("-fx-font-size: 16px; -fx-font-family: 'Segoe UI', sans-serif;");
        pane.getChildren().add(transactionText);
        transactionText.setLayoutX(10);
        transactionText.setLayoutY(20);

        //MontantText
        Text montantText = new Text();
        montantText.setText("MONTANT");
        montantText.setFill(Color.rgb(255, 255, 255, 0.5));
        montantText.setStyle("-fx-font-size: 10.5px; -fx-font-family: 'Segoe UI', sans-serif;");
        pane.getChildren().add(montantText);
        montantText.setLayoutX(125);
        montantText.setLayoutY(20);

        //Montant
        Text montantValeur = new Text();
        double montant = transaction.getMontant();
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        String montantFormate = decimalFormat.format(montant);
        montantValeur.setText(montantFormate + " $");
        montantValeur.setFill(Color.rgb(255, 255, 255, 1));
        montantValeur.setStyle("-fx-font-size: 8px; -fx-font-family: 'Segoe UI', sans-serif;");
        pane.getChildren().add(montantValeur);
        montantValeur.setLayoutX(142);
        montantValeur.setLayoutY(33);

        //No compte titre
        Text noCompteText = new Text();
        noCompteText.setText("NO COMPTE");
        noCompteText.setFill(Color.rgb(255, 255, 255, 0.5));
        noCompteText.setStyle("-fx-font-size: 6.5px; -fx-font-family: 'Segoe UI', sans-serif;");
        pane.getChildren().add(noCompteText);
        noCompteText.setLayoutX(10);
        noCompteText.setLayoutY(40);

        //No Transaction
        Text noTransaction = new Text();
        noTransaction.setText("#"+transaction.getNumeroTransaction());
        noTransaction.setFill(Color.rgb(255, 255, 255, 0.5));
        noTransaction.setStyle("-fx-font-size: 10px; -fx-font-family: 'Segoe UI', sans-serif;");
        pane.getChildren().add(noTransaction);
        noTransaction.setLayoutX(170);
        noTransaction.setLayoutY(45);

        //Panes pour placeholder noCompte
        Pane noPane = new Pane();
        noPane.setStyle("-fx-background-color: rgb(45,45,45,1); -fx-background-radius: 15;");
        noPane.setPrefSize(75, 5);
        pane.getChildren().add(noPane);
        noPane.setLayoutX(10);
        noPane.setLayoutY(45);

        //no Compte
        Text noCompteUser = new Text();
        noCompteUser.setText(String.valueOf(transaction.getCompte().getNumeroCompte()));
        noCompteUser.setFill(Color.rgb(255, 255, 255, 0.5));
        noCompteUser.setStyle("-fx-font-size: 5px; -fx-font-family: 'Segoe UI', sans-serif;");
        noPane.getChildren().add(noCompteUser);
        noCompteUser.setLayoutX(2.5);
        noCompteUser.setLayoutY(5);

        if (transaction.getCompteDest()!=null){

            //No compte titre
            Text noCompteTextDest = new Text();
            noCompteTextDest.setText("NO COMPTE DEST.");
            noCompteTextDest.setFill(Color.rgb(255, 255, 255, 0.5));
            noCompteTextDest.setStyle("-fx-font-size: 6.5px; -fx-font-family: 'Segoe UI', sans-serif;");
            pane.getChildren().add(noCompteTextDest);
            noCompteTextDest.setLayoutX(90);
            noCompteTextDest.setLayoutY(40);

            //Panes pour placeholder noCompteDest
            Pane noPaneDest = new Pane();
            noPaneDest.setStyle("-fx-background-color: rgb(45,45,45,1); -fx-background-radius: 15;");
            noPaneDest.setPrefSize(25, 5);
            pane.getChildren().add(noPaneDest);
            noPaneDest.setLayoutX(90);
            noPaneDest.setLayoutY(45);

            //no CompteDest
            Text noCompteDest = new Text();
            noCompteDest.setText(String.valueOf(transaction.getCompteDest().getNumeroCompte()));
            noCompteDest.setFill(Color.rgb(255, 255, 255, 0.5));
            noCompteDest.setStyle("-fx-font-size: 5px; -fx-font-family: 'Segoe UI', sans-serif;");
            noPaneDest.getChildren().add(noCompteDest);
            noCompteDest.setLayoutX(2.5);
            noCompteDest.setLayoutY(5);
        }


        //panel visuel
        Pane rectangle = new Pane();
        rectangle.setStyle("-fx-background-color: rgb(8,8,8,1); -fx-background-radius: 15;");
        pane.getChildren().add(rectangle);
        rectangle.setPrefSize(192, 4);
        rectangle.setLayoutX(1.5);
        rectangle.setLayoutY(53.5);

        //Setting multimedia
        pane.setOnMouseEntered(event -> {
            pane.setStyle("-fx-background-color: rgb(55,55,55,0.75); -fx-background-radius: 7;");
            rectangle.setStyle("-fx-background-color: rgb(33,33,33,0.75); -fx-background-radius: 15;");
            noPane.setStyle("-fx-background-color: rgb(40,40,40,1); -fx-background-radius: 15;");

        });
        pane.setOnMouseExited(event -> {
            pane.setStyle("-fx-background-color: rgb(27,27,27,0.75); -fx-background-radius: 7;");
            rectangle.setStyle("-fx-background-color: rgb(8,8,8,1); -fx-background-radius: 15;");
            noPane.setStyle("-fx-background-color: rgb(45,45,45,1); -fx-background-radius: 15;");

        });

        //Settings pour le AnchorPane
        AnchorPane.setTopAnchor(pane, y);
        AnchorPane.setLeftAnchor(pane, 10.0);
        anchorPane.getChildren().add(pane);
    }

    /**
     * Appelle la fonction createTransactionPane sur toutes les transactions.
     * @param gestionnaireGuichet
     * @param holder
     * @param filter
     */
    public void updateTransaction(GestionnaireGuichet gestionnaireGuichet,AnchorPane holder,String filter){
        holder.getChildren().clear();
        double y = 10.0;
        int valeurFiltreInt = 0;
        try {
            valeurFiltreInt = Integer.parseInt(filter);
        } catch (NumberFormatException ignored) {}
        if (!gestionnaireGuichet.getTransactions().isEmpty()){
            for(int i = gestionnaireGuichet.getTransactions().size()-1; i>=0;i--){

                if (valeurFiltreInt != 0){
                    if (gestionnaireGuichet.getTransactions().get(i).getNumeroTransaction() == valeurFiltreInt){
                        createTransactionPane(holder,gestionnaireGuichet.getTransactions().get(i),y);
                        y+=65;
                    }
                }
                else{
                    createTransactionPane(holder,gestionnaireGuichet.getTransactions().get(i),y);
                    y+=65;
                }
            }
        }

    }

    /**
     * Appelle la fonction createTransactionPane sur toutes les transactions du client.
     * @param gestionnaireGuichet
     * @param holder
     * @param client
     */
    public void updateTransactionClient(GestionnaireGuichet gestionnaireGuichet,AnchorPane holder,Client client){
        holder.getChildren().clear();
        double y = 10.0;
        if (!client.getTransactions().isEmpty()){
            for(int i = client.getTransactions().size()-1; i>=0;i--){
                createTransactionPane(holder,client.getTransactions().get(i),y);
                y+=65;
            }
        }

    }

    /**
     * Crée un Pane qui affiche des informations d'un compte pour la scène admin.
     * @param anchorPane
     * @param compte
     * @param y
     * @param gestionnaireGuichet
     * @param root
     */
    public void createComptePaneAdmin(AnchorPane anchorPane, Compte compte, double y, GestionnaireGuichet gestionnaireGuichet, Parent root){
        //Settings pour Pane
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: rgb(27,27,27,0.75); -fx-background-radius: 7;");
        pane.setPrefSize(180, 75);

        //Type compte
        Text typeCompte = new Text();
        typeCompte.setText(compte.getClass().getSimpleName().toUpperCase());
        typeCompte.setFill(Color.rgb(255, 255, 255, 1));
        typeCompte.setStyle("-fx-font-size: 12px; -fx-font-family: 'Segoe UI', sans-serif;");
        pane.getChildren().add(typeCompte);
        typeCompte.setLayoutX(10);
        typeCompte.setLayoutY(18);

        //Balance text
        Text balanceText = new Text();
        balanceText.setText("BALANCE");
        balanceText.setFill(Color.rgb(255, 255, 255, 0.5));
        balanceText.setStyle("-fx-font-size: 6px; -fx-font-family: 'Segoe UI', sans-serif;");
        pane.getChildren().add(balanceText);
        balanceText.setLayoutX(10);
        balanceText.setLayoutY(30);

        //Balance Value
        Text balanceValue = new Text();
        double solde = compte.getSolde();
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        String soldeFormate = decimalFormat.format(solde);
        balanceValue.setText(soldeFormate + " $");
        balanceValue.setFill(Color.rgb(255, 255, 255, 1));
        balanceValue.setStyle("-fx-font-size: 7px; -fx-font-family: 'Segoe UI', sans-serif;");
        pane.getChildren().add(balanceValue);
        balanceValue.setLayoutX(10);
        balanceValue.setLayoutY(39);

        //No compte titre
        Text noCompteText = new Text();
        noCompteText.setText("NO COMPTE");
        noCompteText.setFill(Color.rgb(255, 255, 255, 0.5));
        noCompteText.setStyle("-fx-font-size: 5px; -fx-font-family: 'Segoe UI', sans-serif;");
        pane.getChildren().add(noCompteText);
        noCompteText.setLayoutX(10);
        noCompteText.setLayoutY(53.5);

        //titulairetitre
        Text titulaireTitre = new Text();
        titulaireTitre.setText("TITULAIRE");
        titulaireTitre.setFill(Color.rgb(255, 255, 255, 0.75));
        titulaireTitre.setStyle("-fx-font-size: 7px; -fx-font-family: 'Segoe UI', sans-serif;");
        pane.getChildren().add(titulaireTitre);
        titulaireTitre.setLayoutX(100);
        titulaireTitre.setLayoutY(18);

        //titulaire
        Text titulaire = new Text();
        titulaire.setText(compte.getClient().getPrenom().toUpperCase()+" "+compte.getClient().getNom().toUpperCase());
        titulaire.setFill(Color.rgb(255, 255, 255, 1));
        titulaire.setStyle("-fx-font-size: 8px; -fx-font-family: 'Segoe UI', sans-serif;");
        pane.getChildren().add(titulaire);
        titulaire.setLayoutX(100);
        titulaire.setLayoutY(26);

        //NIP titre
        Text nipText = new Text();
        nipText.setText("NIP");
        nipText.setFill(Color.rgb(255, 255, 255, 0.5));
        nipText.setStyle("-fx-font-size: 5px; -fx-font-family: 'Segoe UI', sans-serif;");
        pane.getChildren().add(nipText);
        nipText.setLayoutX(10);
        nipText.setLayoutY(64.15);

        //Panes pour placeholder noCompte
        Pane noPane = new Pane();
        noPane.setStyle("-fx-background-color: rgb(45,45,45,1); -fx-background-radius: 15;");
        noPane.setPrefSize(100, 5);
        pane.getChildren().add(noPane);
        noPane.setLayoutX(10);
        noPane.setLayoutY(54.5);

        //no compte value
        Text noCompteValue = new Text();
        noCompteValue.setText(String.valueOf(compte.getNumeroCompte()));
        noCompteValue.setFill(Color.rgb(255, 255, 255, 0.5));
        noCompteValue.setStyle("-fx-font-size: 5px; -fx-font-family: 'Segoe UI', sans-serif;");
        noPane.getChildren().add(noCompteValue);
        noCompteValue.setLayoutX(2.5);
        noCompteValue.setLayoutY(5);

        //Panes pour placeholder NIP
        Pane nipPane = new Pane();
        nipPane.setStyle("-fx-background-color: rgb(45,45,45,1); -fx-background-radius: 15;");
        nipPane.setPrefSize(25, 5);
        pane.getChildren().add(nipPane);
        nipPane.setLayoutX(10);
        nipPane.setLayoutY(65);

        //nip Compte
        Text nipUser = new Text();
        nipUser.setText(createMaskedString(String.valueOf(compte.getNumeroNip()).length()));
        nipUser.setFill(Color.rgb(255, 255, 255, 0.5));
        nipUser.setStyle("-fx-font-size: 5px; -fx-font-family: 'Segoe UI', sans-serif;");
        nipPane.getChildren().add(nipUser);
        nipUser.setLayoutX(2.5);
        nipUser.setLayoutY(5);

        //panel visuel
        Pane rectangle = new Pane();
        rectangle.setStyle("-fx-background-color: rgb(8,8,8,1); -fx-background-radius: 15;");
        pane.getChildren().add(rectangle);
        rectangle.setPrefSize(4.75, 72);
        rectangle.setLayoutX(174.5);
        rectangle.setLayoutY(2);

        //Setting multimedia
        pane.setOnMouseEntered(event -> {
            pane.setStyle("-fx-background-color: rgb(55,55,55,0.75); -fx-background-radius: 7;");
            rectangle.setStyle("-fx-background-color: rgb(33,33,33,0.75); -fx-background-radius: 15;");
            noPane.setStyle("-fx-background-color: rgb(40,40,40,1); -fx-background-radius: 15;");
            nipPane.setStyle("-fx-background-color: rgb(40,40,40,1); -fx-background-radius: 15;");

        });
        pane.setOnMouseExited(event -> {
            pane.setStyle("-fx-background-color: rgb(27,27,27,0.75); -fx-background-radius: 7;");
            rectangle.setStyle("-fx-background-color: rgb(8,8,8,1); -fx-background-radius: 15;");
            noPane.setStyle("-fx-background-color: rgb(45,45,45,1); -fx-background-radius: 15;");
            nipPane.setStyle("-fx-background-color: rgb(45,45,45,1); -fx-background-radius: 15;");

        });

        //Boutton et fonctionnalité
        Button actionButton = new Button("Effectuer une action");
        actionButton.setPrefSize(pane.getPrefWidth(),pane.getPrefHeight());
        actionButton.setStyle("-fx-background-color: rgb(1,1,1,0)");
        actionButton.setText("");
        actionButton.setCursor(Cursor.HAND);
        actionButton.setOnAction(event -> {
            afficherComptePane(root,compte,gestionnaireGuichet);
        });
        pane.getChildren().add(actionButton);
        //Settings pour le AnchorPane
        AnchorPane.setTopAnchor(pane, y);
        AnchorPane.setLeftAnchor(pane, 10.0);
        anchorPane.getChildren().add(pane);
    }

    /**
     * Crée un Pane qui affiche des informations d'un compte requête (une requête de creation de compte qu'un utilisateur à envoyer, mais qui doit être accépté par l'admin pour être créé).
     * @param anchorPane
     * @param compte
     * @param y
     * @param gestionnaireGuichet
     * @param root
     */
    public void createCompteRequestList(AnchorPane anchorPane, Compte compte, double y, GestionnaireGuichet gestionnaireGuichet, Parent root){
        //Settings pour Pane
        Pane pane = new Pane();
        pane.setStyle("-fx-background-color: rgb(50,40,40,0.75); -fx-background-radius: 7;");
        pane.setPrefSize(180, 75);

        //Type compte
        Text typeCompte = new Text();
        typeCompte.setText(compte.getClass().getSimpleName().toUpperCase());
        typeCompte.setFill(Color.rgb(255, 255, 255, 1));
        typeCompte.setStyle("-fx-font-size: 12px; -fx-font-family: 'Segoe UI', sans-serif;");
        pane.getChildren().add(typeCompte);
        typeCompte.setLayoutX(10);
        typeCompte.setLayoutY(18);

        //Balance text
        Text balanceText = new Text();
        balanceText.setText("BALANCE");
        balanceText.setFill(Color.rgb(255, 255, 255, 0.5));
        balanceText.setStyle("-fx-font-size: 6px; -fx-font-family: 'Segoe UI', sans-serif;");
        pane.getChildren().add(balanceText);
        balanceText.setLayoutX(10);
        balanceText.setLayoutY(30);

        //Balance Value
        Text balanceValue = new Text();
        balanceValue.setText(compte.getSolde()+"$");
        balanceValue.setFill(Color.rgb(255, 255, 255, 1));
        balanceValue.setStyle("-fx-font-size: 7px; -fx-font-family: 'Segoe UI', sans-serif;");
        pane.getChildren().add(balanceValue);
        balanceValue.setLayoutX(10);
        balanceValue.setLayoutY(39);

        //No compte titre
        Text noCompteText = new Text();
        noCompteText.setText("NO COMPTE");
        noCompteText.setFill(Color.rgb(255, 255, 255, 0.5));
        noCompteText.setStyle("-fx-font-size: 5px; -fx-font-family: 'Segoe UI', sans-serif;");
        pane.getChildren().add(noCompteText);
        noCompteText.setLayoutX(10);
        noCompteText.setLayoutY(53.5);

        //titulairetitre
        Text titulaireTitre = new Text();
        titulaireTitre.setText("TITULAIRE");
        titulaireTitre.setFill(Color.rgb(255, 255, 255, 0.75));
        titulaireTitre.setStyle("-fx-font-size: 7px; -fx-font-family: 'Segoe UI', sans-serif;");
        pane.getChildren().add(titulaireTitre);
        titulaireTitre.setLayoutX(100);
        titulaireTitre.setLayoutY(18);

        //titulaire
        Text titulaire = new Text();
        titulaire.setText(compte.getClient().getPrenom().toUpperCase()+" "+compte.getClient().getNom().toUpperCase());
        titulaire.setFill(Color.rgb(255, 255, 255, 1));
        titulaire.setStyle("-fx-font-size: 8px; -fx-font-family: 'Segoe UI', sans-serif;");
        pane.getChildren().add(titulaire);
        titulaire.setLayoutX(100);
        titulaire.setLayoutY(26);

        //NIP titre
        Text nipText = new Text();
        nipText.setText("NIP");
        nipText.setFill(Color.rgb(255, 255, 255, 0.5));
        nipText.setStyle("-fx-font-size: 5px; -fx-font-family: 'Segoe UI', sans-serif;");
        pane.getChildren().add(nipText);
        nipText.setLayoutX(10);
        nipText.setLayoutY(64.15);

        //Panes pour placeholder noCompte
        Pane noPane = new Pane();
        noPane.setStyle("-fx-background-color: rgba(42,27,27,1); -fx-background-radius: 15;");
        noPane.setPrefSize(100, 5);
        pane.getChildren().add(noPane);
        noPane.setLayoutX(10);
        noPane.setLayoutY(54.5);

        //no compte value
        Text noCompteValue = new Text();
        noCompteValue.setText(String.valueOf(compte.getNumeroCompte()));
        noCompteValue.setFill(Color.rgb(255, 255, 255, 0.5));
        noCompteValue.setStyle("-fx-font-size: 5px; -fx-font-family: 'Segoe UI', sans-serif;");
        noPane.getChildren().add(noCompteValue);
        noCompteValue.setLayoutX(2.5);
        noCompteValue.setLayoutY(5);

        //Panes pour placeholder NIP
        Pane nipPane = new Pane();
        nipPane.setStyle("-fx-background-color: rgba(42,27,27,1); -fx-background-radius: 15;");
        nipPane.setPrefSize(25, 5);
        pane.getChildren().add(nipPane);
        nipPane.setLayoutX(10);
        nipPane.setLayoutY(65);

        //nip Compte
        Text nipUser = new Text();
        nipUser.setText(createMaskedString(String.valueOf(compte.getNumeroNip()).length()));
        nipUser.setFill(Color.rgb(255, 255, 255, 0.5));
        nipUser.setStyle("-fx-font-size: 5px; -fx-font-family: 'Segoe UI', sans-serif;");
        nipPane.getChildren().add(nipUser);
        nipUser.setLayoutX(2.5);
        nipUser.setLayoutY(5);

        //panel visuel
        Pane rectangle = new Pane();
        rectangle.setStyle("-fx-background-color: rgba(54,30,30,0.66); -fx-background-radius: 15;");
        pane.getChildren().add(rectangle);
        rectangle.setPrefSize(4.75, 72);
        rectangle.setLayoutX(174.5);
        rectangle.setLayoutY(2);

        //Setting multimedia
        pane.setOnMouseEntered(event -> {
            pane.setStyle("-fx-background-color: rgb(70,60,60,0.75); -fx-background-radius: 7;");
            rectangle.setStyle("-fx-background-color: rgb(94,52,52); -fx-background-radius: 15;");
            noPane.setStyle("-fx-background-color: rgb(72,47,47); -fx-background-radius: 15;");
            nipPane.setStyle("-fx-background-color: rgb(72,47,47); -fx-background-radius: 15;");

        });
        pane.setOnMouseExited(event -> {
            pane.setStyle("-fx-background-color: rgb(50,40,40,0.75); -fx-background-radius: 7;");
            rectangle.setStyle("-fx-background-color: rgba(54,30,30,0.66); -fx-background-radius: 15;");
            noPane.setStyle("-fx-background-color: rgba(42,27,27,1); -fx-background-radius: 15;");
            nipPane.setStyle("-fx-background-color: rgba(42,27,27,1); -fx-background-radius: 15;");

        });

        //Boutton et fonctionnalité
        Button actionButton = new Button("Effectuer une action");
        actionButton.setPrefSize(pane.getPrefWidth(),pane.getPrefHeight());
        actionButton.setStyle("-fx-background-color: rgb(1,1,1,0)");
        actionButton.setText("");
        actionButton.setCursor(Cursor.HAND);
        actionButton.setOnAction(event -> {
            afficherComptePane(root,compte,gestionnaireGuichet);
        });
        pane.getChildren().add(actionButton);
        //Settings pour le AnchorPane
        AnchorPane.setTopAnchor(pane, y);
        AnchorPane.setLeftAnchor(pane, 10.0);
        anchorPane.getChildren().add(pane);
    }

    /**
     * Appelle la fonction createComptePaneAdmin et createCompteRequestList pour tous les comptes et tous les comptes requêtes
     * @param gestionnaireGuichet
     * @param root
     * @param holder
     * @param filter
     */
    public void updateListeCompteAdmin(GestionnaireGuichet gestionnaireGuichet,Parent root,AnchorPane holder,String filter){
        Pane guichetPane = (Pane) root.lookup("#guichetPane");
        Pane creeClientPane = (Pane) root.lookup("#createPane");
        Pane comGlobPane = (Pane) root.lookup("#comGlobalPane");
        Text msgErreur = (Text) root.lookup("#msgErreurPane");
        msgErreur.setVisible(false);
        Pane infoPane = (Pane) root.lookup("#info");
        Button supprimer = (Button) root.lookup("#supprimer");
        Button verouiller = (Button) root.lookup("#verouiller");
        ScrollPane clientHolder = (ScrollPane) root.lookup("#clients");
        TextField montantField = (TextField) root.lookup("#montantHypo");
        montantField.setVisible(false);
        creeClientPane.setVisible(false);
        clientHolder.setVisible(true);
        supprimer.setVisible(false);
        verouiller.setVisible(false);
        infoPane.setVisible(false);
        guichetPane.setVisible(false);
        comGlobPane.setVisible(false);
        holder.getChildren().clear();
        double y = 10.0;
        int valeurFiltreInt = 0;
        try {
            valeurFiltreInt = Integer.parseInt(filter);
        } catch (NumberFormatException ignored) {}

        for(int i = 0; i<gestionnaireGuichet.getComptesGlobales().size();i++){
            if (valeurFiltreInt != 0){
                if (gestionnaireGuichet.getClients().get(i).getCodeClient() == valeurFiltreInt){
                    createComptePaneAdmin(holder,gestionnaireGuichet.getComptesGlobales().get(i),y,gestionnaireGuichet, root);
                    y+=80;
                }
            }
            else{
                createComptePaneAdmin(holder,gestionnaireGuichet.getComptesGlobales().get(i),y,gestionnaireGuichet, root);
                y+=80;
            }
        }
        for(int i = 0; i<gestionnaireGuichet.getRequeteComptes().size();i++){
            if (valeurFiltreInt == 0){
                createCompteRequestList(holder,gestionnaireGuichet.getRequeteComptes().get(i),y,gestionnaireGuichet,root);
                y+=80;
            }

        }
    }

    /**
     * Affiche les information d'un compte
     * @param root
     * @param compte
     * @param gestionnaireGuichet
     */
    public void afficherComptePane(Parent root,Compte compte,GestionnaireGuichet gestionnaireGuichet){
        ScrollPane clientHolder = (ScrollPane) root.lookup("#clients");
        Pane guichetPane = (Pane) root.lookup("#guichetPane");
        Pane creeClientPane = (Pane) root.lookup("#createPane");
        Pane infoPane = (Pane) root.lookup("#info");
        Pane comGlobPane = (Pane) root.lookup("#comGlobalPane");
        Text type = (Text) root.lookup("#type");
        Text balanceText = (Text) root.lookup("#balanceText");
        Text valeur0 = (Text) root.lookup("#valeur0");
        Text info1 = (Text) root.lookup("#info1");
        Text valeur1 = (Text) root.lookup("#value1");
        Text info2 = (Text) root.lookup("#info2");
        Text valeur2 = (Text) root.lookup("#value2");
        Text noText = (Text) root.lookup("#noText");
        Text noTextValue = (Text) root.lookup("#noTextValue");
        Text nip = (Text) root.lookup("#nip");
        Text msgErreur = (Text) root.lookup("#msgErreurPane");
        ImageView image = (ImageView) root.lookup("#image");
        Button supprimer = (Button) root.lookup("#supprimer");
        Button verouiller = (Button) root.lookup("#verouiller");
        TextField montantField = (TextField) root.lookup("#montantHypo");

        guichetPane.setVisible(false);
        comGlobPane.setVisible(false);
        creeClientPane.setVisible(false);
        msgErreur.setVisible(false);
        supprimer.setVisible(true);
        verouiller.setVisible(false);
        clientHolder.setVisible(false);
        infoPane.setVisible(true);
        image.setVisible(false);
        balanceText.setVisible(true);
        valeur0.setVisible(true);
        valeur0.setText(compte.getSolde()+"$");
        supprimer.setText("SUPPRIMER");

        type.setText(compte.getClass().getSimpleName().toUpperCase());
        info1.setText("RETRAIX MAX");
        valeur1.setText(compte.getRetraitMax()+"$");
        info2.setText("TRANSFERT MAX");
        valeur2.setText(compte.getMontantTransfertMaximum()+"$");
        noText.setText("NO COMPTE");
        noTextValue.setText(String.valueOf(compte.getNumeroCompte()));
        nip.setText(createMaskedString(String.valueOf(compte.getNumeroNip()).length()));

        if (compte.getClass().getSimpleName().toUpperCase().equals("HYPOTHECAIRE") && !compte.isDemande() ){
            verouiller.setText("PRÉLEVER");
            verouiller.setVisible(true);
            montantField.setVisible(true);
            verouiller.setOnAction(event -> {
                double valueHypo = 0;
                boolean pass = true;
                try{
                    valueHypo = Double.parseDouble(montantField.getText());

                }catch (Exception e){
                    pass = false;
                    msgErreur.setText("Montant invalide.");
                    msgErreur.setFill(Color.rgb(234,78,78));
                    msgErreur.setVisible(true);
                }
                if (pass){
                    ((Hypothecaire)compte).preleverMontantHypotheque(valueHypo);
                    msgErreur.setText("Montant prélevé.");
                    msgErreur.setFill(Color.rgb(97,150,94));
                    msgErreur.setVisible(true);
                    afficherComptePane(root,compte,gestionnaireGuichet);
                    montantField.setText("");
                }
                montantField.setVisible(false);
            });
        }
        if (compte.isDemande()){
            verouiller.setText("ACCEPTER");
            verouiller.setVisible(true);
            verouiller.setOnAction(event -> {
                compte.setDemande(false);
                gestionnaireGuichet.getComptesGlobales().add(compte);
                compte.getClient().getComptes().add(compte);
                compte.getClient().setRequete(false);
                gestionnaireGuichet.getRequeteComptes().remove(compte);
                msgErreur.setText("Requête de création de compte accepté.");
                msgErreur.setFill(Color.rgb(97,150,94));
                msgErreur.setVisible(true);
            });
        }


        supprimer.setOnAction(event -> {
            for (int i = 0;i<gestionnaireGuichet.getClients().size();i++){
                if (gestionnaireGuichet.getClients().get(i).getCodeClient() == compte.getClient().getCodeClient()){
                    for (int j = 0;j<gestionnaireGuichet.getClients().get(i).getComptes().size();j++){{
                        if (gestionnaireGuichet.getClients().get(i).getComptes().get(j).getNumeroCompte() == compte.getNumeroCompte()){
                            gestionnaireGuichet.getClients().get(i).getComptes().remove(j);
                        }
                    }}
                }
            }
            gestionnaireGuichet.getComptesGlobales().remove(compte);
            msgErreur.setText("Le compte a été supprimé.");
            msgErreur.setFill(Color.rgb(91,215,104));
            msgErreur.setVisible(true);
        });

    }

    /**
     * Affiche le Pane de création de client.
     * @param root
     * @param gestionnaireGuichet
     */
    public void afficherCreeClient(Parent root, GestionnaireGuichet gestionnaireGuichet){
        Pane guichetPane = (Pane) root.lookup("#guichetPane");
        Pane comGlobPane = (Pane) root.lookup("#comGlobalPane");
        ScrollPane clientHolder = (ScrollPane) root.lookup("#clients");
        Pane creeClientPane = (Pane) root.lookup("#createPane");
        Pane infoPane = (Pane) root.lookup("#info");
        Text msgErreur = (Text) root.lookup("#msgErreurPane");
        Button supprimer = (Button) root.lookup("#supprimer");
        Button verouiller = (Button) root.lookup("#verouiller");


        TextField prenom = (TextField) root.lookup("#prenomField");
        TextField nom = (TextField) root.lookup("#nomField");
        TextField courriel = (TextField) root.lookup("#courrielField");
        TextField telephone = (TextField) root.lookup("#telephoneField");
        PasswordField nip = (PasswordField) root.lookup("#nipField");
        TextField montantField = (TextField) root.lookup("#montantHypo");
        comGlobPane.setVisible(false);
        montantField.setVisible(false);
        infoPane.setVisible(false);
        msgErreur.setVisible(false);
        supprimer.setVisible(false);
        verouiller.setVisible(false);
        clientHolder.setVisible(false);
        creeClientPane.setVisible(true);
        guichetPane.setVisible(false);

        supprimer.setText("CONFIRMER");
        supprimer.setVisible(true);
        supprimer.setOnAction(event -> {
            boolean valid = false;
            int valeurNip = 0;

            if (prenom.getText().isEmpty() || nom.getText().isEmpty() || courriel.getText().isEmpty() || telephone.getText().isEmpty()) {
                msgErreur.setText("Tous les champs sont obligatoires.");
                msgErreur.setFill(Color.rgb(212, 99, 99));
                msgErreur.setVisible(true);
            } else {
                valid = true;
            }
            try{
                valeurNip = Integer.parseInt(nip.getText());
            }catch(Exception e){
                valid = false;
            }
            if (valid){
                gestionnaireGuichet.creerClient(gestionnaireGuichet.genererNumeroClient(),nom.getText(),prenom.getText(),telephone.getText(),courriel.getText(),valeurNip);
            }
        });

    }

    /**
     * Affiche le Pane pour les commandes remplir guichet.
     * @param root
     * @param gestionnaireGuichet
     */
    public void afficherRemplirGuichet(Parent root, GestionnaireGuichet gestionnaireGuichet){
        ScrollPane clientHolder = (ScrollPane) root.lookup("#clients");
        Pane guichetPane = (Pane) root.lookup("#guichetPane");
        Pane creeClientPane = (Pane) root.lookup("#createPane");
        Pane infoPane = (Pane) root.lookup("#info");
        Pane comGlobPane = (Pane) root.lookup("#comGlobalPane");
        Text messageOutput = (Text) root.lookup("#messageOutput");

        Text montantGuichet = (Text) root.lookup("#montantGuichet");
        Text valMax = (Text) root.lookup("#valMax");
        Text valDiff = (Text) root.lookup("#valDiff");
        Button confirmer = (Button) root.lookup("#confirmer");

        montantGuichet.setText(gestionnaireGuichet.getBanque().getSolde()+"$");
        valMax.setText(gestionnaireGuichet.getBanque().getMontantMax()+"$");
        valDiff.setText(gestionnaireGuichet.getBanque().getMontantMax()-gestionnaireGuichet.getBanque().getSolde()+"$");

        Button supprimer = (Button) root.lookup("#supprimer");
        Button verouiller = (Button) root.lookup("#verouiller");
        TextField montantField = (TextField) root.lookup("#montantHypo");
        montantField.setVisible(false);
        guichetPane.setVisible(true);
        infoPane.setVisible(false);
        supprimer.setVisible(false);
        verouiller.setVisible(false);
        clientHolder.setVisible(false);
        creeClientPane.setVisible(false);
        comGlobPane.setVisible(false);

        supprimer.setVisible(false);

        confirmer.setOnAction(event -> {
            messageOutput.setText(gestionnaireGuichet.getBanque().getMontantMax()-gestionnaireGuichet.getBanque().getSolde()+"$ ajouté au guichet.");
            messageOutput.setVisible(true);
            gestionnaireGuichet.ajouterFondGuichet();
            afficherRemplirGuichet(root,gestionnaireGuichet);

        });

    }

    /**
     * Affiche le Pane pour les commandes globales
     * @param root
     * @param gestionnaireGuichet
     */
    public void afficherComGlob(Parent root, GestionnaireGuichet gestionnaireGuichet){
        ScrollPane clientHolder = (ScrollPane) root.lookup("#clients");
        Pane guichetPane = (Pane) root.lookup("#guichetPane");
        Pane creeClientPane = (Pane) root.lookup("#createPane");
        Pane infoPane = (Pane) root.lookup("#info");
        Pane comGlobPane = (Pane) root.lookup("#comGlobalPane");

        Text msgSuccess = (Text) root.lookup("#msgSuccess");
        Button payerInteret = (Button) root.lookup("#buttonInteret");
        Button augmenterMarge = (Button) root.lookup("#buttonMarge");

        Button supprimer = (Button) root.lookup("#supprimer");
        Button verouiller = (Button) root.lookup("#verouiller");
        TextField montantField = (TextField) root.lookup("#montantHypo");
        montantField.setVisible(false);
        guichetPane.setVisible(false);
        infoPane.setVisible(false);
        supprimer.setVisible(false);
        verouiller.setVisible(false);
        clientHolder.setVisible(false);
        creeClientPane.setVisible(false);
        comGlobPane.setVisible(true);
        msgSuccess.setVisible(false);
        supprimer.setVisible(false);

        payerInteret.setOnAction(event -> {
            msgSuccess.setText("Tous les comptes épargne ont été payés des intérêts.");
            msgSuccess.setVisible(true);
            gestionnaireGuichet.payerInteret();
        });
        augmenterMarge.setOnAction(event -> {
            msgSuccess.setText("Tous les comptes marges ont été chargés selons leur intérêts.");
            msgSuccess.setVisible(true);
            gestionnaireGuichet.augmenterMarge();
        });

    }

    /**
     * Retourne un String avec des "*" selon la longueur donnée, utilisé pour masquer les nips.
     * @param length
     * @return
     */
    private static String createMaskedString(int length) {
        StringBuilder maskedText = new StringBuilder();
        for (int i = 0; i < length; i++) {
            maskedText.append("*");
        }
        return maskedText.toString();
    }
}

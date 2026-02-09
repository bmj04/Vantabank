package Application.Model;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Scanner;

public class GestionnaireGuichet {
    private Banque banque;
    private ArrayList<Compte> comptesGlobales;
    private ArrayList<Client> clients;
    private ArrayList<Cheque> comptesCheque;
    private ArrayList<Epargne> comptesEpargne;
    private ArrayList<Marge> comptesMarge;
    private ArrayList<Hypothecaire> comptesHypothecaire;
    private ArrayList<Transaction> transactions;
    private ArrayList<Compte> requeteComptes;
    private Client clientCourant;
    private Compte compteCourant;


    public GestionnaireGuichet(Banque banque){
        this.banque=banque;
        this.clients= new ArrayList<Client>();
        this.comptesGlobales = new ArrayList<Compte>();
        this.comptesCheque=new ArrayList<Cheque>();
        this.comptesEpargne=new ArrayList<Epargne>();
        this.comptesMarge=new ArrayList<Marge>();
        this.comptesHypothecaire=new ArrayList<Hypothecaire>();
        this.requeteComptes = new ArrayList<Compte>();
        this.transactions=new ArrayList<Transaction>();
        this.compteCourant=null;
        this.clientCourant = null;
    }

    public Banque getBanque(){return banque;}
    public Compte getCompteCourant(){return compteCourant;}
    public Client getClientCourant(){return clientCourant;}
    public ArrayList<Compte> getComptesGlobales(){return comptesGlobales;}
    public ArrayList<Compte> getRequeteComptes(){return requeteComptes;}
    public void setClientCourant(Client client) {
        clientCourant = client;
    }

    public void setCompteCourant(Compte compte){
        compteCourant = compte;
    }
    public ArrayList<Client> getClients(){return clients;}
    public ArrayList<Cheque> getComptesCheque(){return comptesCheque;}
    public ArrayList<Epargne> getComptesEpargne(){return comptesEpargne;}
    public ArrayList<Marge> getComptesMarge(){return comptesMarge;}
    public ArrayList<Hypothecaire> getComptesHypothecaire(){return comptesHypothecaire;}
    public ArrayList<Transaction> getTransactions(){return transactions;}

    /**
     *
     * @param numClient
     * @return boolean
     *
     * Demande à l'utilisateur de rentrer le nip correspondant au numero du client,
     * si le nip est valide, le programme affiche les comptes du client et demande lequel il veut utiliser.
     * Après avoir choisi le compte, il doit entrer son NIP correspondant et le comptant courrant devient alors
     * le compte choisi.
     *
     * Les variables sont choisi manuellement pour le TP2 mais normalement c'est l'utilisateur qui les entres
     *
     * Retourne Vrai si la connexion est établie
     * Retourne Faux is la connexion a échoué (client ou compte)
     */
    public boolean validerUtilisateur(int numClient){

        //Chercher le client qui correspond au même nom.
        for (Client client : clients) {
            if (client.getCodeClient()==numClient) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param nip
     * @return boolean
     *
     * Normalement ceux sont des entrées par l'utilisateur mais pour le TP2 les variables sont déjà écrits
     *
     * Retourne vrai si le nip entré correspond au nip en parametre sont égaux
     * Retourne faux si les trois tentatives sont échouées
     */
    public boolean validerNip(int nip){
        int essaie = 0;
        while (essaie<3){
            int nipEntrant = nip;
            if (nipEntrant!=nip){
                essaie++;
                System.out.println("NIP incorrect. "+(3-essaie)+" restant(s).");
            }
            else if (nipEntrant==nip){
                return true;
            }
        }
        if (compteCourant!=null){
            compteCourant.setBloquer(true);
        }

        return false;
    }
    public double afficherSoldeCompte(){
        return compteCourant.getSolde();
    }


    //Creerclient et creerCompte sotn des commandes pour les admins
    public void creerClient(int codeClient, String nom, String prenom, String telephone,String courriel, int numeroNIP){
        if (clientCourant.getAdmin()) {
            clients.add(new Client(codeClient, nom, prenom, telephone, courriel, numeroNIP));
        }
    }
    public boolean creerCompte(Client client,String typeCompte,int nipValue, double retraitMax, double transfertMax){

            switch (typeCompte){
                case "CHEQUE":
                    Cheque compteCheque = new Cheque(client,nipValue,genererNouveauNumeroCOmpte(),0.00,retraitMax,transfertMax,100.0);
                    compteCheque.setDemande(true);
                    requeteComptes.add(compteCheque);
                    return true;
                case "EPARGNE":
                    Epargne compteEpargne = new Epargne(client,nipValue,genererNouveauNumeroCOmpte(),0.00,retraitMax,transfertMax);
                    compteEpargne.setDemande(true);
                    requeteComptes.add(compteEpargne);
                    return true;
                case "HYPOTHECAIRE":
                    Hypothecaire compteHypothecaire = new Hypothecaire(client,nipValue,genererNouveauNumeroCOmpte(),0.00,retraitMax,transfertMax);
                    compteHypothecaire.setDemande(true);
                    requeteComptes.add(compteHypothecaire);
                    return true;
                case "MARGE":
                    Marge compteMarge = new Marge(client,nipValue,genererNouveauNumeroCOmpte(),0.00,retraitMax,transfertMax);
                    compteMarge.setDemande(true);
                    requeteComptes.add(compteMarge);
                    return true;
                default:
                    break;
            }
        return false;
    };


    public Transaction creeTransaction(double montant, Compte compte, String typeTransaction,Compte compteDest){
        Transaction uneTransaction = new Transaction(genererNumeroTransaction(),montant,compte,typeTransaction,compteDest);
        transactions.add(uneTransaction);
        return uneTransaction;
    }
    public Transaction creeTransaction(double montant, Compte compte, String typeTransaction){
        Transaction uneTransaction = new Transaction(genererNumeroTransaction(),montant,compte,typeTransaction);
        transactions.add(uneTransaction);
        for (int i = 0; i<clients.size();i++){
            if (clients.get(i).getCodeClient()==compte.getClient().getCodeClient()){
                clients.get(i).getTransactions().add(uneTransaction);
            }
        }
        return uneTransaction;
    }

    /**
     *
     * @param montant
     * @return boolean
     * Retourn vrai si c'est un multiple de 10
     * Retourne Faux si ce n'est pas un multiple de 10
     */
    public boolean multiple10Check(double montant){
        if (montant%10==0){
            return true;
        }
        return false;
    }
    public void afficherSoldeToutLesComptes(){
        for (Compte compte:clientCourant.getComptes()){
            System.out.println("*----------------------------------------*");
            System.out.println(compte.toString());
            System.out.println("*----------------------------------------*");

        }
    }

    public int genererNouveauNumeroCOmpte() {
        int numeroCompteMax = 0;
        for (Compte compte : comptesGlobales) {
            int numeroCompte = compte.getNumeroCompte();
            if (numeroCompte > numeroCompteMax) {
                numeroCompteMax = numeroCompte;
            }
        }
        return numeroCompteMax + 1;
    }
    public int genererNumeroTransaction() {
        int numeroCompteMax = 0;
        for (Transaction transaction : transactions) {
            int numeroCompte = transaction.getNumeroTransaction();
            if (numeroCompte > numeroCompteMax) {
                numeroCompteMax = numeroCompte;
            }
        }
        return numeroCompteMax + 1;
    }
    public int genererNumeroClient() {
        int numeroCompteMax = 0;
        for (Client client : clients) {
            int numeroCompte = client.getCodeClient();
            if (numeroCompte > numeroCompteMax) {
                numeroCompteMax = numeroCompte;
            }
        }
        return numeroCompteMax + 1;
    }
    public void ajouterFondGuichet(){
        if (clientCourant.getAdmin()){
            banque.remplireGuichet();
        }
    }
    public void payerInteret(){
        for (Compte compte:comptesGlobales) {
            if (compte instanceof Epargne){
                ((Epargne) compte).paiementInteret();
            }
        }
    }
    public void preleverMontantHypo(int numCompte, double montant){
        if (clientCourant.getAdmin()) {
            for (Hypothecaire hypothecaire : comptesHypothecaire) {
                if (hypothecaire.getNumeroCompte() == numCompte) {
                    hypothecaire.retrait(montant);
                }
            }
        }
    }
    public void augmenterMarge(){
        if (clientCourant.getAdmin()) {
            for (Compte compte:comptesGlobales) {
                if (compte instanceof Marge){
                    compte.depot(((Marge)compte).getTauxInteret()*compte.getSolde());
                }
            }
        }
    }
    public void debloquerCompte(Compte compte){
        if (clientCourant.getAdmin()){
            compte.setBloquer(false);
        }
    }
    public int getNbCompte(){
        return clientCourant.getComptes().size();
    }
    public double getSoldeGlobal(){
        double montant = 0;
        for (Compte compte:clientCourant.getComptes()){
            if (!(compte instanceof Marge)){
                montant += compte.getSolde();
            }
        }
        return montant;
    }
    public double getMargeGlobal(){
        double montant = 0;
        for (Compte compte:clientCourant.getComptes()){
            if ((compte instanceof Marge)){
                montant += compte.getSolde();
            }
        }
        return montant;
    }

    public void fermerGuichet(){
        System.exit(0);
    }
}
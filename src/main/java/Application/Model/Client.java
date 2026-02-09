package Application.Model;

import java.util.ArrayList;

public class Client {
    private int codeClient;
    private String nom;
    private String prenom;
    private String telephone;
    private String courriel;
    private int numeroNIP;
    private ArrayList<Compte> comptes;
    private boolean admin;
    private boolean bloquer;
    private boolean requete;
    private ArrayList<Transaction> transactions;

    public Client(int codeClient,String nom,String prenom,String telephone,String courriel, int numeroNIP){
        this.codeClient = codeClient;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.courriel = courriel;
        this.numeroNIP = numeroNIP;
        this.comptes = new ArrayList<Compte>();
        this.admin=false;
        this.requete = false;
        this.transactions = new ArrayList<Transaction>();
    }

    public void setAdmin() {
        admin = true;
    }
    public String getTelephone(){return telephone;}
    public boolean getAdmin(){
        return admin;
    }
    public boolean getRequete(){return requete;}
    public String getCourriel(){return courriel;}
    public String getNom(){return nom;}
    public String getPrenom(){return prenom;}
    public int getNumeroNIP(){return numeroNIP;}
    public int getCodeClient(){return codeClient;}
    public boolean getBloquer(){return  bloquer;}
    public ArrayList<Compte> getComptes(){return comptes;}

    public ArrayList<Transaction> getTransactions() {return transactions;}

    public void setBloquer(boolean bloquer) {
        this.bloquer = bloquer;
    }
    public void setRequete(boolean requete){this.requete = requete;}

    /**
     *
     * @param compte
     * Ajoute le compte en parametre dans l'attribut comptes
     */
    public void ajouterCompte(Compte compte){
        comptes.add(compte);
    }
    public String toString() {
        return  "Code Client: " + codeClient + "\n" +
                "Nom: " + nom + "\n" +
                "Prénom: " + prenom + "\n" +
                "Téléphone: " + telephone + "\n" +
                "Courriel: " + courriel + "\n" +
                "Numéro NIP: " + numeroNIP + "\n";
    }


}

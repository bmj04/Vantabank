package Application.Model;

public class Compte {
    protected Client client;
    protected int numeroNip;
    protected int numeroCompte;
    protected double soldeCompte;
    protected double retraitMaximum;
    protected double montantTransfertMaximum;
    private boolean bloquer;
    private boolean demande;

    public Compte(Client client,int numeroNip,int numeroCompte,double soldeCompte,double retraitMaximum,double montantTransfertMaximum){
        this.client = client;
        this.numeroNip = numeroNip;
        this.numeroCompte = numeroCompte;
        this.soldeCompte = soldeCompte;
        this.retraitMaximum = retraitMaximum;
        this.montantTransfertMaximum = montantTransfertMaximum;
        this.bloquer=false;
        this.demande=false;
    }
    public Compte(int numeroNip,int numeroCompte,double soldeCompte,double retraitMaximum,double montantTransfertMaximum){
        this.numeroNip = numeroNip;
        this.numeroCompte = numeroCompte;
        this.soldeCompte = soldeCompte;
        this.retraitMaximum = retraitMaximum;
        this.montantTransfertMaximum = montantTransfertMaximum;
        this.bloquer=false;
    }
    public boolean getBloquer(){return bloquer;}

    public boolean isDemande() {
        return demande;
    }

    public void setBloquer(boolean bloquer) {
        this.bloquer = bloquer;
    }
    public void setDemande(boolean demande){this.demande = demande;}
    public double getRetraitMax() {return retraitMaximum;}
    public double getMontantTransfertMaximum() {return montantTransfertMaximum;}

    public boolean retrait(double montant){
        if (montant<=retraitMaximum && montant>0){
            if (montant<=soldeCompte) {
                soldeCompte -= montant;
            }
            else{
                for (int i =0;i<client.getComptes().size();i++){
                    if (client.getComptes().get(i) instanceof Marge){
                        boolean retrait = client.getComptes().get(i).retrait(montant);
                        System.out.println(montant+" a été retirer de votre compte marge: "+client.getComptes().get(i).getNumeroCompte());
                        return retrait;
                    }
                }
            }
        }
        else{
            System.out.println("Erreur");
        }
        return false;
    }
    public void depot(double montant){
        if (montant>0){
            soldeCompte+=montant;
        }
        else{
            System.out.println("Erreur, montant invalide.");
        }
    }
    public int getNumeroNip(){return numeroNip;}
    public int getNumeroCompte(){return numeroCompte;}
    public double getSolde(){return soldeCompte;}

    public Client getClient() {return client;}

    public String toString() {
        return
                "Type de compte " + getClass().getSimpleName() + "\n" +
                "Numero NIP: " + numeroNip + "\n" +
                "Numero Compte: " + numeroCompte + "\n" +
                "Solde du compte: " + soldeCompte + "\n" +
                "Retrait maximum: " + retraitMaximum + "\n" +
                "Montant de transfert maximum: " + montantTransfertMaximum;
    }
}

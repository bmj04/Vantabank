package Application.Model;

public class Transaction {
    private int numeroTransaction;
    private double montant;
    private Compte compte;
    private String typeTransaction;
    private Compte compteDest;

    public Transaction(int numeroTransaction, double montant, Compte compte, String typeTransaction){
        this.numeroTransaction = numeroTransaction;
        this.montant = montant;
        this.compte = compte;
        this.typeTransaction = typeTransaction;
        this.compteDest = null;
    }
    public Transaction(int numeroTransaction, double montant, Compte compte, String typeTransaction,Compte compteDest){
        this.numeroTransaction = numeroTransaction;
        this.montant = montant;
        this.compte = compte;
        this.typeTransaction = typeTransaction;
        this.compteDest = compteDest;
    }
    public int getNumeroTransaction() {return numeroTransaction;}
    public double getMontant(){return montant;}
    public Compte getCompte() {return compte;}
    public Compte getCompteDest() {return compteDest;}
    public String getTypeTransaction(){return typeTransaction;}
}

package Application.Model;

public class Epargne extends Compte {
    private double tauxInteret;

    public Epargne(Client client, int numeroNip, int numeroCompte, double soldeCompte, double retraitMaximum, double montantTransfertMaximum){
        super(client,numeroNip,numeroCompte,soldeCompte,retraitMaximum,montantTransfertMaximum);

        this.tauxInteret = 1.1;
    }
    public double getTauxInteret(){return tauxInteret;}

    public void paiementInteret(){
        soldeCompte *= tauxInteret;
    }

}

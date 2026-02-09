package Application.Model;

public class Marge extends Compte{
    private double tauxInteret;
    private double marge;

    public Marge(Client client,int numeroNip, int numeroCompte, double soldeCompte, double retraitMaximum, double montantTransfertMaximum){
        super(client,numeroNip,numeroCompte,soldeCompte,retraitMaximum,montantTransfertMaximum);
        this.tauxInteret = 0.1;
        this.marge =0;
    }

    public double getTauxInteret() {
        return tauxInteret;
    }

    public double getMarge(){return marge;}
    public void augmenterSoldeMarge(){
        marge*=1.05;
    }

    public void setMarge(double marge) {
        this.marge = marge;
    }

    public void verifierSiMarge(){
        if (soldeCompte<0){
            marge=0-soldeCompte;
        }
    }
    @Override
    public boolean retrait(double montant){
        if (montant>0) {
            soldeCompte+=montant;
            return true;
        }
        return false;

    }

}

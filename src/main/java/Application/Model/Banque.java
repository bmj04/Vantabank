package Application.Model;

public class Banque extends Compte {
    private double montantMaximum;
    private double montantRemplissage;

    public Banque(int numeroNip, int numeroCompte, double soldeCompte, double retraitMaximum, double montantTransfertMaximum){
        super(numeroNip,numeroCompte,soldeCompte,retraitMaximum,montantTransfertMaximum);
        this.montantMaximum = 20000.0;
        this.montantRemplissage = montantMaximum-soldeCompte;
    }
    public double getMontantMax(){return montantMaximum;}

    public void setMontantMaximum(double montantMaximum) {this.montantMaximum = montantMaximum;}

    public void remplireGuichet(){
        soldeCompte=montantMaximum;
    }
}

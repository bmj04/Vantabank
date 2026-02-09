package Application.Model;

public class Cheque extends Compte {
    private double fraisPaiementFacture;
    private double montantFactureMaximum;
    public Cheque(Client client, int numeroNip, int numeroCompte, double soldeCompte, double retraitMaximum, double montantTransfertMaximum, double montantFactureMaximum){
        super(client,numeroNip,numeroCompte,soldeCompte,retraitMaximum,montantTransfertMaximum);
        this.fraisPaiementFacture = 1.25;
        this.montantFactureMaximum = montantFactureMaximum;
    }
    public double getFraisPaimeentFacture(){return fraisPaiementFacture;}
    public double getMontantFactureMaximum(){return montantFactureMaximum;}
    /**
     *
     * @param montant
     * Fait un retrait du montant entrer en parametre, si le solde est insuffisant
     * le programme va tenter de prélever le montant dans le compte Marge si le client en possède un.
     */
    public boolean paiementFacture(double montant){
        if (montant>0 && montant<=montantFactureMaximum){
            if (montant<=soldeCompte){
                if (montant<=soldeCompte) {
                    soldeCompte -= (montant + fraisPaiementFacture);
                    return true;
                }
            }
            else{
                for (int i =0;i<client.getComptes().size();i++){
                    if (client.getComptes().get(i) instanceof Marge){
                        client.getComptes().get(i).retrait(montant);
                        System.out.println(montant+" a été retirer de votre compte marge: "+client.getComptes().get(i).getNumeroCompte());
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public String toString() {
        return
                "Type de compte " + getClass().getSimpleName() + "\n" +
                "Numero de compte" + numeroCompte + "\n" +
                "Solde du compte: " + soldeCompte + "\n" +
                "Retrait maximum: " + retraitMaximum + "\n" +
                "Transfert maximum: " + montantTransfertMaximum + "\n" +
                "Frais de paiement de facture: " + fraisPaiementFacture + "\n" +
                "Montant maximum pour les factures: " + montantFactureMaximum;
    }
}

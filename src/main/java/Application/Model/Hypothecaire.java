package Application.Model;

public class Hypothecaire extends Compte{
    public Hypothecaire(Client client,int numeroNip, int numeroCompte, double soldeCompte, double retraitMaximum, double montantTransfertMaximum){
        super(client,numeroNip,numeroCompte,soldeCompte,retraitMaximum,montantTransfertMaximum);
    }

    /**
     *
     * @param montant
     * Fait un retrait du montant entrer en parametre, si le solde est insuffisant
     * le programme va tenter de prélever le montant dans le compte Marge si le client en possède un.
     */
    public void preleverMontantHypotheque(double montant){
        if (montant>0){
            if (montant<=soldeCompte) {
                soldeCompte -= montant;
            }
            else{
                boolean aUnCompteMarge=false;
                for (int i =0;i<client.getComptes().size();i++){
                    if (client.getComptes().get(i) instanceof Marge){
                        aUnCompteMarge=true;
                        client.getComptes().get(i).retrait(montant);
                        System.out.println(montant+" a été retirer de votre compte marge: "+client.getComptes().get(i).getNumeroCompte());
                        break;
                    }
                }
                if (!aUnCompteMarge){
                    System.out.println("Pas assez de fond.");
                }
            }
        }
    }
}

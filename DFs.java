package Stage_LIMOS.fermeture;

public class DFs  {

    /* partieGauche -> partieDroite */
    /* Exemple : X -> Y */
    String partieGauche;
    String partieDroite;

    /* Compteur de DFs pour l'ensemble de DFs */
    static int compteurDfs = 0;

    public DFs(String X, String Y){
        partieGauche = X;
        partieDroite = Y;
        compteurDfs++;
    }

    public static int getCompteurDfs(){
        return compteurDfs;
    }

    public static void AfficherDf(DFs df){
        System.out.println(df.partieGauche + "->" + df.partieDroite);
    }

}

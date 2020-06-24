package Stage_LIMOS;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class FermetureLineaire{
    public static void main(String[] args) {

        String univers = "ABCDEF";

        /* Définition des DFs */
        DFs dF0 = new DFs("A","BC");
        DFs dF1 = new DFs("E","CF");
        DFs dF2 = new DFs("B","E");
        DFs dF3 = new DFs("CD","EF");

        /* Création de l'ensemble de DFs */
        ArrayList<DFs> setOfDFs = new ArrayList<DFs>();
        setOfDFs.add(dF0);
        setOfDFs.add(dF1);
        setOfDFs.add(dF2);
        setOfDFs.add(dF3);

        /* Affichage de l'ensemble de DFS */
        System.out.println("Ensemble de dépendances fonctionnelles : ");
        for (int i = 0 ; i < DFs.getCompteurDfs(); i++){
            DFs.AfficherDf(setOfDFs.get(i));
        }

        /* Calcul des fermetures de chaque attribut et stockage dans dico<attribut, sa fermeture> */
        Dictionary<String, StringBuilder> FermetureDeChaqueAttribut = new Hashtable<String, StringBuilder>();
        for (int i = 0; i < univers.length(); i++){
            String attribut = String.valueOf(univers.charAt(i));
            StringBuilder fermeture = AlgoFermetureLineaire(setOfDFs,attribut);
            FermetureDeChaqueAttribut.put(attribut, fermeture);
            System.out.println("Fermeture de " + attribut + " par rapport à F = " + fermeture.toString());
        }
    }

    /* Algorithme : fermeture linéaire */
    public static StringBuilder AlgoFermetureLineaire(ArrayList<DFs> F, String attributDeFermeture){
        /* Analyse attribut->Dfs */

        /* Liste de DF pour chaque attribut X */
        /* Dictionnaire (Attribut, Liste de DFs impliquées à partir de cet attribut */
        Dictionary<String, ArrayList<DFs>> listeDFs = new Hashtable<String, ArrayList<DFs>>();
        /* Compteur du nombre d'attributs W de chaque DF W->Z */
        /* Dictionnaire (DF W->Z, cardinal de W) */
        Dictionary<DFs, Integer> compteurPartieGauche = new Hashtable<DFs, Integer>();
        int compteurDFs =  DFs.getCompteurDfs();
        for(int i = 0 ; i < compteurDFs; i++){
            compteurPartieGauche.put(F.get(i), F.get(i).partieGauche.length());
            for(int j = 0; j < compteurPartieGauche.get(F.get(i)); j++){
                String attributString = String.valueOf(F.get(i).partieGauche.charAt(j));
                AjoutDfListeAttribut(attributString, F.get(i), listeDFs);
            }
        }

        /* Initialisation */
        StringBuilder fermeture = new StringBuilder(attributDeFermeture);
        String update = attributDeFermeture;

        /* Calcul de la fermeture */
        while (!update.isEmpty())
        {
            /* Choix arbitraire du premier attribut de update à chaque tour */
            String attributA = update.substring(0,1);
            update = update.substring(1);

            if(listeDFs.get(attributA) != null){
                for (int i = 0; i < listeDFs.get(attributA).size(); i++){
                    DFs df = (listeDFs.get(attributA)).get(i);
                    CompteurDecremente(compteurPartieGauche, df);
                    if (compteurPartieGauche.get(df) == 0){
                        update = update + AbsentDansFermeture(df.partieDroite, fermeture.toString());
                        AjoutAttributDansFermeture(fermeture,df.partieDroite);
                    }
                }
            }

        }
        return fermeture;
    }

    /* Fonction ajout DF dans listeAttribut */
    public static void AjoutDfListeAttribut (String attribut, DFs df, Dictionary<String, ArrayList<DFs>> dico) {
        ArrayList<DFs> listeAttribut = new ArrayList<DFs>();
        if(dico.get(attribut) != null){
            listeAttribut = CopieListeDf(dico.get(attribut));
        }
        listeAttribut.add(df);
        dico.remove(attribut);
        dico.put(attribut, listeAttribut);
    }

    /* Fonction décrémentation du compteur dans le dictionnaire */
    public static void CompteurDecremente(Dictionary<DFs, Integer> dico, DFs df){
        int compteur = dico.get(df);
        compteur--;
        dico.remove(df);
        dico.put(df, compteur);
    }

    /* Fonction W absent dans fermeture */
    /* Retourne true si W est présent et false sinon */
    public static String AbsentDansFermeture(String partieDroite, String fermeture){
        String absent = new String();
        for (int i = 0 ; i < partieDroite.length(); i++){
            if (!fermeture.contains(String.valueOf(partieDroite.charAt(i)))){
                absent = absent + String.valueOf(partieDroite.charAt(i));
            }
        }
        return absent;
    }

    /* Fonction ajout d'attributs à la fermeture */
    public static void AjoutAttributDansFermeture (StringBuilder fermeture, String attributAAjouter){
        String attributString;
        for (int i =0; i < attributAAjouter.length(); i++)
        {
            attributString = String.valueOf(attributAAjouter.charAt(i));
            if (!fermeture.toString().contains(attributString)){
                fermeture.append(attributString);
            }
        }
    }

    /* Fonction copie d'une listeDF dans une autre */
    public static ArrayList<DFs> CopieListeDf(ArrayList<DFs> listeDF){
        ArrayList<DFs> copie = new ArrayList<DFs>();
        for(int i = 0; i < listeDF.size(); i++){
            copie.add(listeDF.get(i));
        }
        return copie;
    }
}

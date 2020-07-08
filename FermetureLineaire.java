package Stage_LIMOS;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;

public class FermetureLineaire{
    public static void main(String[] args) {

        String univers = "ABCDEF";
        System.out.println("Univers :" + univers);
        System.out.println("");

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
        System.out.println("");

        /* Calcul de tous les fermés */
        ArrayList<String> ensembleDesFermes = new ArrayList<String>();
        AllClosures(ensembleDesFermes, univers, setOfDFs);

        /* Affichage de l'ensemble des fermés */
        System.out.println("Ensemble de tous les fermés : ");
        System.out.print("{");
        for (int i = 0 ; i < ensembleDesFermes.size() - 1; i++){
            System.out.print(ensembleDesFermes.get(i) + " , ");
        }
        System.out.print(ensembleDesFermes.get(ensembleDesFermes.size() - 1));
        System.out.print("}");
    }

    /* ---------------------------------------------------------------------------------------*/
    /* -------------------------------FERMETURE LINEAIRE--------------------------------------*/
    /* ---------------------------------------------------------------------------------------*/

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
        fermeture = TriRapideOrdreAlphabétique(fermeture);
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

    /* ---------------------------------------------------------------------------------------*/
    /* -------------------------------NEXT CLOSURE--------------------------------------------*/
    /* ---------------------------------------------------------------------------------------*/

    /* Fonction NextClosure : calcul du fermé suivant */
    public static StringBuilder NextClosure(StringBuilder A, String univers, ArrayList<DFs> ensembleDfs){
        int longueurUnivers = univers.length();
        String maxAttribut = String.valueOf(univers.charAt(longueurUnivers - 1));
        StringBuilder attribut = new StringBuilder(maxAttribut);
        boolean success = false;
        int indiceBoucleDo = 0;
        do{
            /* Première itération, il faut traiter l'attribut max avant de traiter ses prédéceseurs */
            if(indiceBoucleDo != 0){
                attribut = predecesseurString(attribut, univers);
            }
            if (!A.toString().contains(attribut.toString())){
                String Asaved = A.toString();
                A = ConserverElementInferieurAttribut(A, attribut, univers);
                StringBuilder fermetureA = AlgoFermetureLineaire(ensembleDfs,A.toString());
                if(!PresenceNewAttributInfA(attribut, A, fermetureA, univers)){
                    A = fermetureA;
                    success = true;
                }
                else{
                    A = new StringBuilder(Asaved);
                }
            }
            indiceBoucleDo++;
        }while ((!success) && (univers.indexOf(attribut.toString()) != 0));
        return A;
    }

    /* Fonction prédecesseur dans un String */
    /* Retourne le prédecesseur s'il y en a un sinon, retourne l'attribut donné en entrée */
    public static StringBuilder predecesseurString (StringBuilder caractere, String chaine){
        int indiceAttribut = chaine.indexOf(caractere.toString());
        if ((indiceAttribut < chaine.length()) && (indiceAttribut > 0)){
            String att = String.valueOf(chaine.charAt(indiceAttribut - 1));
            caractere = new StringBuilder(att);
        }
        return caractere;
    }

    /* Fonction qui garde uniquement les éléments inférieurs à attribut dans A */
    public static StringBuilder ConserverElementInferieurAttribut (StringBuilder A, StringBuilder attribut, String univers){
        A.append(attribut.toString());
        StringBuilder newA = new StringBuilder();
        int indiceAttribut = univers.indexOf(attribut.toString());
        for(int i = 0; i < A.length(); i++){
            int indiceTemp = univers.indexOf(A.charAt(i));
            if (indiceTemp <= indiceAttribut)
                newA.append(A.charAt(i));
        }
        return newA;
    }

    /* Fonction PresenceNewAttributInfA qui indique si fermetureA\A possède un attribut inférieur à attribut */
    /* Fonction retroune true s'il y en a au moins un, false sinon */
    public static boolean PresenceNewAttributInfA(StringBuilder attribut, StringBuilder A, StringBuilder fermetureA, String univers){
        boolean present = false;
        StringBuilder fermeturePriveeDeA = new StringBuilder();
        /* Création de l'ensemble fermetureA\A */
        for(int i = 0; i < fermetureA.length(); i++){
            if(!A.toString().contains(String.valueOf(fermetureA.charAt(i)))){
                fermeturePriveeDeA.append(fermetureA.charAt(i));
            }
        }
        int indiceAttribut = univers.indexOf(attribut.toString());
        /* Suppression des éléments >= attribut*/
        int i = 0; //indice de la boucle while
        while ((!present) && (i < fermeturePriveeDeA.length())){
            int indiceTemp = univers.indexOf(String.valueOf(fermeturePriveeDeA.charAt(i)));
            if (indiceAttribut > indiceTemp)
                present = true;
            i++;
        }
        return present;
    }

    /* Fonction tri par ordre alphabétique */
    public static StringBuilder TriRapideOrdreAlphabétique (StringBuilder chaine) {
        int taille = chaine.length();
        int chaineAscii[] = new int[taille];
        for (int i = 0; i < taille; i++){
            chaineAscii[i] = chaine.charAt(i);
        }
        Arrays.sort(chaineAscii);
        chaine = new StringBuilder();
        for (int i = 0; i < taille; i++){
            chaine.append(String.valueOf((char)chaineAscii[i]));
        }
        return chaine;
    }

    /* ---------------------------------------------------------------------------------------*/
    /* -------------------------------ALL CLOSURES--------------------------------------------*/
    /* ---------------------------------------------------------------------------------------*/

    public static void AllClosures (ArrayList<String> ensembleDesFermes, String univers, ArrayList<DFs> F){
        ensembleDesFermes.add("");
        String Asaved = new String();
        StringBuilder fermeSuivant = new StringBuilder();
        boolean fini = false;
        do {
            int taille = ensembleDesFermes.size();
            Asaved = ensembleDesFermes.get(taille - 1);
            StringBuilder A = new StringBuilder(Asaved);
            fermeSuivant = NextClosure(A, univers, F);
            fini = Asaved.equals(fermeSuivant.toString());
            if (!fini)
                ensembleDesFermes.add(fermeSuivant.toString());
        } while (!fini);

    }


}

package Stage_LIMOS.fermeture;

public class FermetureNiveauxBis {
        public static void main(String[] args) {

            /* Définition des DFs */
            DFs dF0 = new DFs("A","BC");
            DFs dF1 = new DFs("E","CF");
            DFs dF2 = new DFs("B","E");
            DFs dF3 = new DFs("CD","EF");

            /* Création de l'ensemble de DFs */
            DFs[] setOfDF = {dF0, dF1, dF2, dF3};

            /* Affichage de l'ensemble de DFS */
            System.out.println("Ensemble de dépendances fonctionnelles : ");
            for (int i = 0 ; i < DFs.getCompteurDfs(); i++){
                DFs.AfficherDf(setOfDF[i]);
            }

            /* Fermeture par niveaux */
            String attributFermeture = "AB";
            String univers = "ABCDEF";
            String fermeture = AlgoFermetureParNiveauxBis(setOfDF, attributFermeture, univers);
            System.out.println("Fermeture de " + attributFermeture + " par rapport à F = " + fermeture);

        }

        /* Fermeture par niveaux */
        public static String AlgoFermetureParNiveauxBis (DFs[] F, String attributDeFermeture, String univers){
            String fermeture = attributDeFermeture;
            int compteurUnused = DFs.getCompteurDfs();
            boolean fini;
            do {
                fini = true;
                for (int i = 0; i < compteurUnused; i++){
                    if ((PresentDans(F[i].partieGauche, fermeture)) && (!PresentDans(F[i].partieDroite, fermeture))){
                        fermeture = AjoutAttributDansFermeture(fermeture, F[i].partieDroite);
                        fini = false;
                    }
                }
            }while ((!fini) || (fermeture.equals(univers)));
            return fermeture;
        }

        /* Fonction W présent dans fermeture */
        /* Retourne true si W est présent et false sinon */
        public static boolean PresentDans(String partieGauche, String fermeture){
            boolean resultatPresence = false;
            int present = - (partieGauche.length());
            for (int i = 0 ; i < partieGauche.length(); i++){
                if (fermeture.contains(String.valueOf(partieGauche.charAt(i)))){
                    present++;
                }
            }
            if (present == 0) resultatPresence = true;
            return resultatPresence;
        }


        /* Fonction Suppression d'une DF */
        public static void SuppressionDf (DFs[] F, int longueurListeF, int rangDF){
            for (int i = rangDF; i < longueurListeF - 1; i++){
                F[i].partieGauche = F[i+1].partieGauche;
                F[i].partieDroite = F[i+1].partieDroite;
            }
        }

        /* Fonction ajout d'attributs à la fermeture */
        public static String AjoutAttributDansFermeture (String fermeture, String attributAAjouter){
            String attributString;
            for (int i =0; i < attributAAjouter.length(); i++)
            {
                attributString = String.valueOf(attributAAjouter.charAt(i));
                if (!fermeture.contains(attributString)){
                    fermeture = fermeture + attributString;
                }
            }
            return fermeture;
        }
}

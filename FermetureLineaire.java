package Stage_LIMOS;
import java.io.*;
import java.util.*;

public class FermetureLineaire{
    public static void main(String[] args) {

        //Chemin pour sauvegarde des fichiers textes d'entrée et de sortie
        final String chemin = "C:/Users/Anaïs Darricarrère/Desktop/Stage Limos/";

        //String univers = "ABCDEF";
        //String univers = LectureUnivers(chemin +"universDFsVide.txt");
        String univers = LectureUnivers(chemin +"universDFs.txt");
        System.out.println("Univers :" + univers);
        System.out.println("");

        /* Définition des DFs */
        /*DFs dF0 = new DFs("A", "BC");
        DFs dF1 = new DFs("E", "CF");
        DFs dF2 = new DFs("B", "E");
        DFs dF3 = new DFs("CD", "EF");
        DFs dF0 = new DFs("A", "B");
        DFs dF1 = new DFs("", "C");*/

        /* Création de l'ensemble de DFs */
        //ArrayList<DFs> setOfDFs = LectureEnsembleDfs(chemin + "universDFsVide.txt");
        ArrayList<DFs> setOfDFs = LectureEnsembleDfs(chemin + "universDFs.txt");
        /*setOfDFs.add(dF0);
        setOfDFs.add(dF1);
        setOfDFs.add(dF2);
        setOfDFs.add(dF3);*/

        /* Affichage de l'ensemble de DFS */
        System.out.println("Ensemble de dépendances fonctionnelles : ");
        for (int i = 0; i < DFs.getCompteurDfs(); i++) {
            DFs.AfficherDf(setOfDFs.get(i));
        }
        System.out.println();

        /* Calcul de tous les fermés */
        ArrayList<String> ensembleDesFermes = new ArrayList<String>();
        AllClosures(ensembleDesFermes, univers, setOfDFs);

        /* Affichage de l'ensemble des fermés */
        //AffichageEnsembleFermes(ensembleDesFermes);
        //System.out.println();

        /* Création de l'arbre des fermés et son affichage */
        TreeMap<String, Boolean> arbreFermes = new TreeMap<String, Boolean>();
        ArbreFermes(arbreFermes, ensembleDesFermes);
        arbreFermes.headMap("");
        //System.out.println("Arbre des fermés : " + arbreFermes.keySet());
        //System.out.println();
        CreerFichierFermes(chemin, "arbreFermes.txt", arbreFermes);

        /* Création du graphe père/fils */
        Dictionary<String, ArrayList<String>> pereFils = new Hashtable<String, ArrayList<String>>();
        GrapheFermes(pereFils, ensembleDesFermes, univers);

        /* Affichage du graphe */
        //AffichagePereFils(pereFils, ensembleDesFermes);
        //System.out.println();

        /* Calcul des inf-irréductibles */
        ArrayList<String> listeInfIrreductibles = EnsembleInfIrreductibles(pereFils,ensembleDesFermes, univers);
        //AffichageInfIrreductibles(listeInfIrreductibles);
        //System.out.println();
        CreerFichierInfIrreductibles(chemin, "infIrreductibles.txt", listeInfIrreductibles);

        /* Calcul relation exemple */
        int [][] relationExemple = CreationRelationExemple(listeInfIrreductibles, univers);
        //AffichageRelationExemple(relationExemple, listeInfIrreductibles, univers);
        CreerFichierRelationExemple(chemin, "relationExemple.txt",relationExemple, listeInfIrreductibles, univers);

    }

    /* ---------------------------------------------------------------------------------------*/
    /* -------------------------------FERMETURE LINEAIRE--------------------------------------*/
    /* ---------------------------------------------------------------------------------------*/

    /* Algorithme : fermeture linéaire */
    public static StringBuilder AlgoFermetureLineaire(ArrayList<DFs> F, String attributDeFermeture){
        // Analyse attribut->Dfs

        String fermetureDuVide = new String();
        //Liste de DF pour chaque attribut X
        //Dictionnaire (Attribut, Liste de DFs impliquées à partir de cet attribut
        Dictionary<String, ArrayList<DFs>> listeDFs = new Hashtable<String, ArrayList<DFs>>();
        /* Compteur du nombre d'attributs W de chaque DF W->Z */
        /* Dictionnaire (DF W->Z, cardinal de W) */
        Dictionary<DFs, Integer> compteurPartieGauche = new Hashtable<DFs, Integer>();
        int compteurDFs =  DFs.getCompteurDfs();
        for(int i = 0 ; i < compteurDFs; i++){
            compteurPartieGauche.put(F.get(i), F.get(i).partieGauche.length());
            if (compteurPartieGauche.get(F.get(i)) == 0){
                fermetureDuVide = fermetureDuVide + F.get(i).partieDroite;
            }
            else {
                for(int j = 0; j < compteurPartieGauche.get(F.get(i)); j++){
                    String attributString = String.valueOf(F.get(i).partieGauche.charAt(j));
                    AjoutDfListeAttribut(attributString, F.get(i), listeDFs);
                }
            }
        }

        // Initialisation de la fermeture par l'attribu et la fermeture du vide
        StringBuilder fermeture = new StringBuilder(attributDeFermeture + fermetureDuVide);

        // Initialisation avec la fermeture initiale;
        String update = fermeture.toString();

        // Calcul de la fermeture
        while (!update.isEmpty())
        {
            //Choix arbitraire du premier attribut de update à chaque tour
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
        fermeture = TriRapideOrdreAlphabetique(fermeture);
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
            // Première itération, il faut traîter l'attribut max avant de traîter ses prédécesseurs
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

        // Création de l'ensemble fermetureA\A
        for(int i = 0; i < fermetureA.length(); i++){
            if(!A.toString().contains(String.valueOf(fermetureA.charAt(i)))){
                fermeturePriveeDeA.append(fermetureA.charAt(i));
            }
        }
        int indiceAttribut = univers.indexOf(attribut.toString());

        // Suppression des éléments >= attribut
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
    public static StringBuilder TriRapideOrdreAlphabetique (StringBuilder chaine) {
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
        // Initialisation de l'ensemble des fermés avec la fermeture du vide
        ensembleDesFermes.add(AlgoFermetureLineaire(F,"").toString());
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

    /* Fonction d'affichage de la liste des fermés */
    public static void AffichageEnsembleFermes(ArrayList<String> ensembleDesFermes){
        System.out.println("Ensemble de tous les fermés : ");
        AfficherListe(ensembleDesFermes);
    }

    /* ---------------------------------------------------------------------------------------*/
    /* -----------------------------------ARBRE-----------------------------------------------*/
    /* ---------------------------------------------------------------------------------------*/

    /* Fonction qui creer l'arbre de tous les fermés */
    public static void ArbreFermes (TreeMap<String, Boolean> tree, ArrayList<String> listeFermes){
        tree.put(listeFermes.get(0), true);
        for(int i = 1; i < listeFermes.size(); i++){
            tree.put(listeFermes.get(i), true);
        }
    }

    /* ---------------------------------------------------------------------------------------*/
    /* -------------------------------GRAPHE -> DICO------------------------------------------*/
    /* ---------------------------------------------------------------------------------------*/

    public static void GrapheFermes (Dictionary<String, ArrayList<String>> pereFils, ArrayList<String> tousLesFermes, String univers){
        Dictionary<Integer, ArrayList<String>> triParTaille = new Hashtable<Integer, ArrayList<String>>();
        TrierParTaille(triParTaille, tousLesFermes, univers);
        // Création du dictionnaire père

        // Initialisation liste fermés sans père
        ArrayList<String> listeOrphelins = new ArrayList<String>();
        // Copie de la liste des fermés excepté la racine
        for (int i = 1; i < tousLesFermes.size(); i++)
            listeOrphelins.add(tousLesFermes.get(i));

        // Boucle for jusqu'à univers.length +1 car l'ensemble vide est traité
        for (int generationI = 0; generationI < univers.length() + 1; generationI++){
            if(triParTaille.get(generationI) != null){
                // Liste des fermés de longueur = generationI
                ArrayList<String> listeFermeLongueurI = triParTaille.get(generationI);
                // Pour chaque fermé, on regarde s'il a des fils de longueur=generation +1
                for (int j = 0 ; j < listeFermeLongueurI.size(); j++){
                    String pere = listeFermeLongueurI.get(j);
                    ArrayList<String> listeFils = RechercheFils(triParTaille, pere, univers, listeOrphelins);
                    //On ajoute le fermé dans le dictionnaire père/fils
                    pereFils.put(listeFermeLongueurI.get(j), listeFils);
                }
            }
        }
        // Vérification que tout fermé a un père
        if (!listeOrphelins.isEmpty()){
            // S'il y a des fermés sans père, il faut trouver leurs ancêtres les plus proches
            for (int i = 0; i < listeOrphelins.size(); i++){
                String fils = listeOrphelins.get(i);
                ArrayList<String> listePeres = RecherchePeres(triParTaille, fils);
                //Ajout des fils orphelins à leurs pères
                for (int k = 0; k < listePeres.size(); k++)
                    pereFils.get(listePeres.get(k)).add(fils);
            }
        }
    }

    /* Fonction Tri par taille dans un dictionnaire */
    public static void TrierParTaille (Dictionary<Integer, ArrayList<String>> dicoTri, ArrayList<String> ensembleFermes, String univers){
        for(int i = 0; i < ensembleFermes.size(); i++){
            int longueur = ensembleFermes.get(i).length();
            ArrayList<String> liste = new ArrayList<String>();
            if (dicoTri.get(longueur) != null){
                liste = dicoTri.get(longueur);
                dicoTri.remove(longueur);
            }
            liste.add(ensembleFermes.get(i));
            dicoTri.put(longueur, liste);
        }
    }

    /* Fonction InclusionString */
    /* Si A est inclus dans B retourne true, false sinon */
    public static boolean InclusionString(String A, String B)
    {
        int compteur = 0;
        boolean inclus = false;
        for (int i = 0; i < A.length(); i++){
            if(B.contains(String.valueOf(A.charAt(i))))
                compteur++;
        }
        if(compteur == A.length())
            inclus = true;
        return inclus;
    }

    /* Fonction Recherche fils */
    /* Retourne la liste des fils */
    public static ArrayList<String> RechercheFils(Dictionary<Integer, ArrayList<String>> triParTaille, String pere, String univers, ArrayList<String> listeOrphelins) {
        ArrayList<String> listeFils = new ArrayList<String>();
        int generationFils = pere.length() + 1;
        // Boucle while jusqu'à univers.length +1 car l'ensemble vide est traité
        while ((listeFils.isEmpty()) && (generationFils != univers.length() + 1)) {
            ArrayList<String> listeFilsPotentiels = triParTaille.get(generationFils);
            if (listeFilsPotentiels != null) {
                for (int k = 0; k < listeFilsPotentiels.size(); k++) {
                    if (InclusionString(pere, listeFilsPotentiels.get(k))) {
                        listeFils.add(listeFilsPotentiels.get(k));
                        if (listeOrphelins.contains(listeFilsPotentiels.get(k)))
                            listeOrphelins.remove(listeFilsPotentiels.get(k));
                    }
                }
            }
            generationFils++;
        }
        return listeFils;
    }

    /* Fonction Recherche pères */
    /* Retourne la liste des pères */
    public static ArrayList<String> RecherchePeres(Dictionary<Integer, ArrayList<String>> triParTaille, String fils) {
        ArrayList<String> listePeres = new ArrayList<String>();
        if (fils.length() != 0){
            int generationPere = fils.length() - 1;
            // Boucle while jusqu'à 0 car l'ensemble vide est traité
            while ((listePeres.isEmpty()) && (generationPere != 0)) {
                ArrayList<String> listePeresPotentiels = triParTaille.get(generationPere);
                if (listePeresPotentiels != null) {
                    for (int k = 0; k < listePeresPotentiels.size(); k++) {
                        if (InclusionString(listePeresPotentiels.get(k), fils)){
                            listePeres.add(listePeresPotentiels.get(k));
                        }
                    }
                }
                generationPere--;
            }
        }
        else
            listePeres.add("");
        return listePeres;
    }


    /* Fonction d'affichage du dictionnaire père fils */
    public static void AffichagePereFils(Dictionary<String, ArrayList<String>> pereFils, ArrayList<String> ensembleDesFermes){
        System.out.println("Graphe de tous les fermés : ");
        for (int i = 0; i < ensembleDesFermes.size(); i++) {
            String index = ensembleDesFermes.get(i);
            System.out.print(index + " a pour fils : ");
            for (int j = 0; j < pereFils.get(index).size() ; j++){
                System.out.print(pereFils.get(index).get(j) + "  ");
            }
            System.out.println();
        }
    }

    /* ---------------------------------------------------------------------------------------*/
    /* -------------------------------INF IRREDUCTIBLES---------------------------------------*/
    /* ---------------------------------------------------------------------------------------*/

    public static ArrayList<String> EnsembleInfIrreductibles(Dictionary<String, ArrayList<String>> pereFils, ArrayList<String> ensembleDesFermes, String univers){
        ArrayList<String> listeInfIrreductibles = new ArrayList<String>();

        //Parcours du dictionnaire en regardant le nombre de fils de chaque fermé : s'il est < 2 alors le fermé est un inf-irréductible
        for(int i = 0; i < ensembleDesFermes.size(); i++){
            // Tout fermé a au moins un fils sauf l'univers qui en a 0 mais l'univers n'est pas un inf-irréductible par définition
            if (pereFils.get(ensembleDesFermes.get(i)).size() == 1)
                listeInfIrreductibles.add(ensembleDesFermes.get(i));
        }
        return listeInfIrreductibles;
    }


    /* Fonction d'affichage de la liste des fermés */
    public static void AffichageInfIrreductibles(ArrayList<String> ensembleInfIrreductibles){
        System.out.println("Ensemble des inf-irréductibles : ");
        AfficherListe(ensembleInfIrreductibles);
    }


    /* ---------------------------------------------------------------------------------------*/
    /* -------------------------------RELATION EXEMPLE----------------------------------------*/
    /* ---------------------------------------------------------------------------------------*/

    public static int[][] CreationRelationExemple (ArrayList<String> listeInfIrreductibles, String univers){
        int[][] relationExemple = new int[listeInfIrreductibles.size() + 1][univers.length()];
        // Initialisation de la première ligne
        for(int j = 0; j < univers.length(); j++)
            relationExemple[0][j] = 0;
        // Construction de la matrice
        for (int i = 0 ; i < listeInfIrreductibles.size(); i++){
            for(int j = 0 ; j < univers.length(); j++){
                if(listeInfIrreductibles.get(i).contains(String.valueOf(univers.charAt(j))))
                    relationExemple[i+1][j] = relationExemple[i][j];
                else
                    relationExemple[i+1][j] = relationExemple[i][j] + 1;
            }
        }
        return relationExemple;
    }

    public static void AffichageRelationExemple(int[][] relationExemple, ArrayList<String> listeInfIrreductibles, String univers){
        System.out.println("Relation exemple :");
        //Première ligne
        for (int i = 0; i < univers.length(); i++)
            System.out.print(" ");
        System.out.print(" | ");
        for (int i = 0; i < univers.length(); i++)
            System.out.print(" " + univers.charAt(i) + " ");
        System.out.println();

        //Ligne
        int tailleLigne = 4*univers.length() + 3 ;
        for(int k = 0 ; k < tailleLigne; k++){
            System.out.print("_");
        }
        System.out.println();

        // Affichage matrice
        for(int i = 0; i < relationExemple.length ; i++){
            if (i == 0){
                for(int k = 0; k < univers.length(); k++)
                    System.out.print(" ");
            }
            else{
                System.out.print(listeInfIrreductibles.get(i-1));
                for(int k = 0; k < univers.length() - listeInfIrreductibles.get(i-1).length(); k++)
                    System.out.print(" ");
            }



            System.out.print(" | ");
            for (int j = 0; j < relationExemple[i].length; j++)
                System.out.print(" " + relationExemple[i][j] + " ");
            System.out.println();
        }
    }

    /* ---------------------------------------------------------------------------------------*/
    /* -------------------------------AFFICHAGE LISTE-----------------------------------------*/
    /* ---------------------------------------------------------------------------------------*/

    public static void AfficherListe(ArrayList<String> liste){
        System.out.print("{");
        for (int i = 0; i < liste.size() - 1; i++) {
            System.out.print(liste.get(i) + " , ");
        }
        System.out.print(liste.get(liste.size() - 1));
        System.out.println("}");
    }

    /* ---------------------------------------------------------------------------------------*/
    /* -------------------------------FICHIER TEXTE-------------------------------------------*/
    /* ---------------------------------------------------------------------------------------*/

    /* Fonction lecture fichier texte comprenant l'univers*/
    public static String LectureUnivers (String cheminFichier){
        String univers = new String();
        try {
            File fichier = new File(cheminFichier);
            FileReader reader = new FileReader(fichier);
            BufferedReader bufferedReader = new BufferedReader(reader);
            //Première ligne est l'univers
            try {
                String ligne = bufferedReader.readLine();
                if (ligne != null){
                    univers = ligne;
                }
                bufferedReader.close();
                reader.close();
            } catch (IOException e) {
                System.out.println("Erreur lors de la lecture du fichier " + cheminFichier);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Le fichier " + cheminFichier + " n'a pas été trouvé");
        }
        return univers;
    }

    /* Fonction lecture fichier texte comprenant les DFs*/

    public static ArrayList<DFs> LectureEnsembleDfs (String cheminFichier){
        ArrayList<DFs> ensembleDfs = new ArrayList<DFs>();
        try {
            File fichier = new File(cheminFichier);
            FileReader reader = new FileReader(fichier);
            BufferedReader bufferedReader = new BufferedReader(reader);
            //Première ligne est l'univers
            //Deuxième ligne vide
            //Ensuite une ligne par DF de la forme "AB -> C" pour A,B-> C
            try {
                String ligne = new String();
                int indiceLigne = 0;
                ligne = bufferedReader.readLine();
                while (ligne != null){
                    indiceLigne++;
                    if ((indiceLigne > 2) && (ligne.contains("->"))){
                        String[] resultat = ligne.split("->");
                        ensembleDfs.add(new DFs(resultat[0].trim(),resultat[1].trim()));
                    }
                    ligne = bufferedReader.readLine();
                }
                bufferedReader.close();
                reader.close();
            } catch (IOException e) {
                System.out.println("Erreur lors de la lecture du fichier " + cheminFichier);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Le fichier " + cheminFichier + " n'a pas été trouvé");
        }
        return ensembleDfs;
    }

    /* Fonctions création fichiers textes sauvergardes */
    public static void CreerFichierFermes(String chemin, String nomFichier, TreeMap<String, Boolean> arbreFermes){
        File fichier = new File(chemin + nomFichier);
        try {
            // Creation du fichier
            fichier.createNewFile();
            FileWriter writer = new FileWriter(fichier);
            try {
                writer.write("Arbre des fermés : \n" + arbreFermes.keySet());
            } finally {
                writer.close();
            }
        }catch (Exception e) {
            System.out.println("Creation du fichier " + nomFichier + " impossible");
        }
    }

    public static void CreerFichierInfIrreductibles(String chemin, String nomFichier, ArrayList<String> listeInfIrreductibles){
        File fichier = new File(chemin + nomFichier);
        try {
            // Creation du fichier
            fichier.createNewFile();
            FileWriter writer = new FileWriter(fichier);
            try {
                writer.write("Ensemble des inf-irréductibles : \n");
                writer.write("{");
                for (int i = 0; i < listeInfIrreductibles.size() - 1; i++) {
                    writer.write(listeInfIrreductibles.get(i) + " , ");
                }
                writer.write(listeInfIrreductibles.get(listeInfIrreductibles.size() - 1));
                writer.write("}");
            } finally {
                writer.close();
            }
        }catch (Exception e) {
            System.out.println("Creation du fichier " + nomFichier + " impossible");
        }
    }

    public static void CreerFichierRelationExemple(String chemin, String nomFichier , int[][] relationExemple, ArrayList<String> listeInfIrreductibles, String univers){
        File fichier = new File(chemin + nomFichier);
        try {
            // Creation du fichier
            fichier.createNewFile();
            FileWriter writer = new FileWriter(fichier);
            try {
                writer.write("Relation exemple : \n");
                //Première ligne
                for (int i = 0; i < univers.length(); i++)
                    writer.write(" ");
                writer.write(" | ");
                for (int i = 0; i < univers.length(); i++)
                    writer.write(" " + univers.charAt(i) + " ");
                writer.write("\n");
                //Ligne
                int tailleLigne = 4*univers.length() + 3 ;
                for(int k = 0 ; k < tailleLigne; k++){
                    writer.write("_");
                }
                writer.write("\n");

                // Affichage matrice
                for(int i = 0; i < relationExemple.length ; i++){
                    if (i == 0){
                        for(int k = 0; k < univers.length(); k++)
                            writer.write(" ");
                    }
                    else{
                        writer.write(listeInfIrreductibles.get(i-1));
                        for(int k = 0; k < univers.length() - listeInfIrreductibles.get(i-1).length(); k++)
                            writer.write(" ");
                    }
                    writer.write(" | ");
                    for (int j = 0; j < relationExemple[i].length; j++)
                        writer.write(" " + relationExemple[i][j] + " ");
                    writer.write("\n");
                }
            } finally {
                writer.close();
            }
        }catch (Exception e) {
            System.out.println("Creation du fichier " + nomFichier + " impossible");
        }
    }

}

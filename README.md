# Stage_LIMOS
Développement d'un algorithme de construction automatique de relation exemple à partir des dépendances fonctionnelles 

En entrée, la procédure prend en argument un fichier texte construit de la manière suivante :
- Première ligne : l'univers,
- Deuxième ligne : ligne blanche de séparation
- Troisième ligne et jusqu'à la fin du fichier : une ligne par dépendance fonctionnelle du type A,B -> C écrite sous la forme " AB -> C"

Algorithmes de fermeture d'un ensemble d'attributs à partir d'un ensemble de DFs.
- Fermeture par niveaux 
- Fermeture linéaire : meilleure implémentation
- Fermeture par niveaux bis

Pour la suite des algortihmes, j'utiliserai donc l'algorithme de fermeture linéaire.

Développement de l'algorithme NextClosure qui permet de calculer le fermé suivant d'un fermé donné en entrée.

Développement de l'algorithme AllClosures qui permet de calculer tous les fermés de cet exemple en réitérant l'algorithme NextClosure jusqu'à ce que le fermé suivant soit le fermé donné en entrée de NextClosure.
L'ensemble des fermés est initialisé avec la fermeture de l'ensemble vide et est stocké dans une liste dans l'ordre lectique, c'est-à dire l'ordre alphabétique inversé.
Création de l'arbre des fermés et récupération de cet arbre dans un fichier "arbreFermes.txt"

Création d'un dictionnaire correspondant à la liste des fils les plus proches de chaque fermé.

Développement de l'algorithme permettant de calculer l'ensemble des inf-irréductibles et recupération de cet ensemble dans un fichier "infIrreductibles.txt".

Construction de la matrice relation exemple vérifiant uniquement les dépendances fonctionnelles données en entrée et récupération de la matrice dans un fichier "relationExemple.txt".
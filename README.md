# Stage_LIMOS
Développement d'un algorithme de construction automatique de relation exemple à partir des dépendances fonctionnelles 

Algorithmes de fermeture d'un ensemble d'attributs à partir d'un ensemble de DFs.
- Fermeture par niveaux 
- Fermeture linéaire : meilleure implémentation
- Fermeture par niveaux bis

Pour la suite des algortihmes, j'utiliserai donc l'algorithme de fermeture linéaire.

Développement de l'algorithme NextClosure qui permet de calculer le fermé suivant d'un fermé donné en entrée.

Développement de l'algorithme AllClosures qui permet de calculer tous les fermés de cet exemple en réitérant l'algorithme NextClosure jusqu'à ce que le fermé suivant soit le fermé donné en entrée de NextClosure.
L'ensemble des fermés est initialisé avec la fermture de l'ensemble vide et est stocké dans une liste dans l'ordre lectique, c'est-à dire l'ordre alphabétique inversé.

Création d'un dictionnaire correspondant à la liste des fils les plus proches de chaque fermé.

Développement de l'algorithme permettant de calculer l'ensemble des inf-irréductibles. 
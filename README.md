# EDD3_IRC_FLCLYV
projet IRC Promo 3 EDD
Infos Git.

Version = logiciel
Révision = version de fichier
Espace de stockage: repository (référentiel)
merged = fusion
Organisation des versions en arbre
to fetch: aller chercher
to pull: tirer
to push: pousser
to commit: confier
to merge: fusionner
to rebase:
to reset: réinitialisation


Aide : git help <nom_commande>

Git travaille sur les répertoires en entier.

GIT: connard

Fichier de configuration sous C:\Users\SCLAUDE\.gitconfig
$git init -> gérer un répertoire en configuration
$git status -> à utiliser fréquemment. Permet de connaître l'état actuel du répertoire (mon ami)
$git add <fichier> -> programmation de l'ajout d'un fichier en configuration
$git commit -m "ma premiere mise en configuration"
$git diff -> Donne les différences
$git diff --name-only -> Avoir le nom des fichiers modifiés

Avec git on joue sur 3 espaces.


git ne gère pas les répertoires vides. Il ne gère que des fichiers.

Pas de fichier de script de compilation en chemin absolu. Tout fichier doit être saisi en relatif
On sauvegarde uniquement les sources.

En fichier de configuration, si j'ai une conf en System, Global et Local, on prend en compte par ordre de priorité:
 - Local
 - Global
 - System

Si je souhaite ignorer des fichiers dans la configuration git, je dois:
Créer un fichier nommé ".gitignore" et y mettre le descriptif des fichiers que je ne souhaite pas.
Exemple: je souhaite ignorer les fichiers vim débutant par . et finissant par ~:
$ vim .gitignore
Dans le fichier ajouter la ligne suivante et l'enregistrer:
.*~
Refaire un git status: les fichiers concernés sont bien ignorés.

$git commit -m "Ajout SCLU" --amend -> Nouveau commit en remplacement du précédent fait précédemment.
$git log --oneline
$git log --decorate -> Coloration avec indications de branche
$git log --oneline --decorate
$git log --graph

$git remote -v -> Indique ce que je peux faire sur le remote
$git push origin master -> Envoyer vers le dépôt distant mes modifications. J'ai un refus car le dépôt distant a changé entre temps (quelqu'un a fait une modification entre temps)
$git fetch -> Se resynchroniser avec le dépôt distant
$git log --oneline --decorate --all
$git pull -> Mise en commun de mes modifications avec celles récupérées du dépôt distant
$vim <Nom fichier> pour résoudre les conflits
$git add <Nom fichier>
$git commit -m "Ajout SCLU"
$git push origin master
$git log --graph -> Graph des modifications
$git log --graph --decorate --all --oneline

Le dépôt distant est origin/master
Attention: toujours faire un fetch avant un pull. Passer par un git pull n'est pas recommandé (Création de branches confuses)

$git branch -> Lister, créer ou supprimer une branche
$git branch xb12 -> Création de la branche xb12
$git branch --list -> Lister les branches
$git branch -d xb12 -> Supprimer la branche xb12

$git chechout -> Changer de branche ou restaurer les fichiers de l'arbre de travail
$git checkout xb12 -> Passer sur la branche xb12

$git merge -> Joindre deux ou plusieurs histoires de développement ensemble
$git status -> En cas de conflit on peut y trouver des infos

le HEAD doit être en fin d'arbre. S'il n'est pas en fin d'arbre, il faut faire attention (on a des alertes) et ne pas faire de commit

$git tag -> Mettre une étiquette à une branche


Steps to remove directory from the repository:
git rm -r --cached FolderName
git commit -m "Removed folder from repository"
git push origin master

# 🎯 Nom du Projet

Une brève description de ton projet ici. Par exemple :  
> Application Java qui démontre [fonctionnalité principale] développée avec IntelliJ IDEA.

## 🛠️ Technologies utilisées

- Java 17 (ou ta version exacte)
- IntelliJ IDEA
- [Autres outils ou librairies si nécessaire]

## 🚀 Lancement du projet

### 1. Cloner le dépôt


⚙️ Configuration spéciale pour IntelliJ IDEA

Si tu exécutes ce projet avec IntelliJ IDEA (en utilisant le bouton vert ▶️), tu pourrais rencontrer une erreur liée à la sécurité de Java :
InaccessibleObjectException.
Pas de panique ! Voici comment ajouter facilement l'option --add-opens pour éviter ce problème :
🧩 Ajouter --add-opens dans IntelliJ IDEA

    Ouvre IntelliJ IDEA et ton projet.

    En haut à droite de la fenêtre, clique sur la petite flèche à côté du bouton vert ▶️.

    Sélectionne "Modifier les configurations…" (ou "Edit Configurations..." en anglais).

    Dans la fenêtre qui s’ouvre :

        Sélectionne ta configuration d’exécution dans la liste à gauche (souvent le nom de ta classe principale).

        Dans le champ "Options de la machine virtuelle (VM options)", ajoute ceci :

    --add-opens java.base/java.lang=ALL-UNNAMED

Clique sur "Appliquer", puis "OK".

Lance à nouveau ton projet avec le bouton vert ▶️. L’erreur ne devrait plus apparaître ✅.

```bash
git clone https://github.com/ton-nom-utilisateur/ton-projet.git
cd ton-projet

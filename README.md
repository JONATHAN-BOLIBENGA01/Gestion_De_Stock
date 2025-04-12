🎯 Gestion de stock

🛠️ Technologies utilisées

    Java 17 (ou ta version exacte)
    IntelliJ IDEA
    librairie Gjson

🚀 Lancement du projet
1. Cloner le dépôt

⚙️ Configuration spéciale pour IntelliJ IDEA

Si tu exécutes ce projet avec IntelliJ IDEA (en utilisant le bouton vert ▶️), tu pourrais rencontrer une erreur liée à la sécurité de Java : InaccessibleObjectException. Pas de panique ! Voici comment ajouter facilement l'option --add-opens pour éviter ce problème : 🧩 Ajouter --add-opens dans IntelliJ IDEA

Ouvre IntelliJ IDEA et ton projet.

En haut à droite de la fenêtre, clique sur la petite flèche à côté du bouton vert ▶️.

Sélectionne "Modifier les configurations…" (ou "Edit Configurations..." en anglais).

Dans la fenêtre qui s’ouvre :

    Sélectionne ta configuration d’exécution dans la liste à gauche (souvent le nom de ta classe principale).

    Dans le champ "Options de la machine virtuelle (VM options)", ajoute ceci :

--add-opens java.base/java.lang=ALL-UNNAMED

Clique sur "Appliquer", puis "OK".

Lance à nouveau ton projet avec le bouton vert ▶️. L’erreur ne devrait plus apparaître ✅.

git clone https://github.com/JONATHAN-BOLIBENGA01/Gestion_De_Stock/edit/gdsAvecJson
cd ton-projet

About
No description, website, or topics provided.
Resources
Readme
License
MIT license
Activity
Stars
0 stars
Watchers
1 watching
Forks
0 forks
Releases
No releases published
Create a new release
Packages
No packages published
Publish your first package
Languages

    Java 100.0% 

Suggested workflows
Based on your tech stack

    Java with Gradle logo
    Java with Gradle

Build and test a Java project using a Gradle wrapper script.
Android CI logo
Android CI
Build an Android project with Gradle.
Publish Java Package with Maven logo
Publish Java Package with Maven

    Build a Java Package using Maven and publish to GitHub Packages.

More workflows
Footer

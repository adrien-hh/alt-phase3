# Laboratoire - Système de Planification

## Description

[Votre explication du problème résolu]

## Installation

### Prérequis

- Java 21
- Maven 3.8+

### Installer les dépendances
```bash
mvn install
```

## Utilisation

### Exécuter le code

```bash
# Le fichier de données doit être dans src/main/resources
mvn exec:java "-Dexec.mainClass=org.alt.Main" "-Dexec.args=<input-file.json>"
```

### Exécuter les tests

```bash
mvn test
```

### Exécuter le linting

Spotless utilise les règles google-java-format :

```bash
# Vérifier les violations de format
mvn spotless:check

# Corriger les violations détectées 
mvn spotless:apply
```

## Évolution depuis version SIMPLE

[Si applicable : quelles extensions avez-vous ajoutées]


## Commandes utiles (récap)

```bash
# Exécuter tous les tests
mvn test

# Golden master seulement
mvn -Dtest=GoldenMasterTest test

# Lancer l'application
mvn exec:java "-Dexec.mainClass=org.alt.Main" "-Dexec.args=<input-file.json>"
```
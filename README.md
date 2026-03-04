# Laboratoire - Système de Planification

## Description

Système de planification automatique pour laboratoire médical. À partir d'une liste d'échantillons à analyser, de techniciens disponibles et d'équipements spécialisés, l'algorithme produit un planning optimisé en respectant les contraintes métier :

- **Priorités** : STAT (urgence vitale) > URGENT > ROUTINE, toujours sans exception
- **Compatibilités** : chaque analyse est assignée à un technicien et un équipement compatibles
- **Pauses déjeuner** : 1h obligatoire par technicien entre 12h et 15h
- **Efficacité** : la durée réelle d'une analyse tient compte du coefficient du technicien
- **Nettoyage** : délai entre deux analyses sur le même équipement

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
mvn compile exec:java "-Dexec.mainClass=org.alt.Main" "-Dexec.args=<input-file.json>"
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

### Nouvelles contraintes implémentées

- **Spécialisations enrichies** : les techniciens ont plusieurs spécialités (BLOOD, CHEMISTRY, MICROBIOLOGY, IMMUNOLOGY, GENETICS) au lieu d'une seule.  
  La compatibilité est déterminée via un mapping `analysisType → spécialité` construit dynamiquement à partir des `compatibleTypes` des équipements.


- **Coefficient d'efficacité** : `durée réelle = Math.round(durée base / coefficient)`. Un technicien expert (1.2) finit 20% plus vite, un junior (0.85) prend plus de temps.


- **Pauses déjeuner** : planifiées automatiquement par l'algorithme dans la fenêtre 12h-15h. Une analyse en cours n'est pas interrompue — la pause est décalée après.


- **Temps de nettoyage** : délai ajouté après chaque analyse sur un équipement avant de pouvoir en démarrer une autre.


- **Délai max STAT** : un échantillon STAT doit démarrer dans les 30 minutes suivant son arrivée, sinon il est comptabilisé en conflit.

### Nouvelles métriques

- `averageWaitTime` : temps moyen entre arrivée et début d'analyse
- `technicianUtilization` : taux d'occupation moyen des techniciens sur leur shift
- `parallelEfficiency` : nombre d'analyses simultanées détectées
- `priorityRespectRate` : 100% si aucune violation de priorité

### Architecture

La version simple utilisait un `LabPlanner` monolithique. La version intermédiaire introduit une séparation des responsabilités :

- `SampleSorter` : tri par priorité puis heure d'arrivée
- `CompatibilityCheckerIntermediate` : vérifie la compatibilité technicien/équipement
  via le mapping analysisType
- `ResourceAssignerIntermediate` : trouve le meilleur créneau disponible
- `MetricsCalculatorIntermediate` : calcule les métriques du planning
- `LabPlannerIntermediate` : orchestre les services ci-dessus

### Corrections apportées aux exemples fournis

Les exemples de l'énoncé contenaient plusieurs incohérences par rapport aux règles
définies dans les contraintes. Les corrections suivantes ont été appliquées :

- **Formule d'efficacité** : la version intermédiaire utilise la formule des livrables : `(Σ occupation par technicien) / nombre techniciens / temps total * 100`.


- **Valeurs de métriques** : les valeurs de `efficiency`, `technicianUtilization` et `averageWaitTime` des exemples 1 et 2 ont été recalculées pour correspondre aux formules officielles des livrables.


- **Inputs des exemples** : certains exemples ne contenaient pas d'`analysisType` sur les échantillons, ni d'équipements avec `compatibleTypes`. Ces données ont été complétées pour être cohérentes avec le jeu de données complet à 20 échantillons.


## Commandes utiles (récapitulatif)

```bash
# Exécuter tous les tests
mvn test

# Golden master seulement
mvn -Dtest=GoldenMasterTest test

# Lancer l'application
mvn exec:java "-Dexec.mainClass=org.alt.Main" "-Dexec.args=<input-file.json>"
```
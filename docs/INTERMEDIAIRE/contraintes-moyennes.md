# ⚙️ CONTRAINTES MOYENNES

## 🎯 Vue d'Ensemble

**8 contraintes essentielles** à respecter pour un planning réaliste de laboratoire médical.

**Niveau intermédiaire** : contraintes métier importantes sans sur-complexité.


---

## 🔴 CONTRAINTES CRITIQUES (Échec si non respectées)

### **1. 🚨 Priorité STAT Absolue**

```
RÈGLE D'OR : STAT > URGENT > ROUTINE
```

**Description** :

* Les échantillons **STAT** (urgence vitale) passent **TOUJOURS** en premier
* Peuvent **interrompre** une analyse URGENT/ROUTINE en cours
* Délai maximum : **30 minutes** après arrivée

**Exemples** :

* ✅ STAT arrivé à 10h → traité avant 10h30, même si URGENT en cours
* ❌ URGENT traité avant un STAT arrivé plus tôt

**Implémentation** :

```
Si nouveau STAT arrive :
1. Vérifier si analyse interruptible en cours

2. Réquisitionner technicien/équipement approprié
3. Reprogrammer analyse interrompue

4. Démarrer STAT immédiatement
```


---

## 📋 Système de Typage

### Structure des Échantillons

Chaque échantillon possède **deux dimensions de typage**:

#### 1. Type de Prélèvement (`type`)

Représente la **nature biologique** de l'échantillon:

* `"BLOOD"`: Échantillon sanguin
* `"URINE"`: Échantillon urinaire
* `"TISSUE"`: Échantillon tissulaire

#### 2. Type d'Analyse (`analysisType`)

Représente la **spécialité médicale** ou l'**examen demandé**:

* Valeur libre (string) décrivant l'analyse
* Exemples: "Numération complète", "Bilan hépatique", "Culture bactérienne", etc.

### Exemple Concret

```json
{
  "id": "SAMPLE001",
  "type": "BLOOD", // Nature du prélèvement
  "analysisType": "Hématologie", // Spécialité médicale
  "duration": 30
}
```

**Note**: Ces deux dimensions sont **indépendantes** et **complémentaires**. Un échantillon sanguin peut nécessiter différents types d'analyses (hématologie, biochimie, etc.).


---

### **2. 🎯 Spécialisations Obligatoires**

```
RÈGLE : Type analyse = Spécialité technicien requise
```

**Correspondances obligatoires** :

* **Hématologie** → Technicien spécialisé Hématologie
* **Biochimie** → Technicien spécialisé Biochimie
* **Microbiologie** → Technicien spécialisé Microbiologie
* **Immunologie** → Technicien spécialisé Immunologie
* **Génétique** → Technicien spécialisé Génétique

**Exemples** :

* ✅ Échantillon "Hématologie" → TECH001 (spécialité Hématologie)
* ❌ Échantillon "Génétique" → TECH003 (pas cette spécialité)

**Cas particuliers** :

* **Techniciens polyvalents** : peuvent traiter plusieurs types
* **Urgences STAT** : priorité sur technicien le plus qualifié


---

### **3. ⏰ Pauses Déjeuner Obligatoires**

```
RÈGLE : 1h de pause entre 12h-15h pour chaque technicien
```

**Contraintes** :

* **Durée** : exactement 60 minutes
* **Fenêtre** : entre 12h00 et 15h00
* **Flexibilité** : heure de début choisie par algorithme
* **Indisponibilité** : aucune analyse pendant la pause

**Exemples planning pauses** :

* TECH001 : 12h30-13h30
* TECH002 : 13h00-14h00
* TECH007 : 12h00-13h00

**Optimisation suggérée** :

* **Étaler les pauses** pour maintenir capacité laboratoire
* **Prioriser fin de matinée** pour analyses STAT après-midi

#### Cas Limites et Comportements Spécifiques

##### 1️⃣ Analyse en Cours au Moment de la Pause

**Situation**: Une analyse démarre à 11:30 et dure 60 minutes (fin à 12:30), alors que la pause est prévue à 12:00.

**Règle**: ✅ **L'analyse en cours N'est PAS interrompue**

* L'analyse continue jusqu'à sa fin (12:30)
* La pause commence après la fin de l'analyse (ex: 12:30-13:00)

**Exemple**:

```
Tech1: Analyse 11:30-12:30 → Pause 12:30-13:00 → Analyse 13:00-14:00
✅ Valide: la pause de 30min est dans la fenêtre 12h-15h
```

##### 2️⃣ Interruption par Échantillon STAT

**Situation**: Un technicien est en pause 12:00-13:00, un échantillon STAT arrive à 12:15.

**Règle**: ⚡ **Un échantillon STAT peut interrompre une pause**

* La pause est suspendue immédiatement
* L'analyse STAT est effectuée en priorité
* Le temps de pause restant doit être reprogrammé plus tard dans la fenêtre 12h-15h
* Cette interruption est comptabilisée dans `metadata.lunchInterruptions`

**Exemple**:

```
Tech1: Pause 12:00-12:15 → STAT 12:15-12:45 → Pause 12:45-13:00
✅ Valide: 30 min de pause au total (15 + 15)
❗ lunchInterruptions: 1
```

##### 3️⃣ Fenêtre de Pause Impossible

**Situation**: Analyses urgentes continues de 12:00 à 15:00, impossible de placer 30 minutes de pause.

**Règle**: 🚨 **La pause DOIT être planifiée, quitte à décaler des analyses ROUTINE**

* Priorité: STAT > URGENT > Pause Déjeuner > ROUTINE
* Les analyses ROUTINE peuvent être décalées après 15h pour libérer un créneau
* Si impossible avec analyses STAT/URGENT continues: accepter que la pause soit décalée après 15h (cas exceptionnel)

**Exemple**:

```
12:00-12:30 STAT S001

12:30-13:00 URGENT S002

13:00-13:30 URGENT S003

13:30-14:00 ROUTINE S004 → DÉCALÉ à 14:30-15:00

14:00-14:30 Pause déjeuner ← INSÉRÉ
✅ Valide: pause dans la fenêtre, ROUTINE décalé
```

##### 4️⃣ Chevauchement Partiel avec Pause

**Situation**: Peut-on démarrer une analyse à 12:45 qui finira à 13:15, alors que la pause est 12:00-13:00?

**Règle**: ❌ **Une nouvelle analyse ne peut PAS démarrer pendant la pause**

* Si la pause est 12:00-13:00, aucune nouvelle analyse ne peut démarrer entre 12:00 et 13:00
* Les analyses doivent démarrer avant 12:00 ou après 13:00

**Exemple**:

```
❌ Interdit: Pause 12:00-13:00, Nouvelle analyse 12:45-13:15
✅ Autorisé: Pause 12:00-13:00, Analyse 11:45-12:15 (démarrée AVANT)
✅ Autorisé: Pause 12:00-13:00, Analyse 13:00-13:30 (démarrée APRÈS)
```

#### Résumé des Règles de Pause Déjeuner

| Situation | Comportement | Comptabilisé |
|-----------|--------------|--------------|
| Analyse en cours avant pause | Continue sans interruption | Non          |
| STAT arrive pendant pause | Interrompt, pause reprogrammée | Oui (lunchInterruptions) |
| Fenêtre 12h-15h saturée | Décaler analyses ROUTINE | Non          |
| Nouvelle analyse pendant pause | **Interdit** | N/A          |


---

## 🟡 CONTRAINTES IMPORTANTES (Points perdus si non respectées)

### **4. 🔬 Équipements Spécialisés**

```
RÈGLE : Type analyse = Type équipement obligatoire
```

**Correspondances** :

* **Analyses Hématologie** → Analyseur Hématologie (EQ001)
* **Analyses Biochimie** → Automate Biochimie (EQ002)
* **Analyses Microbiologie** → Station Microbiologie (EQ003)
* **Analyses Immunologie** → Système Immunologie (EQ004)
* **Analyses Génétique** → Séquenceur Génétique (EQ005)

**Capacités** :

* Certains équipements traitent **plusieurs échantillons simultanément**
* Vérifier `processing_capacity` de chaque équipement
* **File d'attente** si capacité dépassée

#### Algorithme d'Allocation pour Équipements à Capacité Limitée

**Principe**: First-Available-Compatible-Equipment (FACE)

##### Processus d'Allocation


1. **Trier les échantillons** par priorité (STAT > URGENT > ROUTINE) puis heure d'arrivée
2. **Pour chaque échantillon**, itérer sur les équipements compatibles dans l'ordre du tableau
3. **Vérifier la disponibilité**:
   * Capacité actuelle < capacité maximale à l'instant t
   * Pas de maintenance en cours
4. **Sélectionner** le premier équipement qui satisfait les critères
5. **Si aucun équipement disponible**: planifier au prochain créneau disponible

##### Gestion de File d'Attente

**Version Intermédiaire - Pas de File d'Attente Explicite**:

* Quand la capacité d'un équipement est atteinte, passer au suivant dans la liste
* Si tous les équipements compatibles sont à capacité: reporter l'analyse dans le temps
* **Pas de réorganisation** après allocation initiale

**Exemple**:

```typescript
// Équipement EQ001: capacity=2, actuellement 2 analyses en cours
// Échantillon S003 arrive, nécessite EQ001

// Algorithme:
if (getEquipmentUsage(EQ001, currentTime) >= 2) {
  // Option 1: Essayer équipement alternatif compatible
  tryNextCompatibleEquipment();

  // Option 2: Si aucun autre équipement
  scheduleAtNextAvailableTime(EQ001);
}
```

##### Comportement avec Priorités Mélangées

**Question**: Un équipement capacity=2 avec 1 ROUTINE en cours. Peut-on ajouter 2 STAT?

**Réponse**: ❌ Non, la capacité physique est stricte

* Maximum 2 analyses simultanées, peu importe la priorité
* Si STAT arrive et capacité=2 atteinte: doit attendre ou utiliser équipement alternatif

**Note pour Version Avancée**: Une file d'attente sophistiquée avec préemption pourrait être implémentée, mais ce n'est **pas attendu** dans la version intermédiaire.


---


---

### **5. ⚙️ Maintenance Quotidienne**

```
RÈGLE : Chaque équipement a une maintenance quotidienne
```

**Indisponibilités** :

* EQ001 (Hématologie) : 06h00-07h00
* EQ002 (Biochimie) : 06h30-07h30
* EQ003 (Microbiologie) : 07h00-08h00
* EQ004 (Immunologie) : 05h30-06h30
* EQ005 (Génétique) : 19h00-20h00

#### Règle Stricte de Maintenance

**Politique d'Interdiction**: Un équipement ne peut être utilisé s'il y a un **chevauchement quelconque** entre l'analyse et la fenêtre de maintenance.

#### Définition du Chevauchement

Une analyse chevauche une maintenance si:

```typescript

startAnalyse < endMaintenance && endAnalyse > startMaintenance;
```

**En clair**: L'analyse et la maintenance partagent au moins un instant en commun.

#### Cas d'Usage Illustrés

##### ❌ Cas Interdits

**1. Démarrage pendant maintenance**:

```
Maintenance: [06:00 ========= 07:00]
Analyse:              [06:45 ====== 07:15]
❌ Interdit: démarre pendant, termine après
```

**2. Fin pendant maintenance**:

```
Maintenance:     [14:00 ========= 15:00]
Analyse:    [13:45 ====== 14:15]
❌ Interdit: démarre avant, termine pendant
```

**3. Chevauchement total**:

```
Maintenance:    [10:00 ===== 11:00]
Analyse:    [09:30 ============== 11:30]
❌ Interdit: englobe complètement la maintenance
```

**4. Chevauchement partiel minimal**:

```
Maintenance: [08:00 ===== 09:00]
Analyse:        [08:59 === 09:30]
❌ Interdit: même 1 minute de chevauchement
```

##### ✅ Cas Autorisés

**1. Analyse avant maintenance**:

```
Maintenance:       [06:00 ========= 07:00]
Analyse:  [05:00 ==== 06:00]
✅ Autorisé: termine exactement quand maintenance commence
```

**2. Analyse après maintenance**:

```
Maintenance: [06:00 ========= 07:00]
Analyse:                      [07:00 ==== 08:00]
✅ Autorisé: commence exactement quand maintenance termine
```

#### Algorithme de Vérification

```typescript

function isEquipmentAvailable(
  equipment: Equipment,
  startTime: number,
  endTime: number
): boolean {
  for (const maintenance of equipment.maintenance || []) {
    const maintStart = parseTime(maintenance.start);
    const maintEnd = parseTime(maintenance.end);

    // Rejet si ANY overlap
    if (startTime < maintEnd && endTime > maintStart) {
      return false; // CHEVAUCHEMENT DÉTECTÉ
    }
  }
  return true; // Aucun chevauchement
}
```

#### Justification

Cette approche **conservatrice** garantit:

* 🔧 Sécurité matérielle (pas d'utilisation pendant maintenance)
* 📋 Simplicité de validation (règle sans ambiguïté)
* ⚡ Respect des contraintes techniques (calibration, nettoyage, etc.)

**Note**: Si un échantillon ne peut être planifié sur un équipement en maintenance, le système doit:


1. Essayer un équipement alternatif compatible
2. Si aucun disponible: décaler l'analyse après la fin de la maintenance


---

### **6. 🧽 Temps de Nettoyage**

```
RÈGLE : Délai nettoyage entre échantillons (contamination)
```

**Durées par équipement** :

* Hématologie : 10 minutes
* Biochimie : 15 minutes
* Microbiologie : 20 minutes
* Immunologie : 12 minutes
* Génétique : 30 minutes

**Application** :

* **Entre chaque échantillon** sur même équipement
* **Automatique** : inclus dans planning
* **Optimisation** : grouper analyses similaires


---

## 🔵 CONTRAINTES D'OPTIMISATION (Bonus qualité)

### **7. 💪 Efficacité Technicien**

```
RÈGLE : Coefficient d'efficacité appliqué aux durées
```

**Coefficients** :

* **Expert** (>8 ans) : 1.2 (20% plus rapide)
* **Senior** (5-8 ans) : 1.0-1.1 (standard à légèrement plus rapide)
* **Standard** (3-5 ans) : 0.95-1.0 (proche standard)
* **Junior** (<3 ans) : 0.8-0.9 (plus lent)

**Calcul** :

```
Durée réelle = Durée base / Coefficient efficacité

Exemple :
- Analyse 60min, Technicien efficacité 1.2
- Durée réelle = 60 / 1.2 = 50 minutes
```

#### Règle d'Arrondi

**Formule Officielle**:

```typescript

durée_ajustée = Math.round(durée_base / coefficient_efficacité);
```

**Justification**:

* Arrondi au plus proche pour équilibrer sous/sur-estimations
* Cohérence avec le système temporel en minutes entières
* Évite l'accumulation d'erreurs sur les plannings longs

#### Exemples de Calcul avec Arrondi

**Exemple 1 - Arrondi supérieur**:

```typescript

Durée base: 45 minutes

Coefficient: 1.1 (technicien efficace)
Calcul: 45 / 1.1 = 40.909...
Résultat: Math.round(40.909) = 41 minutes ✅
```

**Exemple 2 - Arrondi inférieur**:

```typescript

Durée base: 30 minutes

Coefficient: 0.9 (technicien moins efficace)
Calcul: 30 / 0.9 = 33.333...
Résultat: Math.round(33.333) = 33 minutes ✅
```

**Exemple 3 - Pas d'arrondi nécessaire**:

```typescript

Durée base: 60 minutes

Coefficient: 1.2

Calcul: 60 / 1.2 = 50.000

Résultat: Math.round(50.000) = 50 minutes ✅
```

**Exemple 4 - Cas limite (.5)**:

```typescript

Durée base: 45 minutes

Coefficient: 1.5

Calcul: 45 / 1.5 = 30.000

Résultat: Math.round(30.000) = 30 minutes ✅

Durée base: 47 minutes

Coefficient: 1.5

Calcul: 47 / 1.5 = 31.333...
Résultat: Math.round(31.333) = 31 minutes ✅
```

**Important**: JavaScript's `Math.round()` arrondit `.5` vers le haut (31.5 → 32).


---

### **8. 🚀 Parallélisme Intelligent**

```
RÈGLE : Maximiser analyses simultanées si ressources disponibles
```

**Conditions parallélisme** :

* **Équipements différents** disponibles
* **Techniciens différents** disponibles
* **Pas de conflit** horaire
* **Respect** autres contraintes

**Exemple optimal** :

```
09h00-09h30 :
- TECH001 + EQ001 : Analyse Hématologie
- TECH002 + EQ003 : Analyse Microbiologie
- TECH003 + EQ002 : Analyse Biochimie
→ 3 analyses en parallèle
```


---

## 📊 Matrice de Compatibilité

### **Techniciens → Spécialités**

| Technicien | Hématologie | Biochimie | Microbiologie | Immunologie | Génétique |
|------------|-------------|-----------|---------------|-------------|-----------|
| TECH001    | ✅           | ✅         | ❌             | ❌           | ❌         |
| TECH002    | ❌           | ❌         | ✅             | ✅           | ❌         |
| TECH003    | ❌           | ✅         | ❌             | ✅           | ❌         |
| TECH004    | ✅           | ❌         | ❌             | ❌           | ✅         |
| TECH005    | ❌           | ❌         | ✅             | ❌           | ❌         |
| TECH006    | ❌           | ❌         | ❌             | ✅           | ✅         |
| TECH007    | ✅           | ✅         | ❌             | ✅           | ❌         |
| TECH008    | ✅           | ❌         | ✅             | ❌           | ❌         |

### **Analyses → Équipements**

| Type Analyse | EQ001 | EQ002 | EQ003 | EQ004 | EQ005 |
|--------------|-------|-------|-------|-------|-------|
| Hématologie  | ✅     | ❌     | ❌     | ❌     | ❌     |
| Biochimie    | ❌     | ✅     | ❌     | ❌     | ❌     |
| Microbiologie | ❌     | ❌     | ✅     | ❌     | ❌     |
| Immunologie  | ❌     | ❌     | ❌     | ✅     | ❌     |
| Génétique    | ❌     | ❌     | ❌     | ❌     | ✅     |


---

## 🎯 Validation Contraintes

### **✅ Checklist Obligatoire**

**Avant de valider votre planning** :


1. **🔴 STAT traités en premier** ?
   * Tous les STAT avant tous les URGENT/ROUTINE
   * Délai <30min après arrivée respecté
2. **🎯 Spécialisations respectées** ?
   * Chaque analyse assignée à technicien qualifié
   * Aucune analyse hors spécialité
3. **⏰ Pauses déjeuner planifiées** ?
   * 1h de pause pour chaque technicien
   * Entre 12h00 et 15h00
4. **🔬 Équipements corrects** ?
   * Type analyse = type équipement
   * Capacités respectées
5. **⚙️ Maintenances évitées** ?
   * Aucune analyse pendant maintenance
   * Horaires indisponibilité respectés
6. **🧽 Nettoyages inclus** ?
   * Délai nettoyage entre échantillons
   * Temps total cohérent
7. **💪 Efficacités appliquées** ?
   * Coefficients techniciens pris en compte
   * Durées recalculées correctement
8. **🚀 Parallélisme optimisé** ?
   * Ressources multiples utilisées
   * Aucun conflit temporel


---

## 🚨 Cas d'Exception

### **Interruption STAT**

```
Situation : STAT arrive pendant analyse URGENT

Action :
1. PAUSE analyse URGENT si >50% restante

2. Libérer technicien/équipement pour STAT

3. Reprendre URGENT après STAT terminé
4. Recalculer planning complet si nécessaire
```

### **Conflit Ressources**

```
Situation : 2 analyses simultanées même équipement

Action :
1. Priorité = priorité échantillon (STAT > URGENT > ROUTINE)
2. Si même priorité = premier arrivé servi

3. Reporter analyse moins prioritaire

4. Optimiser allocation sur autres ressources
```

### **Technicien Indisponible**

```
Situation : Technicien en pause, maladie, etc.
Action :
1. Vérifier autres techniciens même spécialité
2. Si aucun = reporter analyse après disponibilité
3. Si STAT = interrompre pause (cas exceptionnel)
4. Recalculer métriques impactées
```


---

**🚀 Contraintes maîtrisées ? Direction [exemples-progressifs.md](exemples-progressifs.md) pour voir l'application !**
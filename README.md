### Rapport

---

## Aperçu du projet

Ce projet a pour objectif de démontrer l'utilisation de la pratique du développement piloté par les tests (TDD) ainsi que la mise en place d'un système de journalisation robuste. Pour ce faire, nous avons intégré les bibliothèques suivantes :
- **DBUnit** : pour les tests de base de données,
- **JUnit** : pour les tests unitaires et d'intégration,
- **SLF4J** : pour la gestion des logs.

## Structure du projet

Le projet est structuré de manière à faciliter la séparation des préoccupations et la maintenabilité du code. Voici un aperçu des principaux répertoires :

- `src/main/java/ch/hearc/cafheg/business` : contient la logique métier relative aux allocations et aux versements.
- `src/main/java/ch/hearc/cafheg/infrastructure` : regroupe les composants d'infrastructure, y compris l'API REST, la logique applicative, les définitions OpenAPI, la génération de PDF et les couches de persistance.
- `src/test/java/ch/hearc/cafheg/business` : inclut les tests unitaires et d'intégration pour la logique métier.

## REST Controller

Le projet inclut un RESTController pour gérer les requêtes HTTP et fournir une interface de programmation aux clients. Voici quelques exemples de commandes `curl` permettant d'interagir avec l'API REST.

### Exemples de commandes `curl`

#### Récupérer tous les allocataires
```sh
curl -X GET "http://localhost:8080/api/allocataires" -H "accept: application/json"
```

#### Récupérer un allocataire par ID
```sh
curl -X GET "http://localhost:8080/api/allocataires/1" -H "accept: application/json"
```

#### Créer un nouvel allocataire
```sh
curl -X POST "http://localhost:8080/api/allocataires" -H "accept: application/json" -H "Content-Type: application/json" -d "{ \"noAvs\": \"AVS123\", \"prenom\": \"Pierre\", \"nom\": \"Dupont\" }"
```

#### Mettre à jour un allocataire
```sh
curl -X PUT "http://localhost:8080/api/allocataires/1" -H "accept: application/json" -H "Content-Type: application/json" -d "{ \"noAvs\": \"AVS123\", \"prenom\": \"Pierre\", \"nom\": \"Dupont\" }"
```

#### Supprimer un allocataire
```sh
curl -X DELETE "http://localhost:8080/api/allocataires/1" -H "accept: application/json"
```

## TDD et Journalisation

Le développement du projet suit la méthodologie TDD, garantissant ainsi une couverture de tests exhaustive dès les premières étapes de développement. Les tests sont écrits avant l'implémentation du code fonctionnel, ce qui permet de définir clairement les attentes et de minimiser les bugs. 

La journalisation est un aspect crucial de ce projet. En utilisant SLF4J, nous assurons une gestion cohérente et performante des logs, ce qui facilite le débogage et la surveillance des activités de l'application.

### Exécution des tests

Afin de pouvoir exécuter les tests, il faut aller sur le répertoire contenant les tests et les exécuter.


---

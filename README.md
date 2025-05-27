# Backend E-commerce API

Ce backend Spring Boot fournit une API REST complète pour une application e-commerce. Il gère l'authentification, les produits, les paniers et les listes de souhaits.

## Fonctionnalités Principales

### Authentification
- Inscription utilisateur (`POST /account`)
- Connexion avec JWT (`POST /token`)
- Protection des routes avec Spring Security
- Droits d'administration spéciaux pour `admin@admin.com`

### Gestion des Produits (`/api/products`)
- Liste des produits (`GET`)
- Détails d'un produit (`GET /{id}`)
- Création de produit (`POST`, admin uniquement)
- Mise à jour de produit (`PATCH /{id}`, admin uniquement)
- Suppression de produit (`DELETE /{id}`, admin uniquement)

### Panier (`/api/cart`)
- Consultation du panier (`GET`)
- Ajout au panier (`POST /items`)
- Modification des quantités (`PATCH /items/{productId}`)
- Suppression d'articles (`DELETE /items/{productId}`)

### Liste de Souhaits (`/api/wishlist`)
- Consultation de la wishlist (`GET`)
- Ajout à la wishlist (`POST /items/{productId}`)
- Suppression de la wishlist (`DELETE /items/{productId}`)

## Modèle de Données

### Product
```typescript
{
  id: number;
  code: string;
  name: string;
  description: string;
  image: string;
  category: string;
  price: number;
  quantity: number;
  internalReference: string;
  shellId: number;
  inventoryStatus: "INSTOCK" | "LOWSTOCK" | "OUTOFSTOCK";
  rating: number;
  createdAt: number;
  updatedAt: number;
}
```

## Sécurité
- Authentification JWT
- Validation des données avec Jakarta Validation
- Protection CORS
- Row-level security pour les données utilisateur

## Tests
- Tests d'intégration pour tous les controllers
- Tests unitaires pour les services
- Couverture des cas d'erreur

## Technologies Utilisées
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- H2 Database
- JWT pour l'authentification
- Lombok
- JUnit 5 pour les tests
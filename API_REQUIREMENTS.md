# CCTV Line App API Requirements

This native app needs JSON API endpoints. It should not scrape the website and should not use WebView.

## 1. Products

### GET /api/products
Returns product list.

Example response:

```json
[
  {
    "id": "cam-8mp-colorvu",
    "sku": "UK-CI-4K-G2",
    "title": "UKVISION 8MP 4K PoE ColorVu Turret Camera",
    "category": "Cameras",
    "price": 84.99,
    "stock": "In Stock",
    "badge": "Trade",
    "features": ["8MP 4K", "PoE", "IP67"],
    "description": "Professional CCTV camera for trade installation.",
    "images": ["https://cctvline.co.uk/storage/products/example.jpg"]
  }
]
```

## 2. Categories

### GET /api/categories
Returns product categories with item counts.

## 3. Login

### POST /api/auth/login
Request:

```json
{
  "email": "installer@example.com",
  "password": "password"
}
```

Response:

```json
{
  "token": "JWT_OR_SESSION_TOKEN",
  "customer": {
    "id": "123",
    "name": "Trade Customer",
    "email": "installer@example.com",
    "tradePricing": true
  }
}
```

## 4. Orders

### GET /api/account/orders
Requires auth token.

## 5. Cart

### POST /api/cart
Request:

```json
{
  "items": [
    { "sku": "DS-7608NXI-K1/8P", "quantity": 1 },
    { "sku": "UK-CI-4K-G2", "quantity": 4 }
  ]
}
```

## 6. Quote

### POST /api/quotes
Used by the CCTV kit builder.

## 7. Trade Credit

### POST /api/trade-credit-application
Should support multipart/form-data for file uploads.

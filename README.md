# CCTV Line Android Native App

Native Android app for `cctvline.co.uk`.

Important: this version is **not WebView**. It uses Kotlin + Jetpack Compose and has real native screens for:

- Home
- Product catalogue
- Product detail
- Search and category filters
- CCTV kit builder
- Cart
- Trade account demo screen
- Order list demo screen
- Trade credit placeholder
- GitHub Actions debug APK build

The product/order data is currently demo/mock data inside the app. When the CCTV Line website API is ready, replace the demo layer with API calls inside `CctvLineApiClient`.

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Android Gradle Plugin
- GitHub Actions for debug APK

## Upload to GitHub from CMD

Open CMD or PowerShell inside this folder and run:

```bash
git init
git add .
git commit -m "Initial native Android app"
git branch -M main
git remote add origin https://github.com/YOUR-USERNAME/cctvline-android-native.git
git push -u origin main
```

After push:

1. Go to your GitHub repository.
2. Open **Actions**.
3. Click **Build Debug APK**.
4. Open the latest run.
5. Download artifact: **cctvline-debug-apk**.
6. Inside it you will find the debug APK.

## Build locally with CMD

If Gradle is installed:

```bash
gradle assembleDebug
```

Output:

```text
app/build/outputs/apk/debug/app-debug.apk
```

If you use Android Studio, open the folder and run:

```bash
Build > Build Bundle(s) / APK(s) > Build APK(s)
```

## Where to connect real API

File:

```text
app/src/main/java/uk/co/cctvline/app/MainActivity.kt
```

Current API placeholder:

```kotlin
object ApiConfig {
    const val BASE_URL = "https://cctvline.co.uk/api/"
}

interface CctvLineApiClient {
    suspend fun getProducts(): List<Product>
    suspend fun getCategories(): List<Category>
    suspend fun getOrders(customerId: String): List<TradeOrder>
}
```

Recommended real endpoints:

```text
GET  /api/categories
GET  /api/products
GET  /api/products/{id}
POST /api/auth/login
GET  /api/account/orders
POST /api/cart
POST /api/quotes
POST /api/trade-credit-application
```

## Notes

- No WebView dependency is used.
- No `android.webkit.WebView` code exists.
- Internet permission is included only for future API connection.
- Checkout, login, quote submission and trade credit are UI-ready but need real backend endpoints.

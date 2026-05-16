package uk.co.cctvline.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import uk.co.cctvline.app.ui.CCTVLineTheme
import uk.co.cctvline.app.ui.CctvBlack
import uk.co.cctvline.app.ui.CctvDarkGrey
import uk.co.cctvline.app.ui.CctvLightBg
import uk.co.cctvline.app.ui.CctvRed

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CCTVLineTheme {
                CCTVLineApp()
            }
        }
    }
}

enum class Tab(val label: String) {
    Home("Home"), Products("Products"), Kit("Kit"), Cart("Cart"), Account("Account")
}

data class Category(
    val name: String,
    val description: String,
    val count: Int
)

data class Product(
    val id: String,
    val sku: String,
    val title: String,
    val category: String,
    val price: Double,
    val stock: String,
    val badge: String,
    val features: List<String>,
    val description: String
)

data class CartLine(
    val product: Product,
    val quantity: Int
)

data class TradeOrder(
    val number: String,
    val status: String,
    val total: Double,
    val date: String
)

object ApiConfig {
    const val BASE_URL = "https://cctvline.co.uk/api/"
}

interface CctvLineApiClient {
    suspend fun getProducts(): List<Product>
    suspend fun getCategories(): List<Category>
    suspend fun getOrders(customerId: String): List<TradeOrder>
}

object DemoData {
    val categories = listOf(
        Category("Cameras", "IP, PoE, ColorVu and dome/turret CCTV cameras", 214),
        Category("Recorders", "NVR, DVR and professional recording systems", 66),
        Category("Storage", "Surveillance HDD and storage upgrades", 29),
        Category("Networking", "PoE switches, RJ45, data cabinets and network accessories", 141),
        Category("Cable", "Cat6, RG59, pre-made CCTV cables and accessories", 118),
        Category("Accessories", "Brackets, power supplies, connectors and installation parts", 233)
    )

    val products = listOf(
        Product(
            id = "cam-8mp-colorvu",
            sku = "UK-CI-4K-G2",
            title = "UKVISION 8MP 4K PoE ColorVu Turret Camera",
            category = "Cameras",
            price = 84.99,
            stock = "In Stock",
            badge = "Trade",
            features = listOf("8MP 4K", "PoE", "Smart Hybrid Light", "IP67", "2-Way Audio"),
            description = "Professional 8MP PoE turret camera for indoor and outdoor CCTV installations, suitable for trade installers and resellers."
        ),
        Product(
            id = "cam-5mp-colorvu",
            sku = "UK-CI-3K-G2",
            title = "UKVISION 5MP 3K PoE ColorVu Turret Camera",
            category = "Cameras",
            price = 62.49,
            stock = "In Stock",
            badge = "Best Seller",
            features = listOf("5MP 3K", "PoE", "Colour Night Vision", "Built-in Mic", "IP67"),
            description = "5MP PoE camera designed for trade CCTV kits where reliable colour imaging and simple installation are required."
        ),
        Product(
            id = "nvr-4ch",
            sku = "DS-7604NXI-K1/4P",
            title = "Hikvision 4CH PoE AcuSense NVR",
            category = "Recorders",
            price = 119.99,
            stock = "In Stock",
            badge = "Recorder",
            features = listOf("4CH", "PoE", "AcuSense", "H.265+", "Hik-Connect"),
            description = "4 channel PoE NVR for small CCTV installations, with smart playback and remote app viewing support."
        ),
        Product(
            id = "nvr-8ch",
            sku = "DS-7608NXI-K1/8P",
            title = "Hikvision 8CH PoE AcuSense NVR",
            category = "Recorders",
            price = 164.99,
            stock = "In Stock",
            badge = "Recorder",
            features = listOf("8CH", "PoE", "AcuSense", "H.265+", "HDMI"),
            description = "8 channel PoE NVR for professional installations with support for multiple cameras and remote access."
        ),
        Product(
            id = "hdd-2tb",
            sku = "HDD-SURV-2TB",
            title = "2TB Surveillance Hard Drive",
            category = "Storage",
            price = 61.08,
            stock = "In Stock",
            badge = "Storage",
            features = listOf("2TB", "24/7 Recording", "CCTV Use", "SATA"),
            description = "Surveillance hard drive suitable for CCTV DVR and NVR recording."
        ),
        Product(
            id = "switch-8poe",
            sku = "UK-SW-8-1000-P",
            title = "UKVISION 8 Port Gigabit PoE Switch",
            category = "Networking",
            price = 79.99,
            stock = "In Stock",
            badge = "PoE",
            features = listOf("8 Port", "Gigabit", "PoE", "VLAN", "Extend Mode"),
            description = "Gigabit PoE switch for CCTV and network installations, suitable for IP camera deployment."
        ),
        Product(
            id = "cat6-20m",
            sku = "CAT6-20M-BLK",
            title = "20m Cat6 RJ45 Network Cable Black",
            category = "Cable",
            price = 7.99,
            stock = "In Stock",
            badge = "Cable",
            features = listOf("20m", "Cat6", "RJ45", "Black"),
            description = "Pre-made network cable for CCTV camera and PoE installations."
        ),
        Product(
            id = "bracket-junction",
            sku = "JUNCTION-BOX-WHT",
            title = "White CCTV Camera Junction Box",
            category = "Accessories",
            price = 8.49,
            stock = "In Stock",
            badge = "Accessory",
            features = listOf("Weather Resistant", "Cable Management", "White", "Wall/Ceiling"),
            description = "Junction box for tidy CCTV camera installation and cable protection."
        )
    )

    val orders = listOf(
        TradeOrder("CL-10284", "Dispatched", 384.94, "16 May 2026"),
        TradeOrder("CL-10251", "Processing", 742.31, "15 May 2026"),
        TradeOrder("CL-10188", "Delivered", 128.47, "12 May 2026")
    )
}

@Composable
fun CCTVLineApp() {
    var selectedTab by rememberSaveable { mutableStateOf(Tab.Home) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    val cart = remember { mutableStateListOf<CartLine>() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun addToCart(product: Product, quantity: Int = 1) {
        val index = cart.indexOfFirst { it.product.id == product.id }
        if (index >= 0) {
            val current = cart[index]
            cart[index] = current.copy(quantity = current.quantity + quantity)
        } else {
            cart.add(CartLine(product, quantity))
        }
        scope.launch { snackbarHostState.showSnackbar("Added to cart") }
    }

    Scaffold(
        topBar = { AppTopBar(cartCount = cart.sumOf { it.quantity }) },
        bottomBar = {
            AppBottomBar(
                selectedTab = selectedTab,
                onSelect = {
                    selectedProduct = null
                    selectedTab = it
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = CctvLightBg
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            selectedProduct?.let { product ->
                ProductDetailScreen(
                    product = product,
                    onBack = { selectedProduct = null },
                    onAddToCart = { addToCart(product) }
                )
            } ?: when (selectedTab) {
                Tab.Home -> HomeScreen(
                    products = DemoData.products,
                    categories = DemoData.categories,
                    onOpenProducts = { selectedTab = Tab.Products },
                    onOpenKit = { selectedTab = Tab.Kit },
                    onProductClick = { selectedProduct = it },
                    onAddToCart = ::addToCart
                )

                Tab.Products -> ProductsScreen(
                    products = DemoData.products,
                    categories = DemoData.categories,
                    onProductClick = { selectedProduct = it },
                    onAddToCart = ::addToCart
                )

                Tab.Kit -> KitBuilderScreen(
                    products = DemoData.products,
                    onAddKit = { lines ->
                        lines.forEach { line -> addToCart(line.product, line.quantity) }
                        scope.launch { snackbarHostState.showSnackbar("Kit added to cart") }
                    },
                    onRequestQuote = {
                        scope.launch { snackbarHostState.showSnackbar("Quote request saved locally. Connect API to submit it.") }
                    }
                )

                Tab.Cart -> CartScreen(
                    cart = cart,
                    onIncrease = { product -> addToCart(product) },
                    onDecrease = { product ->
                        val index = cart.indexOfFirst { it.product.id == product.id }
                        if (index >= 0) {
                            val current = cart[index]
                            if (current.quantity <= 1) cart.removeAt(index) else cart[index] = current.copy(quantity = current.quantity - 1)
                        }
                    },
                    onClear = { cart.clear() }
                )

                Tab.Account -> AccountScreen(orders = DemoData.orders)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(cartCount: Int) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CctvRed),
                    contentAlignment = Alignment.Center
                ) {
                    Text("CL", color = Color.White, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("CCTV Line", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Trade CCTV Supplier", color = Color.White.copy(alpha = 0.72f), fontSize = 12.sp)
                }
            }
        },
        actions = {
            if (cartCount > 0) {
                Badge(
                    containerColor = CctvRed,
                    contentColor = Color.White,
                    modifier = Modifier.padding(end = 16.dp)
                ) { Text(cartCount.toString()) }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = CctvBlack)
    )
}

@Composable
fun AppBottomBar(selectedTab: Tab, onSelect: (Tab) -> Unit) {
    NavigationBar(containerColor = Color.White) {
        Tab.entries.forEach { tab ->
            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = { onSelect(tab) },
                icon = {
                    Text(
                        text = when (tab) {
                            Tab.Home -> "⌂"
                            Tab.Products -> "▦"
                            Tab.Kit -> "◎"
                            Tab.Cart -> "▣"
                            Tab.Account -> "●"
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                label = { Text(tab.label, fontSize = 11.sp) }
            )
        }
    }
}

@Composable
fun HomeScreen(
    products: List<Product>,
    categories: List<Category>,
    onOpenProducts: () -> Unit,
    onOpenKit: () -> Unit,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product, Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HeroCard(onOpenProducts = onOpenProducts, onOpenKit = onOpenKit)
        }
        item {
            SectionHeader("Trade Categories", "Native catalogue, not WebView")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(categories) { category -> CategoryCard(category) }
            }
        }
        item {
            SectionHeader("Featured Products", "Ready for API product feed")
        }
        items(products.take(5)) { product ->
            WideProductCard(product, onClick = { onProductClick(product) }, onAddToCart = { onAddToCart(product, 1) })
        }
    }
}

@Composable
fun HeroCard(onOpenProducts: () -> Unit, onOpenKit: () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CctvBlack),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .background(Brush.linearGradient(listOf(CctvBlack, CctvDarkGrey, CctvRed.copy(alpha = 0.85f))))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Build CCTV systems faster", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                Text(
                    "Native Android trade app for products, kit building, quotes, cart and customer account. No WebView.",
                    color = Color.White.copy(alpha = 0.82f),
                    fontSize = 14.sp
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onOpenKit,
                        colors = ButtonDefaults.buttonColors(containerColor = CctvRed)
                    ) { Text("Build Kit") }
                    OutlinedButton(
                        onClick = onOpenProducts,
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.7f))
                    ) { Text("Browse Products", color = Color.White) }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = CctvBlack)
            subtitle?.let { Text(it, color = Color.Gray, fontSize = 12.sp) }
        }
    }
}

@Composable
fun CategoryCard(category: Category) {
    Card(
        modifier = Modifier
            .width(170.dp)
            .height(120.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(category.name, fontWeight = FontWeight.Bold, color = CctvBlack)
            Text(category.description, fontSize = 12.sp, color = Color.Gray, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text("${category.count} items", fontSize = 12.sp, color = CctvRed, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun ProductsScreen(
    products: List<Product>,
    categories: List<Category>,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product, Int) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("All") }

    val filteredProducts = products.filter { product ->
        val matchesCategory = selectedCategory == "All" || product.category == selectedCategory
        val matchesQuery = query.isBlank() || product.title.contains(query, ignoreCase = true) || product.sku.contains(query, ignoreCase = true)
        matchesCategory && matchesQuery
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search product, SKU or model") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedCategory == "All",
                        onClick = { selectedCategory = "All" },
                        label = { Text("All") }
                    )
                }
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category.name,
                        onClick = { selectedCategory = category.name },
                        label = { Text(category.name) }
                    )
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 168.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredProducts) { product ->
                ProductGridCard(product, onClick = { onProductClick(product) }, onAddToCart = { onAddToCart(product, 1) })
            }
        }
    }
}

@Composable
fun ProductGridCard(product: Product, onClick: () -> Unit, onAddToCart: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ProductImagePlaceholder(product)
            Text(product.badge, color = CctvRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(product.title, maxLines = 2, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold, color = CctvBlack)
            Text(product.sku, color = Color.Gray, fontSize = 12.sp)
            Text("£${product.price.formatPrice()}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = CctvBlack)
            Text(product.stock, color = Color(0xFF087E45), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Button(
                onClick = onAddToCart,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = CctvRed)
            ) { Text("Add") }
        }
    }
}

@Composable
fun WideProductCard(product: Product, onClick: () -> Unit, onAddToCart: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(94.dp)) { ProductImagePlaceholder(product) }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(product.title, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(product.sku, color = Color.Gray, fontSize = 12.sp)
                Text(product.features.take(3).joinToString(" • "), color = Color.Gray, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("£${product.price.formatPrice()}", fontWeight = FontWeight.Black, color = CctvBlack)
            }
            Button(onClick = onAddToCart, colors = ButtonDefaults.buttonColors(containerColor = CctvRed)) { Text("Add") }
        }
    }
}

@Composable
fun ProductImagePlaceholder(product: Product) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.1f)
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.linearGradient(listOf(Color(0xFFF2F3F5), Color(0xFFE0E2E6)))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(CctvBlack),
                contentAlignment = Alignment.Center
            ) {
                Text(product.category.take(1), color = Color.White, fontWeight = FontWeight.Black, fontSize = 22.sp)
            }
            Spacer(Modifier.height(6.dp))
            Text(product.category, color = Color.Gray, fontSize = 11.sp)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductDetailScreen(product: Product, onBack: () -> Unit, onAddToCart: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TextButton(onClick = onBack) { Text("‹ Back") }
            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    ProductImagePlaceholder(product)
                    Text(product.title, fontWeight = FontWeight.Black, fontSize = 24.sp, color = CctvBlack)
                    Text(product.sku, color = Color.Gray)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Badge(containerColor = CctvRed, contentColor = Color.White) { Text(product.badge) }
                        Text(product.stock, color = Color(0xFF087E45), fontWeight = FontWeight.SemiBold)
                    }
                    Text("£${product.price.formatPrice()}", fontWeight = FontWeight.Black, fontSize = 28.sp, color = CctvBlack)
                    Text(product.description, color = Color.DarkGray)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        product.features.forEach { feature -> AssistChip(onClick = {}, label = { Text(feature) }) }
                    }
                    Button(
                        onClick = onAddToCart,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = CctvRed)
                    ) { Text("Add to Cart") }
                }
            }
        }
    }
}

@Composable
fun KitBuilderScreen(products: List<Product>, onAddKit: (List<CartLine>) -> Unit, onRequestQuote: () -> Unit) {
    val recorders = products.filter { it.category == "Recorders" }
    val cameras = products.filter { it.category == "Cameras" }
    val cables = products.filter { it.category == "Cable" }
    val storage = products.filter { it.category == "Storage" }

    var recorderIndex by rememberSaveable { mutableIntStateOf(0) }
    var cameraIndex by rememberSaveable { mutableIntStateOf(0) }
    var cameraQty by rememberSaveable { mutableIntStateOf(4) }
    var hddIndex by rememberSaveable { mutableIntStateOf(0) }
    var includeCable by rememberSaveable { mutableStateOf(true) }

    val recorder = recorders.getOrNull(recorderIndex) ?: products.first()
    val camera = cameras.getOrNull(cameraIndex) ?: products.first()
    val hdd = storage.getOrNull(hddIndex)
    val cable = cables.firstOrNull()
    val kitLines = buildList {
        add(CartLine(recorder, 1))
        add(CartLine(camera, cameraQty))
        if (hdd != null) add(CartLine(hdd, 1))
        if (includeCable && cable != null) add(CartLine(cable, cameraQty))
    }
    val total = kitLines.sumOf { it.product.price * it.quantity }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader("CCTV Kit Builder", "Select recorder, cameras, storage and cable")
        }
        item {
            OptionBlock("1. Recorder") {
                recorders.forEachIndexed { index, item ->
                    SelectRow(item.title, item.sku, recorderIndex == index) { recorderIndex = index }
                }
            }
        }
        item {
            OptionBlock("2. Camera") {
                cameras.forEachIndexed { index, item ->
                    SelectRow(item.title, item.sku, cameraIndex == index) { cameraIndex = index }
                }
                QuantitySelector("Camera Quantity", cameraQty, onMinus = { if (cameraQty > 1) cameraQty-- }, onPlus = { if (cameraQty < 32) cameraQty++ })
            }
        }
        item {
            OptionBlock("3. Storage") {
                storage.forEachIndexed { index, item ->
                    SelectRow(item.title, item.sku, hddIndex == index) { hddIndex = index }
                }
            }
        }
        item {
            OptionBlock("4. Cable") {
                SelectRow("Include ${cable?.title ?: "camera cable"}", "One cable per camera", includeCable) { includeCable = !includeCable }
            }
        }
        item {
            Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = CctvBlack)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Kit Summary", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    kitLines.forEach { line ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${line.quantity} × ${line.product.sku}", color = Color.White.copy(alpha = 0.84f), fontSize = 13.sp)
                            Text("£${(line.product.price * line.quantity).formatPrice()}", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    Divider(color = Color.White.copy(alpha = 0.16f))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                        Text("£${total.formatPrice()}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 22.sp)
                    }
                    Button(
                        onClick = { onAddKit(kitLines) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = CctvRed)
                    ) { Text("Add Complete Kit to Cart") }
                    OutlinedButton(
                        onClick = onRequestQuote,
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.65f))
                    ) { Text("Request Trade Quote", color = Color.White) }
                }
            }
        }
    }
}

@Composable
fun OptionBlock(title: String, content: @Composable Column.() -> Unit) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, fontWeight = FontWeight.Black, color = CctvBlack)
            content()
        }
    }
}

@Composable
fun SelectRow(title: String, subtitle: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .background(if (selected) CctvRed.copy(alpha = 0.08f) else CctvLightBg)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(if (selected) CctvRed else Color.LightGray)
        )
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, color = CctvBlack)
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun QuantitySelector(label: String, value: Int, onMinus: () -> Unit, onPlus: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = onMinus) { Text("−") }
            Text(value.toString(), modifier = Modifier.padding(horizontal = 14.dp), fontWeight = FontWeight.Black)
            OutlinedButton(onClick = onPlus) { Text("+") }
        }
    }
}

@Composable
fun CartScreen(cart: List<CartLine>, onIncrease: (Product) -> Unit, onDecrease: (Product) -> Unit, onClear: () -> Unit) {
    val total = cart.sumOf { it.product.price * it.quantity }
    if (cart.isEmpty()) {
        EmptyState("Cart is empty", "Add products or build a CCTV kit first.")
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { SectionHeader("Cart", "Native cart ready for checkout API") }
        items(cart) { line ->
            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(76.dp)) { ProductImagePlaceholder(line.product) }
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(line.product.title, maxLines = 2, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold)
                        Text(line.product.sku, color = Color.Gray, fontSize = 12.sp)
                        Text("£${line.product.price.formatPrice()}", color = CctvBlack, fontWeight = FontWeight.Black)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        TextButton(onClick = { onIncrease(line.product) }) { Text("+") }
                        Text(line.quantity.toString(), fontWeight = FontWeight.Black)
                        TextButton(onClick = { onDecrease(line.product) }) { Text("−") }
                    }
                }
            }
        }
        item {
            Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = CctvBlack)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Cart Total", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("£${total.formatPrice()}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 22.sp)
                    }
                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = CctvRed)
                    ) { Text("Checkout API Required") }
                    OutlinedButton(onClick = onClear, modifier = Modifier.fillMaxWidth(), border = BorderStroke(1.dp, Color.White.copy(alpha = 0.65f))) {
                        Text("Clear Cart", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun AccountScreen(orders: List<TradeOrder>) {
    var loggedIn by rememberSaveable { mutableStateOf(false) }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (!loggedIn) {
            item {
                Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Trade Login", fontWeight = FontWeight.Black, fontSize = 22.sp, color = CctvBlack)
                        Text("Demo login for UI only. Connect this to CCTV Line auth API.", color = Color.Gray)
                        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        Button(onClick = { loggedIn = true }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = CctvRed)) {
                            Text("Login Demo")
                        }
                    }
                }
            }
        } else {
            item {
                Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Trade Account", fontWeight = FontWeight.Black, fontSize = 22.sp)
                        Text(if (email.isBlank()) "installer@company.co.uk" else email, color = Color.Gray)
                        Text("Trade pricing: Enabled", color = Color(0xFF087E45), fontWeight = FontWeight.SemiBold)
                        Button(onClick = { loggedIn = false }, colors = ButtonDefaults.buttonColors(containerColor = CctvBlack)) { Text("Sign Out") }
                    }
                }
            }
            item { SectionHeader("Recent Orders") }
            items(orders) { order ->
                Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(order.number, fontWeight = FontWeight.Bold)
                            Text(order.date, color = Color.Gray, fontSize = 12.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(order.status, color = CctvRed, fontWeight = FontWeight.Bold)
                            Text("£${order.total.formatPrice()}", fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
            item {
                Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Trade Credit Application", fontWeight = FontWeight.Black)
                        Text("Future module: company details, VAT number, Companies House number, references and document upload.", color = Color.Gray)
                        OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("API Required") }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState(title: String, message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(CctvRed), contentAlignment = Alignment.Center) {
            Text("CL", color = Color.White, fontWeight = FontWeight.Black, fontSize = 24.sp)
        }
        Spacer(Modifier.height(18.dp))
        Text(title, fontSize = 22.sp, fontWeight = FontWeight.Black, color = CctvBlack)
        Text(message, color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
    }
}

fun Double.formatPrice(): String = "%,.2f".format(this)

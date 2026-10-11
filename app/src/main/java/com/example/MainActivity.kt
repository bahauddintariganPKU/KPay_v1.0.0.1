package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.model.CryptoNetwork
import com.example.model.TransactionHistoryItem
import com.example.ui.components.AppBottomNavigationBar
import com.example.ui.components.BottomNavTab
import com.example.ui.components.QrisModalSheet
import com.example.ui.components.ReceiptShareDialog
import com.example.ui.components.TransactionDetailDialog
import com.example.ui.screens.BuyStablecoinScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DompetScreen
import com.example.ui.screens.SayaScreen
import com.example.ui.screens.TransaksiScreen
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

enum class AppDestination {
    MAIN_FLOW,
    BUY_STABLECOIN
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    var currentDestination by remember { mutableStateOf(AppDestination.MAIN_FLOW) }
    var selectedTab by remember { mutableStateOf(BottomNavTab.BERANDA) }

    var rupiahBalance by remember { mutableLongStateOf(15_850_000L) }
    var usdtBalance by remember { mutableDoubleStateOf(1_450.50) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showQrisSheet by remember { mutableStateOf(false) }
    val qrisSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // State for Detail Transaksi & Rubah Harga Jual
    var selectedTransactionDetail by remember { mutableStateOf<TransactionHistoryItem?>(null) }
    val detailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // State for Bagikan Struk
    var receiptToShare by remember { mutableStateOf<TransactionHistoryItem?>(null) }

    var transactions by remember {
        mutableStateOf(
            listOf(
                TransactionHistoryItem(
                    id = "TRX-8829104",
                    title = "Beli 50 USDT (TRC-20)",
                    subtitle = "Beli Stablecoin",
                    date = "Hari ini, 10:45 WIB",
                    amountRupiah = "Rp 812.500",
                    amountCrypto = "+50.00 USDT",
                    isPositive = false,
                    category = "CRYPTO",
                    costPrice = 812500L,
                    sellingPrice = 825000L,
                    targetNumber = "TXq83nLKa72F4wY9mZ9k1aPqX89yBc2V1r",
                    serialNumber = "TxHash: 0x9f8c148e29a018bc72",
                    paymentMethod = "Saldo IDR Dompet"
                ),
                TransactionHistoryItem(
                    id = "TRX-8829103",
                    title = "Token Listrik PLN 100k",
                    subtitle = "ID: 14285719283",
                    date = "Kemarin, 19:20 WIB",
                    amountRupiah = "Rp 101.500",
                    isPositive = false,
                    category = "PLN",
                    costPrice = 101500L,
                    sellingPrice = 104000L,
                    targetNumber = "14285719283",
                    serialNumber = "Token: 2948-1092-4819-2048",
                    paymentMethod = "Saldo IDR Dompet"
                ),
                TransactionHistoryItem(
                    id = "TRX-8829102",
                    title = "Pulsa Telkomsel 50k",
                    subtitle = "081234567890",
                    date = "09 Okt 2026, 14:10 WIB",
                    amountRupiah = "Rp 51.500",
                    isPositive = false,
                    category = "PULSA",
                    costPrice = 51500L,
                    sellingPrice = 53000L,
                    targetNumber = "081234567890",
                    serialNumber = "SN: 849201948201",
                    paymentMethod = "Saldo IDR Dompet"
                ),
                TransactionHistoryItem(
                    id = "TRX-8829101",
                    title = "Top Up Saldo Bank BCA",
                    subtitle = "Virtual Account",
                    date = "08 Okt 2026, 09:30 WIB",
                    amountRupiah = "Rp 2.000.000",
                    isPositive = true,
                    category = "TOPUP",
                    costPrice = 2000000L,
                    sellingPrice = 2000000L,
                    paymentMethod = "Bank BCA Transfer"
                )
            )
        )
    }

    AnimatedContent(
        targetState = currentDestination,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "AppDestinationTransition"
    ) { destination ->
        when (destination) {
            AppDestination.MAIN_FLOW -> {
                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    containerColor = BackgroundLight,
                    bottomBar = {
                        AppBottomNavigationBar(
                            selectedTab = selectedTab,
                            onTabSelected = { tab ->
                                if (tab == BottomNavTab.QRIS) {
                                    showQrisSheet = true
                                } else {
                                    selectedTab = tab
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = innerPadding.calculateBottomPadding())
                    ) {
                        when (selectedTab) {
                            BottomNavTab.BERANDA -> {
                                DashboardScreen(
                                    rupiahBalance = rupiahBalance,
                                    usdtBalance = usdtBalance,
                                    transactionList = transactions,
                                    onBeliStablecoinClick = {
                                        currentDestination = AppDestination.BUY_STABLECOIN
                                    },
                                    onPpobPurchase = { title, costAmount, targetNumber, serialNumber ->
                                        if (rupiahBalance >= costAmount) {
                                            rupiahBalance -= costAmount
                                            val sellingPriceInit = costAmount + 2500L
                                            val newTx = TransactionHistoryItem(
                                                id = "TRX-${(1000000..9999999).random()}",
                                                title = title,
                                                subtitle = targetNumber,
                                                date = "Hari ini, Baru saja",
                                                amountRupiah = "Rp " + "%,d".format(costAmount).replace(',', '.'),
                                                isPositive = false,
                                                category = if (title.contains("PLN", ignoreCase = true)) "PLN" else "PULSA",
                                                costPrice = costAmount,
                                                sellingPrice = sellingPriceInit,
                                                targetNumber = targetNumber,
                                                serialNumber = serialNumber,
                                                paymentMethod = "Saldo IDR Dompet"
                                            )
                                            transactions = listOf(newTx) + transactions
                                            // Munculkan detail transaksi dengan fitur rubah harga jual
                                            selectedTransactionDetail = newTx
                                        } else {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Saldo tidak cukup untuk melakukan transaksi.")
                                            }
                                        }
                                    },
                                    onTopUpRupiah = { topUpAmount ->
                                        rupiahBalance += topUpAmount
                                        val newTx = TransactionHistoryItem(
                                            id = "TRX-${(1000000..9999999).random()}",
                                            title = "Top Up Saldo",
                                            subtitle = "Instan Top Up",
                                            date = "Hari ini, Baru saja",
                                            amountRupiah = "Rp " + "%,d".format(topUpAmount).replace(',', '.'),
                                            isPositive = true,
                                            category = "TOPUP",
                                            costPrice = topUpAmount,
                                            sellingPrice = topUpAmount
                                        )
                                        transactions = listOf(newTx) + transactions
                                    },
                                    onTransactionClick = { tx ->
                                        selectedTransactionDetail = tx
                                    }
                                )
                            }

                            BottomNavTab.DOMPET -> {
                                DompetScreen(
                                    rupiahBalance = rupiahBalance,
                                    usdtBalance = usdtBalance,
                                    onTopUpClick = {
                                        rupiahBalance += 500_000L
                                        val newTx = TransactionHistoryItem(
                                            id = "TRX-${(1000000..9999999).random()}",
                                            title = "Top Up Saldo",
                                            subtitle = "Instan Top Up",
                                            date = "Hari ini, Baru saja",
                                            amountRupiah = "Rp 500.000",
                                            isPositive = true,
                                            category = "TOPUP",
                                            costPrice = 500000L,
                                            sellingPrice = 500000L
                                        )
                                        transactions = listOf(newTx) + transactions
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Top Up Berhasil! +Rp 500.000 ditambahkan.")
                                        }
                                    },
                                    onBeliCryptoClick = {
                                        currentDestination = AppDestination.BUY_STABLECOIN
                                    }
                                )
                            }

                            BottomNavTab.QRIS -> {
                                DashboardScreen(
                                    rupiahBalance = rupiahBalance,
                                    usdtBalance = usdtBalance,
                                    transactionList = transactions,
                                    onBeliStablecoinClick = {
                                        currentDestination = AppDestination.BUY_STABLECOIN
                                    },
                                    onPpobPurchase = { _, _, _, _ -> },
                                    onTopUpRupiah = { _ -> },
                                    onTransactionClick = { tx ->
                                        selectedTransactionDetail = tx
                                    }
                                )
                            }

                            BottomNavTab.TRANSAKSI -> {
                                TransaksiScreen(
                                    transactions = transactions,
                                    onItemClick = { tx ->
                                        selectedTransactionDetail = tx
                                    }
                                )
                            }

                            BottomNavTab.SAYA -> {
                                SayaScreen(
                                    onLogoutClick = {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Sesi akun aktif.")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            AppDestination.BUY_STABLECOIN -> {
                BuyStablecoinScreen(
                    currentRupiahBalance = rupiahBalance,
                    onBackClick = {
                        currentDestination = AppDestination.MAIN_FLOW
                    },
                    onPurchaseSuccess = { amountRupiah, amountUsdt, network, walletAddress ->
                        rupiahBalance -= amountRupiah
                        usdtBalance += amountUsdt
                        val newTx = TransactionHistoryItem(
                            id = "TRX-${(1000000..9999999).random()}",
                            title = "Beli %.2f USDT (%s)".format(amountUsdt, network.displayName),
                            subtitle = "Ke: ${walletAddress.take(6)}...${walletAddress.takeLast(4)}",
                            date = "Hari ini, Baru saja",
                            amountRupiah = "Rp " + "%,d".format(amountRupiah).replace(',', '.'),
                            amountCrypto = "+%.2f USDT".format(amountUsdt),
                            isPositive = false,
                            category = "CRYPTO",
                            costPrice = amountRupiah,
                            sellingPrice = amountRupiah + 10000L,
                            targetNumber = walletAddress,
                            serialNumber = "TxHash: 0x" + (1000000..9999999).random()
                        )
                        transactions = listOf(newTx) + transactions
                        // Buka Detail Transaksi langsung
                        selectedTransactionDetail = newTx
                    }
                )
            }
        }
    }

    // Modal Sheet for QRIS Scanner
    if (showQrisSheet) {
        QrisModalSheet(
            sheetState = qrisSheetState,
            onDismiss = { showQrisSheet = false },
            onPaymentSuccess = { amount, merchantName ->
                showQrisSheet = false
                if (rupiahBalance >= amount) {
                    rupiahBalance -= amount
                    val newTx = TransactionHistoryItem(
                        id = "TRX-${(1000000..9999999).random()}",
                        title = "QRIS - $merchantName",
                        subtitle = "Pembayaran Merchant",
                        date = "Hari ini, Baru saja",
                        amountRupiah = "Rp " + "%,d".format(amount).replace(',', '.'),
                        isPositive = false,
                        category = "PPOB",
                        costPrice = amount,
                        sellingPrice = amount,
                        targetNumber = merchantName,
                        serialNumber = "NMID: ID" + (1000000000..9999999999).random()
                    )
                    transactions = listOf(newTx) + transactions
                    // Buka Detail Transaksi
                    selectedTransactionDetail = newTx
                } else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Saldo tidak mencukupi untuk pembayaran QRIS.")
                    }
                }
            }
        )
    }

    // Modal Sheet: Detail Transaksi & Fitur Rubah Harga Jual (Gambar Pena Kecil)
    if (selectedTransactionDetail != null) {
        TransactionDetailDialog(
            transaction = selectedTransactionDetail!!,
            sheetState = detailSheetState,
            onDismiss = { selectedTransactionDetail = null },
            onUpdateSellingPrice = { txId, newPrice ->
                transactions = transactions.map { item ->
                    if (item.id == txId) item.copy(sellingPrice = newPrice) else item
                }
                selectedTransactionDetail = selectedTransactionDetail?.copy(sellingPrice = newPrice)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Harga jual berhasil diubah menjadi Rp %,d".format(newPrice))
                }
            },
            onShareReceipt = { tx ->
                receiptToShare = tx
            }
        )
    }

    // Dialog: Bukti Struk Pembeli
    if (receiptToShare != null) {
        ReceiptShareDialog(
            transaction = receiptToShare!!,
            onDismiss = { receiptToShare = null },
            onShareDone = {
                receiptToShare = null
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Struk berhasil dibagikan!")
                }
            }
        )
    }
}

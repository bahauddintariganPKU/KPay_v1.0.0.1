package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TransactionHistoryItem
import com.example.ui.theme.DividerColor
import com.example.ui.theme.IceBlue
import com.example.ui.theme.IceBlueCard
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueDark
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SoftBlueBorder
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailDialog(
    transaction: TransactionHistoryItem,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onUpdateSellingPrice: (transactionId: String, newSellingPrice: Long) -> Unit,
    onShareReceipt: (TransactionHistoryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditPriceDialog by remember { mutableStateOf(false) }
    var currentSellingPrice by remember(transaction.sellingPrice) {
        mutableStateOf(
            if (transaction.sellingPrice > 0) transaction.sellingPrice
            else if (transaction.costPrice > 0) transaction.costPrice + 2000L
            else 50000L
        )
    }

    val cost = if (transaction.costPrice > 0) transaction.costPrice else (currentSellingPrice - 2000L).coerceAtLeast(0L)
    val profit = (currentSellingPrice - cost).coerceAtLeast(0L)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = PureWhite,
        modifier = modifier.testTag("transaction_detail_modal")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Detail Transaksi & Struk",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_detail_button")
                ) {
                    Icon(
                        imageVector = AppIcons.Close,
                        contentDescription = "Tutup",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Success Badge Circle
            Surface(
                shape = CircleShape,
                color = SuccessGreen,
                modifier = Modifier.size(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = AppIcons.Check,
                        contentDescription = "Berhasil",
                        tint = PureWhite,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Transaksi Berhasil",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SuccessGreen
            )

            Text(
                text = transaction.date,
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Struk Card Container
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = IceBlueCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftBlueBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // ID & Status
                    DetailInfoRow("No. Referensi", transaction.id)
                    DetailInfoRow("Produk", transaction.title)
                    if (transaction.targetNumber != null) {
                        DetailInfoRow("Nomor Tujuan", transaction.targetNumber)
                    } else if (transaction.subtitle.isNotBlank()) {
                        DetailInfoRow("Tujuan / Info", transaction.subtitle)
                    }

                    if (transaction.serialNumber != null) {
                        DetailInfoRow("No. Seri / Token", transaction.serialNumber)
                    }

                    DetailInfoRow("Metode Pembayaran", transaction.paymentMethod)
                    DetailInfoRow("Status Transaksi", transaction.status)

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = SoftBlueBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Harga Modal Agen
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Harga Modal (Beli)",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = "Biaya potong saldo dompet",
                                fontSize = 10.sp,
                                color = TextSecondary.copy(alpha = 0.8f)
                            )
                        }
                        Text(
                            text = "Rp " + "%,d".format(cost).replace(',', '.'),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // FITUR RUBAH HARGA JUAL (DENGAN GAMBAR PENA KECIL)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PureWhite,
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, PrimaryBlue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showEditPriceDialog = true }
                            .testTag("rubah_harga_jual_trigger")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Harga Jual ke Pembeli",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    // GAMBAR PENA KECIL
                                    Surface(
                                        shape = CircleShape,
                                        color = IceBlue,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = AppIcons.EditPen,
                                                contentDescription = "Ubah Harga Jual",
                                                tint = PrimaryBlue,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "Klik pena kecil untuk edit harga",
                                    fontSize = 10.sp,
                                    color = PrimaryBlue
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Rp " + "%,d".format(currentSellingPrice).replace(',', '.'),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PrimaryBlue
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                // Gambar Pena Kecil Button
                                Icon(
                                    imageVector = AppIcons.EditPen,
                                    contentDescription = "Edit Pena",
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Keuntungan Agen
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Estimasi Keuntungan Anda",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                        Text(
                            text = "+Rp " + "%,d".format(profit).replace(',', '.'),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SuccessGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons: Bagikan Struk & Selesai
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val updatedTx = transaction.copy(sellingPrice = currentSellingPrice)
                        onShareReceipt(updatedTx)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("share_receipt_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = AppIcons.Share,
                        contentDescription = "Bagikan",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Bagikan Struk",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("done_detail_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text(
                        text = "Selesai",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                }
            }
        }
    }

    // Dialog Pengeditan Harga Jual
    if (showEditPriceDialog) {
        var inputPriceText by remember { mutableStateOf(currentSellingPrice.toString()) }

        AlertDialog(
            onDismissRequest = { showEditPriceDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = IceBlue, modifier = Modifier.size(32.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = AppIcons.EditPen,
                                contentDescription = "Edit",
                                tint = PrimaryBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Rubah Harga Jual",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = TextPrimary
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Tentukan harga jual yang akan tertera pada struk pembeli. Harga modal Anda tetap Rp %,d.".format(cost),
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = inputPriceText,
                        onValueChange = { inputPriceText = it.filter { ch -> ch.isDigit() } },
                        prefix = {
                            Text(
                                text = "Rp ",
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                        },
                        label = { Text("Harga Jual Baru") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_selling_price_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = SoftBlueBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Rekomendasi Margin Keuntungan:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Quick Margin buttons (+1000, +2000, +3000, +5000)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(1000L, 2000L, 3000L, 5000L).forEach { marginAdd ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = IceBlue,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        inputPriceText = (cost + marginAdd).toString()
                                    }
                            ) {
                                Text(
                                    text = "+%d rb".format(marginAdd / 1000),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            }
                        }
                    }

                    val newPriceVal = inputPriceText.toLongOrNull() ?: 0L
                    val newProfit = (newPriceVal - cost).coerceAtLeast(0L)

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Keuntungan baru: +Rp %,d".format(newProfit),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = inputPriceText.toLongOrNull() ?: currentSellingPrice
                        if (parsed >= cost) {
                            currentSellingPrice = parsed
                            onUpdateSellingPrice(transaction.id, parsed)
                            showEditPriceDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    modifier = Modifier.testTag("save_selling_price_button")
                ) {
                    Text("Simpan Harga")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showEditPriceDialog = false },
                    modifier = Modifier.testTag("cancel_edit_price_button")
                ) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun DetailInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = TextSecondary)
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            textAlign = TextAlign.End
        )
    }
}

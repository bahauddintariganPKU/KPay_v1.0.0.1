package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TransactionHistoryItem
import com.example.ui.theme.IceBlueCard
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SoftBlueBorder
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ReceiptShareDialog(
    transaction: TransactionHistoryItem,
    onDismiss: () -> Unit,
    onShareDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayPrice = if (transaction.sellingPrice > 0) transaction.sellingPrice else transaction.costPrice

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Struk Pembelian Pelanggan",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Struk Paper Surface
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = IceBlueCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftBlueBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "FINTECH & PPOB MITRA",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = PrimaryBlue
                        )
                        Text(
                            text = "BUKTI TRANSAKSI PEMBAYARAN",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = SoftBlueBorder)
                        Spacer(modifier = Modifier.height(8.dp))

                        ReceiptLine("Waktu", transaction.date)
                        ReceiptLine("No. Trx", transaction.id)
                        ReceiptLine("Layanan", transaction.title)
                        if (transaction.targetNumber != null) {
                            ReceiptLine("Tujuan", transaction.targetNumber)
                        }
                        if (transaction.serialNumber != null) {
                            ReceiptLine("SN / Token", transaction.serialNumber)
                        }
                        ReceiptLine("Status", "LUNAS / BERHASIL")

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = SoftBlueBorder)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "TOTAL BAYAR",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Rp %,d".format(displayPrice).replace(',', '.'),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = PrimaryBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Terima kasih atas pembayaran Anda",
                            fontSize = 10.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onShareDone,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                modifier = Modifier.testTag("confirm_share_receipt_button")
            ) {
                Text("Kirim / Cetak Struk")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dismiss_share_receipt_button")
            ) {
                Text("Tutup")
            }
        }
    )
}

@Composable
private fun ReceiptLine(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = TextSecondary)
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }
}

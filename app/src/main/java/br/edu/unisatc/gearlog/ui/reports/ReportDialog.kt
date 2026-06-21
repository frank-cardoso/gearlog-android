package br.edu.unisatc.gearlog.ui.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.edu.unisatc.gearlog.model.ReportType
import br.edu.unisatc.gearlog.model.Vehicle
import br.edu.unisatc.gearlog.model.LogRecord
import br.edu.unisatc.gearlog.ui.theme.PremiumCard
import br.edu.unisatc.gearlog.ui.theme.premiumCard

@Composable
fun ReportDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    vehicle: Vehicle,
    logs: List<LogRecord>,
    onGenerate: (ReportType) -> Unit
) {
    if (!show) return

    val selected = remember { mutableStateOf(ReportType.FULL) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                onGenerate(selected.value)
                onDismiss()
            }) {
                Text("Gerar e Compartilhar")
            }
        },
        title = {
            Text(text = "Exportar Relatório")
        },
        text = {
            Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.premiumCard, modifier = Modifier.padding(4.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .clickable { selected.value = ReportType.FULL }
                        .padding(6.dp)) {
                        RadioButton(selected = selected.value == ReportType.FULL, onClick = { selected.value = ReportType.FULL })
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(text = "Histórico Completo", color = MaterialTheme.colorScheme.onSurface)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .clickable { selected.value = ReportType.MAINTENANCE }
                        .padding(6.dp)) {
                        RadioButton(selected = selected.value == ReportType.MAINTENANCE, onClick = { selected.value = ReportType.MAINTENANCE })
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(text = "Apenas Manutenções", color = MaterialTheme.colorScheme.onSurface)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .clickable { selected.value = ReportType.UPGRADE }
                        .padding(6.dp)) {
                        RadioButton(selected = selected.value == ReportType.UPGRADE, onClick = { selected.value = ReportType.UPGRADE })
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(text = "Apenas Upgrades", color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    )
}


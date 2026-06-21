package br.edu.unisatc.gearlog.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import br.edu.unisatc.gearlog.model.LogRecord
import br.edu.unisatc.gearlog.model.ReportType
import br.edu.unisatc.gearlog.model.Vehicle
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object PdfReportGenerator {

    fun generateVehicleReport(context: Context, vehicle: Vehicle, logs: List<LogRecord>, reportType: ReportType) {
        // Filter logs
        val filtered = when (reportType) {
            ReportType.FULL -> logs
            ReportType.MAINTENANCE -> logs.filter { it.type.equals("MAINTENANCE", ignoreCase = true) }
            ReportType.UPGRADE -> logs.filter { it.type.equals("MOD", ignoreCase = true) || it.type.equals("UPGRADE", ignoreCase = true) }
        }

        val pageWidth = 595
        val pageHeight = 842

        val doc = PdfDocument()
        // Use dark text so it is visible on the default white PDF background
        val paintHeader = Paint().apply {
            color = Color.BLACK
            textSize = 18f
            isFakeBoldText = true
        }
        val paintSub = Paint().apply {
            color = Color.DKGRAY
            textSize = 12f
        }
        val paintLine = Paint().apply {
            color = Color.DKGRAY
            strokeWidth = 1f
        }
        val paintTable = Paint().apply {
            color = Color.BLACK
            textSize = 11f
        }

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currency = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        var pageIndex = 1
        var y = 40f
        val leftMargin = 36f
        val rightMargin = (pageWidth - 36).toFloat()

        // start first page
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex).create()
        var currentPage = doc.startPage(pageInfo)
        var canvas: Canvas = currentPage.canvas

        // Header
        canvas.drawText("GearLog", leftMargin, y, paintHeader)
        y += 22f
        canvas.drawText("Veículo: ${vehicle.brand} ${vehicle.model}", leftMargin, y, paintSub)
        y += 16f
        canvas.drawText("Placa: ${vehicle.plate}    Odômetro: ${vehicle.odometer}", leftMargin, y, paintSub)
        y += 16f
        canvas.drawText("Relatório: ${reportType.name}", leftMargin, y, paintSub)
        y += 18f
        canvas.drawLine(leftMargin, y, rightMargin, y, paintLine)
        y += 18f

        // Table header
        canvas.drawText("Data", leftMargin, y, paintTable)
        canvas.drawText("Título", leftMargin + 100f, y, paintTable)
        canvas.drawText("Tipo", leftMargin + 320f, y, paintTable)
        canvas.drawText("Valor", leftMargin + 420f, y, paintTable)
        y += 14f
        canvas.drawLine(leftMargin, y, rightMargin, y, paintLine)
        y += 12f

        var total = 0.0

        for (log in filtered) {
            if (y > pageHeight - 120) {
                // finish current page and start a new one
                doc.finishPage(currentPage)
                pageIndex++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex).create()
                currentPage = doc.startPage(pageInfo)
                canvas = currentPage.canvas
                y = 40f

                // redraw header on new page
                canvas.drawText("GearLog", leftMargin, y, paintHeader)
                y += 22f
                canvas.drawText("Veículo: ${vehicle.brand} ${vehicle.model}", leftMargin, y, paintSub)
                y += 16f
                canvas.drawText("Placa: ${vehicle.plate}    Odômetro: ${vehicle.odometer}", leftMargin, y, paintSub)
                y += 16f
                canvas.drawText("Relatório: ${reportType.name}", leftMargin, y, paintSub)
                y += 18f
                canvas.drawLine(leftMargin, y, rightMargin, y, paintLine)
                y += 18f

                // Table header
                canvas.drawText("Data", leftMargin, y, paintTable)
                canvas.drawText("Título", leftMargin + 100f, y, paintTable)
                canvas.drawText("Tipo", leftMargin + 320f, y, paintTable)
                canvas.drawText("Valor", leftMargin + 420f, y, paintTable)
                y += 14f
                canvas.drawLine(leftMargin, y, rightMargin, y, paintLine)
                y += 12f
            }

            val date = dateFormat.format(Date(log.date))
            canvas.drawText(date, leftMargin, y, paintTable)
            canvas.drawText(log.title, leftMargin + 100f, y, paintTable)
            canvas.drawText(log.type, leftMargin + 320f, y, paintTable)
            canvas.drawText(currency.format(log.cost), leftMargin + 420f, y, paintTable)
            y += 16f
            total += log.cost
        }

        // Footer with total
        canvas.drawLine(leftMargin, pageHeight - 80f, rightMargin, pageHeight - 80f, paintLine)
        val totalText = "Total investido: ${currency.format(total)}"
        canvas.drawText(totalText, leftMargin, pageHeight - 60f, paintSub)

        // finish current page
        doc.finishPage(currentPage)

        // Write to file
        try {
            val fileName = "gearlog_report_${vehicle.id}_${System.currentTimeMillis()}.pdf"
            val file = File(context.cacheDir, fileName)
            FileOutputStream(file).use { out ->
                doc.writeTo(out)
            }
            doc.close()

            // Share via FileProvider
            val authority = "${context.packageName}.fileprovider"
            val uri = FileProvider.getUriForFile(context, authority, file)

            val share = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(share, "Compartilhar relatório"))

        } catch (e: Exception) {
            e.printStackTrace()
            doc.close()
        }
    }
}




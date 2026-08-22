package com.example.rentmanagement.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import java.io.File
import java.io.FileOutputStream

/**
 * Generic CSV / PDF table export for the Reports module (spec section 13:
 * "Allow exporting reports as PDF and CSV").
 */
object ReportExporter {

    fun exportCsv(context: Context, fileName: String, headers: List<String>, rows: List<List<String>>): File {
        val dir = File(context.filesDir, "reports").apply { mkdirs() }
        val file = File(dir, fileName)
        file.bufferedWriter().use { writer ->
            writer.appendLine(headers.joinToString(",") { csvEscape(it) })
            rows.forEach { row -> writer.appendLine(row.joinToString(",") { csvEscape(it) }) }
        }
        return file
    }

    private fun csvEscape(value: String): String =
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"${value.replace("\"", "\"\"")}\""
        } else value

    fun exportPdf(context: Context, fileName: String, title: String, headers: List<String>, rows: List<List<String>>): File {
        val pageWidth = 595
        val pageHeight = 842
        val document = PdfDocument()
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        val titlePaint = Paint().apply { textSize = 18f; isFakeBoldText = true; color = Color.BLACK }
        val headerPaint = Paint().apply { textSize = 11f; isFakeBoldText = true; color = Color.BLACK }
        val cellPaint = Paint().apply { textSize = 10f; color = Color.BLACK }
        val linePaint = Paint().apply { color = Color.LTGRAY }

        val marginLeft = 32f
        val marginTop = 40f
        val rowHeight = 20f
        val colCount = headers.size.coerceAtLeast(1)
        val colWidth = (pageWidth - marginLeft * 2) / colCount

        var y = marginTop
        canvas.drawText(title, marginLeft, y, titlePaint)
        y += 26f
        headers.forEachIndexed { i, h -> canvas.drawText(h, marginLeft + i * colWidth, y, headerPaint) }
        y += 6f
        canvas.drawLine(marginLeft, y, pageWidth - marginLeft, y, linePaint)
        y += rowHeight

        var pageNum = 1
        for (row in rows) {
            if (y > pageHeight - 60f) {
                document.finishPage(page)
                pageNum++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas
                y = marginTop
            }
            row.forEachIndexed { i, cell -> canvas.drawText(cell, marginLeft + i * colWidth, y, cellPaint) }
            y += rowHeight
        }
        document.finishPage(page)

        val dir = File(context.filesDir, "reports").apply { mkdirs() }
        val file = File(dir, fileName)
        FileOutputStream(file).use { document.writeTo(it) }
        document.close()
        return file
    }
}

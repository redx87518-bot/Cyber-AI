package com.cyberfusion.core.utils

import android.content.Context
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font
import java.io.File
import java.io.FileOutputStream

object PdfReportGenerator {
    init {
        PDFBoxResourceLoader.init(null)
    }

    fun generateReport(context: Context, content: String, outputFile: File) {
        val document = PDDocument()
        val page = PDPage()
        document.addPage(page)

        val contentStream = PDPageContentStream(document, page)
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14f)
        contentStream.beginText()
        contentStream.newLineAtOffset(50f, 750f)
        contentStream.showText("CyberFusion AI Report")
        contentStream.endText()

        contentStream.setFont(PDType1Font.HELVETICA, 10f)
        contentStream.beginText()
        contentStream.newLineAtOffset(50f, 720f)
        
        val lines = content.split("\n")
        var yPosition = 720f
        for (line in lines) {
            if (yPosition < 50f) {
                contentStream.endText()
                contentStream.close()
                val newPage = PDPage()
                document.addPage(newPage)
                contentStream.setFont(PDType1Font.HELVETICA, 10f)
                contentStream.beginText()
                contentStream.newLineAtOffset(50f, 750f)
                yPosition = 750f
            }
            contentStream.showText(line)
            yPosition -= 14f
            contentStream.newLineAtOffset(0f, -14f)
        }
        contentStream.endText()
        contentStream.close()

        FileOutputStream(outputFile).use { document.save(it) }
        document.close()
    }
}

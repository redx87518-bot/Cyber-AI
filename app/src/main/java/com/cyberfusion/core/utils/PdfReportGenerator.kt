package com.cyberfusion.core.utils

import android.content.Context
import com.cyberfusion.core.report.AgentReport
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font
import com.tom_roush.pdfbox.pdmodel.font.PDType0Font
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReportGenerator {
    init {
        try {
            PDFBoxResourceLoader.init(null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun generateReport(context: Context, report: AgentReport, outputFile: File): Boolean {
        var document: PDDocument? = null
        var contentStream: PDPageContentStream? = null
        
        return try {
            val dir = outputFile.parentFile
            if (dir != null && !dir.exists()) {
                dir.mkdirs()
            }
            
            document = PDDocument()
            var page = PDPage()
            document.addPage(page)
            contentStream = PDPageContentStream(document, page)
            var yPosition = 750f

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val margin = 50f
            val maxWidth = PDRectangle.A4.width - margin * 2
            var currentPage = 1

            fun newPage() {
                contentStream?.endText()
                contentStream?.close()
                page = PDPage()
                document?.addPage(page)
                contentStream = PDPageContentStream(document, page)
                yPosition = 750f
                currentPage++
            }

            fun checkPageBreak(requiredHeight: Float = 20f) {
                if (yPosition < margin + requiredHeight) {
                    newPage()
                }
            }

            fun writeSection(title: String) {
                checkPageBreak(30f)
                contentStream?.setFont(PDType1Font.HELVETICA_BOLD, 14f)
                contentStream?.beginText()
                contentStream?.newLineAtOffset(margin, yPosition)
                contentStream?.showText(title)
                contentStream?.endText()
                yPosition -= 25f
            }

            fun writeLine(text: String, fontSize: Float = 10f, bold: Boolean = false, indent: Float = 0f) {
                checkPageBreak(fontSize + 10f)
                contentStream?.setFont(if (bold) PDType1Font.HELVETICA_BOLD else PDType1Font.HELVETICA, fontSize)
                contentStream?.beginText()
                contentStream?.newLineAtOffset(margin + indent, yPosition)
                
                val words = text.split(" ")
                var currentLine = StringBuilder()
                var lineWidth = 0
                val maxLineWidth = ((maxWidth - indent) / fontSize * 0.8f).toInt()
                
                for (word in words) {
                    if (lineWidth + word.length + 1 > maxLineWidth && currentLine.isNotEmpty()) {
                        contentStream?.showText(currentLine.toString())
                        yPosition -= fontSize + 4f
                        checkPageBreak(fontSize + 10f)
                        contentStream?.beginText()
                        contentStream?.newLineAtOffset(margin + indent, yPosition)
                        currentLine = StringBuilder(word)
                        lineWidth = word.length
                    } else {
                        if (currentLine.isNotEmpty()) {
                            currentLine.append(" ")
                            lineWidth++
                        }
                        currentLine.append(word)
                        lineWidth += word.length
                    }
                }
                if (currentLine.isNotEmpty()) {
                    contentStream?.showText(currentLine.toString())
                    yPosition -= fontSize + 4f
                }
                contentStream?.endText()
            }

            fun writeSeparator() {
                checkPageBreak(20f)
                contentStream?.setLineWidth(1f)
                contentStream?.moveTo(margin, yPosition)
                contentStream?.lineTo(maxWidth + margin, yPosition)
                contentStream?.stroke()
                yPosition -= 15f
            }

            writeSection("CyberFusion Investigation Report")
            writeLine("Report ID: ${report.reportId}", bold = true)
            writeLine("Generated: ${dateFormat.format(Date(report.generatedAt))}")
            writeLine("Severity: ${report.severity ?: "N/A"} | Confidence: ${report.confidence ?: "N/A"}%")
            writeSeparator()

            writeSection("Executive Summary")
            writeLine(report.summary)
            writeSeparator()

            writeSection("User Request")
            writeLine(report.metadata["userRequest"] ?: "N/A")
            writeSeparator()

            writeSection("Scope")
            writeLine(report.metadata["scope"] ?: "Investigation as requested")
            writeSeparator()

            writeSection("Methodology")
            writeLine(report.methodology ?: "Automated agent investigation with threat intelligence enrichment")
            writeSeparator()

            writeSection("Agent Plan")
            report.metadata["plan"]?.let { writeLine(it) } ?: writeLine("No plan recorded")
            writeSeparator()

            writeSection("Tools Used")
            report.metadata["toolsUsed"]?.let { writeLine(it) } ?: writeLine("No tools recorded")
            writeSeparator()

            writeSection("Timeline")
            report.metadata["timeline"]?.let { writeLine(it) } ?: writeLine("No timeline recorded")
            writeSeparator()

            writeSection("Evidence")
            if (report.evidence.isEmpty()) {
                writeLine("No evidence collected")
            } else {
                report.evidence.forEach { evidence ->
                    writeLine("[${evidence.type}] ${evidence.source}: ${evidence.content.take(500)}", indent = 10f)
                }
            }
            writeSeparator()

            writeSection("Findings")
            report.findings.forEach { finding ->
                writeLine("${finding.title} (${finding.severity}, Confidence: ${finding.confidence}%)", bold = true)
                writeLine(finding.description, indent = 10f)
                writeLine("")
            }
            writeSeparator()

            writeSection("MITRE ATT&CK")
            if (report.mitreAttack.isEmpty()) writeLine("No MITRE ATT&CK mappings") else {
                report.mitreAttack.forEach { writeLine("- $it") }
            }
            writeSeparator()

            writeSection("ISO 27001 Controls")
            if (report.iso27001Controls.isEmpty()) writeLine("No ISO 27001 mappings") else {
                report.iso27001Controls.forEach { writeLine("- $it") }
            }
            writeSeparator()

            writeSection("Limitations")
            if (report.limitations.isEmpty()) writeLine("No limitations recorded") else {
                report.limitations.forEach { writeLine("- $it") }
            }
            writeSeparator()

            writeSection("Recommendations")
            report.recommendations.forEach { writeLine("- $it") }
            writeSeparator()

            writeSection("Conclusion")
            writeLine(report.summary)
            writeSeparator()

            writeSection("Execution Metadata")
            writeLine("Report ID: ${report.reportId}")
            writeLine("Agent Version: 1.0.0")
            writeLine("Execution Time: ${dateFormat.format(Date(report.generatedAt))}")
            writeLine("Page: $currentPage")
            report.metadata.forEach { (key, value) -> writeLine("$key: $value") }

            contentStream?.endText()
            contentStream?.close()
            
            FileOutputStream(outputFile).use { document?.save(it) }
            document?.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                contentStream?.close()
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
            try {
                document?.close()
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
            false
        }
    }
}

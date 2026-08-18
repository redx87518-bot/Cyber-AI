package com.cyberfusion.core.utils

import android.content.Context
import android.net.Uri
import com.cyberfusion.core.agent.AgentReport
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReportGenerator {
    init {
        PDFBoxResourceLoader.init(null)
    }

    fun generateReport(context: Context, report: AgentReport, outputFile: File) {
        val document = PDDocument()
        var page = PDPage()
        document.addPage(page)
        var contentStream = PDPageContentStream(document, page)
        var yPosition = 750f

        fun newPage() {
            contentStream.endText()
            contentStream.close()
            page = PDPage()
            document.addPage(page)
            contentStream = PDPageContentStream(document, page)
            yPosition = 750f
        }

        fun checkPage(requiredSpace: Float = 50f) {
            if (yPosition < requiredSpace) newPage()
        }

        fun writeLine(text: String, fontSize: Float = 10f, bold: Boolean = false, indent: Float = 0f) {
            checkPage(30f)
            contentStream.setFont(if (bold) PDType1Font.HELVETICA_BOLD else PDType1Font.HELVETICA, fontSize)
            contentStream.beginText()
            contentStream.newLineAtOffset(50f + indent, yPosition)
            val wrapped = wrapText(text, if (bold) 90 else 95)
            for (line in wrapped) {
                checkPage(20f)
                contentStream.showText(line)
                yPosition -= fontSize + 4f
                contentStream.newLineAtOffset(0f, -(fontSize + 4f))
            }
            contentStream.endText()
        }

        fun writeSection(title: String) {
            checkPage(40f)
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14f)
            contentStream.beginText()
            contentStream.newLineAtOffset(50f, yPosition)
            contentStream.showText(title)
            contentStream.endText()
            yPosition -= 20f
        }

        fun writeSeparator() {
            checkPage(20f)
            contentStream.setLineWidth(1f)
            contentStream.moveTo(50f, yPosition)
            contentStream.lineTo(550f, yPosition)
            contentStream.stroke()
            yPosition -= 15f
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

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
        report.metadata.forEach { (key, value) -> writeLine("$key: $value") }

        contentStream.endText()
        contentStream.close()
        FileOutputStream(outputFile).use { document.save(it) }
        document.close()
    }

    private fun wrapText(text: String, maxCharsPerLine: Int): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = StringBuilder()
        for (word in words) {
            if ((currentLine.length + word.length + 1) > maxCharsPerLine) {
                lines.add(currentLine.toString())
                currentLine = StringBuilder(word)
            } else {
                if (currentLine.isNotEmpty()) currentLine.append(" ")
                currentLine.append(word)
            }
        }
        if (currentLine.isNotEmpty()) lines.add(currentLine.toString())
        return lines
    }
}

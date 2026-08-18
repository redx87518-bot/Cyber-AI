# PDF Reports

## Generation
PDF reports are generated using PDFBox Android.

## Report Contents
- Title and Report ID
- Executive Summary
- User Request and Scope
- Methodology
- Agent Plan
- Tools Used
- Timeline
- Evidence
- Findings
- MITRE ATT&CK Mapping
- ISO 27001 Controls
- Limitations
- Recommendations
- Conclusion
- Execution Metadata

## Error Handling
- PDF generation runs off main thread
- All exceptions caught and logged
- Failed PDFs are automatically cleaned up
- User sees error message instead of crash

## File Location
PDFs are stored in app-specific external storage:
`/Android/data/com.cyberfusion.app/files/`

## Sharing
Users can:
- Open PDF directly
- Share via Android share sheet
- Regenerate if needed
 

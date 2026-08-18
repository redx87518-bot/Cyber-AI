# AI Agent System

## Overview
The CyberFusion AI agent operates as an autonomous cybersecurity assistant that plans, executes, and reports on security tasks.

## Agent Flow
1. **User Request** - Natural language input
2. **Planning** - Build execution plan with steps
3. **Tool Execution** - Run cybersecurity tools
4. **Evidence Collection** - Gather and correlate findings
5. **AI Synthesis** - Summarize results
6. **Report Generation** - Create PDF report

## Planning
The agent uses keyword-based planning to determine required tools:
- "alert"/"siem" → getAlerts
- "investigate"/"incident" → getInvestigations
- "ip"/"domain"/"hash"/"url" → enrichIOC
- "risk"/"grc"/"compliance" → getRisks
- "cve"/"vulnerability" → getNvdCve
- "malware"/"hash" → queryMalwareBazaar
- "mitre"/"attack" → mitreLookup
- "iso"/"27001" → iso27001Lookup
- "report"/"pdf" → generateReport

## Providers
- **OpenRouter** - Cloud AI (optional)
- **LocalAI** - Local model support
- **Groq**, **Gemini**, **OpenAI** - Additional cloud providers

## Model Management
Users can configure:
- Active AI provider
- Fallback provider
- Model selection per provider
- Enable/disable providers

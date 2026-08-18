# CyberFusion Architecture

## Overview
CyberFusion is an Android-based AI cybersecurity platform built with Kotlin and Jetpack Compose.

## Tech Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Database**: Room
- **Networking**: Ktor
- **AI**: Google ADK Kotlin, OpenRouter, local providers
- **PDF**: PDFBox Android
- **Navigation**: Jetpack Navigation Compose

## Architecture Layers

### UI Layer
- `com.cyberfusion.ui.features.*` - Feature screens
- `com.cyberfusion.ui.components.*` - Reusable components
- `com.cyberfusion.ui.navigation.*` - Navigation setup
- `com.cyberfusion.ui.theme.*` - Theming

### Domain Layer
- `com.cyberfusion.core.agent.*` - Agent orchestration
- `com.cyberfusion.core.ai.*` - AI providers and tools
- `com.cyberfusion.core.evidence.*` - Evidence tracking
- `com.cyberfusion.core.report.*` - Report models

### Data Layer
- `com.cyberfusion.core.database.*` - Room entities and repositories
- `com.cyberfusion.core.network.*` - API clients
- `com.cyberfusion.core.utils.*` - Utilities (PDF, logging)

## Key Components

### Agent System
- `AgentService` - Interface for agent operations
- `DefaultAgentService` - Main agent implementation
- `AgentPlan` / `AgentPlanStep` - Planning structures
- `AgentEvent` - Event streaming

### AI Providers
- `AIProviderAdapter` - Provider interface
- `OpenRouterAdapter` - OpenRouter cloud AI
- `LocalAIAdapter` - Local model support
- `AIProviderFactory` - Provider creation

### Tools
- `AIToolRegistry` - Central tool registry
- `ToolRepositories` - Data source for tools
- Tools: alerts, IOC enrichment, threat intel, GRC, reporting, etc.

### Navigation
- `CyberFusionNavHost` - Single NavHost
- `Screen` sealed class - Route definitions
- Bottom bar + More menu navigation

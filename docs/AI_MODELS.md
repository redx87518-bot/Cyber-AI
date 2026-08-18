# AI Models

## Local Models
- **Falcon-H1-Tiny-90M Tool Calling** (~47 MB)
  - ARM64 compatible
  - Tool-call format supported
  - Offline capable

## Cloud Providers
- **OpenRouter** - Multi-model gateway
- **Groq** - Fast inference
- **Gemini** - Google AI
- **OpenAI** - GPT models

## Configuration
Users can configure providers in Settings:
- API key entry
- Model selection
- Enable/disable
- Primary/fallback designation

## Fallback Behavior
If primary provider fails:
1. Log error
2. Attempt fallback provider
3. Show user-friendly error if both fail

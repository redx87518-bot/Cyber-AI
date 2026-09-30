# AI Models

## Primary: Rax AI (ai.raxcore.dev)

CyberFusion's agent engine is **Rax AI**. The endpoint is fully OpenAI-compatible:

- **Base URL**: `https://ai.raxcore.dev/api/v1`
- **Auth**: `Authorization: Bearer rax_...` (free key at ai.raxcore.dev)
- **Endpoint**: `POST /chat/completions` (JSON, `model`, `messages`)

### Models (both selectable in Settings → AI Providers → Rax AI)

| Model | Role | Strengths |
|-------|------|-----------|
| `rax-4.0` | The workhorse | Open-source, sub-50ms, real-time triage |
| `rax-4.5` | The deep thinker | 262K context, long investigations & reasoning |

The agent prefers `rax-4.5` by default (deep reasoning); switch to `rax-4.0`
for fastest responses. "Test" validates the key against `GET /models`.

## Fallback / legacy providers

OpenRouter, Groq, Gemini and OpenAI remain configurable in Settings.
Provider priority: **Rax AI** → user-designated primary → first enabled provider.

## Local Models

- **Falcon-H1-Tiny-90M Tool Calling** (~47 MB) — offline capable, tool-call format

## Fallback Behavior

If the primary provider fails: log error → attempt next enabled provider →
show a user-friendly error. The agentic loop also degrades gracefully to raw
tool-result synthesis if the model call fails after tools have run.

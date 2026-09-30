# AI Agent System

## Overview
The CyberFusion agent is an autonomous senior-analyst loop powered by **Rax AI**.
It plans, calls real security tools with real arguments, collects evidence, and
remembers what it learns.

## Agentic Loop (ReAct-style)
1. **System prompt** — the agent receives the full security tool registry with
   concrete parameter schemas, its long-term memory, and the memory protocol.
2. **Reasoning** — Rax AI plans silently and emits tool calls as fenced blocks:

   ```tool
   [{"tool":"checkAbuseIPDB","args":{"ip":"1.2.3.4"}}]
   ```

3. **Execution** — `AIToolRegistry.executeTool` runs each call with the model's
   arguments; results are appended to the conversation (`TOOL RESULTS:` turn).
4. **Iterate** — the model continues calling tools or produces the final answer.
   Bounded at 8 iterations.
5. **Synthesis** — final answers include executive summary, key findings,
   severity, recommended actions and career-learning notes.
6. **Memory write-back** — durable facts are saved (see below) and stripped
   from the user-facing reply.

## Long-Term Memory
Memories persist in Room (`agent_memories`) and are recalled by importance,
recency and IOC relevance:

- kinds: `ioc_reputation`, `infrastructure`, `methodology`, `user_preference`
- the model saves facts by appending a ```memory block``` to its final answer
- memories are injected into every subsequent investigation — the agent gets
  smarter over time; stale low-importance memories are pruned after 30 days

## Planning
A deterministic pre-plan still runs keyword-based steps (alerts, IOCs, GRC,
CVE, MITRE, ISO, reports) so results exist even before model reasoning; the
agentic loop then extends and refines them with tool calls.

## Tools
All 28 tools in `AIToolRegistry` are exposed to the model with real schemas —
see docs/TOOLS.md.

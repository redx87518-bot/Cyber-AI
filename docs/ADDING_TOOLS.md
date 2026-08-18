# Adding New Tools

## Steps

1. **Create Tool Entry** in `AIToolRegistry.tools`
```kotlin
AITool("toolName", "Description", mapOf("param" to "String"))
```

2. **Implement Execution Function**
```kotlin
private suspend fun executeToolName(parameters: Map<String, String>, repositories: ToolRepositories): AIToolResult {
    // Implementation
}
```

3. **Register in executeTool**
Add case to the `when` statement in `AIToolRegistry.executeTool()`

4. **Add UI Category**
Update `ToolsScreen.kt` category mapping if needed

5. **Add Tests**
Create unit tests in `app/src/test/java/`

## Tool Requirements
- Must accept `Map<String, String>` parameters
- Must return `AIToolResult`
- Must not crash on invalid input
- Must handle network errors gracefully

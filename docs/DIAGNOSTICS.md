# Diagnostics and Logging

## Logging System
The app uses `CyberFusionLogger` for structured logging.

### Log Levels
- `DEBUG` - Development details
- `INFO` - General information
- `WARN` - Warning conditions
- `ERROR` - Error conditions

### What Gets Logged
- AI requests and responses (metadata only)
- Tool calls and results
- Navigation events
- PDF generation events
- API errors
- Crash information

### What Does NOT Get Logged
- API keys
- Passwords
- Tokens
- Private credentials

## Diagnostics Screen
Access via More → Diagnostics

Features:
- View recent logs
- Clear logs
- Export logs for support

## Debug Mode
Enable in developer settings to increase log verbosity.

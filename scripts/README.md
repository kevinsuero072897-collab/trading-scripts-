# Scripts

This folder contains standalone utility scripts and tools for the trading system.

## Contents

### Java Scripts

- **`strategy.11.java`** - Advanced trading strategy implementations including ScheduledEventVolatilityOverlayStrategy and ImpliedLiquidityMappingStrategy

### PowerShell Scripts

- **`topstep-auth-recurring.ps1`** - PowerShell 7+ script for recurring Topstep authentication with environment variable support

### Batch Scripts

- **`create-desktop-shortcut.bat`** - Windows batch script to automatically create a desktop shortcut for running the Topstep token utility

## Usage

### Compiling Java Scripts

```bash
# For strategy implementations
javac strategy.11.java
java strategy.11
```

### Running PowerShell Scripts

```powershell
# Requires PowerShell 7+
pwsh topstep-auth-recurring.ps1
```

### Creating Desktop Shortcut

```cmd
create-desktop-shortcut.bat
```

**Note**: The batch script references `run-topstep-token.bat` which is not present in the repository. You may need to create this file or modify the script to reference the appropriate target.

## Dependencies

- Java 8+ for Java scripts
- PowerShell 7+ for `.ps1` scripts  
- Windows environment for `.bat` scripts
- `.env` file required for PowerShell authentication scripts (see topstep-token-service for details)
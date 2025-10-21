# Getting Started with Atlassian Low Level Design

Welcome to the Atlassian Low Level Design repository! This guide will help you navigate and use the various modules.

## 📁 Repository Structure

```
AtlassianLowLevelDesign/
├── pom.xml                          # Parent POM (multi-module)
├── README.md                        # Main documentation
├── GETTING_STARTED.md               # This file
├── INTERVIEW_STRATEGY.md            # Interview coding strategy
├── INTERVIEW_CHEAT_SHEET.md         # Quick reference
│
└── logging-lld/                     # Logging Library Module
    ├── pom.xml                      # Module POM
    ├── README.md                    # Module documentation
    ├── QUICK_START.md               # Quick start guide
    ├── PROJECT_SUMMARY.md           # Project summary
    ├── DESIGN_DECISIONS.md          # Design rationale
    └── src/
        ├── main/java/               # Source code
        └── test/java/               # Test code
```

## 🚀 Quick Start

### Prerequisites
- **Java:** 11 or higher
- **Maven:** 3.6 or higher

Check your versions:
```bash
java -version
mvn -version
```

### Clone and Build

```bash
# Navigate to the project directory
cd AtlassianLowLevelDesign

# Build all modules
mvn clean install

# Expected output: BUILD SUCCESS
```

## 📚 Working with Modules

### Option 1: Build All Modules (Recommended)

From the root directory:
```bash
# Clean and build everything
mvn clean install

# Run all tests
mvn test

# Clean everything
mvn clean
```

### Option 2: Build Specific Module

```bash
# Navigate to the module
cd logging-lld

# Build the module
mvn clean install

# Run tests
mvn test

# Run the demo
mvn exec:java -Dexec.mainClass="com.loggingmc.DriverApplication"
```

## 🎯 Logging Library Module

### Quick Demo

```bash
cd logging-lld
mvn exec:java -Dexec.mainClass="com.loggingmc.DriverApplication"
```

**Expected Output:**
```
=== Logging Library Demo ===

Demo 1: Synchronous Logger
--------------------------------------------------
13-10-2025-21-39-19 [DEBUG] This is a DEBUG message
13-10-2025-21-39-19 [INFO] This is an INFO message
...
```

### Run Tests

```bash
cd logging-lld
mvn test
```

**Expected Output:**
```
Tests run: 43, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### Run Examples

```bash
cd logging-lld
mvn exec:java -Dexec.mainClass="com.loggingmc.examples.UsageExamples"
```

## 📖 Documentation Guide

### For Learning the Implementation

1. **Start with the main README**
   - [Main README](./README.md) - Overview of all modules

2. **Read the module documentation**
   - [Logging Library README](./logging-lld/README.md) - Detailed features
   - [Quick Start Guide](./logging-lld/QUICK_START.md) - Usage examples
   - [Project Summary](./logging-lld/PROJECT_SUMMARY.md) - Complete overview
   - [Design Decisions](./logging-lld/DESIGN_DECISIONS.md) - Design rationale

3. **Study the code**
   - Start with simple classes: `LogLevel`, `LogMessage`
   - Then interfaces: `Logger`, `Sink`
   - Then implementations: `SyncLogger`, `AsyncLogger`
   - Finally, the factory: `LoggerFactory`

### For Interview Preparation

1. **Read the interview strategy**
   - [Interview Strategy](logging-lld/INTERVIEW_STRATEGY.md) - Detailed 60-min approach
   - [Interview Cheat Sheet](./INTERVIEW_CHEAT_SHEET.md) - Quick reference

2. **Practice coding**
   - Follow the implementation order (1-10)
   - Time yourself (aim for 45-60 minutes)
   - Build incrementally, demo frequently

3. **Review design decisions**
   - Understand trade-offs
   - Prepare for follow-up questions
   - Know alternative approaches

## 🛠️ Development Workflow

### Adding a New Module

1. **Create module directory**
   ```bash
   mkdir new-module-lld
   cd new-module-lld
   mkdir -p src/main/java src/test/java
   ```

2. **Create module POM**
   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <project xmlns="http://maven.apache.org/POM/4.0.0"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
            http://maven.apache.org/xsd/maven-4.0.0.xsd">
       <modelVersion>4.0.0</modelVersion>

       <parent>
           <groupId>com.atlassian.lld</groupId>
           <artifactId>AtlassianLowLevelDesign</artifactId>
           <version>1.0-SNAPSHOT</version>
       </parent>

       <artifactId>new-module-lld</artifactId>
       <packaging>jar</packaging>

       <name>New Module - Low Level Design</name>
       <description>Description of the module</description>

       <dependencies>
           <dependency>
               <groupId>junit</groupId>
               <artifactId>junit</artifactId>
               <scope>test</scope>
           </dependency>
       </dependencies>
   </project>
   ```

3. **Update parent POM**
   Add to the `<modules>` section in root `pom.xml`:
   ```xml
   <modules>
       <module>logging-lld</module>
       <module>new-module-lld</module>
   </modules>
   ```

4. **Build and verify**
   ```bash
   cd ..
   mvn clean install
   ```

### Running Specific Tests

```bash
# Run all tests in a module
cd logging-lld
mvn test

# Run a specific test class
mvn test -Dtest=SyncLoggerTest

# Run a specific test method
mvn test -Dtest=SyncLoggerTest#testLoggerCreation
```

### Debugging

```bash
# Run with debug output
mvn -X test

# Run with specific Java options
mvn test -DargLine="-Xmx512m"
```

## 📊 Verification Checklist

After building, verify everything works:

```bash
# ✅ Build succeeds
mvn clean install

# ✅ All tests pass
mvn test

# ✅ Demo runs
cd logging-lld
mvn exec:java -Dexec.mainClass="com.loggingmc.DriverApplication"

# ✅ Examples run
mvn exec:java -Dexec.mainClass="com.loggingmc.examples.UsageExamples"
```

## 🎓 Learning Path

### Beginner
1. Read the main README
2. Run the logging demo
3. Study the basic classes (LogLevel, LogMessage)
4. Run the tests

### Intermediate
1. Study the SyncLogger implementation
2. Understand thread safety mechanisms
3. Review the test cases
4. Try modifying the code

### Advanced
1. Study the AsyncLogger implementation
2. Understand the design decisions
3. Practice building from scratch
4. Prepare for interview questions

## 🔧 Troubleshooting

### Build Fails

**Problem:** Maven build fails
```bash
# Solution: Clean and rebuild
mvn clean
mvn install
```

**Problem:** Java version mismatch
```bash
# Check Java version
java -version

# Should be Java 11 or higher
# Update JAVA_HOME if needed
export JAVA_HOME=/path/to/java11
```

### Tests Fail

**Problem:** Tests fail intermittently
```bash
# Solution: Run tests again (may be timing issues)
mvn clean test
```

**Problem:** Specific test fails
```bash
# Run with verbose output
mvn test -Dtest=FailingTest -X
```

### Demo Doesn't Run

**Problem:** Main class not found
```bash
# Solution: Compile first
mvn clean compile
mvn exec:java -Dexec.mainClass="com.loggingmc.DriverApplication"
```

## 📞 Getting Help

### Documentation
- Main README: [README.md](./README.md)
- Module README: [logging-lld/README.md](./logging-lld/README.md)
- Interview Guide: [INTERVIEW_STRATEGY.md](logging-lld/INTERVIEW_STRATEGY.md)

### Common Commands Reference

```bash
# Build everything
mvn clean install

# Run tests
mvn test

# Run specific module
cd logging-lld && mvn test

# Run demo
cd logging-lld && mvn exec:java -Dexec.mainClass="com.loggingmc.DriverApplication"

# Clean everything
mvn clean

# Skip tests during build
mvn install -DskipTests

# Run with debug
mvn -X test
```

## 🎯 Next Steps

1. ✅ Build the project: `mvn clean install`
2. ✅ Run the demo: `cd logging-lld && mvn exec:java`
3. ✅ Run the tests: `mvn test`
4. ✅ Read the documentation: Start with [README.md](./README.md)
5. ✅ Study the code: Begin with simple classes
6. ✅ Practice: Use the [Interview Strategy](logging-lld/INTERVIEW_STRATEGY.md)

## 🌟 Tips

- **Build incrementally** - Don't try to understand everything at once
- **Run frequently** - Test your understanding by running code
- **Read tests** - Tests are great documentation
- **Practice coding** - Build from scratch to solidify learning
- **Time yourself** - Practice under interview conditions

---

**Happy Learning! 🚀**

For more details, see the [main README](./README.md).


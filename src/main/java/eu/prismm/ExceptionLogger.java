package eu.prismm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Map;

public class ExceptionLogger {
    private static final Logger LOGGER = LogManager.getLogger(NoConsoleSpam.MOD_ID);
    private static final String ERROR_DIRECTORY = "Console Errors";
    private final FilterConfig.ExceptionSettings settings;
    private boolean isInitialized = false;
    
    // Pattern to match common mod/plugin package patterns
    private static final Pattern MOD_PACKAGE_PATTERN = Pattern.compile(
            "(net\\.minecraft|net\\.fabricmc|io\\.fabric|com\\.mojang|" +
            "net\\.minecraftforge|org\\.bukkit|org\\.spigotmc|" +
            "cpw\\.mods|mezz\\.jei|vazkii\\.botania|" +
            "joptsimple|com\\.google|org\\.apache|org\\.slf4j|" +
            "java\\.util|java\\.lang|java\\.io|java\\.nio|" +
            "oshi\\..|joml\\..|" +
            "[a-z0-9_]+\\.[a-z0-9_]+\\.[a-z0-9_]+)",
            Pattern.CASE_INSENSITIVE);
    
    // Pattern to identify Fabric-specific exceptions
    private static final Pattern FABRIC_EXCEPTION_PATTERN = Pattern.compile(
            "(net\\.fabricmc|io\\.fabric)",
            Pattern.CASE_INSENSITIVE);
    
    // Pattern to identify Minecraft-specific exceptions
    private static final Pattern MINECRAFT_EXCEPTION_PATTERN = Pattern.compile(
            "(net\\.minecraft|com\\.mojang)",
            Pattern.CASE_INSENSITIVE);
    
    // Categories of exceptions to recognize
    private static final Map<String, Pattern> EXCEPTION_CATEGORIES = new HashMap<>();
    
    static {
        // Initialize exception categories
        EXCEPTION_CATEGORIES.put("Runtime", Pattern.compile(".*RuntimeException"));
        EXCEPTION_CATEGORIES.put("IO", Pattern.compile(".*IOException"));
        EXCEPTION_CATEGORIES.put("Concurrent", Pattern.compile(".*ConcurrentModificationException|.*InterruptedException"));
        EXCEPTION_CATEGORIES.put("Reflection", Pattern.compile(".*ReflectiveOperationException|.*IllegalAccessException|.*NoSuchMethodException|.*InstantiationException"));
        EXCEPTION_CATEGORIES.put("Security", Pattern.compile(".*SecurityException"));
        EXCEPTION_CATEGORIES.put("Network", Pattern.compile(".*SocketException|.*ConnectException|.*UnknownHostException"));
        EXCEPTION_CATEGORIES.put("Parse", Pattern.compile(".*ParseException|.*NumberFormatException|.*DateTimeException"));
        EXCEPTION_CATEGORIES.put("Data", Pattern.compile(".*SQLException|.*JsonException|.*JsonSyntaxException"));
        EXCEPTION_CATEGORIES.put("Minecraft", Pattern.compile(".*CommandSyntaxException"));
        EXCEPTION_CATEGORIES.put("Graphics", Pattern.compile(".*RenderException|.*TextureException"));
        EXCEPTION_CATEGORIES.put("Memory", Pattern.compile(".*OutOfMemoryError"));
    }
    
    public ExceptionLogger(FilterConfig.ExceptionSettings settings) {
        this.settings = settings;
        if (settings.isCaptureExceptions()) {
            try {
                ensureErrorDirectoryExists();
                isInitialized = true;
                LOGGER.info("Exception logger initialized with directory: {}", new File(ERROR_DIRECTORY).getAbsolutePath());
            } catch (Exception e) {
                LOGGER.error("Failed to initialize exception logger", e);
            }
        }
    }
    
    private void ensureErrorDirectoryExists() {
        File errorDir = new File(ERROR_DIRECTORY);
        if (!errorDir.exists()) {
            if (errorDir.mkdirs()) {
                LOGGER.info("Created error directory: {}", errorDir.getAbsolutePath());
            } else {
                LOGGER.error("Failed to create error directory: {}", errorDir.getAbsolutePath());
                isInitialized = false;
            }
        }
    }
    
    /**
     * Create a directory for a specific exception source if it doesn't exist
     * 
     * @param source The source identifier (mod/plugin name or class)
     * @return The directory File object
     */
    private File ensureSourceDirectoryExists(String source) {
        if (!settings.isOrganizeBySource()) {
            return new File(ERROR_DIRECTORY);
        }
        
        File sourceDir = new File(ERROR_DIRECTORY, source);
        if (!sourceDir.exists()) {
            if (sourceDir.mkdirs()) {
                LOGGER.debug("Created source-specific error directory: {}", sourceDir.getAbsolutePath());
            } else {
                LOGGER.error("Failed to create source-specific error directory: {}", sourceDir.getAbsolutePath());
                // Fall back to main error directory
                return new File(ERROR_DIRECTORY);
            }
        }
        return sourceDir;
    }
    
    /**
     * Extract the source information from the exception stack trace
     * 
     * @param throwable The exception to analyze
     * @return A string identifying the source (mod/plugin name or main class)
     */
    private String extractExceptionSource(Throwable throwable) {
        if (throwable == null) {
            return "Unknown";
        }
        
        // Get the exception class name as a fallback
        String source = throwable.getClass().getSimpleName();
        
        // First check if this is a categorized exception type
        String category = categorizeException(throwable);
        if (category != null) {
            source = category + "-" + source;
        }
        
        // Try to extract mod/plugin from stack trace
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        if (stackTrace != null && stackTrace.length > 0) {
            // First check the direct cause of the exception
            for (StackTraceElement element : stackTrace) {
                String className = element.getClassName();
                Matcher matcher = MOD_PACKAGE_PATTERN.matcher(className);
                if (matcher.find()) {
                    String packageName = matcher.group(1);
                    String[] parts = packageName.split("\\.");
                    if (parts.length >= 2) {
                        // Use the second part of the package as identifier (e.g., 'minecraft' from 'net.minecraft')
                        // or third part for more specific identification
                        source = parts.length >= 3 ? parts[2] : parts[1];
                        break;
                    }
                }
            }
        }
        
        // Clean up the source name to make it safe for filesystem
        return source.replaceAll("[^a-zA-Z0-9.-]", "_");
    }
    
    /**
     * Categorize the exception based on its class hierarchy
     * 
     * @param throwable The exception to categorize
     * @return A category name or null if no specific category is found
     */
    private String categorizeException(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        
        String className = throwable.getClass().getName();
        
        // Check against each category pattern
        for (Map.Entry<String, Pattern> entry : EXCEPTION_CATEGORIES.entrySet()) {
            if (entry.getValue().matcher(className).matches()) {
                return entry.getKey();
            }
        }
        
        // Check class hierarchy
        if (throwable instanceof RuntimeException) {
            return "Runtime";
        } else if (throwable instanceof IOException) {
            return "IO";
        } else if (throwable instanceof Error) {
            return "Error";
        } else if (throwable instanceof Exception) {
            return "General";
        }
        
        return null;
    }
    
    /**
     * Check if the exception should be captured based on its type and our settings
     * 
     * @param throwable The exception to check
     * @return true if this exception type should be captured, false otherwise
     */
    private boolean shouldCaptureException(Throwable throwable) {
        if (throwable == null) {
            return false;
        }
        
        // Check for runtime exceptions
        if (throwable instanceof RuntimeException) {
            return settings.isCaptureRuntimeExceptions();
        }
        
        // Check for IO exceptions
        if (throwable instanceof IOException) {
            return settings.isCaptureIOExceptions();
        }
        
        // Check for errors - these are serious and should usually be logged
        if (throwable instanceof Error) {
            // Don't capture assertion errors in development environments
            if (throwable instanceof AssertionError) {
                return true;
            }
            return true;
        }
        
        // Check for common checked exceptions
        if (throwable instanceof ClassNotFoundException ||
            throwable instanceof CloneNotSupportedException ||
            throwable instanceof IllegalAccessException ||
            throwable instanceof InstantiationException ||
            throwable instanceof InterruptedException ||
            throwable instanceof NoSuchFieldException ||
            throwable instanceof NoSuchMethodException ||
            throwable instanceof ReflectiveOperationException) {
            return true;
        }
        
        // Check for Fabric-specific exceptions
        if (settings.isCaptureFabricExceptions()) {
            // Check the exception class itself
            if (FABRIC_EXCEPTION_PATTERN.matcher(throwable.getClass().getName()).find()) {
                return true;
            }
            
            // Check the stack trace
            for (StackTraceElement element : throwable.getStackTrace()) {
                if (FABRIC_EXCEPTION_PATTERN.matcher(element.getClassName()).find()) {
                    return true;
                }
            }
        }
        
        // Check for Minecraft-specific exceptions
        if (settings.isCaptureMinecraftExceptions()) {
            // Check the exception class itself
            if (MINECRAFT_EXCEPTION_PATTERN.matcher(throwable.getClass().getName()).find()) {
                return true;
            }
            
            // Check the stack trace
            for (StackTraceElement element : throwable.getStackTrace()) {
                if (MINECRAFT_EXCEPTION_PATTERN.matcher(element.getClassName()).find()) {
                    return true;
                }
            }
        }
        
        // Capture everything else by default
        return true;
    }
    
    /**
     * Log an exception to a file
     * 
     * @param message The log message associated with the exception
     * @param exception The exception to log
     */
    public void logException(String message, Throwable exception) {
        if (!isInitialized || !settings.isCaptureExceptions() || !shouldCaptureException(exception)) {
            return;
        }
        
        // Extract the source from the exception
        String source = extractExceptionSource(exception);
        
        // Generate a unique filename based on timestamp and exception type
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(new Date());
        String exceptionType = exception.getClass().getSimpleName();
        String filename = String.format("%s_%s.log", timestamp, exceptionType);
        
        // Create the source-specific directory and file
        File sourceDir = ensureSourceDirectoryExists(source);
        File logFile = new File(sourceDir, filename);
        
        try (FileWriter fw = new FileWriter(logFile);
             PrintWriter pw = new PrintWriter(fw)) {
            
            // Write header information
            pw.println("Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
            pw.println("Exception Type: " + exception.getClass().getName());
            pw.println("Category: " + (categorizeException(exception) != null ? categorizeException(exception) : "Uncategorized"));
            pw.println("Source: " + source);
            pw.println("Message: " + message);
            pw.println("Exception Message: " + exception.getMessage());
            
            // Add information about cause if present
            Throwable cause = exception.getCause();
            if (cause != null) {
                pw.println("Caused by: " + cause.getClass().getName() + ": " + cause.getMessage());
            }
            
            pw.println("\nStackTrace:");
            
            // Write the stack trace
            exception.printStackTrace(pw);
            
            // Add any suppressed exceptions
            Throwable[] suppressed = exception.getSuppressed();
            if (suppressed != null && suppressed.length > 0) {
                pw.println("\nSuppressed Exceptions:");
                for (Throwable t : suppressed) {
                    pw.println("  Suppressed: " + t.getClass().getName() + ": " + t.getMessage());
                    t.printStackTrace(pw);
                }
            }
            
            LOGGER.debug("Logged exception to file: {}", logFile.getAbsolutePath());
            
            // Enforce max file count if needed
            enforceMaxFileCount();
        } catch (IOException e) {
            LOGGER.error("Failed to write exception to log file", e);
        }
    }
    
    private void enforceMaxFileCount() {
        File dir = new File(ERROR_DIRECTORY);
        enforceMaxFileCountInDir(dir);
        
        // Check subdirectories too
        File[] subdirs = dir.listFiles(File::isDirectory);
        if (subdirs != null) {
            for (File subdir : subdirs) {
                enforceMaxFileCountInDir(subdir);
            }
        }
    }
    
    private void enforceMaxFileCountInDir(File dir) {
        File[] files = dir.listFiles(File::isFile);
        
        if (files != null && files.length > settings.getMaxBackupIndex()) {
            // Sort files by last modified time (oldest first)
            Arrays.sort(files, (f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));
            
            // Delete oldest files until we're under the limit
            for (int i = 0; i < files.length - settings.getMaxBackupIndex(); i++) {
                if (files[i].delete()) {
                    LOGGER.debug("Deleted old log file: {}", files[i].getName());
                } else {
                    LOGGER.warn("Failed to delete old log file: {}", files[i].getName());
                }
            }
        }
    }
    
    public boolean isInitialized() {
        return isInitialized;
    }
} 
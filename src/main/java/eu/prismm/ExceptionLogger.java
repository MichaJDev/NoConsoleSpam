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

public class ExceptionLogger {
    private static final Logger LOGGER = LogManager.getLogger(NoConsoleSpam.MOD_ID);
    private static final String ERROR_DIRECTORY = "Console Errors";
    private final FilterConfig.ExceptionSettings settings;
    private boolean isInitialized = false;
    
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
    
    public void logException(String message, Throwable exception) {
        if (!isInitialized || !settings.isCaptureExceptions()) {
            return;
        }
        
        // Generate a unique filename based on timestamp and exception type
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(new Date());
        String exceptionType = exception.getClass().getSimpleName();
        String filename = String.format("%s_%s.log", timestamp, exceptionType);
        
        // Create the full path
        File logFile = new File(ERROR_DIRECTORY, filename);
        
        try (FileWriter fw = new FileWriter(logFile);
             PrintWriter pw = new PrintWriter(fw)) {
            
            // Write header information
            pw.println("Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
            pw.println("Exception: " + exception.getClass().getName());
            pw.println("Message: " + message);
            pw.println("\nStackTrace:");
            
            // Write the stack trace
            exception.printStackTrace(pw);
            
            LOGGER.debug("Logged exception to file: {}", logFile.getAbsolutePath());
            
            // Enforce max file count if needed
            enforceMaxFileCount();
        } catch (IOException e) {
            LOGGER.error("Failed to write exception to log file", e);
        }
    }
    
    private void enforceMaxFileCount() {
        File dir = new File(ERROR_DIRECTORY);
        File[] files = dir.listFiles();
        
        if (files != null && files.length > settings.getMaxBackupIndex()) {
            // Sort files by last modified time (oldest first)
            java.util.Arrays.sort(files, (f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));
            
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
package eu.prismm;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

public class NoConsoleSpam implements ModInitializer, PreLaunchEntrypoint {
    public static final String MOD_ID = "noconsolespam";
    public static final String VERSION = "1.4.1"; // Updated version to reflect Minecraft 1.21.4 and 1.21.5 compatibility
    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    
    private FilterConfig filterConfig;
    private ExceptionLogger exceptionLogger;
    
    @Override
    public void onPreLaunch() {
        LOGGER.info("Pre-initializing NoConsoleSpam v{}", VERSION);
        
        try {
            // Load configuration from JSON
            filterConfig = new FilterConfig();
            
            // Initialize exception logger
            exceptionLogger = new ExceptionLogger(filterConfig.getExceptionSettings());
            
            // Configure log4j filtering
            configureLogging();
        } catch (Exception e) {
            LOGGER.error("Failed to pre-initialize NoConsoleSpam", e);
            // Ensure we have at least default instances to prevent NPEs
            if (filterConfig == null) {
                filterConfig = new FilterConfig();
            }
            if (exceptionLogger == null) {
                exceptionLogger = new ExceptionLogger(filterConfig.getExceptionSettings());
            }
        }
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing NoConsoleSpam v{}", VERSION);
        
        try {
            // Make sure filterConfig is initialized
            if (filterConfig == null) {
                LOGGER.warn("FilterConfig was null, initializing now...");
                filterConfig = new FilterConfig();
            }
            
            // Make sure exceptionLogger is initialized
            if (exceptionLogger == null) {
                LOGGER.warn("ExceptionLogger was null, initializing now...");
                exceptionLogger = new ExceptionLogger(filterConfig.getExceptionSettings());
            }
            
            LOGGER.info("NoConsoleSpam is now filtering console spam");
            
            // Log details about exception handling configuration
            FilterConfig.ExceptionSettings exSettings = filterConfig.getExceptionSettings();
            if (exSettings.isCaptureExceptions()) {
                LOGGER.info("Exception logging is enabled with the following settings:");
                LOGGER.info("  - Organizing by source: {}", exSettings.isOrganizeBySource());
                LOGGER.info("  - Runtime exceptions: {}", exSettings.isCaptureRuntimeExceptions());
                LOGGER.info("  - IO exceptions: {}", exSettings.isCaptureIOExceptions());
                LOGGER.info("  - Fabric exceptions: {}", exSettings.isCaptureFabricExceptions());
                LOGGER.info("  - Minecraft exceptions: {}", exSettings.isCaptureMinecraftExceptions());
                
                // Log additional exception type settings
                LOGGER.info("  - Reflection exceptions: {}", exSettings.isCaptureReflectionExceptions());
                LOGGER.info("  - Security exceptions: {}", exSettings.isCaptureSecurityExceptions());
                LOGGER.info("  - Network exceptions: {}", exSettings.isCaptureNetworkExceptions());
                LOGGER.info("  - Data exceptions: {}", exSettings.isCaptureDataExceptions());
                LOGGER.info("  - JVM Errors: {}", exSettings.isCaptureErrors());
                LOGGER.info("  - Concurrent exceptions: {}", exSettings.isCaptureConcurrentExceptions());
                
                LOGGER.info("  - Max backup files: {}", exSettings.getMaxBackupIndex());
                LOGGER.info("Exception logs will be saved to: {}", "Console Errors/[source]/[timestamp]_[type].log");
            } else {
                LOGGER.info("Exception logging is disabled");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to initialize NoConsoleSpam", e);
        }
    }

    private void configureLogging() {
        try {
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            Configuration config = context.getConfiguration();
            
            // Add custom filter to root logger
            LoggerConfig rootLogger = config.getRootLogger();
            rootLogger.addFilter(new SpamFilter(
                    filterConfig.getSpamPatterns(), 
                    filterConfig.getIgnoredLoggers(), 
                    exceptionLogger));
            
            context.updateLoggers();
            LOGGER.info("Console spam filtering activated with {} patterns and {} ignored loggers",
                    filterConfig.getSpamPatterns().size(),
                    filterConfig.getIgnoredLoggers().size());
        } catch (Exception e) {
            LOGGER.error("Failed to configure logging", e);
        }
    }
} 
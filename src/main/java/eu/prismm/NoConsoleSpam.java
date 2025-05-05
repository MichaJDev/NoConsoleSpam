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
    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    
    private FilterConfig filterConfig;
    private ExceptionLogger exceptionLogger;
    
    @Override
    public void onPreLaunch() {
        LOGGER.info("Pre-initializing NoConsoleSpam");
        
        // Load configuration from JSON
        filterConfig = new FilterConfig();
        
        // Initialize exception logger
        exceptionLogger = new ExceptionLogger(filterConfig.getExceptionSettings());
        
        // Configure log4j filtering
        configureLogging();
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing NoConsoleSpam");
        LOGGER.info("NoConsoleSpam is now filtering console spam");
    }

    private void configureLogging() {
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
    }
} 
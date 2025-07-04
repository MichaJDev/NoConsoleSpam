package eu.prismm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class FilterConfig {
    private static final Logger LOGGER = LogManager.getLogger(NoConsoleSpam.MOD_ID);
    private static final String CONFIG_FILENAME = "spamfilters.json";
    private static final String CONFIG_DIRECTORY = "config/NoConsoleSpam";
    private static final String RESOURCE_CONFIG_PATH = "spamfilters.json";
    
    private Set<Pattern> spamPatterns = new HashSet<>();
    private Set<String> ignoredLoggers = new HashSet<>();
    private ExceptionSettings exceptionSettings = new ExceptionSettings();
    private File configFile;
    
    public FilterConfig() {
        ensureConfigDirectoryExists();
        loadConfig();
    }
    
    private void ensureConfigDirectoryExists() {
        File configDir = new File(CONFIG_DIRECTORY);
        if (!configDir.exists()) {
            if (configDir.mkdirs()) {
                LOGGER.info("Created config directory: {}", configDir.getAbsolutePath());
            } else {
                LOGGER.error("Failed to create config directory: {}", configDir.getAbsolutePath());
            }
        }
        
        configFile = new File(configDir, CONFIG_FILENAME);
        
        // Create default config file if it doesn't exist
        if (!configFile.exists()) {
            createDefaultConfigFile();
        }
    }
    
    private void createDefaultConfigFile() {
        try {
            // Copy the default config from resources
            InputStream defaultConfig = getClass().getClassLoader().getResourceAsStream(RESOURCE_CONFIG_PATH);
            if (defaultConfig != null) {
                Files.copy(defaultConfig, configFile.toPath());
                LOGGER.info("Created default config file at: {}", configFile.getAbsolutePath());
                defaultConfig.close();
            } else {
                // If resource file doesn't exist, create a minimal default config
                JsonObject defaultJson = createDefaultJsonConfig();
                try (FileWriter writer = new FileWriter(configFile)) {
                    new GsonBuilder().setPrettyPrinting().create().toJson(defaultJson, writer);
                }
                LOGGER.info("Created a new default config file at: {}", configFile.getAbsolutePath());
            }
        } catch (IOException e) {
            LOGGER.error("Failed to create default config file", e);
        }
    }
    
    private JsonObject createDefaultJsonConfig() {
        JsonObject config = new JsonObject();
        
        JsonArray spamPatternsArray = new JsonArray();
        spamPatternsArray.add(".*Lithium.*");
        spamPatternsArray.add(".*mismatch.*");
        spamPatternsArray.add(".*overloading.*");
        spamPatternsArray.add(".*moving.*wrongly.*");
        spamPatternsArray.add(".*Class Analysis Error.*");
        config.add("spamPatterns", spamPatternsArray);
        
        JsonArray ignoredLoggersArray = new JsonArray();
        ignoredLoggersArray.add("net.minecraft.class_5458");
        ignoredLoggersArray.add("net.minecraft.class_5459");
        ignoredLoggersArray.add("net.minecraft.class_5460");
        config.add("ignoredLoggers", ignoredLoggersArray);
        
        JsonObject exceptionSettingsObj = new JsonObject();
        exceptionSettingsObj.addProperty("captureExceptions", true);
        exceptionSettingsObj.addProperty("logFile", "logs/exceptions.log");
        exceptionSettingsObj.addProperty("maxLogSize", 10 * 1024 * 1024);
        exceptionSettingsObj.addProperty("maxBackupIndex", 3);
        exceptionSettingsObj.addProperty("organizeBySource", true);
        exceptionSettingsObj.addProperty("captureRuntimeExceptions", true);
        exceptionSettingsObj.addProperty("captureIOExceptions", true);
        exceptionSettingsObj.addProperty("captureFabricExceptions", true);
        exceptionSettingsObj.addProperty("captureMinecraftExceptions", true);
        
        // Add more specific exception type settings
        exceptionSettingsObj.addProperty("captureReflectionExceptions", true);
        exceptionSettingsObj.addProperty("captureSecurityExceptions", true);
        exceptionSettingsObj.addProperty("captureNetworkExceptions", true);
        exceptionSettingsObj.addProperty("captureDataExceptions", true);
        exceptionSettingsObj.addProperty("captureErrors", true);
        exceptionSettingsObj.addProperty("captureConcurrentExceptions", true);
        
        config.add("exceptionSettings", exceptionSettingsObj);
        
        return config;
    }
    
    public void loadConfig() {
        try {
            // First try to load from the config directory
            if (configFile.exists()) {
                try (Reader reader = new FileReader(configFile)) {
                    JsonObject config = new Gson().fromJson(reader, JsonObject.class);
                    loadPatternsFromJson(config);
                    loadLoggersFromJson(config);
                    loadExceptionSettingsFromJson(config);
                    LOGGER.info("Loaded config from: {}", configFile.getAbsolutePath());
                    return;
                } catch (Exception e) {
                    LOGGER.error("Error loading config from file: {}", configFile.getAbsolutePath(), e);
                }
            }
            
            // Fall back to the resource file if loading from config directory failed
            InputStream stream = getClass().getClassLoader().getResourceAsStream(RESOURCE_CONFIG_PATH);
            if (stream == null) {
                LOGGER.error("Could not find spam filter configuration file. Using default patterns.");
                loadDefaultPatterns();
                return;
            }
            
            try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                JsonObject config = new Gson().fromJson(reader, JsonObject.class);
                
                loadPatternsFromJson(config);
                loadLoggersFromJson(config);
                loadExceptionSettingsFromJson(config);
                
                LOGGER.info("Loaded {} spam patterns and {} ignored loggers from bundled configuration", 
                        spamPatterns.size(), ignoredLoggers.size());
            }
        } catch (IOException e) {
            LOGGER.error("Error loading spam filter configuration", e);
            loadDefaultPatterns();
        }
    }
    
    private void loadPatternsFromJson(JsonObject config) {
        spamPatterns.clear();
        if (config.has("spamPatterns")) {
            JsonArray patternsArray = config.getAsJsonArray("spamPatterns");
            patternsArray.forEach(element -> {
                String pattern = element.getAsString();
                try {
                    spamPatterns.add(Pattern.compile(pattern));
                    LOGGER.debug("Added spam pattern: {}", pattern);
                } catch (PatternSyntaxException e) {
                    LOGGER.error("Invalid pattern syntax: {}", pattern, e);
                }
            });
        }
    }
    
    private void loadLoggersFromJson(JsonObject config) {
        ignoredLoggers.clear();
        if (config.has("ignoredLoggers")) {
            JsonArray loggersArray = config.getAsJsonArray("ignoredLoggers");
            loggersArray.forEach(element -> {
                String logger = element.getAsString();
                ignoredLoggers.add(logger);
                LOGGER.debug("Added ignored logger: {}", logger);
            });
        }
    }
    
    private void loadExceptionSettingsFromJson(JsonObject config) {
        if (config.has("exceptionSettings")) {
            JsonObject settingsObj = config.getAsJsonObject("exceptionSettings");
            
            if (settingsObj.has("captureExceptions")) {
                exceptionSettings.setCaptureExceptions(settingsObj.get("captureExceptions").getAsBoolean());
            }
            
            if (settingsObj.has("logFile")) {
                exceptionSettings.setLogFile(settingsObj.get("logFile").getAsString());
            }
            
            if (settingsObj.has("maxLogSize")) {
                exceptionSettings.setMaxLogSize(settingsObj.get("maxLogSize").getAsLong());
            }
            
            if (settingsObj.has("maxBackupIndex")) {
                exceptionSettings.setMaxBackupIndex(settingsObj.get("maxBackupIndex").getAsInt());
            }
            
            // Load new exception settings
            if (settingsObj.has("organizeBySource")) {
                exceptionSettings.setOrganizeBySource(settingsObj.get("organizeBySource").getAsBoolean());
            }
            
            if (settingsObj.has("captureRuntimeExceptions")) {
                exceptionSettings.setCaptureRuntimeExceptions(settingsObj.get("captureRuntimeExceptions").getAsBoolean());
            }
            
            if (settingsObj.has("captureIOExceptions")) {
                exceptionSettings.setCaptureIOExceptions(settingsObj.get("captureIOExceptions").getAsBoolean());
            }
            
            if (settingsObj.has("captureFabricExceptions")) {
                exceptionSettings.setCaptureFabricExceptions(settingsObj.get("captureFabricExceptions").getAsBoolean());
            }
            
            if (settingsObj.has("captureMinecraftExceptions")) {
                exceptionSettings.setCaptureMinecraftExceptions(settingsObj.get("captureMinecraftExceptions").getAsBoolean());
            }
            
            // Load additional exception type settings
            if (settingsObj.has("captureReflectionExceptions")) {
                exceptionSettings.setCaptureReflectionExceptions(settingsObj.get("captureReflectionExceptions").getAsBoolean());
            }
            
            if (settingsObj.has("captureSecurityExceptions")) {
                exceptionSettings.setCaptureSecurityExceptions(settingsObj.get("captureSecurityExceptions").getAsBoolean());
            }
            
            if (settingsObj.has("captureNetworkExceptions")) {
                exceptionSettings.setCaptureNetworkExceptions(settingsObj.get("captureNetworkExceptions").getAsBoolean());
            }
            
            if (settingsObj.has("captureDataExceptions")) {
                exceptionSettings.setCaptureDataExceptions(settingsObj.get("captureDataExceptions").getAsBoolean());
            }
            
            if (settingsObj.has("captureErrors")) {
                exceptionSettings.setCaptureErrors(settingsObj.get("captureErrors").getAsBoolean());
            }
            
            if (settingsObj.has("captureConcurrentExceptions")) {
                exceptionSettings.setCaptureConcurrentExceptions(settingsObj.get("captureConcurrentExceptions").getAsBoolean());
            }
        }
    }
    
    private void loadDefaultPatterns() {
        spamPatterns.clear();
        ignoredLoggers.clear();
        
        // Default spam patterns
        spamPatterns.add(Pattern.compile(".*Lithium.*"));
        spamPatterns.add(Pattern.compile(".*mismatch.*"));
        spamPatterns.add(Pattern.compile(".*overloading.*"));
        spamPatterns.add(Pattern.compile(".*moving.*wrongly.*"));
        spamPatterns.add(Pattern.compile(".*Class Analysis Error.*"));
        
        // Default ignored loggers
        ignoredLoggers.add("net.minecraft.class_5458");
        ignoredLoggers.add("net.minecraft.class_5459");
        ignoredLoggers.add("net.minecraft.class_5460");
        
        LOGGER.info("Loaded default spam patterns and ignored loggers");
    }
    
    public Set<Pattern> getSpamPatterns() {
        return spamPatterns;
    }
    
    public Set<String> getIgnoredLoggers() {
        return ignoredLoggers;
    }
    
    public ExceptionSettings getExceptionSettings() {
        return exceptionSettings;
    }
    
    public static class ExceptionSettings {
        private boolean captureExceptions = true;
        private String logFile = "logs/exceptions.log";
        private long maxLogSize = 10 * 1024 * 1024; // 10MB
        private int maxBackupIndex = 3;
        private boolean organizeBySource = true;
        private boolean captureRuntimeExceptions = true;
        private boolean captureIOExceptions = true;
        private boolean captureFabricExceptions = true;
        private boolean captureMinecraftExceptions = true;
        
        // Additional exception type settings
        private boolean captureReflectionExceptions = true;
        private boolean captureSecurityExceptions = true;
        private boolean captureNetworkExceptions = true;
        private boolean captureDataExceptions = true;
        private boolean captureErrors = true;
        private boolean captureConcurrentExceptions = true;
        
        public boolean isCaptureExceptions() {
            return captureExceptions;
        }
        
        public void setCaptureExceptions(boolean captureExceptions) {
            this.captureExceptions = captureExceptions;
        }
        
        public String getLogFile() {
            return logFile;
        }
        
        public void setLogFile(String logFile) {
            this.logFile = logFile;
        }
        
        public long getMaxLogSize() {
            return maxLogSize;
        }
        
        public void setMaxLogSize(long maxLogSize) {
            this.maxLogSize = maxLogSize;
        }
        
        public int getMaxBackupIndex() {
            return maxBackupIndex;
        }
        
        public void setMaxBackupIndex(int maxBackupIndex) {
            this.maxBackupIndex = maxBackupIndex;
        }
        
        public boolean isOrganizeBySource() {
            return organizeBySource;
        }
        
        public void setOrganizeBySource(boolean organizeBySource) {
            this.organizeBySource = organizeBySource;
        }
        
        public boolean isCaptureRuntimeExceptions() {
            return captureRuntimeExceptions;
        }
        
        public void setCaptureRuntimeExceptions(boolean captureRuntimeExceptions) {
            this.captureRuntimeExceptions = captureRuntimeExceptions;
        }
        
        public boolean isCaptureIOExceptions() {
            return captureIOExceptions;
        }
        
        public void setCaptureIOExceptions(boolean captureIOExceptions) {
            this.captureIOExceptions = captureIOExceptions;
        }
        
        public boolean isCaptureFabricExceptions() {
            return captureFabricExceptions;
        }
        
        public void setCaptureFabricExceptions(boolean captureFabricExceptions) {
            this.captureFabricExceptions = captureFabricExceptions;
        }
        
        public boolean isCaptureMinecraftExceptions() {
            return captureMinecraftExceptions;
        }
        
        public void setCaptureMinecraftExceptions(boolean captureMinecraftExceptions) {
            this.captureMinecraftExceptions = captureMinecraftExceptions;
        }
        
        public boolean isCaptureReflectionExceptions() {
            return captureReflectionExceptions;
        }
        
        public void setCaptureReflectionExceptions(boolean captureReflectionExceptions) {
            this.captureReflectionExceptions = captureReflectionExceptions;
        }
        
        public boolean isCaptureSecurityExceptions() {
            return captureSecurityExceptions;
        }
        
        public void setCaptureSecurityExceptions(boolean captureSecurityExceptions) {
            this.captureSecurityExceptions = captureSecurityExceptions;
        }
        
        public boolean isCaptureNetworkExceptions() {
            return captureNetworkExceptions;
        }
        
        public void setCaptureNetworkExceptions(boolean captureNetworkExceptions) {
            this.captureNetworkExceptions = captureNetworkExceptions;
        }
        
        public boolean isCaptureDataExceptions() {
            return captureDataExceptions;
        }
        
        public void setCaptureDataExceptions(boolean captureDataExceptions) {
            this.captureDataExceptions = captureDataExceptions;
        }
        
        public boolean isCaptureErrors() {
            return captureErrors;
        }
        
        public void setCaptureErrors(boolean captureErrors) {
            this.captureErrors = captureErrors;
        }
        
        public boolean isCaptureConcurrentExceptions() {
            return captureConcurrentExceptions;
        }
        
        public void setCaptureConcurrentExceptions(boolean captureConcurrentExceptions) {
            this.captureConcurrentExceptions = captureConcurrentExceptions;
        }
    }
} 
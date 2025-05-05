package eu.prismm;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.core.time.Instant;
import org.apache.logging.log4j.core.time.MutableInstant;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.ReadOnlyStringMap;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class SpamFilter extends AbstractFilter {
    private final Set<Pattern> spamPatterns;
    private final Set<String> ignoredLoggers;
    private final ExceptionLogger exceptionLogger;
    
    public SpamFilter(Set<Pattern> spamPatterns, Set<String> ignoredLoggers, ExceptionLogger exceptionLogger) {
        this.spamPatterns = spamPatterns;
        this.ignoredLoggers = ignoredLoggers;
        this.exceptionLogger = exceptionLogger;
    }

    @Override
    public Result filter(LogEvent event) {
        if (event == null) {
            return Result.NEUTRAL;
        }

        // Check if the logger is in the ignored list
        if (ignoredLoggers.contains(event.getLoggerName())) {
            return Result.DENY;
        }

        // Get the message
        String message = event.getMessage().getFormattedMessage();

        // Check if this is an exception and we need to log it separately
        Throwable throwable = event.getThrown();
        if (throwable != null && exceptionLogger != null && exceptionLogger.isInitialized()) {
            exceptionLogger.logException(message, throwable);
            // We still want to filter the console output based on patterns
        }

        // Check if the message matches any spam patterns
        for (Pattern pattern : spamPatterns) {
            if (pattern.matcher(message).matches()) {
                return Result.DENY;
            }
        }

        return Result.NEUTRAL;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
        return filter(new SimpleLogEvent(logger.getName(), level, marker, msg, t));
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
        if (msg == null) {
            return Result.NEUTRAL;
        }
        Message message = new SimpleMessage(msg.toString());
        return filter(new SimpleLogEvent(logger.getName(), level, marker, message, t));
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
        if (msg == null) {
            return Result.NEUTRAL;
        }
        Message message = new SimpleMessage(msg);
        return filter(new SimpleLogEvent(logger.getName(), level, marker, message, null));
    }

    private static class SimpleMessage implements Message {
        private final String message;

        SimpleMessage(String message) {
            this.message = message;
        }

        @Override
        public String getFormattedMessage() {
            return message;
        }

        @Override
        public String getFormat() {
            return message;
        }

        @Override
        public Object[] getParameters() {
            return new Object[0];
        }

        @Override
        public Throwable getThrowable() {
            return null;
        }
    }

    private static class SimpleLogEvent implements LogEvent {
        private final String loggerName;
        private final Level level;
        private final Marker marker;
        private final Message message;
        private final Throwable throwable;
        private final MutableInstant instant = new MutableInstant();
        private boolean endOfBatch;

        SimpleLogEvent(String loggerName, Level level, Marker marker, Message message, Throwable throwable) {
            this.loggerName = loggerName;
            this.level = level;
            this.marker = marker;
            this.message = message;
            this.throwable = throwable;
            this.instant.initFromEpochMilli(System.currentTimeMillis(), 0);
        }

        SimpleLogEvent(String loggerName, Level level, Marker marker, Message message) {
            this(loggerName, level, marker, message, null);
        }

        @Override
        public Message getMessage() {
            return message;
        }

        @Override
        public String getLoggerName() {
            return loggerName;
        }

        @Override
        public Level getLevel() {
            return level;
        }

        @Override
        public Marker getMarker() {
            return marker;
        }

        @Override
        public String getThreadName() {
            return Thread.currentThread().getName();
        }

        @Override
        public long getTimeMillis() {
            return instant.getEpochMillisecond();
        }

        @Override
        public Instant getInstant() {
            return instant;
        }

        @Override
        public StackTraceElement getSource() {
            return null;
        }

        @Override
        public String getLoggerFqcn() {
            return null;
        }

        @Override
        public ThreadContext.ContextStack getContextStack() {
            return ThreadContext.getImmutableStack();
        }

        @Override
        public Map<String, String> getContextMap() {
            return ThreadContext.getImmutableContext();
        }

        @Override
        public ReadOnlyStringMap getContextData() {
            return null;
        }

        @Override
        public Throwable getThrown() {
            return throwable;
        }

        @Override
        public org.apache.logging.log4j.core.impl.ThrowableProxy getThrownProxy() {
            return throwable != null ? new org.apache.logging.log4j.core.impl.ThrowableProxy(throwable) : null;
        }

        @Override
        public boolean isEndOfBatch() {
            return endOfBatch;
        }

        @Override
        public void setEndOfBatch(boolean endOfBatch) {
            this.endOfBatch = endOfBatch;
        }

        @Override
        public boolean isIncludeLocation() {
            return false;
        }

        @Override
        public void setIncludeLocation(boolean locationRequired) {
            // No-op
        }

        @Override
        public long getNanoTime() {
            return 0;
        }

        @Override
        public int getThreadPriority() {
            return Thread.currentThread().getPriority();
        }

        @Override
        public long getThreadId() {
            return Thread.currentThread().getId();
        }

        @Override
        public LogEvent toImmutable() {
            return this;
        }
    }
} 
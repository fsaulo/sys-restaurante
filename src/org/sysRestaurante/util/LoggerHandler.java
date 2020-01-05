package org.sysRestaurante.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LoggerHandler extends Handler {

    public LoggerHandler() {
        LogManager.getLogManager().reset();
    }

    @Override
    public void publish(LogRecord logRecord) {}

    @Override
    public void flush() {}

    @Override
    public void close() throws SecurityException {}

    public static Logger getGenericConsoleHandler(String referenceToClass) {
        LoggerFormatter format = new LoggerFormatter();
        ConsoleHandler console = new ConsoleHandler();
        
        console.setLevel(Level.ALL);
        console.setFormatter(format);

        Logger loggerObject;
        loggerObject = Logger.getLogger(referenceToClass);
        loggerObject.addHandler(console);

        return loggerObject;
    }
}

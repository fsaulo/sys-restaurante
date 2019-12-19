package util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LoggerHandler {

    public LoggerHandler() {
        LogManager.getLogManager().reset();
    }

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

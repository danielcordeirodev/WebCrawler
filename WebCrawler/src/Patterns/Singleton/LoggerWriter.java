/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Patterns.Singleton;

import java.io.*;
import java.util.Date;

/**
 *
 * @author BRKsCosta
 */
public final class LoggerWriter {

    private static final String LOGGERFILE = "logger.txt";
    private PrintStream printStream;
    private static LoggerWriter instance = new LoggerWriter();

    private LoggerWriter() {
        connect();
    }

    public static LoggerWriter getInstance() {

        return instance;
    }

    public boolean connect() {
        if (printStream == null) {
            try {
                printStream = new PrintStream(new FileOutputStream(LOGGERFILE), true);
            } catch (FileNotFoundException ex) {
                printStream = null;
                return false;

            }
            return true;
        }
        return true;
    }

    public void writeToLog(String str) throws LoggerException {
        if (printStream == null) {
            throw new LoggerException("Connection fail");
        }

        printStream.println(new Date().toString() + "  " + str);

    }

    @Override
    public String toString() {
        return "Logger{" + "printStream=" + printStream + '}';
    }

}

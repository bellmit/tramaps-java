/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util.logging;

import ch.geomo.tramaps.map.displacement.alg.EdgeAdjuster;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public enum Loggers {

    /* util class */;

    private static final Map<Class, Logger> cache = new HashMap<>();

    static {
        // currently not working standalone -> log file won't be found when running standalone
        System.setProperty("java.util.logging.config.file", "./src/main/resources/logging.properties");
        try {
            LogManager.getLogManager().readConfiguration();
        }
        catch (IOException e) {
            Logger.getGlobal().severe("Cannot read Logger configuration!");
        }
    }

    @NotNull
    public static Logger get(@NotNull Object obj) {
        return getLogger(obj);
    }

    public static void flag(@NotNull Object obj, @NotNull String message) {
        info(obj, message, '*');
    }

    public static void flag(@NotNull Object obj, @NotNull Object messageObj) {
        info(obj, "Obj: " + messageObj.toString(), ' ');
    }

    public static void separator(@NotNull Object obj) {
        info(obj, "==================================================================================================");
    }

    public static void debug(@NotNull Object obj, @NotNull String message) {
        get(obj).finest("    " + message);
    }

    public static void info(@NotNull Object obj, @NotNull String message, char markCharacter) {
        if (obj instanceof EdgeAdjuster) {
            return;
        }
        get(obj).info(" " + markCharacter + "  " + message);
    }

    public static void info(@NotNull Object obj, @NotNull Object messageObj) {
        info(obj, "Obj: " + messageObj.toString(), 'i');
    }

    public static void info(@NotNull Object obj, @NotNull String message) {
        info(obj, message, 'i');
    }

    public static void warning(@NotNull Object obj, @NotNull String message) {
        get(obj).warning(" !  " + message);
    }

    public static void error(@NotNull Object obj, @NotNull String message) {
        get(obj).severe(" E  " + message);
    }

    @NotNull
    public static Logger get(@NotNull Class objClass) {
        return getLogger(objClass);
    }

    @NotNull
    public static Logger getLogger(@NotNull Object obj) {
        return get(obj.getClass());
    }

    @NotNull
    public static Logger getLogger(@NotNull Class objClass) {
        Logger logger = cache.get(objClass);
        if (logger == null) {
            logger = Logger.getLogger(objClass.getSimpleName());
            cache.put(objClass, logger);
        }
        return logger;
    }

}

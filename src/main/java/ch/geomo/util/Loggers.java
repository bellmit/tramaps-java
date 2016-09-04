/*
 * Copyright (c) 2016 Thomas Zuberbuehler. All rights reserved.
 */

package ch.geomo.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Provides {@link Logger} instances.
 */
public final class Loggers {

    private static Loggers instance;

    private Map<Class, Logger> cache;

    private Loggers() {
        cache = new HashMap<>();
        // currently not working standalone -> log file won't be found when running standalone
        System.setProperty("java.util.logging.config.file", "./src/main/resources/logging.properties");
        try {
            LogManager.getLogManager().readConfiguration();
        }
        catch (IOException e) {
            Logger.getGlobal().severe("Cannot read Logger configuration!");
        }
    }

    public static Loggers getInstance() {
        if (instance == null) {
            instance = new Loggers();
        }
        return instance;
    }

    @NotNull
    public static Logger get(@NotNull Object obj) {
        return getInstance().getLogger(obj);
    }

    public static void info(@NotNull Object obj, @NotNull String message) {
        get(obj).info(message);
    }

    public static void warning(@NotNull Object obj, @NotNull String message) {
        get(obj).warning(message);
    }

    public static void severe(@NotNull Object obj, @NotNull String message) {
        get(obj).severe(message);
    }

    @NotNull
    public static Logger get(@NotNull Class objClass) {
        return getInstance().getLogger(objClass);
    }

    @NotNull
    public Logger getLogger(@NotNull Object obj) {
        return get(obj.getClass());
    }

    @NotNull
    public Logger getLogger(@NotNull Class objClass) {
        Logger logger = cache.get(objClass);
        if (logger == null) {
            logger = Logger.getLogger(objClass.getSimpleName());
            cache.put(objClass, logger);
        }
        return logger;
    }

}

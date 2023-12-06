package me.snorochevskiy.spi.demo.main;

import me.snorochevskiy.spi.demo.WriterPlugin;

import java.util.Arrays;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Writer class that relies on plugins to perform the actual writing.
 */
public class ExtendableWritingApp {

    private final Class configClass;

    public ExtendableWritingApp(Class configurationClass) {
        configClass = configurationClass;
    }

    public void write(String str) {
        // Getting all the SPI plugins for WriterPlugin interface
        ServiceLoader<WriterPlugin> serviceLoader = ServiceLoader.load(WriterPlugin.class);
        Iterator<WriterPlugin> plugins = serviceLoader.iterator();

        // Iterating plugins and executing only those that are registered
        // by putting corresponding activation annotation of a config.class
        while (plugins.hasNext()) {
            WriterPlugin plugin = plugins.next();

            if (isPluginEnabled(plugin)) {
                plugin.write(str);
            }
        }
    }

    private boolean isPluginEnabled(WriterPlugin plugin) {
        // Iteration through config. class annotation in search for an annotation
        // defined as an activation annotation by the plugin.
        return Arrays.stream(configClass.getAnnotations())
                .anyMatch(a -> a.annotationType().getName().equals(plugin.activatingAnnotation().getName()));
    }

}

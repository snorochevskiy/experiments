package me.snorochevskiy.spi.demo;

/**
 * This interface defines extension that can be created by third-part developer and used as a plugin
 * in the main application.
 *
 * The main application just simply writes a string using a set of writer implementations.
 * And each implementation should be defined by implementing this interfaces and
 * creating a JAR file according to SPI requirements.
 */
public interface WriterPlugin {

    /**
     * Returns a class of annotation that is used as switcher to turn on given writer plugin.
     * Annotation should be available in runtime, i.e. should be defined with
     * <>@Retention(RetentionPolicy.RUNTIME)</>
     *
     * @return
     */
    Class activatingAnnotation();

    /**
     * The method that should perform an actual writing.
     * @param str
     */
    void write(String str);
}

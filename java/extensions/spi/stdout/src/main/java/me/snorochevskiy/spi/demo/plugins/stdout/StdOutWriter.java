package me.snorochevskiy.spi.demo.plugins.stdout;

import me.snorochevskiy.spi.demo.WriterPlugin;

public class StdOutWriter implements WriterPlugin {
    @Override
    public Class activatingAnnotation() {
        return EnableStdOutWriter.class;
    }

    @Override
    public void write(String str) {
        System.out.println(str);
    }
}

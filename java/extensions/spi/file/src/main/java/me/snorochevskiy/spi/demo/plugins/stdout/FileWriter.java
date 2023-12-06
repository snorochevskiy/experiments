package me.snorochevskiy.spi.demo.plugins.stdout;

import me.snorochevskiy.spi.demo.WriterPlugin;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class FileWriter implements WriterPlugin {
    @Override
    public Class activatingAnnotation() {
        return EnableFileWriter.class;
    }

    @Override
    public void write(String str) {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream("out.txt"))) {
            pw.write(str);
            pw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

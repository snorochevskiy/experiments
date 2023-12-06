package me.snorochevskiy.spi.demo.main;

import me.snorochevskiy.spi.demo.plugins.stdout.EnableFileWriter;
import me.snorochevskiy.spi.demo.plugins.stdout.EnableStdOutWriter;

@EnableStdOutWriter // Enables StdOutWriter plugin
@EnableFileWriter   // Enables FileWriter plugin
public class Main {

    public static void main(String[] args) {
        ExtendableWritingApp app = new ExtendableWritingApp(Main.class);
        app.write("Hello SPI");
    }

}

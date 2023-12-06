package me.snorochevskiy;

import me.snorochevskiy.io.*;
import me.snorochevskiy.io.IoRuntime;


public class App {
    public static void main( String[] args ) {
        var rt = new IoRuntime();

        Io<String> io = Io.pure("123")
                .map(String::length)
                .shift()
                .flatMap((Integer i) -> Io.delay(() -> "Text " + i));

        var result = rt.execSync(io);

        System.out.println("Result: " + result);
    }
}

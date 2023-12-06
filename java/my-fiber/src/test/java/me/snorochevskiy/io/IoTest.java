package me.snorochevskiy.io;

import me.snorochevskiy.io.file.FileUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class IoTest {

    private IoRuntime rt = new IoRuntime();

    @Test
    public void testPure() {
        Io<Integer> io = Io.pure(2).flatMap(a -> Io.pure(3).map(b -> a + b));
        Assert.assertEquals(Integer.valueOf(5), rt.execSync(io));
    }

    @Test
    public void testSimpleComposition() {
        Io<String> io = Io.pure("123")
                .map(String::length)
                .flatMap((Integer i) -> Io.delay(() -> "Text " + i));

        Assert.assertEquals("Text 3", rt.execSync(io));
    }

    @Test
    public void testBlocking() {
        Io<String> io = Io.blocking(() -> {
            var res = getClass().getClassLoader().getResource("test_text.txt");
            try {
                return new String(Files.readAllBytes(Paths.get(res.toURI())));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Assert.assertEquals("123_abc", rt.execSync(io));
    }

    @Test
    public void testAsyncFileRead() {
        Io<String> io = Io.pure("test_text.txt")
                .map(name -> getClass().getClassLoader().getResource(name).getPath())
                .flatMap(path -> Io.fromFuture(FileUtil.readFile(path)))
                .map(bytes -> new String(bytes));

        Assert.assertEquals("123_abc", rt.execSync(io));
    }

    @Test
    public void testSimpleCompositionWithRawBlocks() {
        Io<String> a = new Io.Pure<String>("123");
        Io<Integer> b = new Io.IoMap<>(a, String::length);
        Io<String> c = new Io.FlatMap<>(b, (Integer i) -> new Io.Delayed<>(() ->"Text " + i));

        Assert.assertEquals("Text 3", rt.execSync(c));
    }

    @Test
    public void testStackSafe() {
        Io<Integer> io = Io.pure(0);
        for (var i = 0; i < 1_000_000; i++) {
            io = io.flatMap(a -> Io.pure(a + 2));
        }
        Assert.assertEquals(2000000, rt.execSync(io).intValue());
    }
}

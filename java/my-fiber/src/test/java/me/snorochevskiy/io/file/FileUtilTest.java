package me.snorochevskiy.io.file;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class FileUtilTest {
    @Test
    public void testAsyncFileRead() throws ExecutionException, InterruptedException {
        var resource = getClass().getClassLoader().getResource("test_text.txt");
        CompletableFuture<byte[]> bytes = FileUtil.readFile(resource.getPath());
        var str = new String(bytes.get());
        System.out.println(str);
    }
}

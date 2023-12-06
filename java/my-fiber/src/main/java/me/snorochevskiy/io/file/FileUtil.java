package me.snorochevskiy.io.file;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class FileUtil {

    private static final int READ_CHUNK_SIZE = 4094;
    static class Recursive<I> {
        public I func;
    }

    static public CompletableFuture<byte[]> readFile(String file) {
        try {
            var resultFuture = new CompletableFuture<byte[]>();
            var path = Paths.get(file);
            AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);
            ByteBuffer buffer = ByteBuffer.allocate(READ_CHUNK_SIZE);

            List<byte[]> content = new ArrayList<>();
            Recursive<Consumer<Long>> recursive = new Recursive<>();
            recursive.func = (Long start) -> {
                fileChannel.read(
                        buffer, start, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                            @Override
                            public void completed(Integer result, ByteBuffer attachment) {
                                if (result.intValue() == READ_CHUNK_SIZE) {
                                    content.add(attachment.array());
                                    recursive.func.accept(start + result);
                                } else {
                                    try {
                                        fileChannel.close();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    var totalSize = content.stream()
                                            .mapToInt(a -> a.length)
                                            .sum();
                                    var completeResult = new byte[totalSize + result];
                                    int offset = 0;
                                    for (byte[] arr : content) {
                                        System.arraycopy(arr, 0, completeResult, offset, READ_CHUNK_SIZE);
                                        offset += arr.length;
                                    }
                                    System.arraycopy(attachment.array(), 0, completeResult, offset, result);
                                    resultFuture.complete(completeResult);
                                }
                            }
                            @Override
                            public void failed(Throwable exc, ByteBuffer attachment) {
                                // ignore for simplicity
                            }
                        });
            };
            recursive.func.accept(0L);
            return resultFuture;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}

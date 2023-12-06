package me.snorochevskiy.io;

import java.util.Stack;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class IoFiber<A> {

    private Io<A> initIo;

    private IoRuntime rt;

    private CompletableFuture<A> cf = new CompletableFuture<>();

    private Io current;

    private Stack<Func> transformations = new Stack<>();


    public IoFiber(Io<A> io, IoRuntime rt) {
        initIo = io;
        current = initIo;
        this.rt = rt;
    }

    public Result<A> exec() {
        return (Result<A>) unwrap();
    }

    public CompletableFuture<A> getCF() {
        return cf;
    }

    private Result<Object> unwrap() {
        lbl: do {
            switch (current) {
                case Io.Pure<?> x -> {
                    current = applyFuncs(transformations, x.value());
                }
                case Io.Delayed<?> x -> {
                    current = applyFuncs(transformations, x.supplier().get());
                }
                case Io.IoMap x -> {
                    transformations.push(new MapFunc(x.transformation()));
                    current = x.value();
                }
                case Io.FlatMap x -> {
                    transformations.push(new FlatMapFunc(x.transformation()));
                    current = x.value();
                }
                case Io.Blocking<?> x -> {
                    rt.runBlocking(
                            () -> current = Io.pure(x.supplier().get()),
                            this
                    );
                    return new Suspended<>();
                }
                case Io.IoFuture<?> x -> {
                    var cf = x.сf()
                            .thenAccept(v -> current = Io.pure(v) );
                    rt.waitCompletableFuture(cf, this);
                    return new Suspended<>();
                }
                case Io.Shift<?> x -> {
                    current = x.value();
                    return new NeedShift<>();
                }
            }
        } while (!transformations.isEmpty() || !(current instanceof Io.Pure<?>));
        return new Done<>(((Io.Pure<?>)current).value());
    }

    private Io applyFuncs(Stack<Func> stack, Object value) {
        Object current = value;
        while (!stack.isEmpty()) {
            switch (stack.pop()) {
                case FlatMapFunc(Function<Object, Io<Object>> f) -> {
                    return f.apply(current);
                }
                case MapFunc(Function<Object, Object> f) -> {
                    current = f.apply(current);
                }
            }
        }
        return Io.pure(current);
    }

    sealed interface Func {}
    record MapFunc(Function<Object, Object> f) implements Func {}
    record FlatMapFunc(Function<Object, Io<Object>> f) implements Func {}

    sealed interface Result<R> {}
    record Done<R>(R value) implements Result<R> {}
    record Suspended<R>() implements Result<R> {}
    record NeedShift<R>() implements Result<R> {}
}


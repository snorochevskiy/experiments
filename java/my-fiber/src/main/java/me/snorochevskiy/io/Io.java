package me.snorochevskiy.io;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public sealed interface Io<A> {

    default <B> Io<B> map(Function<A, B> transformation) {
        return new IoMap<B,A>(this, transformation);
    }

    default <B> Io<B> flatMap(Function<A, Io<B>> transformation) {
        return new FlatMap<B,A>(this, transformation);
    }

    default Io<A> shift() {
        return new Shift<>(this);
    }

    static <T> Pure<T> pure(T v) {
        return new Pure<>(v);
    }

    static <T> Delayed<T> delay(Supplier<T> supplier) {
        return new Delayed<>(supplier);
    }

    static <T> Blocking<T> blocking(Supplier<T> supplier) {
        return new Blocking<>(supplier);
    }

    static <T> IoFuture<T> fromFuture(CompletableFuture<T> cf) {
        return new IoFuture<>(cf);
    }

    record Pure<X>(X value) implements Io<X> {}
    record Delayed<X>(Supplier<X> supplier) implements Io<X> {}
    record Blocking<X>(Supplier<X> supplier) implements Io<X> {}
    record Shift<X>(Io<X> value) implements Io<X> {}
    record IoFuture<X>(CompletableFuture<X> сf) implements Io<X> {}
    record IoMap<Y,X>(Io<X> value, Function<X, Y> transformation) implements Io<Y> {}
    record FlatMap<Y,X>(Io<X> value, Function<X, Io<Y>> transformation) implements Io<Y> {}
}


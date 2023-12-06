# Toy green-thread runtime

This is a toy project that represents a minimalistic green-thread runtime.

It operates on fiber that consist of following types of blocks:

* `Io` - an abstract interface that represents a block of a fiber
* `Pure` - fiber block that just wraps a value, doesn't perform any computation, but serves for composability with other fiber block types
* `Delay` - wraps a computation (Runnable)
* `Blocking` - wraps a blocking computation that runs on a dedicated thread pool
* `IoFuture` - wraps CompletableFuture
* `IoMap` - a lazy transformation of a nested value with a function
* `FlatMap` - a lazy transformation of a nested value with a function that returns Io

The fiber (a code sequence that does something) is built as a composition of blocks mentioned above using ma and flatMap methods.

E.g. simple fiber that just calculates a number `5`;

```java
Io<Integer> io = Io.pure(5);
```

A fiber that calculates length of a string:

```java
Io<Integer> io = Io.pure("Hello world").map(String::length);
```

To calculate a result of a fiber, it should be executed on the `IoRuntime`.

```java
IoRuntime rt = new IoRuntime();

Io<Integer> io = Io.pure("abc").map(String::length);
int = rt.execSync(io); // 3
```

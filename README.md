# Static Gson

This library makes Gson faster by generationg TypeAapterFactory with annotation processing.

## Benchmark

See `MainActivity.java` for details.

On Xperia Z4 / Android 5.0.2:

```
D/XXX: start benchmarking Dynamic Gson
D/XXX: Dynamic Gson in serialization: 365ms
D/XXX: Dynamic Gson in deserialization: 287ms

D/XXX: start benchmarking Static Gson
D/XXX: Static Gson in serialization: 149ms
D/XXX: Static Gson in deserialization: 174ms

D/XXX: start benchmarking Moshi
D/XXX: Moshi in serialization: 214ms
D/XXX: Moshi in deserialization: 516ms

D/XXX: start benchmarking LoganSquare
D/XXX: LoganSquare in serialization: 82ms
D/XXX: LoganSquare in deserialization: 243ms
```
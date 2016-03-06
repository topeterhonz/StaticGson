# Static Gson

This library makes Gson faster by generationg TypeAapterFactory with annotation processing.

## Benchmark

See `MainActivity.java` for details.

On Xperia Z4 / Android 5.0.2:

```
D/XXX : start benchmarking dynamic gson
D/XXX : dynamic gson in serialization: 318ms
D/XXX : dynamic gson in deserialization: 298ms

D/XXX : start benchmarking static gson
D/XXX : static gson in serialization: 141ms
D/XXX : static gson in deserialization: 177ms

D/XXX : start benchmarking moshi
D/XXX : moshi in serialization: 204ms
D/XXX : moshi in deserialization: 449ms
```
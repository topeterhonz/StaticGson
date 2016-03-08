# Static Gson [![Circle CI](https://circleci.com/gh/gfx/StaticGson.svg?style=svg)](https://circleci.com/gh/gfx/StaticGson) [ ![Download](https://api.bintray.com/packages/gfx/maven/static-gson/images/download.svg) ](https://bintray.com/gfx/maven/static-gson/)

This library makes Gson faster by generationg TypeAapterFactory with annotation processing.

## Gradle Dependencies

```gradle
dependencies {
    apt 'com.github.gfx.static_gson:static-gson-processor:v0.9.0'
    compile 'com.github.gfx.static_gson:static-gson:v0.9.0'
}
```

## Usage

Add `@JsonSerializable` to JSON serializable models:

```java
@JsonSerializable(fieldNamingPolicy = FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
public class User {
    public String firstName; // serialized to "first_name"
    public Stirng lastName; // serialized to "last_name"
}
```

Then, give `StaticGsonTypeAdapterFactory.newInstance()` to `GsonBuilder`:

```java
Gson gson = new GsonBuilder()
        .registerTypeAdapterFactory(StaticGsonTypeAdapterFactory.newInstance())
        .create();
```

That's all. `Gson#toJson()` and `Gson#fromGson()` becomes faster
for `@JsonSerializable` classes.

## Benchmark

See `example/MainActivity.java` for details.

On Xperia Z4 / Android 5.0.2:

```
$ adb logcat -v tag | ag D/XXX

D/XXX     : start benchmarking Dynamic Gson
D/XXX     : Dynamic Gson in serialization: 449ms
D/XXX     : Dynamic Gson in deserialization: 387ms
D/XXX     : start benchmarking Static Gson
D/XXX     : Static Gson in serialization: 198ms
D/XXX     : Static Gson in deserialization: 233ms
D/XXX     : start benchmarking Moshi
D/XXX     : Moshi in serialization: 270ms
D/XXX     : Moshi in deserialization: 656ms
D/XXX     : start benchmarking LoganSquare
D/XXX     : LoganSquare in serialization: 111ms
D/XXX     : LoganSquare in deserialization: 268ms
```


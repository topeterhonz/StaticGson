# Json deserializing rules

Allow Json to Model deserializing to fail more gracefully

When a Json object of the following types is expected but other types are encountered. This is simply parsed as a null.
1. String
2. Object
3. List
4. Map

E.g. When a String is expected `{"string": "someString"}` but a json object `{"name": {}}` is encountered.

For the primitive types. i.e. int, bool, float etc, They are parsed to their default value.

## Annotations

* @JsonStrict object must be parsed to its correct type. This must not fail gracefully
* @JsonStrict works for for primitive (e.g. int) and boxed primitive (e.g. Integer) types as well
* @NonNull means the value must exist in the Json and the field should not be simply left as null.
* @NonNull implies @JsonStrict
* @JsonMustSet is similar to @NonNull but applied to primitive types only. It means the value must exist in the Json and the field should not be simply left with its uninitialized default value.
* @NonNull should not be used on boxed primitive types. Consider use unbox type instead with @JsonMustSet
* All objects without @JsonStrict or @NonNull and primitive types without @JsonMustSet annotations are by default parsed gracefully

All graceful failures must be logged.

Graceful failure should result in an exception in dev for faster feedback loop.

## Parsing model with child object

When failure occurred on a strict child object
* If parent is strict. Fail the parsing.
* If parent is graceful. Just let the child field fail gracefully without failing the parent.
* A _List_ itself may be strict or graceful but its _items_ are all parsed gracefully. If some particular items fail, the rest of the items continue to work.





package com.github.gfx.static_gson

import com.github.gfx.static_gson.annotation.JsonSerializable
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Before
import org.junit.Test

class KotlinGracefulFailureTest {

    lateinit var gson: Gson

    @Before
    fun setUp() {
        gson = GsonBuilder()
                .registerTypeAdapterFactory(StaticGsonTypeAdapterFactory.newInstance())
                .registerTypeAdapterFactory(CollectionTypeAdapterFactory())
                .registerTypeAdapterFactory(ArrayTypeAdapter.FACTORY)
                .serializeNulls()
                .create()
    }


    @Test
    fun deserializeNullableString() {
        // incorrect type
        jsonString("value" to JSONObject())
                .toModel<NullableStringModel>()
                .value
                .assert(nullValue())

        // correct type
        jsonString("value" to "string")
                .toModel<NullableStringModel>()
                .value
                .assert(equalTo("string"))

        // null
        jsonString("value" to null)
                .toModel<NullableStringModel>()
                .value
                .assert(nullValue())

        // not set
        jsonString()
                .toModel<NullableStringModel>()
                .value
                .assert(nullValue())
    }

    @JsonSerializable
    data class NullableStringModel(val value: String?)

    @Test
    fun deserializeString() {

        assertThat(throwsException { fromJson<StringModel>(jsonString("value" to JSONObject())) },
                instanceOf<JsonGracefulException>())

        assertThat(throwsException { fromJson<StringModel>(jsonString("value" to null)) },
                instanceOf<JsonGracefulException>())

        assertThat(throwsException { fromJson<StringModel>(jsonString()) },
                instanceOf<JsonGracefulException>())

        assertThat<String>(fromJson<StringModel>(jsonString("value" to "string")).value, equalTo("string"))
    }

    @JsonSerializable
    data class StringModel(var value: String)

    @Test
    fun deserializeMixedString() {

        val correct = "string"
        val incorrect = JSONObject()

        val correctMatcher = equalTo("string")

        // correct
        jsonString("nullable" to correct,
                "nonNull" to correct)
                .toModel<MixedStringModel>()
                .also {
                    it.nullable.assert(correctMatcher)
                    it.nonNull.assert(correctMatcher)
                }

        // incorrect type
        jsonString("nullable" to incorrect,
                "nonNull" to correct)
                .toModel<MixedStringModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }
        // null
        jsonString("nullable" to null,
                "nonNull" to correct)
                .toModel<MixedStringModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }

        // not set
        jsonString("nonNull" to correct)
                .toModel<MixedStringModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }

        Logger.setDelegate(null)
    }

    @JsonSerializable
    data class MixedStringModel(val nullable: String?, var nonNull: String)


    @Test
    fun deserializeNullableObject() {

        // incorrect type
        assertThat<StringModel>(fromJson<NullableObjectModel>(jsonString("value" to "string")).value, nullValue())
        // correct type
        assertThat<StringModel>(fromJson<NullableObjectModel>(jsonString("value" to json("value" to "string"))).value, equalTo(StringModel("string")))
        // null
        assertThat<StringModel>(fromJson<NullableObjectModel>(jsonString("value" to null)).value, nullValue())
        // not set
        assertThat<StringModel>(fromJson<NullableObjectModel>(jsonString()).value, nullValue())
    }

    @JsonSerializable
    data class NullableObjectModel(var value: StringModel?)

    @Test
    fun deserializeObject() {

        // incorrect type
        assertThat(throwsException { fromJson<ObjectModel>(jsonString("value" to "string")) },
                instanceOf<JsonGracefulException>())

        // null
        assertThat(throwsException { fromJson<ObjectModel>(jsonString("value" to null)) },
                instanceOf<JsonGracefulException>())

        // not set
        assertThat(throwsException { fromJson<ObjectModel>(jsonString()) },
                instanceOf<JsonGracefulException>())

        // correct type
        assertThat<StringModel>(fromJson<ObjectModel>(jsonString("value" to json("value" to "string"))).value, equalTo(StringModel("string")))
    }

    @JsonSerializable
    data class ObjectModel(var value: StringModel)


    @Test
    fun deserializeMixedObject() {

        val correct = json("value" to "string")
        val incorrect = "string"

        val correctMatcher = equalTo(StringModel("string"))

        // correct
        jsonString("nullable" to correct,
                "nonNull" to correct)
                .toModel<MixedObjectModel>()
                .also {
                    it.nullable.assert(correctMatcher)
                    it.nonNull.assert(correctMatcher)
                }

        // incorrect type
        jsonString("nullable" to incorrect,
                "nonNull" to correct)
                .toModel<MixedObjectModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }
        // null
        jsonString("nullable" to null,
                "nonNull" to correct)
                .toModel<MixedObjectModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }

        // not set
        jsonString("nonNull" to correct)
                .toModel<MixedObjectModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }

    }

    @JsonSerializable
    data class MixedObjectModel(val nullable: StringModel?, var nonNull: StringModel)


    @Test
    fun deserializeNullableList() {

        // incorrect type
        assertThat<List<StringModel>>(fromJson<NullableListModel>(jsonString("value" to "string")).value, nullValue())

        // null
        assertThat<List<StringModel>>(fromJson<NullableListModel>(jsonString("value" to null)).value, nullValue())

        // not set
        assertThat<List<StringModel>>(fromJson<NullableListModel>(jsonString()).value, nullValue())

        // correct type
        assertThat<List<StringModel>>(fromJson<NullableListModel>(jsonString("value" to jsonArray(json("value" to "string")))).value, equalTo(listOf<StringModel>(StringModel("string"))))
    }

    @JsonSerializable
    data class NullableListModel(var value: List<StringModel>?)

    @Test
    fun deserializeList() {

        // incorrect type
        assertThat(throwsException { fromJson<ListModel>(jsonString("value" to "string")) },
                instanceOf<JsonGracefulException>())

        // null
        assertThat(throwsException { fromJson<ListModel>(jsonString("value" to null)) },
                instanceOf<JsonGracefulException>())

        // not set
        assertThat(throwsException { fromJson<ListModel>(jsonString()) },
                instanceOf<JsonGracefulException>())

        // correct type
        assertThat<List<StringModel>>(fromJson<ListModel>(jsonString("value" to jsonArray(json("value" to "string")))).value, equalTo(listOf<StringModel>(StringModel("string"))))
    }

    @JsonSerializable
    data class ListModel(var value: List<StringModel>)

    @Test
    fun deserializeMixedList() {
        // correct type

        val correct = jsonArray(json("value" to "string"))
        val incorrect = "string"

        val correctMatcher = equalTo(listOf<StringModel>(StringModel("string")))

        // correct
        jsonString("nullable" to correct,
                "nonNull" to correct)
                .toModel<MixedListModel>()
                .also {
                    it.nullable.assert(correctMatcher)
                    it.nonNull.assert(correctMatcher)
                }

        // incorrect type
        jsonString("nullable" to incorrect,
                "nonNull" to correct)
                .toModel<MixedListModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }
        // null
        jsonString("nullable" to null,
                "nonNull" to correct)
                .toModel<MixedListModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }

        // not set
        jsonString("nonNull" to correct)
                .toModel<MixedListModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }
    }

    @JsonSerializable
    data class MixedListModel(val nullable: List<StringModel>?, var nonNull: List<StringModel>)


    @Test
    fun deserializeNullableMap() {

        // incorrect type
        assertThat<Map<String, String>>(fromJson<NullableMapModel>(jsonString("value" to "string")).value, nullValue())

        // null
        assertThat<Map<String, String>>(fromJson<NullableMapModel>(jsonString("value" to null)).value, nullValue())

        // not set
        assertThat<Map<String, String>>(fromJson<NullableMapModel>(jsonString()).value, nullValue())

        // correct type
        assertThat<Map<String, String>>(fromJson<NullableMapModel>(jsonString("value" to json("value" to "string"))).value, equalTo(mapOf("value" to "string")))
    }

    @JsonSerializable
    data class NullableMapModel(var value: Map<String, String>?)


    @Test
    @Throws(Exception::class)
    fun deserializeMap() {

        // incorrect type
        assertThat(throwsException { fromJson<MapModel>(jsonString("value" to "string")) },
                instanceOf<JsonGracefulException>())

        // null
        assertThat(throwsException { fromJson<MapModel>(jsonString("value" to null)) },
                instanceOf<JsonGracefulException>())

        // not set
        assertThat(throwsException { fromJson<MapModel>(jsonString()) },
                instanceOf<JsonGracefulException>())

        // correct type
        assertThat<Map<String, String>>(fromJson<MapModel>(jsonString("value" to json("value" to "string"))).value, equalTo(mapOf("value" to "string")))
    }

    @JsonSerializable
    data class MapModel(var value: Map<String, String>)


    @Test
    fun deserializeMixedMap() {
        // correct type

        val correct = json("value" to "string")
        val incorrect = "string"

        val correctMatcher = equalTo(mapOf("value" to "string"))

        // correct
        jsonString("nullable" to correct,
                "nonNull" to correct)
                .toModel<MixedMapModel>()
                .also {
                    it.nullable.assert(correctMatcher)
                    it.nonNull.assert(correctMatcher)
                }

        // incorrect type
        jsonString("nullable" to incorrect,
                "nonNull" to correct)
                .toModel<MixedMapModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }
        // null
        jsonString("nullable" to null,
                "nonNull" to correct)
                .toModel<MixedMapModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }

        // not set
        jsonString("nonNull" to correct)
                .toModel<MixedMapModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }
    }

    @JsonSerializable
    data class MixedMapModel(val nullable: Map<String, String>?, var nonNull: Map<String, String>)


    @Test
    fun deserializeNullableInt() {
        // incorrect type
        assertThat<Int>(fromJson<NullableIntModel>(jsonString("value" to "string")).value, nullValue())
        assertThat<Int>(fromJson<NullableIntModel>(jsonString("value" to json())).value, nullValue())

        // null
        assertThat<Int>(fromJson<NullableIntModel>(jsonString("value" to null)).value, nullValue())

        // not set
        assertThat<Int>(fromJson<NullableIntModel>(jsonString()).value, nullValue())

        // correct type
        assertThat<Int>(fromJson<NullableIntModel>(jsonString("value" to 1)).value, equalTo(1))
    }

    @JsonSerializable
    data class NullableIntModel(val value: Int?)


    @Test
    fun deserializeInt() {
        // incorrect type
        assertThat(throwsException { fromJson<IntModel>(jsonString("value" to "string")) },
                instanceOf<JsonGracefulException>())

        // incorrect type
        assertThat(throwsException { fromJson<IntModel>(jsonString("value" to json())) },
                instanceOf<JsonGracefulException>())

        // null
        assertThat(throwsException { fromJson<IntModel>(jsonString("value" to null)) },
                instanceOf<JsonGracefulException>())

        // not set
        assertThat(throwsException { fromJson<IntModel>(jsonString()) },
                instanceOf<JsonGracefulException>())

        // correct type
        assertThat<Int>(fromJson<IntModel>(jsonString("value" to 1)).value, equalTo(1))
    }

    @JsonSerializable
    data class IntModel(val value: Int)

    @Test
    fun deserializeMixedInt() {

        val correct = 1
        val incorrectObject = JSONObject()
        val incorrectString = "string"

        val correctMatcher = equalTo(1)

        // correct
        jsonString("nullable" to correct,
                "nonNull" to correct)
                .toModel<MixedIntModel>()
                .also {
                    it.nullable.assert(correctMatcher)
                    it.nonNull.assert(correctMatcher)
                }

        // incorrect type
        jsonString("nullable" to incorrectObject,
                "nonNull" to correct)
                .toModel<MixedIntModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }
        // incorrect type
        jsonString("nullable" to incorrectString,
                "nonNull" to correct)
                .toModel<MixedIntModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }
        // null
        jsonString("nullable" to null,
                "nonNull" to correct)
                .toModel<MixedIntModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }

        // not set
        jsonString("nonNull" to correct)
                .toModel<MixedIntModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }
    }


    @JsonSerializable
    data class MixedIntModel(val nullable: Int?, var nonNull: Int?)


    @Test
    fun deserializeNullableLong() {
        // incorrect type
        assertThat<Long>(fromJson<NullableLongModel>(jsonString("value" to "string")).value, nullValue())
        assertThat<Long>(fromJson<NullableLongModel>(jsonString("value" to json())).value, nullValue())

        // null
        assertThat<Long>(fromJson<NullableLongModel>(jsonString("value" to null)).value, nullValue())

        // not set
        assertThat<Long>(fromJson<NullableLongModel>(jsonString()).value, nullValue())

        // correct type
        assertThat<Long>(fromJson<NullableLongModel>(jsonString("value" to 1L)).value, equalTo(1L))
    }

    @JsonSerializable
    data class NullableLongModel(val value: Long?)


    @Test
    fun deserializeLong() {
        // incorrect type
        assertThat(throwsException { fromJson<LongModel>(jsonString("value" to "string")) },
                instanceOf<JsonGracefulException>())

        // incorrect type
        assertThat(throwsException { fromJson<LongModel>(jsonString("value" to json())) },
                instanceOf<JsonGracefulException>())

        // null
        assertThat(throwsException { fromJson<LongModel>(jsonString("value" to null)) },
                instanceOf<JsonGracefulException>())

        // not set
        assertThat(throwsException { fromJson<LongModel>(jsonString()) },
                instanceOf<JsonGracefulException>())

        // correct type
        assertThat<Long>(fromJson<LongModel>(jsonString("value" to 1L)).value, equalTo(1L))
    }

    @JsonSerializable
    data class LongModel(val value: Long)


    @Test
    fun deserializeMixedLong() {

        val correct = 1L
        val incorrectObject = JSONObject()
        val incorrectString = "string"

        val correctMatcher = equalTo(1L)

        // correct
        jsonString("nullable" to correct,
                "nonNull" to correct)
                .toModel<MixedLongModel>()
                .also {
                    it.nullable.assert(correctMatcher)
                    it.nonNull.assert(correctMatcher)
                }

        // incorrect type
        jsonString("nullable" to incorrectObject,
                "nonNull" to correct)
                .toModel<MixedLongModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }
        // incorrect type
        jsonString("nullable" to incorrectString,
                "nonNull" to correct)
                .toModel<MixedLongModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }
        // null
        jsonString("nullable" to null,
                "nonNull" to correct)
                .toModel<MixedLongModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }

        // not set
        jsonString("nonNull" to correct)
                .toModel<MixedLongModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }
    }

    @JsonSerializable
    data class MixedLongModel(val nullable: Long?, var nonNull: Long?)


    @Test
    fun deserializeNullableFloat() {
        // incorrect type
        assertThat<Float>(fromJson<NullableFloatModel>(jsonString("value" to "string")).value, nullValue())
        assertThat<Float>(fromJson<NullableFloatModel>(jsonString("value" to json())).value, nullValue())

        // null
        assertThat<Float>(fromJson<NullableFloatModel>(jsonString("value" to null)).value, nullValue())

        // not set
        assertThat<Float>(fromJson<NullableFloatModel>(jsonString()).value, nullValue())

        // correct type
        assertThat<Float>(fromJson<NullableFloatModel>(jsonString("value" to 1F)).value, equalTo(1F))
    }

    @JsonSerializable
    data class NullableFloatModel(val value: Float?)


    @Test
    fun deserializeFloat() {
        // incorrect type
        assertThat(throwsException { fromJson<FloatModel>(jsonString("value" to "string")) },
                instanceOf<JsonGracefulException>())

        // incorrect type
        assertThat(throwsException { fromJson<FloatModel>(jsonString("value" to json())) },
                instanceOf<JsonGracefulException>())

        // null
        assertThat(throwsException { fromJson<FloatModel>(jsonString("value" to null)) },
                instanceOf<JsonGracefulException>())

        // not set
        assertThat(throwsException { fromJson<FloatModel>(jsonString()) },
                instanceOf<JsonGracefulException>())

        // correct type
        assertThat<Float>(fromJson<FloatModel>(jsonString("value" to 1F)).value, equalTo(1F))
    }

    @JsonSerializable
    data class FloatModel(val value: Float)


    @Test
    fun deserializeMixedFloat() {

        val correct = 1f
        val incorrectObject = JSONObject()
        val incorrectString = "string"

        val correctMatcher = equalTo(1f)

        // correct
        jsonString("nullable" to correct,
                "nonNull" to correct)
                .toModel<MixedFloatModel>()
                .also {
                    it.nullable.assert(correctMatcher)
                    it.nonNull.assert(correctMatcher)
                }

        // incorrect type
        jsonString("nullable" to incorrectObject,
                "nonNull" to correct)
                .toModel<MixedFloatModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }
        // incorrect type
        jsonString("nullable" to incorrectString,
                "nonNull" to correct)
                .toModel<MixedFloatModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }
        // null
        jsonString("nullable" to null,
                "nonNull" to correct)
                .toModel<MixedFloatModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }

        // not set
        jsonString("nonNull" to correct)
                .toModel<MixedFloatModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }
    }

    @JsonSerializable
    data class MixedFloatModel(val nullable: Float?, var nonNull: Float?)

    @Test
    fun deserializeNullableDouble() {
        // incorrect type
        assertThat<Double>(fromJson<NullableDoubleModel>(jsonString("value" to "string")).value, nullValue())
        assertThat<Double>(fromJson<NullableDoubleModel>(jsonString("value" to json())).value, nullValue())

        // null
        assertThat<Double>(fromJson<NullableDoubleModel>(jsonString("value" to null)).value, nullValue())

        // not set
        assertThat<Double>(fromJson<NullableDoubleModel>(jsonString()).value, nullValue())

        // correct type
        assertThat<Double>(fromJson<NullableDoubleModel>(jsonString("value" to 1.0)).value, equalTo(1.0))
    }

    @JsonSerializable
    data class NullableDoubleModel(val value: Double?)


    @Test
    fun deserializeDouble() {
        // incorrect type
        assertThat(throwsException { fromJson<DoubleModel>(jsonString("value" to "string")) },
                instanceOf<JsonGracefulException>())

        // incorrect type
        assertThat(throwsException { fromJson<DoubleModel>(jsonString("value" to json())) },
                instanceOf<JsonGracefulException>())

        // null
        assertThat(throwsException { fromJson<DoubleModel>(jsonString("value" to null)) },
                instanceOf<JsonGracefulException>())

        // not set
        assertThat(throwsException { fromJson<DoubleModel>(jsonString()) },
                instanceOf<JsonGracefulException>())

        // correct type
        assertThat<Double>(fromJson<DoubleModel>(jsonString("value" to 1.0)).value, equalTo(1.0))
    }

    @JsonSerializable
    data class DoubleModel(val value: Double)


    @Test
    fun deserializeMixedDouble() {

        val correct = 1.0
        val incorrectObject = JSONObject()
        val incorrectString = "string"

        val correctMatcher = equalTo(1.0)

        // correct
        jsonString("nullable" to correct,
                "nonNull" to correct)
                .toModel<MixedDoubleModel>()
                .also {
                    it.nullable.assert(correctMatcher)
                    it.nonNull.assert(correctMatcher)
                }

        // incorrect type
        jsonString("nullable" to incorrectObject,
                "nonNull" to correct)
                .toModel<MixedDoubleModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }
        // incorrect type
        jsonString("nullable" to incorrectString,
                "nonNull" to correct)
                .toModel<MixedDoubleModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }
        // null
        jsonString("nullable" to null,
                "nonNull" to correct)
                .toModel<MixedDoubleModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }

        // not set
        jsonString("nonNull" to correct)
                .toModel<MixedDoubleModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }
    }

    @JsonSerializable
    data class MixedDoubleModel(val nullable: Double?, var nonNull: Double?)


    @Test
    fun deserializeNullableBoolean() {
        // incorrect type
        assertThat<Boolean>(fromJson<NullableBooleanModel>(jsonString("value" to "string")).value, nullValue())
        assertThat<Boolean>(fromJson<NullableBooleanModel>(jsonString("value" to json())).value, nullValue())
        assertThat<Boolean>(fromJson<NullableBooleanModel>(jsonString("value" to 3)).value, nullValue())

        // null
        assertThat<Boolean>(fromJson<NullableBooleanModel>(jsonString("value" to null)).value, nullValue())

        // not set
        assertThat<Boolean>(fromJson<NullableBooleanModel>(jsonString()).value, nullValue())

        // correct type
        assertThat<Boolean>(fromJson<NullableBooleanModel>(jsonString("value" to true)).value, equalTo(true))
        assertThat<Boolean>(fromJson<NullableBooleanModel>(jsonString("value" to false)).value, equalTo(false))

        // flexible types
        assertThat<Boolean>(fromJson<NullableBooleanModel>(jsonString("value" to "true")).value, equalTo(true))
        assertThat<Boolean>(fromJson<NullableBooleanModel>(jsonString("value" to "false")).value, equalTo(false))

        assertThat<Boolean>(fromJson<NullableBooleanModel>(jsonString("value" to "True")).value, equalTo(true))
        assertThat<Boolean>(fromJson<NullableBooleanModel>(jsonString("value" to "False")).value, equalTo(false))

        assertThat<Boolean>(fromJson<NullableBooleanModel>(jsonString("value" to 1)).value, equalTo(true))
        assertThat<Boolean>(fromJson<NullableBooleanModel>(jsonString("value" to 0)).value, equalTo(false))
    }

    @JsonSerializable
    data class NullableBooleanModel(val value: Boolean?)


    @Test
    fun deserializeBoolean() {
        // incorrect type
        assertThat(throwsException { fromJson<BooleanModel>(jsonString("value" to "string")) },
                instanceOf<JsonGracefulException>())

        // incorrect type
        assertThat(throwsException { fromJson<BooleanModel>(jsonString("value" to json())) },
                instanceOf<JsonGracefulException>())

        // incorrect type
        assertThat(throwsException { fromJson<BooleanModel>(jsonString("value" to 3)) },
                instanceOf<JsonGracefulException>())

        // null
        assertThat(throwsException { fromJson<BooleanModel>(jsonString("value" to null)) },
                instanceOf<JsonGracefulException>())

        // not set
        assertThat(throwsException { fromJson<BooleanModel>(jsonString()) },
                instanceOf<JsonGracefulException>())

        // correct type
        assertThat<Boolean>(fromJson<BooleanModel>(jsonString("value" to true)).value, equalTo(true))
        assertThat<Boolean>(fromJson<BooleanModel>(jsonString("value" to false)).value, equalTo(false))

        // flexible types
        assertThat<Boolean>(fromJson<BooleanModel>(jsonString("value" to "true")).value, equalTo(true))
        assertThat<Boolean>(fromJson<BooleanModel>(jsonString("value" to "false")).value, equalTo(false))
        assertThat<Boolean>(fromJson<BooleanModel>(jsonString("value" to "True")).value, equalTo(true))
        assertThat<Boolean>(fromJson<BooleanModel>(jsonString("value" to "False")).value, equalTo(false))

        assertThat<Boolean>(fromJson<BooleanModel>(jsonString("value" to 1)).value, equalTo(true))
        assertThat<Boolean>(fromJson<BooleanModel>(jsonString("value" to 0)).value, equalTo(false))
    }

    @JsonSerializable
    data class BooleanModel(val value: Boolean)


    @Test
    fun deserializeMixedBoolean() {

        val correct = true
        val incorrectObject = JSONObject()
        val incorrectString = "string"

        val correctMatcher = equalTo(true)

        // correct
        jsonString("nullable" to correct,
                "nonNull" to correct)
                .toModel<MixedBooleanModel>()
                .also {
                    it.nullable.assert(correctMatcher)
                    it.nonNull.assert(correctMatcher)
                }

        // incorrect type
        jsonString("nullable" to incorrectObject,
                "nonNull" to correct)
                .toModel<MixedBooleanModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }
        // incorrect type
        jsonString("nullable" to incorrectString,
                "nonNull" to correct)
                .toModel<MixedBooleanModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }
        // null
        jsonString("nullable" to null,
                "nonNull" to correct)
                .toModel<MixedBooleanModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }

        // not set
        jsonString("nonNull" to correct)
                .toModel<MixedBooleanModel>()
                .also {
                    it.nullable.assert(nullValue())
                    it.nonNull.assert(correctMatcher)
                }
    }

    @JsonSerializable
    data class MixedBooleanModel(
            val nullable: Boolean?,
            var nonNull: Boolean?)

    @Test
    fun deserializeNested() {

        val json = jsonString("parent" to json("child" to null))

        // Strict parent with strict child.
        // Parsing should fail
        assertThat(throwsException { fromJson<StrictParentStrictChild>(json) },
                instanceOf<JsonGracefulException>())

        // Strict parent with graceful child
        // Child should fail gracefully but leaving parent continue to work

        val result2 = fromJson<StrictParentGracefulChild>(json)
        assertThat(result2, notNullValue())
        assertThat<StrictParentGracefulChild.Parent>(result2.parent, notNullValue())
        assertThat<String>(result2.parent.child, nullValue())

        // Graceful parent with strict child
        // The failure of child should result in the parent failing gracefully
        val result3 = fromJson<GracefulParentStrictChild>(json)
        assertThat(result3, notNullValue())
        assertThat<GracefulParentStrictChild.Parent>(result3.parent, nullValue())

        // Graceful parent with graceful child
        // Child should fail gracefully but leaving parent continue to work
        val result4 = fromJson<GracefulParentGracefulChild>(json)
        assertThat(result4, notNullValue())
        assertThat<GracefulParentGracefulChild.Parent>(result4.parent, notNullValue())
        assertThat<String>(result4.parent!!.child, nullValue())
    }

    @JsonSerializable
    class StrictParentStrictChild(val parent: Parent) {
        @JsonSerializable
        class Parent(val child: String)
    }

    @JsonSerializable
    class StrictParentGracefulChild(val parent: Parent) {
        @JsonSerializable
        class Parent(val child: String?)
    }

    @JsonSerializable
    class GracefulParentStrictChild(val parent: Parent?) {
        @JsonSerializable
        class Parent(val child: String)
    }

    @JsonSerializable
    class GracefulParentGracefulChild(val parent: Parent?) {
        @JsonSerializable
        class Parent(val child: String?)
    }


    @Test
    fun deserializePartialObjectList() {
        // second item is bad
        val json = jsonString(
                "list" to jsonArray(
                        json("value2" to json("value" to 1), "value" to 1),
                        json("value2" to json("value" to "bad"), "value" to 2),
                        json("value2" to json("value" to 3), "value" to 3)
                )
        )

        val result = fromJson<PartialObjectList>(json)

        // gracefully skips the second item
        assertThat(result.list.size, equalTo(2))
        assertThat(result.list[0].value, equalTo(1))
        assertThat(result.list[0].value2.value, equalTo(1))
        assertThat(result.list[1].value, equalTo(3))
        assertThat(result.list[1].value2.value, equalTo(3))
    }

    @JsonSerializable
    class PartialObjectList(val list: List<ListItem>) {
        @JsonSerializable
        class ListItem(val value: Int, val value2: IntModel)
    }

    @Test
    fun deserializeFieldWithDeclaredDefault() {
        val json = jsonString(
                "intValue" to 2,
                "stringValue" to "bar",
                "boolValue" to true,
                "listValue" to jsonArray(2)
        )

        val result = fromJson<FieldWithDeclaredDefault>(json)
        assertThat(result.intValue, equalTo(2))
        assertThat(result.stringValue, equalTo("bar"))
        assertThat(result.boolValue, equalTo(true))
        assertThat(result.listValue, equalTo(listOf(2)))
        assertThat(result.defaultIntValue, equalTo(2))
        assertThat(result.defaultStringValue, equalTo("bar"))
        assertThat(result.defaultBoolValue, equalTo(true))
        assertThat(result.listValue, equalTo(listOf(2)))
    }

    @JsonSerializable
    data class FieldWithDeclaredDefault(
            val intValue: Int = 1,
            val stringValue: String = "foo",
            val boolValue: Boolean = false,
            var listValue: List<Int> = listOf(2),
            val defaultIntValue: Int = 2,
            val defaultStringValue: String = "bar",
            val defaultBoolValue: Boolean = true,
            val defaultListValue: List<Int> = listOf(2)

    )

    /*
    This allow asserting multiple exceptions within a test
 */
    private inline fun throwsException(call: () -> Any): Exception? {
        var e: Exception? = null
        try {
            call()
        } catch (ex: Exception) {
            e = ex
        }
        return e
    }


    inline fun <reified T : Any?> T.assert(matcher: Matcher<T>, reason: String? = null) = assertThat(reason
            ?: "", this, matcher)

    inline fun <reified T : Any> fromJson(json: String) = gson.fromJson<T>(json, T::class.java)

    inline fun <reified T : Any> instanceOf() = CoreMatchers.instanceOf<Any>(T::class.java)

    inline fun <reified T : Any> String.toModel() = fromJson<T>(this)

    fun jsonArray(vararg objects: Any) = JSONArray(listOf(*objects))

    fun json(vararg properties: Pair<String, Any?>) = JSONObject(mapOf(*properties))

    fun jsonString(vararg properties: Pair<String, Any?>) = json(*properties).toString()


}

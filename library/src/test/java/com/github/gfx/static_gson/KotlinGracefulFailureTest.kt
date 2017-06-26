package com.github.gfx.static_gson

import com.github.gfx.static_gson.annotation.JsonSerializable
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
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
        assertThat<String>(fromJson<NullableStringModel>(jsonString("value" to JSONObject())).value, nullValue())
        // correct type
        assertThat<String>(fromJson<NullableStringModel>(jsonString("value" to "string")).value, equalTo("string"))
        // null
        assertThat<String>(fromJson<NullableStringModel>(jsonString("value" to null)).value, nullValue())
        // not set
        assertThat<String>(fromJson<NullableStringModel>(jsonString()).value, nullValue())
    }

    @JsonSerializable
    data class NullableStringModel(val value: String?)

    @Test
    fun deserializeString() {
        assertThat(throwsException<JsonSyntaxException> { fromJson<StringModel>(jsonString("value" to JSONObject())) },
                instanceOf<JsonSyntaxException>())

        assertThat(throwsException<JsonGracefulException> { fromJson<StringModel>(jsonString("value" to null)) },
                instanceOf<JsonGracefulException>())

        assertThat(throwsException<JsonGracefulException> { fromJson<StringModel>(jsonString()) },
                instanceOf<JsonGracefulException>())

        assertThat<String>(fromJson<StringModel>(jsonString("value" to "string")).value, equalTo("string"))
    }

    @JsonSerializable
    data class StringModel(var value: String)


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
        assertThat(throwsException<JsonSyntaxException> { fromJson<ObjectModel>(jsonString("value" to "string")) },
                instanceOf<JsonSyntaxException>())

        // null
        assertThat(throwsException<JsonGracefulException> { fromJson<ObjectModel>(jsonString("value" to null)) },
                instanceOf<JsonGracefulException>())

        // not set
        assertThat(throwsException<JsonGracefulException> { fromJson<ObjectModel>(jsonString()) },
                instanceOf<JsonGracefulException>())

        // correct type
        assertThat<StringModel>(fromJson<ObjectModel>(jsonString("value" to json("value" to "string"))).value, equalTo(StringModel("string")))
    }

    @JsonSerializable
    data class ObjectModel(var value: StringModel)


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
    @Throws(Exception::class)
    fun deserializeList() {

        // incorrect type
        assertThat(throwsException<JsonSyntaxException> { fromJson<ListModel>(jsonString("value" to "string")) },
                instanceOf<JsonSyntaxException>())

        // null
        assertThat(throwsException<JsonGracefulException> { fromJson<ListModel>(jsonString("value" to null)) },
                instanceOf<JsonGracefulException>())

        // not set
        assertThat(throwsException<JsonGracefulException> { fromJson<ListModel>(jsonString()) },
                instanceOf<JsonGracefulException>())

        // correct type
        assertThat<List<StringModel>>(fromJson<ListModel>(jsonString("value" to jsonArray(json("value" to "string")))).value, equalTo(listOf<StringModel>(StringModel("string"))))
    }

    @JsonSerializable
    data class ListModel(var value: List<StringModel>)

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
        assertThat(throwsException<JsonSyntaxException> { fromJson<MapModel>(jsonString("value" to "string")) },
                instanceOf<JsonSyntaxException>())

        // null
        assertThat(throwsException<JsonGracefulException> { fromJson<MapModel>(jsonString("value" to null)) },
                instanceOf<JsonGracefulException>())

        // not set
        assertThat(throwsException<JsonGracefulException> { fromJson<MapModel>(jsonString()) },
                instanceOf<JsonGracefulException>())

        // correct type
        assertThat<Map<String, String>>(fromJson<MapModel>(jsonString("value" to json("value" to "string"))).value, equalTo(mapOf("value" to "string")))
    }

    @JsonSerializable
    data class MapModel(var value: Map<String, String>)


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
        assertThat(throwsException<NumberFormatException> { fromJson<IntModel>(jsonString("value" to "string")) },
                instanceOf<NumberFormatException>())

        // incorrect type
        assertThat(throwsException<JsonSyntaxException> { fromJson<IntModel>(jsonString("value" to json())) },
                instanceOf<JsonSyntaxException>())

        // null
        assertThat(throwsException<JsonGracefulException> { fromJson<IntModel>(jsonString("value" to null)) },
                instanceOf<JsonGracefulException>())

        // not set
        assertThat(throwsException<JsonGracefulException> { fromJson<IntModel>(jsonString()) },
                instanceOf<JsonGracefulException>())

        // correct type
        assertThat<Int>(fromJson<IntModel>(jsonString("value" to 1)).value, equalTo(1))
    }

    @JsonSerializable
    data class IntModel(val value: Int)


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
        assertThat(throwsException<NumberFormatException> { fromJson<LongModel>(jsonString("value" to "string")) },
                instanceOf<NumberFormatException>())

        // incorrect type
        assertThat(throwsException<JsonSyntaxException> { fromJson<LongModel>(jsonString("value" to json())) },
                instanceOf<JsonSyntaxException>())

        // null
        assertThat(throwsException<JsonGracefulException> { fromJson<LongModel>(jsonString("value" to null)) },
                instanceOf<JsonGracefulException>())

        // not set
        assertThat(throwsException<JsonGracefulException> { fromJson<LongModel>(jsonString()) },
                instanceOf<JsonGracefulException>())

        // correct type
        assertThat<Long>(fromJson<LongModel>(jsonString("value" to 1L)).value, equalTo(1L))
    }

    @JsonSerializable
    data class LongModel(val value: Long)


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
        assertThat(throwsException<NumberFormatException> { fromJson<FloatModel>(jsonString("value" to "string")) },
                instanceOf<NumberFormatException>())

        // incorrect type
        assertThat(throwsException<JsonSyntaxException> { fromJson<FloatModel>(jsonString("value" to json())) },
                instanceOf<JsonSyntaxException>())

        // null
        assertThat(throwsException<JsonGracefulException> { fromJson<FloatModel>(jsonString("value" to null)) },
                instanceOf<JsonGracefulException>())

        // not set
        assertThat(throwsException<JsonGracefulException> { fromJson<FloatModel>(jsonString()) },
                instanceOf<JsonGracefulException>())

        // correct type
        assertThat<Float>(fromJson<FloatModel>(jsonString("value" to 1F)).value, equalTo(1F))
    }

    @JsonSerializable
    data class FloatModel(val value: Float)


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
        assertThat(throwsException<NumberFormatException> { fromJson<DoubleModel>(jsonString("value" to "string")) },
                instanceOf<NumberFormatException>())

        // incorrect type
        assertThat(throwsException<JsonSyntaxException> { fromJson<DoubleModel>(jsonString("value" to json())) },
                instanceOf<JsonSyntaxException>())

        // null
        assertThat(throwsException<JsonGracefulException> { fromJson<DoubleModel>(jsonString("value" to null)) },
                instanceOf<JsonGracefulException>())

        // not set
        assertThat(throwsException<JsonGracefulException> { fromJson<DoubleModel>(jsonString()) },
                instanceOf<JsonGracefulException>())

        // correct type
        assertThat<Double>(fromJson<DoubleModel>(jsonString("value" to 1.0)).value, equalTo(1.0))
    }

    @JsonSerializable
    data class DoubleModel(val value: Double)


    @Test
    fun deserializeNullableBoolean() {
        // incorrect type
        assertThat<Boolean>(fromJson<NullableBooleanModel>(jsonString("value" to "string")).value, nullValue())
        assertThat<Boolean>(fromJson<NullableBooleanModel>(jsonString("value" to json())).value, nullValue())

        // null
        assertThat<Boolean>(fromJson<NullableBooleanModel>(jsonString("value" to null)).value, nullValue())

        // not set
        assertThat<Boolean>(fromJson<NullableBooleanModel>(jsonString()).value, nullValue())

        // correct type
        assertThat<Boolean>(fromJson<NullableBooleanModel>(jsonString("value" to true)).value, equalTo(true))
    }

    @JsonSerializable
    data class NullableBooleanModel(val value: Boolean?)


    @Test
    fun deserializeBoolean() {
        // incorrect type
        assertThat(throwsException<JsonSyntaxException> { fromJson<BooleanModel>(jsonString("value" to "string")) },
                instanceOf<JsonSyntaxException>())

        // incorrect type
        assertThat(throwsException<JsonSyntaxException> { fromJson<BooleanModel>(jsonString("value" to json())) },
                instanceOf<JsonSyntaxException>())

        // null
        assertThat(throwsException<JsonGracefulException> { fromJson<BooleanModel>(jsonString("value" to null)) },
                instanceOf<JsonGracefulException>())

        // not set
        assertThat(throwsException<JsonGracefulException> { fromJson<BooleanModel>(jsonString()) },
                instanceOf<JsonGracefulException>())

        // correct type
        assertThat<Boolean>(fromJson<BooleanModel>(jsonString("value" to true)).value, equalTo(true))
    }

    @JsonSerializable
    data class BooleanModel(val value: Boolean)

    @Test
    fun deserializeNested() {

        val json = jsonString("parent" to json("child" to null))

        // Strict parent with strict child.
        // Parsing should fail
        assertThat(throwsException<JsonGracefulException> { fromJson<StrictParentStrictChild>(json) },
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
    internal class GracefulParentGracefulChild(val parent: Parent?) {
        @JsonSerializable
        class Parent(val child: String?)
    }


    @Test
    fun deserializePartialList() {
        // second item is bad
        val json = jsonString(
                "list" to jsonArray(
                        json("value" to 1),
                        json("value" to "bad"),
                        json("value" to 3)
                )
        )

        val result = fromJson<PartialList>(json)

        // gracefully skips the second item
        assertThat(result.list.size, equalTo(2))
        assertThat(result.list[0].value, equalTo(1))
        assertThat(result.list[1].value, equalTo(3))
    }

    @JsonSerializable
    class PartialList(val list: List<ListItem>) {
        @JsonSerializable
        class ListItem(val value: Int)
    }

    /*
        This allow asserting multiple exceptions within a test
     */
    private inline fun <reified T : Exception> throwsException(call: () -> Unit): T? {
        var e: T? = null
        try {
            call()
        } catch (ex: Exception) {
            e = ex as? T
        }
        return e
    }


    inline fun <reified T : Any> fromJson(json: String)
            = gson.fromJson<T>(json, T::class.java)

    inline fun <reified T : Exception> instanceOf()
            = CoreMatchers.instanceOf<T>(T::class.java)


    fun jsonArray(vararg objects: Any)
            = JSONArray(listOf(*objects))

    fun json(vararg properties: Pair<String, Any?>)
            = JSONObject(mapOf(*properties))

    fun jsonString(vararg properties: Pair<String, Any?>)
            = json(*properties).toString()


}

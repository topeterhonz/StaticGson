package com.github.gfx.static_gson;

import android.support.annotation.NonNull;

import com.github.gfx.static_gson.annotation.JsonMustSet;
import com.github.gfx.static_gson.annotation.JsonSerializable;
import com.github.gfx.static_gson.annotation.JsonStrict;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class GracefulFailureTest {

    Gson gson;

    @Before
    public void setUp() throws Exception {
        gson = new GsonBuilder()
                .registerTypeAdapterFactory(StaticGsonTypeAdapterFactory.newInstance())
                .registerTypeAdapterFactory(new CollectionTypeAdapterFactory())
                .registerTypeAdapterFactory(ArrayTypeAdapter.FACTORY)
                .serializeNulls()
                .create();
    }

    @Test
    public void deserializeString() throws Exception {
        // Expected string but provided object
        String json = new JSONObject()
                .put("bad", new JSONObject())
                .put("good", "string")
                .toString();

        StringModel result = gson.fromJson(json, StringModel.class);
        assertThat(result.bad, nullValue());
        assertThat(result.good, is("string"));
    }

    @JsonSerializable
    public static class StringModel {
        public String bad;
        public String good;
    }

    @Test
    public void deserializeStrictString() throws Exception {
        // Expected string but provided object
        String json = new JSONObject()
                .put("bad", new JSONObject())
                .toString();

        Exception ex = throwsException(() -> gson.fromJson(json, StrictStringModel.class));
        assertThat(ex, instanceOf(JsonGracefulException.class));

        // test that strict can be null
        StrictStringModel strictNullResult = gson.fromJson(new JSONObject().toString(), StrictStringModel.class);
        assertThat(strictNullResult.nullable, nullValue());
    }

    @JsonSerializable
    public static class StrictStringModel {
        @JsonStrict
        public String bad;
        @JsonStrict
        public String nullable;
    }

    @Test
    public void deserializeNonNullString() throws Exception {
        // Expected string but provided object
        String json = new JSONObject()
                .put("bad", new JSONObject())
                .toString();

        Exception ex1 = throwsException(() -> gson.fromJson(json, NonNullStringModel.class));
        assertThat(ex1, instanceOf(JsonGracefulException.class));

        // test that non-null cannot be null
        Exception ex2 = throwsException(() -> gson.fromJson(new JSONObject().toString(), NonNullStringModel.class));
        assertThat(ex2, instanceOf(JsonGracefulException.class));
    }

    @JsonSerializable
    public static class NonNullStringModel {
        @NonNull
        public String bad;
        @NonNull
        public String fail;
    }

    @Test
    public void deserializeObject() throws Exception {
        // Expected object but provided string
        String json = new JSONObject()
                .put("bad", "bad")
                .put("good", new JSONObject())
                .toString();

        ObjectModel result = gson.fromJson(json, ObjectModel.class);
        assertThat(result.bad, nullValue());
        assertThat(result.good, notNullValue());
    }

    @JsonSerializable
    public static class ObjectModel {
        public ObjectModel bad;
        public ObjectModel good;
    }

    @Test
    public void deserializeStrictObject() throws Exception {
        // Expected object but provided string
        String json = new JSONObject()
                .put("strictBad", "bad")
                .toString();

        Exception ex = throwsException(() -> gson.fromJson(json, StrictObjectModel.class));
        assertThat(ex, instanceOf(JsonGracefulException.class));

        // test that strict can be null
        StrictObjectModel strictNullResult = gson.fromJson(new JSONObject().toString(), StrictObjectModel.class);
        assertThat(strictNullResult.strictNull, nullValue());
    }


    @JsonSerializable
    public static class StrictObjectModel {
        @JsonStrict
        public ObjectModel strictBad;
        @JsonStrict
        public ObjectModel strictNull;
    }


    @Test
    public void deserializeNonNullObject() throws Exception {
        // Expected object but provided string
        String json = new JSONObject()
                .put("bad", "bad")
                .toString();

        Exception ex1 = throwsException(() -> gson.fromJson(json, NonNullObjectModel.class));
        assertThat(ex1, instanceOf(JsonGracefulException.class));

        // test that non-null cannot be null
        Exception ex2 = throwsException(() -> gson.fromJson(new JSONObject().toString(), NonNullObjectModel.class));
        assertThat(ex2, instanceOf(JsonGracefulException.class));
    }

    @JsonSerializable
    public static class NonNullObjectModel {
        @NonNull
        public ObjectModel bad;
        @NonNull
        public ObjectModel fail;
    }

    @Test
    public void deserializeList() throws Exception {

        // Expected list but provided string
        String json = new JSONObject()
                .put("bad", "bad")
                .put("good", new JSONArray())
                .toString();

        ListModel result = gson.fromJson(json, ListModel.class);
        assertThat(result.bad, nullValue());
        assertThat(result.good, notNullValue());
    }

    @JsonSerializable()
    public static class ListModel {
        public List<ObjectModel> bad;
        public List<ObjectModel> good;
    }

    @Test
    public void deserializeStrictList() throws Exception {
        // Expected list but provided string
        String json = new JSONObject()
                .put("strictBad", "bad")
                .toString();

        Exception ex = throwsException(() -> gson.fromJson(json, StrictListModel.class));
        assertThat(ex, instanceOf(JsonGracefulException.class));

        // test that strict can be null
        StrictListModel strictNullResult = gson.fromJson(new JSONObject().toString(), StrictListModel.class);
        assertThat(strictNullResult.strictNull, nullValue());

    }

    @JsonSerializable()
    public static class StrictListModel {
        @JsonStrict
        public List<ObjectModel> strictBad;
        @JsonStrict
        public List<ObjectModel> strictNull;
    }

    @Test
    public void deserializeNonNullList() throws Exception {
        // Expected list but provided string
        String json = new JSONObject()
                .put("bad", "bad")
                .toString();

        Exception ex1 = throwsException(() -> gson.fromJson(json, NonNullListModel.class));
        assertThat(ex1, instanceOf(JsonGracefulException.class));

        // test that non-null cannot be null
        Exception ex2 = throwsException(() -> gson.fromJson(new JSONObject().toString(), NonNullListModel.class));
        assertThat(ex2, instanceOf(JsonGracefulException.class));
    }

    @JsonSerializable()
    public static class NonNullListModel {
        @NonNull
        public List<ObjectModel> bad;
        @NonNull
        public List<ObjectModel> fail;
    }

    @Test
    public void deserializeMap() throws Exception {
        // Expected map but provided string
        String json = new JSONObject()
                .put("bad", "bad")
                .put("good", new JSONObject())
                .toString();
        MapModel result = gson.fromJson(json, MapModel.class);
        assertThat(result.bad, nullValue());
        assertThat(result.good, notNullValue());
    }

    @JsonSerializable()
    public static class MapModel {
        public Map<String, String> bad;
        public Map<String, String> good;
    }

    @Test
    public void deserializeStrictMap() throws Exception {
        // Expected map but provided string
        String json = new JSONObject()
                .put("strictBad", "bad")
                .toString();

        Exception ex = throwsException(() -> gson.fromJson(json, StrictMapModel.class));
        assertThat(ex, instanceOf(JsonGracefulException.class));

        // test that strict can be null
        StrictMapModel strictNullResult = gson.fromJson(new JSONObject().toString(), StrictMapModel.class);
        assertThat(strictNullResult.strictNull, nullValue());

    }

    @JsonSerializable()
    public static class StrictMapModel {
        @JsonStrict
        public Map<String, String> strictBad;
        @JsonStrict
        public Map<String, String> strictNull;
    }

    @Test
    public void deserializeNonNullMap() throws Exception {
        // Expected Map but provided string
        String json = new JSONObject()
                .put("bad", "bad")
                .toString();

        Exception ex1 = throwsException(() -> gson.fromJson(json, NonNullMapModel.class));
        assertThat(ex1, instanceOf(JsonGracefulException.class));

        // test that non-null cannot be null
        Exception ex2 = throwsException(() -> gson.fromJson(new JSONObject().toString(), NonNullMapModel.class));
        assertThat(ex2, instanceOf(JsonGracefulException.class));
    }

    @JsonSerializable()
    public static class NonNullMapModel {
        @NonNull
        public Map<String, String> bad;
        @NonNull
        public Map<String, String> fail;
    }

    @Test
    public void deserializeInteger() throws Exception {
        // Expected Integer but provided string

        String json = new JSONObject()
                .put("bad", "bad")
                .put("good", 1)
                .put("badNullable", "bad")
                .put("goodNullable", 1)
                .toString();

        IntegerModel result = gson.fromJson(json, IntegerModel.class);
        assertThat(result.bad, is(0));
        assertThat(result.good, is(1));
        assertThat(result.badNullable, nullValue());
        assertThat(result.goodNullable, is(1));
    }

    @JsonSerializable()
    public static class IntegerModel {
        public int bad;
        public int good;
        public Integer badNullable;
        public Integer goodNullable;
    }

    @Test
    public void deserializeStrictInteger() throws Exception {
        // Expected list but provided string
        String json1 = new JSONObject()
                .put("value", "bad")
                .toString();

        Exception ex1 = throwsException(() -> gson.fromJson(json1, StrictIntegerModel.class));
        assertThat(ex1, instanceOf(JsonGracefulException.class));

        String json2 = new JSONObject()
                .put("nullableValue", "bad")
                .toString();

        Exception ex2 = throwsException(() -> gson.fromJson(json2, StrictIntegerModel.class));
        assertThat(ex2, instanceOf(JsonGracefulException.class));


        // test that strict can be null
        StrictIntegerModel strictNullResult = gson.fromJson(new JSONObject().toString(), StrictIntegerModel.class);
        assertThat(strictNullResult.nullableValue, nullValue());
        assertThat(strictNullResult.value, is(0));
    }

    @Test
    public void deserializeStrictPrimitivePositive() throws Exception {
        // sample integer as a primitive. Positive case
        String json = new JSONObject()
                .put("value", 1)
                .put("nullableValue", 2)
                .toString();
        StrictIntegerModel result = gson.fromJson(json, StrictIntegerModel.class);
        assertThat(result.value, is(1));
        assertThat(result.nullableValue, is(2));
    }

    @JsonSerializable()
    public static class StrictIntegerModel {
        @JsonStrict
        public int value;
        @JsonStrict
        public Integer nullableValue;
    }

    @Test
    public void deserializeMustSetInteger() throws Exception {
        // Expected integer but provided string
        String json1 = new JSONObject()
                .put("value", "bad")
                .toString();

        Exception ex1 = throwsException(() -> gson.fromJson(json1, MustSetIntegerModel.class));
        assertThat(ex1, instanceOf(JsonGracefulException.class));

        String json2 = new JSONObject()
                .toString();

        Exception ex2 = throwsException(() -> gson.fromJson(json2, MustSetIntegerModel.class));
        assertThat(ex2, instanceOf(JsonGracefulException.class));
    }


    @Test
    public void deserializeMustSetPrimitivePositive() throws Exception {
        // Just sample integer as a primitive. Positive case
        String json = new JSONObject()
                .put("value", 1)
                .toString();

        MustSetIntegerModel result = gson.fromJson(json, MustSetIntegerModel.class);
        assertThat(result.value, is(1));
    }


    @JsonSerializable()
    public static class MustSetIntegerModel {
        @JsonMustSet
        public int value;
    }

    @Test
    public void deserializeBoolean() throws Exception {
        // Expected  but provided string

        String json = new JSONObject()
                .put("bad", "bad")
                .put("good", true)
                .put("badNullable", "bad")
                .put("goodNullable", true)
                .toString();

        BooleanModel result = gson.fromJson(json, BooleanModel.class);
        assertThat(result.bad, is(false));
        assertThat(result.good, is(true));
        assertThat(result.badNullable, nullValue());
        assertThat(result.goodNullable, is(true));
    }

    @Test
    public void deserializeFlexibleStringBoolean() throws Exception {
        // Expected  but provided string

        String json = new JSONObject()
                .put("bad", "bad")
                .put("good", "true")
                .put("badNullable", "bad")
                .put("goodNullable", "true")
                .toString();

        BooleanModel result = gson.fromJson(json, BooleanModel.class);
        assertThat(result.bad, is(false));
        assertThat(result.good, is(true));
        assertThat(result.badNullable, nullValue());
        assertThat(result.goodNullable, is(true));
    }

    @Test
    public void deserializeFlexibleIntBoolean() throws Exception {
        // Expected  but provided string

        String json = new JSONObject()
                .put("bad", "2")
                .put("good", 1)
                .put("badNullable", "2")
                .put("goodNullable", 1)
                .toString();

        BooleanModel result = gson.fromJson(json, BooleanModel.class);
        assertThat(result.bad, is(false));
        assertThat(result.good, is(true));
        assertThat(result.badNullable, nullValue());
        assertThat(result.goodNullable, is(true));
    }

    @Test
    public void deserializeStrictBoolean() throws Exception {
        // Expected boolean but provided string
        String json1 = new JSONObject()
                .put("strictBad", "bad")
                .toString();

        Exception ex1 = throwsException(() -> gson.fromJson(json1, BooleanModel.class));
        assertThat(ex1, instanceOf(JsonGracefulException.class));

        String json2 = new JSONObject()
                .put("strictNullableBad", "bad")
                .toString();

        Exception ex2 = throwsException(() -> gson.fromJson(json2, BooleanModel.class));
        assertThat(ex2, instanceOf(JsonGracefulException.class));

        // test that strict can be null
        BooleanModel strictNullResult = gson.fromJson(new JSONObject().toString(), BooleanModel.class);
        assertThat(strictNullResult.strictNull, nullValue());
        assertThat(strictNullResult.strictDefault, is(false));
    }

    @JsonSerializable()
    public static class BooleanModel {
        public boolean bad;
        public boolean good;
        public Boolean badNullable;
        public Boolean goodNullable;

        @JsonStrict
        public boolean strictBad;
        @JsonStrict
        public Boolean strictNullableBad;

        @JsonStrict
        public Boolean strictNull;
        @JsonStrict
        public boolean strictDefault;
    }


    @Test
    public void deserializeMustSetBoolean() throws Exception {
        // Expected boolean but provided string
        String json1 = new JSONObject()
                .put("value", "bad")
                .toString();

        Exception ex1 = throwsException(() -> gson.fromJson(json1, MustSetBooleanModel.class));
        assertThat(ex1, instanceOf(JsonGracefulException.class));

        String json2 = new JSONObject()
                .toString();

        Exception ex2 = throwsException(() -> gson.fromJson(json2, MustSetBooleanModel.class));
        assertThat(ex2, instanceOf(JsonGracefulException.class));
    }

    @JsonSerializable()
    public static class MustSetBooleanModel {
        @JsonMustSet
        public boolean value;
    }


    @Test
    public void deserializeFloat() throws Exception {
        // Expected float but provided string

        String json = new JSONObject()
                .put("bad", "bad")
                .put("good", 1.1f)
                .put("badNullable", "bad")
                .put("goodNullable", 1.1f)
                .toString();

        FloatModel result = gson.fromJson(json, FloatModel.class);
        assertThat(result.bad, is(0.f));
        assertThat(result.good, is(1.1f));
        assertThat(result.badNullable, nullValue());
        assertThat(result.goodNullable, is(1.1f));
    }

    @Test
    public void deserializeStrictFloat() throws Exception {
        // Expected list but provided string
        String json1 = new JSONObject()
                .put("strictBad", "bad")
                .toString();

        Exception ex1 = throwsException(() -> gson.fromJson(json1, FloatModel.class));
        assertThat(ex1, instanceOf(JsonGracefulException.class));

        String json2 = new JSONObject()
                .put("strictNullableBad", "bad")
                .toString();

        Exception ex2 = throwsException(() -> gson.fromJson(json2, FloatModel.class));
        assertThat(ex2, instanceOf(JsonGracefulException.class));

        // test that strict can be null
        FloatModel strictNullResult = gson.fromJson(new JSONObject().toString(), FloatModel.class);
        assertThat(strictNullResult.strictNull, nullValue());
        assertThat(strictNullResult.strictDefault, is(0.f));
    }

    @JsonSerializable()
    public static class FloatModel {
        public float bad;
        public float good;
        public Float badNullable;
        public Float goodNullable;
        @JsonStrict
        public float strictBad;
        @JsonStrict
        public Float strictNullableBad;
        @JsonStrict
        public Float strictNull;
        @JsonStrict
        public float strictDefault;
    }


    @Test
    public void deserializeMustSetFloat() throws Exception {
        // Expected float but provided string
        String json1 = new JSONObject()
                .put("value", "bad")
                .toString();

        Exception ex1 = throwsException(() -> gson.fromJson(json1, MustSetFloatModel.class));
        assertThat(ex1, instanceOf(JsonGracefulException.class));

        String json2 = new JSONObject()
                .toString();

        Exception ex2 = throwsException(() -> gson.fromJson(json2, MustSetFloatModel.class));
        assertThat(ex2, instanceOf(JsonGracefulException.class));
    }

    @JsonSerializable()
    public static class MustSetFloatModel {
        @JsonMustSet
        public float value;
    }


    @Test
    public void deserializeDouble() throws Exception {
        // Expected float but provided string

        String json = new JSONObject()
                .put("bad", "bad")
                .put("good", 1.1d)
                .put("badNullable", "bad")
                .put("goodNullable", 1.1d)
                .toString();

        DoubleModel result = gson.fromJson(json, DoubleModel.class);
        assertThat(result.bad, is(0.d));
        assertThat(result.good, is(1.1d));
        assertThat(result.badNullable, nullValue());
        assertThat(result.goodNullable, is(1.1d));
    }

    @Test
    public void deserializeStrictDouble() throws Exception {
        // Expected double but provided string
        String json1 = new JSONObject()
                .put("strictBad", "bad")
                .toString();

        Exception ex1 = throwsException(() -> gson.fromJson(json1, DoubleModel.class));
        assertThat(ex1, instanceOf(JsonGracefulException.class));

        String json2 = new JSONObject()
                .put("strictNullableBad", "bad")
                .toString();

        Exception ex2 = throwsException(() -> gson.fromJson(json2, DoubleModel.class));
        assertThat(ex2, instanceOf(JsonGracefulException.class));

        // test that strict can be null
        DoubleModel strictNullResult = gson.fromJson(new JSONObject().toString(), DoubleModel.class);
        assertThat(strictNullResult.strictNull, nullValue());
        assertThat(strictNullResult.strictDefault, is(0d));
    }

    @JsonSerializable()
    public static class DoubleModel {
        public double bad;
        public double good;
        public Double badNullable;
        public Double goodNullable;
        @JsonStrict
        public double strictBad;
        @JsonStrict
        public Double strictNullableBad;
        @JsonStrict
        public Double strictNull;
        @JsonStrict
        public double strictDefault;
    }


    @Test
    public void deserializeMustSetDouble() throws Exception {
        // Expected double but provided string
        String json1 = new JSONObject()
                .put("value", "bad")
                .toString();

        Exception ex1 = throwsException(() -> gson.fromJson(json1, MustSetDoubleModel.class));
        assertThat(ex1, instanceOf(JsonGracefulException.class));

        String json2 = new JSONObject()
                .toString();

        Exception ex2 = throwsException(() -> gson.fromJson(json2, MustSetDoubleModel.class));
        assertThat(ex2, instanceOf(JsonGracefulException.class));
    }

    @JsonSerializable()
    public static class MustSetDoubleModel {
        @JsonMustSet
        public double value;
    }


    @Test
    public void deserializeLong() throws Exception {
        // Expected Long but provided string

        String json = new JSONObject()
                .put("bad", "bad")
                .put("good", 1l)
                .put("badNullable", "bad")
                .put("goodNullable", 1l)
                .toString();

        LongModel result = gson.fromJson(json, LongModel.class);
        assertThat(result.bad, is(0l));
        assertThat(result.good, is(1l));
        assertThat(result.badNullable, nullValue());
        assertThat(result.goodNullable, is(1l));
    }

    @Test
    public void deserializeStrictLong() throws Exception {
        // Expected list but provided string
        String json1 = new JSONObject()
                .put("strictBad", "bad")
                .toString();

        Exception ex1 = throwsException(() -> gson.fromJson(json1, LongModel.class));
        assertThat(ex1, instanceOf(JsonGracefulException.class));

        String json2 = new JSONObject()
                .put("strictNullableBad", "bad")
                .toString();

        Exception ex2 = throwsException(() -> gson.fromJson(json2, LongModel.class));
        assertThat(ex2, instanceOf(JsonGracefulException.class));

        // test that strict can be null
        LongModel strictNullResult = gson.fromJson(new JSONObject().toString(), LongModel.class);
        assertThat(strictNullResult.strictNull, nullValue());
        assertThat(strictNullResult.strictDefault, is(0l));
    }

    @JsonSerializable()
    public static class LongModel {
        public long bad;
        public long good;
        public Long badNullable;
        public Long goodNullable;
        @JsonStrict
        public long strictBad;
        @JsonStrict
        public Long strictNullableBad;
        @JsonStrict
        public Long strictNull;
        @JsonStrict
        public long strictDefault;
    }


    @Test
    public void deserializeMustSetLong() throws Exception {
        // Expected long but provided string
        String json1 = new JSONObject()
                .put("value", "bad")
                .toString();

        Exception ex1 = throwsException(() -> gson.fromJson(json1, MustSetLongModel.class));
        assertThat(ex1, instanceOf(JsonGracefulException.class));

        String json2 = new JSONObject()
                .toString();

        Exception ex2 = throwsException(() -> gson.fromJson(json2, MustSetLongModel.class));
        assertThat(ex2, instanceOf(JsonGracefulException.class));
    }

    @JsonSerializable()
    public static class MustSetLongModel {
        @JsonMustSet
        public long value;
    }

    @Test
    public void deserializeNested() throws Exception {

        String json = new JSONObject()
                .put("parent", new JSONObject()
                        .put("child", (String) null))
                .toString();


        // Strict parent with strict child.
        // Parsing should fail
        Exception ex1 = throwsException(() -> gson.fromJson(json, StrictParentStrictChild.class));
        assertThat(ex1, instanceOf(JsonGracefulException.class));

        // Strict parent with graceful child
        // Child should fail gracefully but leaving parent continue to work
        StrictParentGracefulChild result2 = gson.fromJson(json, StrictParentGracefulChild.class);
        assertThat(result2, notNullValue());
        assertThat(result2.parent, notNullValue());
        assertThat(result2.parent.child, nullValue());

        // Graceful parent with strict child
        // The failure of child should result in the parent failing gracefully
        GracefulParentStrictChild result3 = gson.fromJson(json, GracefulParentStrictChild.class);
        assertThat(result3, notNullValue());
        assertThat(result3.parent, nullValue());

        // Graceful parent with graceful child
        // Child should fail gracefully but leaving parent continue to work
        GracefulParentGracefulChild result4 = gson.fromJson(json, GracefulParentGracefulChild.class);
        assertThat(result4, notNullValue());
        assertThat(result4.parent, notNullValue());
        assertThat(result4.parent.child, nullValue());
    }

    @JsonSerializable()
    static class StrictParentStrictChild {

        @NonNull
        public Parent parent;

        @JsonSerializable()
        static class Parent {
            @NonNull
            public String child;
        }
    }

    @JsonSerializable()
    static class StrictParentGracefulChild {

        @NonNull
        public Parent parent;

        @JsonSerializable()
        static class Parent {
            public String child;
        }
    }

    @JsonSerializable()
    static class GracefulParentStrictChild {

        public Parent parent;

        @JsonSerializable()
        static class Parent {
            @NonNull
            public String child;
        }
    }

    @JsonSerializable()
    static class GracefulParentGracefulChild {

        public Parent parent;

        @JsonSerializable()
        static class Parent {
            public String child;
        }
    }

    @Test
    public void deserializePartialList() throws Exception {

        // second item is bad
        String json = new JSONObject()
                .put("list", new JSONArray()
                        .put(new JSONObject().put("value", 1))
                        .put(new JSONObject().put("value", "bad"))
                        .put(new JSONObject().put("value", 3))
                )
                .toString();

        PartialList result = gson.fromJson(json, PartialList.class);

        // gracefully skips the second item
        assertThat(result.list.size(), is(2));
        assertThat(result.list.get(0).value, is(1));
        assertThat(result.list.get(1).value, is(3));
    }

    @JsonSerializable()
    static class PartialList {
        List<ListItem> list;

        @JsonSerializable()
        static class ListItem {
            @JsonStrict
            int value;
        }
    }


    @Test
    public void deserializePartialArray() throws Exception {

        // second item is bad
        String json = new JSONObject()
                .put("array", new JSONArray()
                        .put(new JSONObject().put("value", 1))
                        .put(new JSONObject().put("value", "bad"))
                        .put(new JSONObject().put("value", 3))
                )
                .toString();

        PartialArray result = gson.fromJson(json, PartialArray.class);

        // gracefully skips the second item
        assertThat(result.array.length, is(2));
        assertThat(result.array[0].value, is(1));
        assertThat(result.array[1].value, is(3));
    }

    @JsonSerializable()
    static class PartialArray {
        ListItem[] array;

        @JsonSerializable()
        static class ListItem {
            @JsonStrict
            int value;
        }
    }


    @Test
    public void deserializePrivateModel() throws Exception {

        // second item is bad
        String json = new JSONObject()
                .put("array", new JSONArray()
                        .put(new JSONObject().put("value", 1))
                )
                .put("list", new JSONArray()
                        .put(new JSONObject().put("value", 1))
                )
                .put("map", new JSONObject()
                        .put("a", new JSONObject().put("value", 1))
                )
                .put("string", "a")
                .put("integerPrimitive", 1)
                .put("integerBoxed", 1)
                .put("floatPrimitive", 1.f)
                .put("floatBoxed", 1.f)
                .put("longPrimitive", 1L)
                .put("longBoxed", 1L)
                .put("doublePrimitive", 1.D)
                .put("doubleBoxed", 1.D)
                .put("booleanPrimitive", true)
                .put("booleanBoxed", true)
                .toString();

        PrivateModel result = gson.fromJson(json, PrivateModel.class);

        assertThat(result.array.length, is(1));
        assertThat(result.array[0].value, is(1));

        assertThat(result.list.size(), is(1));
        assertThat(result.list.get(0).value, is(1));

        assertThat(result.map.size(), is(1));
        assertThat(result.map.get("a").value, is(1));

        assertThat(result.string, is("a"));

        assertThat(result.integerPrimitive, is(1));
        assertThat(result.integerBoxed, is(1));
        assertThat(result.floatPrimitive, is(1.f));
        assertThat(result.floatBoxed, is(1.f));
        assertThat(result.longPrimitive, is(1L));
        assertThat(result.longBoxed, is(1L));
        assertThat(result.doublePrimitive, is(1.D));
        assertThat(result.doubleBoxed, is(1.D));
        assertThat(result.booleanPrimitive, is(true));
        assertThat(result.booleanBoxed, is(true));

    }

    @JsonSerializable
    static class PrivateModel {

        public PrivateModel(
                int integerPrimitive,
                Integer integerBoxed,
                float floatPrimitive,
                Float floatBoxed,
                long longPrimitive,
                Long longBoxed,
                double doublePrimitive,
                Double doubleBoxed,
                boolean booleanPrimitive,
                Boolean booleanBoxed
        ) {
            this.integerPrimitive = integerPrimitive;
            this.integerBoxed = integerBoxed;
            this.floatPrimitive = floatPrimitive;
            this.floatBoxed = floatBoxed;
            this.longPrimitive = longPrimitive;
            this.longBoxed = longBoxed;
            this.doublePrimitive = doublePrimitive;
            this.doubleBoxed = doubleBoxed;
            this.booleanPrimitive = booleanPrimitive;
            this.booleanBoxed = booleanBoxed;
        }

        private final ListItem[] array = null;

        private final List<ListItem> list = null;

        private final Map<String, ListItem> map = null;

        private final String string = null;

        private final int integerPrimitive;
        private final Integer integerBoxed;

        private final float floatPrimitive;
        private final Float floatBoxed;

        private final long longPrimitive;
        private final Long longBoxed;

        private final double doublePrimitive;
        private final Double doubleBoxed;

        private final boolean booleanPrimitive;
        private final Boolean booleanBoxed;

        @JsonSerializable
        static class ListItem {
            public int value;
        }
    }

    /*
        This allow asserting multiple exceptions within a test
     */
    private Exception throwsException(Call call) {
        Exception e = null;
        try {
            call.call();
        } catch (Exception ex) {
            e = ex;
        }
        return e;
    }

    interface Call {
        void call();
    }
}

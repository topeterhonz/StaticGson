package com.github.gfx.static_gson.model;

import com.github.gfx.static_gson.annotation.JsonSerializable;

@JsonSerializable
public class ModelWithTransient {

    public transient final String value = "value";
}

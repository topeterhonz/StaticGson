package com.github.gfx.static_gson.model;

import com.github.gfx.static_gson.annotation.JsonSerializable;

@JsonSerializable
public class Author {

    public String name;

    public static Author create(String name) {
        Author author = new Author();
        author.name = name;
        return author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Author)) {
            return false;
        }

        Author author = (Author) o;

        return !(name != null ? !name.equals(author.name) : author.name != null);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}

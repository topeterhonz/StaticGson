package com.github.gfx.static_gson.model;

import com.github.gfx.static_gson.annotation.JsonSerializable;

import java.util.List;

@JsonSerializable
public class Book {

    public String title;

    public List<Author> authors;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Book)) {
            return false;
        }

        Book book = (Book) o;

        return !(title != null ? !title.equals(book.title) : book.title != null) && !(
                authors != null ? !authors.equals(book.authors) : book.authors != null);

    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (authors != null ? authors.hashCode() : 0);
        return result;
    }

    @JsonSerializable
    public static class Author {

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
}

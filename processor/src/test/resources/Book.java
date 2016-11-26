import com.github.gfx.static_gson.annotation.JsonSerializable;

import java.util.List;

@JsonSerializable
public class Book {

    public String title;

    public List<String> authors;
}

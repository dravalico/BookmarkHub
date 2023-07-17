package it.units.sim.bookmarkhub.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.Objects;

public final class Bookmark implements Serializable {
    @DocumentId
    public String id;
    @PropertyName("user_id")
    public String userId;
    @PropertyName("name")
    public String name;
    @PropertyName("url")
    public String url;
    @PropertyName("additional_data")
    public String data;
    @PropertyName("category")
    public String category;

    public Bookmark() {
    }

    public Bookmark(String userId, String name, String url, String data, String category) {
        this.userId = userId;
        this.name = name;
        this.url = url;
        this.data = data;
        this.category = category;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Bookmark) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.userId, that.userId) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.url, that.url) &&
                Objects.equals(this.data, that.data) &&
                Objects.equals(this.category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, name, url, data, category);
    }

    @Override
    public String toString() {
        return "BookmarkEntity[" +
                "id=" + id + ", " +
                "userId=" + userId + ", " +
                "name=" + name + ", " +
                "url=" + url + ", " +
                "data=" + data + ", " +
                "category=" + category + ']';
    }

}

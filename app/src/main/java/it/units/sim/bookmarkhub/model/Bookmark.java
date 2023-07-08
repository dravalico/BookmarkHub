package it.units.sim.bookmarkhub.model;

import com.google.firebase.firestore.PropertyName;

import java.util.Objects;

public final class Bookmark {
    @PropertyName("user_id")
    public String userId;
    @PropertyName("name")
    public String name;
    @PropertyName("url")
    public String url;
    @PropertyName("category")
    public String category;

    public Bookmark() {
    }

    public Bookmark(String userId, String name, String url, String category) {
        this.userId = userId;
        this.name = name;
        this.url = url;
        this.category = category;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Bookmark) obj;
        return Objects.equals(this.userId, that.userId) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.url, that.url) &&
                Objects.equals(this.category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name, url, category);
    }

    @Override
    public String toString() {
        return "BookmarkEntity[" +
                "userId=" + userId + ", " +
                "name=" + name + ", " +
                "url=" + url + ", " +
                "category=" + category + ']';
    }

}

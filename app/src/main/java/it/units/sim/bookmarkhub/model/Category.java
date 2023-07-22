package it.units.sim.bookmarkhub.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.Objects;

public final class Category implements Serializable {
    @DocumentId
    public String id;
    @PropertyName("user_id")
    public String userId;
    @PropertyName("category_name")
    public String name;

    public Category() {
    }

    public Category(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category that = (Category) o;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name);
    }

    @Override
    public String toString() {
        return "CategoriesEntity{" +
                "id=" + id + ", " +
                "ownerId='" + userId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

}

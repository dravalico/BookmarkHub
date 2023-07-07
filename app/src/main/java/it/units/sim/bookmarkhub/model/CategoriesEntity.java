package it.units.sim.bookmarkhub.model;

import com.google.firebase.firestore.PropertyName;

import java.util.List;
import java.util.Objects;

public final class CategoriesEntity {
    @PropertyName("owner_id")
    public String ownerId;
    @PropertyName("name")
    public List<String> categories;

    public CategoriesEntity() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoriesEntity that = (CategoriesEntity) o;
        return Objects.equals(ownerId, that.ownerId) && Objects.equals(categories, that.categories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerId, categories);
    }

    @Override
    public String toString() {
        return "CategoriesEntity{" +
                "ownerId='" + ownerId + '\'' +
                ", categories=" + categories +
                '}';
    }

}

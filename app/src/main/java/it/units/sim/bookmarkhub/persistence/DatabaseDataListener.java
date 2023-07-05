package it.units.sim.bookmarkhub.persistence;

public interface DatabaseDataListener<T> {
    void onSuccess(T data);

    void onFailure(String errorMessage);
}

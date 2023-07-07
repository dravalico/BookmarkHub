package it.units.sim.bookmarkhub.repository;

public interface DatabaseEventListener {

    void onSuccess();

    void onFailure(String errorMessage);

}

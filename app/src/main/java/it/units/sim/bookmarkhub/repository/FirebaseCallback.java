package it.units.sim.bookmarkhub.repository;

public interface FirebaseCallback {
    void onSuccess();

    void onFailure(int errorStringId);
}

package it.units.sim.bookmarkhub.persistence;

import com.google.firebase.auth.FirebaseUser;

public interface DatabaseAuthListener {
    void onSuccess(FirebaseUser user);

    void onFailure(String errorMessage);
}

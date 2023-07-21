package it.units.sim.bookmarkhub.ui.util;

import android.app.Activity;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.List;
import java.util.stream.Collectors;

import it.units.sim.bookmarkhub.model.Category;
import it.units.sim.bookmarkhub.repository.FirebaseCategoriesHelper;

public class ViewUtil {

    private ViewUtil() {
    }

    public static void fetchCategoriesFromFirebase(Context context, ArrayAdapter<String> adapter) {
        FirebaseCategoriesHelper.getCategoriesListOfCurrentUser(new FirebaseCategoriesHelper.CategoriesCallback() {
            @Override
            public void onSuccess(List<Category> category) {
                setAdapterSpinnerValues(context, adapter, category.stream()
                        .map(c -> c.name)
                        .collect(Collectors.toList()));
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void setAdapterSpinnerValues(Context context, ArrayAdapter<String> adapter, List<String> categories) {
        ((Activity) context).runOnUiThread(() -> {
            adapter.clear();
            adapter.addAll(categories);
            adapter.notifyDataSetChanged();
        });
    }

}

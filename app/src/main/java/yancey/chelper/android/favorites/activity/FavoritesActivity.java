package yancey.chelper.android.favorites.activity;

import java.util.function.Consumer;

import yancey.chelper.android.common.activity.CustomActivity;
import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.android.favorites.view.FavoritesView;

public class FavoritesActivity extends CustomActivity<FavoritesView> {

    @Override
    protected FavoritesView createView(Consumer<CustomView> openView) {
        return new FavoritesView(this, openView);
    }

}
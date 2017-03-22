package gov.cdc.mester2;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import io.realm.Realm;

public class SearchActivity extends AppCompatActivity implements SearchFragment.OnFragmentInteractionListener {
    private Realm realm;
    @SuppressWarnings("FieldCanBeLocal")
    private final String TAG = "Search Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);

        realm = Realm.getDefaultInstance();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onFragmentInteraction(String conditionName) {
        Log.d(TAG, "onFragmentInteraction: " +conditionName);
        Intent resultIntent = new Intent();
        // TODO Add extras or a data URI to this intent as appropriate.
        resultIntent.putExtra("conditionName", conditionName);
        setResult(1, resultIntent);
        finish();
    }

    /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setIconified(false);
        MenuItemCompat.expandActionView(searchMenuItem);


        return super.onCreateOptionsMenu(menu);
    }*/
}

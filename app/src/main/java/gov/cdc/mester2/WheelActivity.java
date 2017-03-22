package gov.cdc.mester2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import io.realm.Realm;
import io.realm.RealmList;


public class WheelActivity extends AppCompatActivity implements ConditionsPagerFragment.OnFragmentInteractionListener {
    @SuppressWarnings("FieldCanBeLocal")
    private final String TAG = "Wheel Activity";
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel);

        realm = Realm.getDefaultInstance();

        /* TODO: Uncomment this block for production
        SharedPreferences sp = getSharedPreferences("MEC_Preferences", 0);
        if(!sp.getBoolean("conditionsAdded", false)) {
            SharedPreferences.Editor editor = sp.edit(); */
            populateRealm();
        /*
            editor.putBoolean("conditionsAdded", true).apply();
        }
        */

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, ConditionsPagerFragment.newInstance());
        transaction.commit();


    }

    /**
     * In case if you require to handle drawer open and close states
     */
    private void setupActionBarDrawerToggle() {

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                //Snackbar.make(view, R.string.drawer_close, Snackbar.LENGTH_SHORT).show();
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                //Snackbar.make(drawerView, R.string.drawer_open, Snackbar.LENGTH_SHORT).show();
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    private void setupDrawerContent(final NavigationView navigationView) {


        //setting up selected item listener
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        closeNavDrawer();
                        switch (menuItem.getItemId()) {
                            case R.id.nav_about:
                                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                                break;
                            case R.id.nav_eula:
                                startActivity(new Intent(getApplicationContext(), EulaActivity.class));

                                break;
                            case R.id.nav_support:
                                startActivity(new Intent(getApplicationContext(), SupportActivity.class));

                                break;
                        }
                        return true;
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_search:
                startActivityForResult(new Intent(this, SearchActivity.class), 1);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1) {
            ConditionsPagerFragment pagerFragment = (ConditionsPagerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            pagerFragment.setDisplayCondition(data.getStringExtra("conditionName"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.wheel, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        } else {
            super.onBackPressed();
        }
    }

    private boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    private void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_white);

        ab.setHomeActionContentDescription("Menu");

        ab.setDisplayHomeAsUpEnabled(true);

        setupActionBarDrawerToggle();
        if (mNavigationView != null) {
            setupDrawerContent(mNavigationView);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        Menu menu = mNavigationView.getMenu();

        for(int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setChecked(false);
        }
    }

    private void populateRealm() {

        //TODO: Remove the next three lines before production
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();

        Condition parentCondition;
        Condition subCondition;
        RealmList<Condition> childConditions;
        String conditionName;
        String subConditionName;

        parentCondition = new Condition();
        parentCondition.setHasChildren(true);
        conditionName = "Cancers";
        parentCondition.setName(conditionName);
        parentCondition.setId(conditionName);
        childConditions = new RealmList<>();

        subConditionName = "Breast cancer (current)";
        subCondition = new Condition(subConditionName, new Rating("4"),
                new Rating("4"),new Rating("4"), new Rating("4"), new Rating("4"),
                new Rating("1"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "Cervical cancer (pre-treatment)";
        subCondition = new Condition(subConditionName, new Rating("2"),
                new Rating("1"),new Rating("2"), new Rating("2"), new Rating("4"),
                new Rating("4"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        parentCondition.setChildConditions(childConditions);

        realm.beginTransaction();
        realm.copyToRealm(parentCondition);
        realm.commitTransaction();

        parentCondition = new Condition();
        parentCondition.setHasChildren(true);
        conditionName = "Vaginal Bleeding";
        parentCondition.setName(conditionName);
        parentCondition.setId(conditionName);
        childConditions = new RealmList<>();

        subConditionName = "Unexplained";
        subCondition = new Condition(subConditionName, new Rating("2"),
                new Rating("2"),new Rating("3"), new Rating("3"), new Rating("4"),
                new Rating("4"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);
        parentCondition.setChildConditions(childConditions);

        realm.beginTransaction();
        realm.copyToRealm(parentCondition);
        realm.commitTransaction();

        conditionName = "PID";
        parentCondition = new Condition();
        parentCondition.setHasChildren(true);
        parentCondition.setName(conditionName);
        parentCondition.setId(conditionName);
        childConditions = new RealmList<>();

        subConditionName = "Current";
        subCondition = new Condition(subConditionName, new Rating("1"),
                new Rating("1"),new Rating("1"), new Rating("1"), new Rating("4"),
                new Rating("4"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);
        parentCondition.setChildConditions(childConditions);

        realm.beginTransaction();
        realm.copyToRealm(parentCondition);
        realm.commitTransaction();

        conditionName = "Sexually Transmitted Infections";
        parentCondition = new Condition();
        parentCondition.setHasChildren(true);
        parentCondition.setName(conditionName);
        parentCondition.setId(conditionName);
        childConditions = new RealmList<>();

        subConditionName = "Gonorrhea Chlamydia";
        subCondition = new Condition(subConditionName, new Rating("1"),
                new Rating("1"),new Rating("1"), new Rating("1"), new Rating("4"),
                new Rating("4"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "Other STIs and vaginitis";
        subCondition = new Condition(subConditionName, new Rating("1"),
                new Rating("1"),new Rating("1"), new Rating("1"), new Rating("2"),
                new Rating("2"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "Increased risk of STI";
        subCondition = new Condition(subConditionName, new Rating("1"),
                new Rating("1"),new Rating("1"), new Rating("1"), new Rating("2/3"),
                new Rating("2/3"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        parentCondition.setChildConditions(childConditions);

        realm.beginTransaction();
        realm.copyToRealm(parentCondition);
        realm.commitTransaction();

        conditionName = "HIV/AIDS";
        parentCondition = new Condition();
        parentCondition.setHasChildren(true);
        parentCondition.setName(conditionName);
        parentCondition.setId(conditionName);
        childConditions = new RealmList<>();

        subConditionName = "HIV Infection or AIDS";
        subCondition = new Condition(subConditionName, new Rating("1"),
                new Rating("1"),new Rating("1"), new Rating("1"), new Rating("3"),
                new Rating("3"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        parentCondition.setChildConditions(childConditions);

        realm.beginTransaction();
        realm.copyToRealm(parentCondition);
        realm.commitTransaction();

        conditionName = "Age";
        parentCondition = new Condition();
        parentCondition.setHasChildren(true);
        parentCondition.setName(conditionName);
        parentCondition.setId(conditionName);
        childConditions = new RealmList<>();

        subConditionName = "Adolescents < 18 years";
        subCondition = new Condition(subConditionName, new Rating("1"),
                new Rating("1"),new Rating("2"), new Rating("1"), new Rating("2"),
                new Rating("2"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        parentCondition.setChildConditions(childConditions);

        realm.beginTransaction();
        realm.copyToRealm(parentCondition);
        realm.commitTransaction();

        conditionName = "Smoking";
        parentCondition = new Condition();
        parentCondition.setHasChildren(true);
        parentCondition.setName(conditionName);
        parentCondition.setId(conditionName);
        childConditions = new RealmList<>();

        subConditionName = "Age < 35";
        subCondition = new Condition(subConditionName, new Rating("1"),
                new Rating("1"),new Rating("2"), new Rating("1"), new Rating("2"),
                new Rating("2"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "Age ≥ 35";
        subCondition = new Condition(subConditionName, new Rating("4"),
                new Rating("1"),new Rating("1"), new Rating("1"), new Rating("1"),
                new Rating("1"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        parentCondition.setChildConditions(childConditions);

        realm.beginTransaction();
        realm.copyToRealm(parentCondition);
        realm.commitTransaction();

        conditionName = "Drug Interactions";
        parentCondition = new Condition();
        parentCondition.setHasChildren(true);
        parentCondition.setName(conditionName);
        parentCondition.setId(conditionName);
        childConditions = new RealmList<>();

        subConditionName = "Rifampicin/rifabutin";
        subCondition = new Condition(subConditionName, new Rating("3"),
                new Rating("3"),new Rating("1"), new Rating("2"), new Rating("1"),
                new Rating("1"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "Certain anti-convulsants";
        subCondition = new Condition(subConditionName, new Rating("3"),
                new Rating("3"),new Rating("1"), new Rating("2"), new Rating("1"),
                new Rating("1"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "ARV therapy";
        subCondition = new Condition(subConditionName, new Rating("3"),
                new Rating("3"),new Rating("1"), new Rating("2"), new Rating("2/3"),
                new Rating("2/3"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        parentCondition.setChildConditions(childConditions);

        realm.beginTransaction();
        realm.copyToRealm(parentCondition);
        realm.commitTransaction();

        conditionName = "Hypertension";
        parentCondition = new Condition();
        parentCondition.setHasChildren(true);
        parentCondition.setName(conditionName);
        parentCondition.setId(conditionName);
        childConditions = new RealmList<>();

        subConditionName = "140-159/90-99";
        subCondition = new Condition(subConditionName, new Rating("3"),
                new Rating("1"),new Rating("2"), new Rating("1"), new Rating("1"),
                new Rating("1"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "≥160/≥100";
        subCondition = new Condition(subConditionName, new Rating("4"),
                new Rating("2"),new Rating("3"), new Rating("2"), new Rating("2"),
                new Rating("1"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        parentCondition.setChildConditions(childConditions);

        realm.beginTransaction();
        realm.copyToRealm(parentCondition);
        realm.commitTransaction();

        conditionName = "Cardiovascular Disease";
        parentCondition = new Condition();
        parentCondition.setHasChildren(true);
        parentCondition.setName(conditionName);
        parentCondition.setId(conditionName);
        childConditions = new RealmList<>();

        subConditionName = "Stroke";
        subCondition = new Condition(subConditionName, new Rating("4"),
                new Rating("2"),new Rating("3"), new Rating("2"), new Rating("2"),
                new Rating("1"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "Ischemic";
        subCondition = new Condition(subConditionName, new Rating("4"),
                new Rating("2"),new Rating("3"), new Rating("2"), new Rating("2"),
                new Rating("1"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "Multiple risk factors";
        subCondition = new Condition(subConditionName, new Rating("3/4"),
                new Rating("2"),new Rating("3"), new Rating("2"), new Rating("2"),
                new Rating("1"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        parentCondition.setChildConditions(childConditions);

        realm.beginTransaction();
        realm.copyToRealm(parentCondition);
        realm.commitTransaction();

        conditionName = "Deep Vein Thrombosis";
        parentCondition = new Condition();
        parentCondition.setHasChildren(true);
        parentCondition.setName(conditionName);
        parentCondition.setId(conditionName);
        childConditions = new RealmList<>();

        subConditionName = "History";
        subCondition = new Condition(subConditionName, new Rating("4"),
                new Rating("2"),new Rating("2"), new Rating("2"), new Rating("2"),
                new Rating("1"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "Acute";
        subCondition = new Condition(subConditionName, new Rating("4"),
                new Rating("2"),new Rating("2"), new Rating("2"), new Rating("2"),
                new Rating("2"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "On therapy";
        subCondition = new Condition(subConditionName, new Rating("4"),
                new Rating("2"),new Rating("2"), new Rating("2"), new Rating("2"),
                new Rating("2"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "Major Surgery";
        subCondition = new Condition(subConditionName, new Rating("4"),
                new Rating("2"),new Rating("2"), new Rating("2"), new Rating("2"),
                new Rating("1"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        parentCondition.setChildConditions(childConditions);

        realm.beginTransaction();
        realm.copyToRealm(parentCondition);
        realm.commitTransaction();

        conditionName = "Headaches";
        parentCondition = new Condition();
        parentCondition.setHasChildren(true);
        parentCondition.setName(conditionName);
        parentCondition.setId(conditionName);
        childConditions = new RealmList<>();

        subConditionName = "Migraine without aura";
        subCondition = new Condition(subConditionName, new Rating("3"),
                new Rating("1"),new Rating("2"), new Rating("2"), new Rating("2"),
                new Rating("1"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "Migraine with aura";
        subCondition = new Condition(subConditionName, new Rating("4"),
                new Rating("2"),new Rating("2"), new Rating("2"), new Rating("2"),
                new Rating("1"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        parentCondition.setChildConditions(childConditions);

        realm.beginTransaction();
        realm.copyToRealm(parentCondition);
        realm.commitTransaction();

        conditionName = "Liver Diseases";
        parentCondition = new Condition();
        parentCondition.setHasChildren(true);
        parentCondition.setName(conditionName);
        parentCondition.setId(conditionName);
        childConditions = new RealmList<>();

        subConditionName = "Tumors";
        subCondition = new Condition(subConditionName, new Rating("4"),
                new Rating("3"),new Rating("3"), new Rating("3"), new Rating("3"),
                new Rating("1"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "Severe cirrhosis";
        subCondition = new Condition(subConditionName, new Rating("4"),
                new Rating("3"),new Rating("3"), new Rating("3"), new Rating("3"),
                new Rating("1"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "Hepatitis acute / flare";
        subCondition = new Condition(subConditionName, new Rating("3/4"),
                new Rating("1"),new Rating("1"), new Rating("1"), new Rating("1"),
                new Rating("1"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        parentCondition.setChildConditions(childConditions);

        realm.beginTransaction();
        realm.copyToRealm(parentCondition);
        realm.commitTransaction();

        conditionName = "IBD";
        parentCondition = new Condition();
        parentCondition.setHasChildren(true);
        parentCondition.setName(conditionName);
        parentCondition.setId(conditionName);
        childConditions = new RealmList<>();

        subConditionName = "IBD";
        subCondition = new Condition(subConditionName, new Rating("2/3"),
                new Rating("2"),new Rating("2"), new Rating("1"), new Rating("1"),
                new Rating("1"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        parentCondition.setChildConditions(childConditions);

        realm.beginTransaction();
        realm.copyToRealm(parentCondition);
        realm.commitTransaction();

        conditionName = "Diabetes";
        parentCondition = new Condition();
        parentCondition.setHasChildren(true);
        parentCondition.setName(conditionName);
        parentCondition.setId(conditionName);
        childConditions = new RealmList<>();

        subConditionName = "Current";
        subCondition = new Condition(subConditionName, new Rating("2"),
                new Rating("2"),new Rating("2"), new Rating("2"), new Rating("2"),
                new Rating("1"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        parentCondition.setChildConditions(childConditions);

        realm.beginTransaction();
        realm.copyToRealm(parentCondition);
        realm.commitTransaction();

        conditionName = "Obesity";
        parentCondition = new Condition();
        parentCondition.setHasChildren(true);
        parentCondition.setName(conditionName);
        parentCondition.setId(conditionName);
        childConditions = new RealmList<>();

        subConditionName = "Obesity";
        subCondition = new Condition(subConditionName, new Rating("2"),
                new Rating("1"),new Rating("2"), new Rating("1"), new Rating("1"),
                new Rating("1"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        parentCondition.setChildConditions(childConditions);

        realm.beginTransaction();
        realm.copyToRealm(parentCondition);
        realm.commitTransaction();

        conditionName = "Bariatric Surgery";
        parentCondition = new Condition();
        parentCondition.setHasChildren(true);
        parentCondition.setName(conditionName);
        parentCondition.setId(conditionName);
        childConditions = new RealmList<>();

        subConditionName = "Restrictive procedures";
        subCondition = new Condition(subConditionName, new Rating("1"),
                new Rating("1"),new Rating("1"), new Rating("1"), new Rating("1"),
                new Rating("1"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "Malabsorbtive procedures";
        subCondition = new Condition(subConditionName, new Rating("3"),
                new Rating("3"),new Rating("1"), new Rating("1"), new Rating("1"),
                new Rating("1"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        parentCondition.setChildConditions(childConditions);

        realm.beginTransaction();
        realm.copyToRealm(parentCondition);
        realm.commitTransaction();

        //TODO: move the "A's" to notes once notes are implemented
        conditionName = "Postpartum";
        parentCondition = new Condition();
        parentCondition.setHasChildren(true);
        parentCondition.setName(conditionName);
        parentCondition.setId(conditionName);
        childConditions = new RealmList<>();

        subConditionName = "< 21 days";
        subCondition = new Condition(subConditionName, new Rating("4"),
                new Rating("2"),new Rating("2"), new Rating("2"), new Rating("A"),
                new Rating("A"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "21-< 30 days breastfeeding";
        subCondition = new Condition(subConditionName, new Rating("3"),
                new Rating("2"),new Rating("2"), new Rating("2"), new Rating("A"),
                new Rating("A"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "30-42 days breastfeeding";
        subCondition = new Condition(subConditionName, new Rating("3"),
                new Rating("1"),new Rating("1"), new Rating("1"), new Rating("A"),
                new Rating("A"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "21-42 days not breastfeeding";
        subCondition = new Condition(subConditionName, new Rating("3"),
                new Rating("1"),new Rating("1"), new Rating("1"), new Rating("A"),
                new Rating("A"), null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        parentCondition.setChildConditions(childConditions);

        realm.beginTransaction();
        realm.copyToRealm(parentCondition);
        realm.commitTransaction();


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onFragmentInteraction() {

    }
}

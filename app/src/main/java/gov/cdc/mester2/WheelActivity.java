package gov.cdc.mester2;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import io.realm.Realm;
import io.realm.RealmList;


public class WheelActivity extends AppCompatActivity implements ConditionsPagerFragment.OnFragmentInteractionListener {
    @SuppressWarnings("FieldCanBeLocal")
    private final String TAG = "Wheel Activity";
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel);

        realm = Realm.getDefaultInstance();


        SharedPreferences sp = getSharedPreferences("MEC_Preferences", 0);
        if(!sp.getBoolean("conditionsAdded", false)) {
            SharedPreferences.Editor editor = sp.edit();
            populateRealm();
            editor.putBoolean("conditionsAdded", true).apply();
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

        if(savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, ConditionsPagerFragment.newInstance());
            transaction.commit();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        uncheckAllMenuItems(mNavigationView);
    }

    /**
     * In case if you require to handle drawer open and close states
     */
    private void setupActionBarDrawerToggle() {

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
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
                        // first set all menu items to unchecked
                        uncheckAllMenuItems(navigationView);

                        // now set clicked menu item to checked
                        menuItem.setChecked(true);

                        Intent intent = new Intent();
                        closeNavDrawer();
                        switch (menuItem.getItemId()) {
                            case R.id.nav_about:
                                intent.setClass(getApplicationContext(), AboutActivity.class);
                                break;
                            case R.id.nav_eula:
                                intent = new Intent(getApplicationContext(), EulaActivity.class);

                                break;
                            case R.id.nav_support:
                                intent = new Intent(getApplicationContext(), SupportActivity.class);
                                break;
                        }
                        startActivity(intent);
                        return true;
                    }
                });
    }

    private void uncheckAllMenuItems(NavigationView navigationView) {
        final Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
                item.setChecked(false);
        }
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
        Log.d(TAG, "onActivityResult: requestCode: " +requestCode +" resultCode: " +resultCode +" data: " +data);
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

    private void populateRealm() {

        //TODO: Remove the next three lines before production
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();

        Note A = new Note("A", "IUD insertion postpartum = 1 or 2 except purperal sepsis which = 4");
        Note B = new Note("B", "If not breastfeeding = 1");
        Note C = new Note("C", "For women without risk factors for VTE, COC/P/R = 2");
        Note D = new Note("D", "Or other forms of purulent cervicitis");
        Note E = new Note("E", "Recommendations for both IUDs refer to < 20 yrs");
        Note F = new Note("F", "If very high likelihood of exposure to gonorrhea or chlamydia = 3");
        Note G = new Note("G", "For women with mild IBD and no other risk factors for VTE = 2, for women with increased risk of VTE = 3");
        Note H = new Note("H", "HIV-infected and AIDS, clinically well on ARV therapy = 2; AIDS, not clinically well = 3");
        Note I = new Note("I", "COC/P/R and < 15 cigarettes/day = 3");
        Note J = new Note("J", "Refers to Ritonavir-boosted protease inhibitors");
        Note K = new Note("K", "These recommendations also apply to controlled hypertension. Consult the full US MEC document for a clarification of this recommendation.");
        Note L = new Note("L", "These recommendations also apply to vascular disease");
        Note M = new Note("M", "If age < 35 yrs then this condition = 2");
        Note N = new Note("N", "For ≥ 18 years and BMI ≥ 30 kg/m² = 1");
        Note O = new Note("O", "For nephropathy/retinopathy/neuropathy and other vascular disease or diabetes for > 20 yrs' duration COC/P/R = 3/4 depending on disease severity, and DMPA = 3");
        Note P = new Note("P", "For patch and ring, this condition = 1");
        Note Q = new Note("Q", "Includes: phenytoin, carbamazepine, barbiturates, primidone, topiramate, oxcarbazepine. For lamotrigine COCs = 3 • Other methods = 1");
        Note R = new Note("R", "For women with low risk for recurrent DVT = 3");
        Note S = new Note("S", "Established on anticoagulant therapy for ≥ 3 months");
        Note T = new Note("T", "Refers to surgery with prolonged immobilization");
        Note U = new Note("U", "Focal nodular hyperplasia = 1 or 2 for all methods");
        Note V = new Note("V", "Category should be assessed according to the number, severity, and combination of risk factors");
        Note W = new Note("W", "The category should be assessed according to the severity of the condition");
        Note cross = new Note("✝", "Consult the full US MEC document for a clarification of this recommendation.");


        realm.beginTransaction();
        realm.copyToRealm(A);
        realm.copyToRealm(B);
        realm.copyToRealm(C);
        realm.copyToRealm(D);
        realm.copyToRealm(E);
        realm.copyToRealm(F);
        realm.copyToRealm(G);
        realm.copyToRealm(H);
        realm.copyToRealm(I);
        realm.copyToRealm(J);
        realm.copyToRealm(K);
        realm.copyToRealm(L);
        realm.copyToRealm(M);
        realm.copyToRealm(N);
        realm.copyToRealm(O);
        realm.copyToRealm(P);
        realm.copyToRealm(Q);
        realm.copyToRealm(R);
        realm.copyToRealm(S);
        realm.copyToRealm(T);
        realm.copyToRealm(U);
        realm.copyToRealm(V);
        realm.copyToRealm(W);
        realm.copyToRealm(cross);
        realm.commitTransaction();

        Condition parentCondition;
        Condition subCondition;
        RealmList<Condition> childConditions;
        String conditionName;
        String subConditionName;
        RealmList<Note> crossOnly = new RealmList<>(cross);
        RealmList<Note> ratingNotes;

        parentCondition = new Condition();
        parentCondition.setHasChildren(true);
        conditionName = "Cancers";
        parentCondition.setName(conditionName);
        parentCondition.setId(conditionName);
        childConditions = new RealmList<>();

        subConditionName = "Breast cancer (current)";
        subCondition = new Condition(subConditionName, new Rating("4"),
                new Rating("4"),new Rating("4"), new Rating("4"), new Rating("4"),
                new Rating("1"), null, null, null, null, null, null, null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "Cervical cancer (pre-treatment)";
        subCondition = new Condition(subConditionName, new Rating("2"),
                new Rating("1"),new Rating("2"), new Rating("2"), new Rating("4"),
                new Rating("4"), null, null, null, null, null, null, null, parentCondition);
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
        subCondition = new Condition(subConditionName, new Rating("2", crossOnly),
                new Rating("2", crossOnly),new Rating("3", crossOnly), new Rating("3", crossOnly), new Rating("4", crossOnly),
                new Rating("4", crossOnly), null, null, null, null, null, null, null, parentCondition);
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
                new Rating("4"), null, null, null, null, null, null, null, parentCondition);
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
                new Rating("4"), null, null, null, null, null, null, new RealmList<>(D), parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "Other STIs and vaginitis";
        subCondition = new Condition(subConditionName, new Rating("1"),
                new Rating("1"),new Rating("1"), new Rating("1"), new Rating("2"),
                new Rating("2"), null, null, null, null, null, null, null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "Increased risk of STI";
        ratingNotes = new RealmList<>(F, cross);
        subCondition = new Condition(subConditionName, new Rating("1"),
                new Rating("1"),new Rating("1"), new Rating("1"), new Rating("2/3", ratingNotes),
                new Rating("2/3", ratingNotes), null, null, null, null, null, null, null, parentCondition);
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
        ratingNotes = new RealmList<>(H);
        subCondition = new Condition(subConditionName, new Rating("1", crossOnly),
                new Rating("1", crossOnly),new Rating("1", crossOnly), new Rating("1", crossOnly), new Rating("3", ratingNotes),
                new Rating("3", ratingNotes), null, null, null, null, null, null, null, parentCondition);
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
        ratingNotes = new RealmList<>(E);
        subCondition = new Condition(subConditionName, new Rating("1"),
                new Rating("1"),new Rating("2"), new Rating("1"), new Rating("2", ratingNotes),
                new Rating("2", ratingNotes),
                new Rating("9"),
                new Rating("9"),new Rating("9"), new Rating("9"), new Rating("9"),
                new Rating("9"),
                null, parentCondition);
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
                new Rating("2"), null, null, null, null, null, null, null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "Age ≥ 35";
        ratingNotes = new RealmList<>(I);
        subCondition = new Condition(subConditionName, new Rating("4", ratingNotes),
                new Rating("1"),new Rating("1"), new Rating("1"), new Rating("1"),
                new Rating("1"), null, null, null, null, null, null, null, parentCondition);
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
        subCondition = new Condition(subConditionName, new Rating("3", crossOnly),
                new Rating("3", crossOnly),new Rating("1"), new Rating("2", crossOnly), new Rating("1"),
                new Rating("1"), null, null, null, null, null, null, null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "Certain anti-convulsants";
        subCondition = new Condition(subConditionName, new Rating("3", crossOnly),
                new Rating("3", crossOnly),new Rating("1"), new Rating("2", crossOnly), new Rating("1"),
                new Rating("1"), null, null, null, null, null, null, new RealmList<>(Q), parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "ARV therapy";
        ratingNotes = new RealmList<>(H, cross);
        subCondition = new Condition(subConditionName, new Rating("3", crossOnly),
                new Rating("3", crossOnly),new Rating("1"), new Rating("2", crossOnly), new Rating("2/3", ratingNotes),
                new Rating("2/3", ratingNotes), null, null, null, null, null, null, new RealmList<>(J), parentCondition);
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
                new Rating("1"), null, null, null, null, null, null, new RealmList<>(K), parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "≥160/≥100";
        subCondition = new Condition(subConditionName, new Rating("4"),
                new Rating("2"),new Rating("3"), new Rating("2"), new Rating("2"),
                new Rating("1"), null, null, null, null, null, null, new RealmList<>(L), parentCondition);
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
                new Rating("1"), null, null, null, null, null, null, null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "Ischemic";
        subCondition = new Condition(subConditionName, new Rating("4"),
                new Rating("2"),new Rating("3"), new Rating("2"), new Rating("2"),
                new Rating("1"), null, null, null, null, null, null, null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "Multiple risk factors";
        subCondition = new Condition(subConditionName, new Rating("3/4", new RealmList<>(V, cross)),
                new Rating("2", crossOnly),new Rating("3", crossOnly), new Rating("2", crossOnly), new Rating("2"),
                new Rating("1"), null, null, null, null, null, null, null, parentCondition);
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
        subCondition = new Condition(subConditionName, new Rating("4", new RealmList<>(R)),
                new Rating("2"),new Rating("2"), new Rating("2"), new Rating("2"),
                new Rating("1"), null, null, null, null, null, null, null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "Acute";
        subCondition = new Condition(subConditionName, new Rating("4"),
                new Rating("2"),new Rating("2"), new Rating("2"), new Rating("2"),
                new Rating("2"), null, null, null, null, null, null, null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "On therapy";
        ratingNotes = new RealmList<>(R,S, cross);
        RealmList<Note> sOnly = new RealmList<>(S);
        subCondition = new Condition(subConditionName, new Rating("4", ratingNotes),
                new Rating("2", sOnly),new Rating("2", sOnly), new Rating("2", sOnly), new Rating("2", sOnly),
                new Rating("2", sOnly), null, null, null, null, null, null, null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "Major Surgery";
        subCondition = new Condition(subConditionName, new Rating("4"),
                new Rating("2"),new Rating("2"), new Rating("2"), new Rating("2"),
                new Rating("1"), null, null, null, null, null, null, new RealmList<>(T), parentCondition);
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
        subCondition = new Condition(subConditionName, new Rating("3", new RealmList<>(M, cross)),
                new Rating("1", crossOnly),new Rating("2", crossOnly), new Rating("2", crossOnly), new Rating("2", crossOnly),
                new Rating("1", crossOnly), null, null, null, null, null, null, null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "Migraine with aura";
        subCondition = new Condition(subConditionName, new Rating("4", crossOnly),
                new Rating("2", crossOnly),new Rating("2", crossOnly), new Rating("2", crossOnly), new Rating("2", crossOnly),
                new Rating("1", crossOnly), null, null, null, null, null, null, null, parentCondition);
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
                new Rating("1"), null, null, null, null, null, null, new RealmList<>(U), parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "Severe cirrhosis";
        subCondition = new Condition(subConditionName, new Rating("4"),
                new Rating("3"),new Rating("3"), new Rating("3"), new Rating("3"),
                new Rating("1"), null, null, null, null, null, null, null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "Hepatitis acute / flare";
        subCondition = new Condition(subConditionName, new Rating("3/4", new RealmList<>(W, cross)),
                new Rating("1"),new Rating("1"), new Rating("1"), new Rating("1"),
                new Rating("1"), null, null, null, null, null, null, null, parentCondition);
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
        subCondition = new Condition(subConditionName, new Rating("2/3", new RealmList<>(G, cross)),
                new Rating("2"),new Rating("2"), new Rating("1"), new Rating("1"),
                new Rating("1"), null, null, null, null, null, null, null, parentCondition);
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
        ratingNotes = new RealmList<>(O);
        subCondition = new Condition(subConditionName, new Rating("2", ratingNotes),
                new Rating("2"),new Rating("2", ratingNotes), new Rating("2"), new Rating("2"),
                new Rating("1"), null, null, null, null, null, null, null, parentCondition);
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
                new Rating("1"),new Rating("2", new RealmList<>(N)), new Rating("1"), new Rating("1"),
                new Rating("1"), null, null, null, null, null, null, null, parentCondition);
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
                new Rating("1"), null, null, null, null, null, null, null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "Malabsorbtive procedures";
        subCondition = new Condition(subConditionName, new Rating("3", new RealmList<>(P)),
                new Rating("3"),new Rating("1"), new Rating("1"), new Rating("1"),
                new Rating("1"), null, null, null, null, null, null, null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        parentCondition.setChildConditions(childConditions);

        realm.beginTransaction();
        realm.copyToRealm(parentCondition);
        realm.commitTransaction();

        conditionName = "Postpartum";
        parentCondition = new Condition();
        parentCondition.setHasChildren(true);
        parentCondition.setName(conditionName);
        parentCondition.setId(conditionName);
        childConditions = new RealmList<>();
        RealmList<Note> aOnly = new RealmList<>(A);

        subConditionName = "< 21 days";
        ratingNotes = new RealmList<>(B);
        subCondition = new Condition(subConditionName, new Rating("4"),
                new Rating("2", ratingNotes),new Rating("2", ratingNotes), new Rating("2", ratingNotes), new Rating("", aOnly),
                new Rating("", aOnly), null, null, null, null, null, null, null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "21-< 30 days breastfeeding";
        subCondition = new Condition(subConditionName, new Rating("3", crossOnly),
                new Rating("2", crossOnly),new Rating("2", crossOnly), new Rating("2", crossOnly), new Rating("", aOnly),
                new Rating("", aOnly), null, null, null, null, null, null, null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "30-42 days breastfeeding";
        subCondition = new Condition(subConditionName, new Rating("3", new RealmList<>(C, cross)),
                new Rating("1", crossOnly),new Rating("1", crossOnly), new Rating("1", crossOnly), new Rating("", aOnly),
                new Rating("", aOnly), null, null, null, null, null, null, null, parentCondition);
        subCondition.setName(subConditionName);
        childConditions.add(subCondition);

        subConditionName = "21-42 days not breastfeeding";
        subCondition = new Condition(subConditionName, new Rating("3", new RealmList<>(C, cross)),
                new Rating("1"),new Rating("1"), new Rating("1"), new Rating("", aOnly),
                new Rating("", aOnly), null, null, null, null, null, null, null, parentCondition);
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

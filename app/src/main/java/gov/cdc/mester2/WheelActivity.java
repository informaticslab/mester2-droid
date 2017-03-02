package gov.cdc.mester2;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


public class WheelActivity extends AppCompatActivity {
    private final String TAG = "Wheel Activity";
    protected ActionBarDrawerToggle mDrawerToggle;
    protected DrawerLayout mDrawerLayout;
    protected NavigationView mNavigationView;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel);

        realm = Realm.getDefaultInstance();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);


        mPager = (ViewPager) findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_host);
    }

    /**
     * In case if you require to handle drawer open and close states
     */
    protected void setupActionBarDrawerToogle() {

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

    protected void setupDrawerContent(final NavigationView navigationView) {
        //setting up selected item listener
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        if (menuItem.isChecked()) mDrawerLayout.closeDrawers();
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

        }

        return super.onOptionsItemSelected(item);
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

    protected boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    protected void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        populateRealm();

        /* TODO: Use this block for production
        SharedPreferences sp = getSharedPreferences("MEC_Preferences", 0);
        if(!sp.getBoolean("conditionsAdded", false)) {
            SharedPreferences.Editor editor = sp.edit();
            populateRealm();
            editor.putBoolean("conditionsAdded", true).apply();
        }
        */


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_white);

        ab.setHomeActionContentDescription("Menu");

        ab.setDisplayHomeAsUpEnabled(true);

        setupActionBarDrawerToogle();
        if (mNavigationView != null) {
            setupDrawerContent(mNavigationView);
        }

        mPagerAdapter = new ContentPagerAdapter(this, realm);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(10);

        tabLayout.setupWithViewPager(mPager);
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

    private class ContentPagerAdapter extends PagerAdapter {
        private Context mContext;
        private RealmResults<Condition> query;

        private ArrayList<Condition> displayConditions = new ArrayList<>();

        public ContentPagerAdapter(Context context, Realm realm) {
            mContext = context;
            Log.d(TAG, "ContentPagerAdapter: started");
            query = realm.where(Condition.class).findAllSorted("name");
            for(Condition condition : query){
                Log.d(TAG, "ContentPagerAdapter: parentCondition.hasChildren(): " +(condition.getChildConditions().size() > 0));
                if(condition.getChildConditions().size() > 0) {
                    for(Condition child : condition.getChildConditions()) {
                        displayConditions.add(child);
                    }
                }
            }
            Log.d(TAG, "ContentPagerAdapter: displayConditionsSize: " +displayConditions.size());
        }

        @Override
        public Object instantiateItem(final ViewGroup container, final int position) {
            Condition currentCondition = displayConditions.get(position);
            Log.d(TAG, "instantiateItem: started");
            LayoutInflater inflater = LayoutInflater.from(mContext);

            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.condition_details_fragment, container, false);
            container.addView(layout);

            Button right = (Button) layout.findViewById(R.id.rightArrow);
            right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int next = position == displayConditions.size() -1 ? 0 : position + 1;
                    mPager.setCurrentItem(next, true);
                }
            });

            Button left = (Button) layout.findViewById(R.id.leftArrow);
            left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int prev = position == 0 ? displayConditions.size() -1 : position - 1;
                    mPager.setCurrentItem(prev, true);
                }
            });

            TextView title = (TextView) layout.findViewById(R.id.parentConditionTitle);
            title.setText(currentCondition.getParentCondition().getName());

            TextView subTitle = (TextView) layout.findViewById(R.id.subConditionTitle);
            subTitle.setText(currentCondition.getName());

            TextView chcRating = (TextView) layout.findViewById(R.id.chcRating);
            chcRating.setText(currentCondition.getChcInitiation().getRating());

            TextView popRating = (TextView) layout.findViewById(R.id.popRating);
            popRating.setText(currentCondition.getPopInitiation().getRating());

            TextView dmpaRating = (TextView) layout.findViewById(R.id.dmpaRating);
            dmpaRating.setText(currentCondition.getDmpaInitiation().getRating());

            TextView implantsRating = (TextView) layout.findViewById(R.id.implantsRating);
            implantsRating.setText(currentCondition.getImplantsInitiation().getRating());

            TextView lngiudRating = (TextView) layout.findViewById(R.id.lngiudRating);
            lngiudRating.setText(currentCondition.getLngiudInitiation().getRating());

            TextView cuiudRating = (TextView) layout.findViewById(R.id.cuiudRating);
            cuiudRating.setText(currentCondition.getCuiudInitiation().getRating());

            return layout;
        }

/*      Set toolbar title to parent condition name
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            toolbar.setTitle(displayConditions.get(position).getParentCondition().getName());
        }*/

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Condition currentCondition = displayConditions.get(position);
            return "" +currentCondition.getParentCondition().getName() +"\n" +currentCondition.getName();

        }

        @Override
        public int getCount() {
            return displayConditions.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public void o(int pageNumber) {
            // Just define a callback method in your fragment and call it like this!

        }
    }
}

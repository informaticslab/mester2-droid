package gov.cdc.mester2;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConditionsPagerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConditionsPagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConditionsPagerFragment extends Fragment {
    private final String TAG = "ConditionsPagerFragment";
    private final boolean DEBUG = true;
    private ViewPager mPager;
    private Realm realm;
    private ArrayList<Condition> displayConditions = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    public ConditionsPagerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ConditionsPagerFragment.
     */
    public static ConditionsPagerFragment newInstance() {
        return new ConditionsPagerFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        Animation animation = super.onCreateAnimation(transit, enter, nextAnim);

            if (animation == null && nextAnim != 0) {
                animation = AnimationUtils.loadAnimation(getActivity(), nextAnim);
            }

            if (animation != null) {
                getView().setLayerType(View.LAYER_TYPE_HARDWARE, null);

                animation.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationEnd(Animation animation) {
                        getView().setLayerType(View.LAYER_TYPE_NONE, null);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }

                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    // ...other AnimationListener methods go here...
                });
            }

        return animation;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        realm = Realm.getDefaultInstance();
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_conditions_pager, container, false);
        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tab_host);

        PagerAdapter mPagerAdapter = new ContentPagerAdapter(getActivity(), realm);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(10);

        tabLayout.setupWithViewPager(mPager);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();
    }

    public void setDisplayCondition(String conditionName) {
        Condition condition = realm.where(Condition.class).equalTo("name", conditionName).findFirst();
        Condition firstChild = condition.getChildConditions().first();

        if(DEBUG) {
            Log.d(TAG, "setDisplayCondition: " + conditionName);
            Log.d(TAG, "setDisplayCondition: Position of first child of " + conditionName + " is " +displayConditions.indexOf(firstChild) +": " +firstChild.getName());
        }


        mPager.setCurrentItem(displayConditions.indexOf(firstChild), true);
    }

    private class ContentPagerAdapter extends PagerAdapter {
        private Context mContext;
        private RealmResults<Condition> query;

        public ContentPagerAdapter(Context context, Realm realm) {
            mContext = context;
            if (DEBUG) {
                Log.d(TAG, "ContentPagerAdapter: started");
            }
            query = realm.where(Condition.class).findAllSorted("name");
            for(Condition condition : query){
                if(DEBUG) {
                    Log.d(TAG, "ContentPagerAdapter: parentCondition.hasChildren(): " + (condition.getChildConditions().size() > 0));
                }
                if(condition.getChildConditions().size() > 0) {
                    for(Condition child : condition.getChildConditions()) {
                        displayConditions.add(child);
                    }
                }
            }
            if(DEBUG) {
                Log.d(TAG, "ContentPagerAdapter: displayConditionsSize: " + displayConditions.size());
            }
        }

        @Override
        public Object instantiateItem(final ViewGroup container, final int position) {
            Condition currentCondition = displayConditions.get(position);
            if(DEBUG) {
                Log.d(TAG, "instantiateItem: started");
            }
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
    }
}

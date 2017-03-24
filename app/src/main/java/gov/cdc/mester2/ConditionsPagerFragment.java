package gov.cdc.mester2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static android.view.View.GONE;


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
    private final boolean DEBUG = false;
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
        mPager = null;

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
            final Condition currentCondition = displayConditions.get(position);
            if(DEBUG) {
                Log.d(TAG, "instantiateItem: started");
            }
            LayoutInflater inflater = LayoutInflater.from(mContext);

            final ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.condition_details_fragment, container, false);
            container.addView(layout);
            final TextSwitcher[] textSwitchers = setupRatingTextSwitchers(layout);

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


            boolean hasContinuation = true;
            boolean hasInitiation = true;

            RadioGroup initiationContinuationControl = (RadioGroup) layout.findViewById(R.id.initiation_continuation_control);

            if(currentCondition.getChcContinuation() == null &&
                    currentCondition.getPopContinuation() == null &&
                    currentCondition.getDmpaContinuation() == null &&
                    currentCondition.getImplantsContinuation() == null &&
                    currentCondition.getLngiudContinuation() == null &&
                    currentCondition.getCuiudContinuation() == null) {

                hasContinuation = false;

            }

            if(currentCondition.getChcInitiation() == null &&
                    currentCondition.getPopInitiation() == null &&
                    currentCondition.getDmpaInitiation() == null &&
                    currentCondition.getImplantsInitiation() == null &&
                    currentCondition.getLngiudInitiation() == null &&
                    currentCondition.getCuiudInitiation() == null) {

                hasInitiation = false;
            }

            if(!hasContinuation) {
                //Doesn't have Continuation ratings, hide continuation selector and check initiation
                initiationContinuationControl.findViewById(R.id.continuationRadioButton).setVisibility(GONE);
                initiationContinuationControl.check(R.id.initiationRadioButton);
            } else if(!hasInitiation){
                initiationContinuationControl.findViewById(R.id.initiationRadioButton).setVisibility(GONE);
                initiationContinuationControl.check(R.id.continuationRadioButton);
            } else {
                initiationContinuationControl.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                        setRatingText(textSwitchers, i, currentCondition);
                    }
                });
            }
            int ratingToDisplay = initiationContinuationControl.getCheckedRadioButtonId();


            TextView title = (TextView) layout.findViewById(R.id.parentConditionTitle);
            title.setText(currentCondition.getParentCondition().getName());

            TextView subTitle = (TextView) layout.findViewById(R.id.subConditionTitle);
            subTitle.setText(currentCondition.getName());

            setRatingText(textSwitchers, ratingToDisplay, currentCondition);

            TextView conditionNotes = (TextView) layout.findViewById(R.id.conditionNotes);
            if(currentCondition.hasNotes()) {
                conditionNotes.setVisibility(View.VISIBLE);
                conditionNotes.setText("*" + currentCondition.getNotes().first().getText());
            } else {
                conditionNotes.setVisibility(GONE);
            }

            return layout;
        }

        private TextSwitcher[] setupRatingTextSwitchers(final ViewGroup layout) {
            TextSwitcher[] ratingTextViews = {
                    (TextSwitcher) layout.findViewById(R.id.chcRating),
                    (TextSwitcher) layout.findViewById(R.id.popRating),
                    (TextSwitcher) layout.findViewById(R.id.dmpaRating),
                    (TextSwitcher) layout.findViewById(R.id.implantsRating),
                    (TextSwitcher) layout.findViewById(R.id.lngiudRating),
                    (TextSwitcher) layout.findViewById(R.id.cuiudRating)
            };

            for(int i = 0; i < ratingTextViews.length; i++) {
                final TextSwitcher textSwitcher = ratingTextViews[i];

                textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
                    @Override
                    public View makeView() {
                        LayoutInflater inflater = LayoutInflater.from(getActivity());
                        TextView t = (TextView) inflater.inflate(R.layout.rating_textview_layout, null);
                        t.setHeight(200);
                        return t;
                    }
                });

                Animation in = AnimationUtils.loadAnimation(getActivity(),
                        android.R.anim.fade_in);
                Animation out = AnimationUtils.loadAnimation(getActivity(),
                        android.R.anim.fade_out);
                textSwitcher.setInAnimation(in);
                textSwitcher.setOutAnimation(out);
            }

            return ratingTextViews;
        }

        private void setRatingText(TextSwitcher[] textSwitchers, int id, Condition condition) {
            ArrayList<Rating> ratings = new ArrayList<>();
            switch (id) {
                case R.id.initiationRadioButton:
                    ratings.add(condition.getChcInitiation());
                    ratings.add(condition.getPopInitiation());
                    ratings.add(condition.getDmpaInitiation());
                    ratings.add(condition.getImplantsInitiation());
                    ratings.add(condition.getLngiudInitiation());
                    ratings.add(condition.getCuiudInitiation());
                    break;
                case R.id.continuationRadioButton:
                    ratings.add(condition.getChcContinuation());
                    ratings.add(condition.getPopContinuation());
                    ratings.add(condition.getDmpaContinuation());
                    ratings.add(condition.getImplantsContinuation());
                    ratings.add(condition.getLngiudContinuation());
                    ratings.add(condition.getCuiudContinuation());
                    break;
                default:
                    break;
            }
            for(int i = 0; i < textSwitchers.length; i++) {
                TextSwitcher textSwitcher = textSwitchers[i];
                Rating rating = ratings.get(i);
                textSwitcher.setText(concatRatingAndNotes(rating));
                configureRatingTextView(textSwitcher, rating);
            }
        }

        private SpannableString concatRatingAndNotes(Rating currentRating) {
            if(currentRating == null) {
                return new SpannableString("");
            }
            String rating = currentRating.getRating();
            RealmList<Note> notes = currentRating.getNotes();
            String tempString = rating;
            for (Note note : notes) {
                tempString += note.getName();
            }
            SpannableString returnString = new SpannableString(tempString);
            if (rating.length() > 0) {
                returnString.setSpan(new SuperscriptSpan(), rating.length(), returnString.length(), 0);
                returnString.setSpan(new RelativeSizeSpan(0.6f), rating.length(), returnString.length(), 0);
            }
            return returnString;
        }

        private void configureRatingTextView(final TextSwitcher textSwitcher, final Rating rating) {
            if(rating == null) {
                return;
            }
            if(!rating.getNotes().isEmpty()) {
                textSwitcher.setClickable(true);
                textSwitcher.setFocusable(true);
                textSwitcher.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent noteActivityIntent = new Intent(getContext(), NoteActivity.class);
                        String notes = "";
                        for(Note note : rating.getNotes()) {
                            notes += note.getName() +"\n" +note.getText() +"\n\n";
                        }
                        noteActivityIntent.putExtra("notes", notes);
                        noteActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(noteActivityIntent);
                    }
                });
            } else {
                textSwitcher.setClickable(false);
                textSwitcher.setFocusable(false);
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Condition currentCondition = displayConditions.get(position);
            return "" +currentCondition.getParentCondition().getName() +"\n" +currentCondition.getName() +(currentCondition.hasNotes() ? "*" : "");
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

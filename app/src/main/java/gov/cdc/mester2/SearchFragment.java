package gov.cdc.mester2;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import io.realm.Case;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener {
    private RecyclerView recyclerView;
    private Realm realm;
    private SearchRecyclerAdapter adapter;
    private RealmResults<Condition> conditionQuery;

    private OnFragmentInteractionListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchFragment.
     */
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
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


        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.search_recycler_view);

        conditionQuery = realm.where(Condition.class)
                .equalTo("hasChildren", true)
                .findAllSorted("name");

        adapter = new SearchRecyclerAdapter(conditionQuery);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
/*        searchView.setIconified(false);
        MenuItemCompat.expandActionView(item);*/
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        conditionQuery.removeAllChangeListeners();
        realm.close();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        adapter.setFilter(query);
        recyclerView.scrollToPosition(0);
        return true;
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
        void onFragmentInteraction(String conditionName);
    }

        private class SearchRecyclerAdapter extends RealmRecyclerViewAdapter<Condition, SearchRecyclerAdapter.ViewHolder> {

        // Provide a direct reference to each of the views within a data item
        // Used to cache the views within the item layout for fast access
        class ViewHolder extends RecyclerView.ViewHolder {
            // Your holder should contain a member variable
            // for any view that will be set as you render a row
            TextView conditionTitle;

            // We also create a constructor that accepts the entire item row
            // and does the view lookups to find each subview
            public ViewHolder(View itemView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(itemView);

                conditionTitle = (TextView) itemView.findViewById(R.id.condition_name);
            }
        }

        public SearchRecyclerAdapter(@Nullable OrderedRealmCollection<Condition> data) {
                super(data, true);

        }

        @Override
        public SearchRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            final View conditionView = inflater.inflate(R.layout.search_list_item, parent, false);
            conditionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Condition condition = adapter.getItem(recyclerView.getChildAdapterPosition(view));
                    mListener.onFragmentInteraction(condition != null ? condition.getName() : null);
                }
            });
            return new ViewHolder(conditionView);

        }

        @Override
        public void onBindViewHolder(SearchRecyclerAdapter.ViewHolder holder, int position) {
            Condition condition = getItem(position);

            TextView textView = holder.conditionTitle;
            textView.setText(condition != null ? condition.getName() : null);
        }


        @Override
        public int getItemCount() {
            return getData().size();
        }

        void setFilter(String queryText) {
            RealmResults<Condition> query = realm.where(Condition.class)
                    .equalTo("hasChildren", true)
                    .beginsWith("name", queryText, Case.INSENSITIVE)
                    .findAllSorted("name");

            updateData(query);


            //Text view from layout that is initially set as non-visible. Visibility is changed dynamically below.
            TextView noResults = (TextView) getActivity().findViewById(R.id.no_results);
            noResults.setVisibility(query.size() == 0 ? View.VISIBLE : View.GONE);

            }
        }
    }


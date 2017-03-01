package gov.cdc.mester2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmQuery;

/**
 * Created by jason on 2/22/17.
 */

public class ConditionFragment extends Fragment {
    private Realm realm;
    private Condition condition;

    public static ConditionFragment create(String conditionID) {
        ConditionFragment fragment = new ConditionFragment();
        Bundle args = new Bundle();
        args.putString("conditionID", conditionID);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //open the realm instance
        realm = Realm.getDefaultInstance();

        String conditionID = getArguments().getString("conditionID");
        RealmQuery<Condition> query = realm.where(Condition.class);
        query.equalTo("id", conditionID);
        condition = query.findFirst();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the layout containing a title and body text
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.condition_fragment, container, false);

        ((TextView) rootView.findViewById(R.id.chcRating)).setText(condition.getChcInitiation().getRating());
        ((TextView) rootView.findViewById(R.id.popRating)).setText(condition.getPopInitiation().getRating());
        ((TextView)rootView.findViewById(R.id.dmpaRating)).setText(condition.getDmpaInitiation().getRating());
        ((TextView) rootView.findViewById(R.id.implantsRating)).setText(condition.getImplantsInitiation().getRating());
        ((TextView) rootView.findViewById(R.id.lngiudRating)).setText(condition.getLngiudInitiation().getRating());
        ((TextView)rootView.findViewById(R.id.cuiudRating)).setText(condition.getCuiudInitiation().getRating());


        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //close the realm instance
        realm.close();
    }
}
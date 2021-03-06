package com.kychow.jayjoska;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.AsyncHttpClient;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Karena Chow on 7/18/18.
 * @brief Categories Fragment implements basic category functionality and displays
 * a category RecyclerView.
 */
public class CategoriesFragment extends Fragment {

    private OnNextButtonClicked mListener; // Handles communications with activity
    private AsyncHttpClient client; // Talks to the Yelp Fusion API
    private RecyclerView mRecyclerView; // Holds the grid of Categories
    private CategoryAdapter mAdapter; // Handles the communication between RV and mCategories
    private ArrayList<String> mCategories; // Holds all the categories from the Fusion API
    @BindView(R.id.next_btn) FloatingActionButton nextBtn; // Button that takes you to the next frag
    private ArrayList<String> mSelections; // Contains the categories that the user selects
    private Bundle savedState;
    private boolean firstTime = true;


    public CategoriesFragment() {
    }

    public static CategoriesFragment newInstance() {
        CategoriesFragment fragment = new CategoriesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        ButterKnife.bind(this, view);
        mRecyclerView = view.findViewById(R.id.rvCategories);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        client = new AsyncHttpClient();
        // Provide API with API key
        client.addHeader("Authorization", "Bearer " + getString(R.string.yelp_api_key));

        if (savedState == null) {
            savedState = new Bundle();
            mCategories = new ArrayList<>(); // change to array of categories
            mAdapter = new CategoryAdapter(mCategories);
            mSelections = mAdapter.getSelection();
            mRecyclerView.setAdapter(mAdapter);
            getCategories();
        } else {
            mCategories = (ArrayList<String>) savedState.getSerializable("mCategories");
            mSelections = (ArrayList<String>) savedState.getSerializable("mSelections");
            mAdapter = new CategoryAdapter(mCategories);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setSelection(mSelections);
        }

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelections.size() == 5) {
                    mListener.sendCategories(mSelections);
                }
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        savedState.putSerializable("mCategories", mCategories);
        savedState.putSerializable("mSelections", mSelections);
        firstTime = false;
        super.onDestroyView();
    }

    /*
     * @brief getCategories fetches the categories from the Yelp api and populates the recycler view.
     * In order to get the full category name, the alias must be placed directly into the url, and not
     * passed as an argument.
     */
    public void getCategories() {
        String[] cats = MainActivity.getCategoryAliases();
        for (int i = 0; i < cats.length; i++) {
            mCategories.add(cats[i]);
            mAdapter.notifyItemInserted(i);
        }

        /*
        String url;

        // todo migrate category aliases to categories fragment
        final String[] categoryAliases = MainActivity.getCategoryAliases();
        for (int i = 0; i < categoryAliases.length; i++) {
            url = getString(R.string.base_url) + getString(R.string.categories) + categoryAliases[i];

            client.get(url, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    String alias;
                    try {
                        JSONObject category = response.getJSONObject("category");
                        alias = category.getString("alias");
                        if (Categories.getAliasAndTitle().containsKey(alias)) {
                            mCategories.add(alias);
                            mAdapter.notifyItemInserted(mCategories.size() - 1);
                            Log.i("getCategories", alias);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    if (errorResponse != null) {
                        Log.i("categoriesFragment", errorResponse.toString());
                    }
                }
            });
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        */
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (OnNextButtonClicked) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnNextButtonClicked");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnNextButtonClicked {
        void sendCategories(ArrayList<String> categories);
    }

    public boolean isFirstTime() {
        return firstTime;
    }

}

package com.CS2340.shelterapp.Controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.CS2340.shelterapp.Model.ShelterData;
import com.CS2340.shelterapp.Model.Shelters;
import com.CS2340.shelterapp.R;

import java.util.List;

import static android.support.v4.app.NavUtils.navigateUpFromSameTask;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ShelterItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 *
 * @author Farzam
 * @version 1.0
 */
public class ShelterItemListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelteritem_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        View recyclerView = findViewById(R.id.shelteritem_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        SearchView searchBar = findViewById(R.id.search_bar);
        searchBar.setQueryHint("Search Shelters");
        searchBar.setSubmitButtonEnabled(true);

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if ((query == null) || ("".equals(query))) {
                    setupRecyclerView((RecyclerView) recyclerView);
                }
                setupRecyclerView((RecyclerView) recyclerView, searchBar.getQuery());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if ((query == null) || ("".equals(query))) {
                    setupRecyclerView((RecyclerView) recyclerView);
                }
                setupRecyclerView((RecyclerView) recyclerView, searchBar.getQuery());
                return true;
            }
        });



        if (findViewById(R.id.shelteritem_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(Shelters.INSTANCE.getItems()));
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, CharSequence query) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(Shelters.INSTANCE
                .findItemsByQuery(query)));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<ShelterData> mValues;

        /**
         * Recyvler view adapter for the ListView.
         *
         * @param items the list of ShelterData items
         */
        SimpleItemRecyclerViewAdapter(List<ShelterData> items) {
            mValues = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.shelteritem_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText("" + mValues.get(position).getKey());
            holder.mContentView.setText(mValues.get(position).getName());

            holder.mView.setOnClickListener((v) -> {
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putInt(ShelterItemDetailFragment.ARG_ITEM_ID, holder.mItem.getKey());
                    ShelterItemDetailFragment fragment = new ShelterItemDetailFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.shelteritem_detail_container, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ShelterItemDetailActivity.class);
                    intent.putExtra(ShelterItemDetailFragment.ARG_ITEM_ID, holder.mItem.getKey());

                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            final View mView;
            final TextView mIdView;
            final TextView mContentView;
            ShelterData mItem;

            ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = view.findViewById(R.id.id_text);
                mContentView = view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + ";";
            }
        }
    }
}

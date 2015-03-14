package com.ataulm.wutson.discover;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ataulm.wutson.R;

public class DiscoverByGenreView extends FrameLayout {

    private Adapter adapter;
    private OnShowClickListener listener;

    public DiscoverByGenreView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DiscoverByGenreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        View.inflate(getContext(), R.layout.merge_discover_by_genre, this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.discover_by_genre_list);

        int spanCount = getResources().getInteger(R.integer.discover_by_genre_span_count);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        adapter = new Adapter(LayoutInflater.from(getContext()));
        recyclerView.setAdapter(adapter);
    }

    void update(ShowsInGenre showsInGenre, OnShowClickListener listener) {
        adapter.update(showsInGenre);
        this.listener = listener;
    }

    private class Adapter extends RecyclerView.Adapter<ShowSummaryViewHolder> {

        private final LayoutInflater layoutInflater;
        private ShowsInGenre showsInGenre;

        Adapter(LayoutInflater layoutInflater) {
            this.layoutInflater = layoutInflater;
        }

        void update(ShowsInGenre showsInGenre) {
            this.showsInGenre = showsInGenre;
            notifyDataSetChanged();
        }

        @Override
        public ShowSummaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ShowSummaryView view = (ShowSummaryView) layoutInflater.inflate(R.layout.view_discover_by_genre_item, parent, false);
            return new ShowSummaryViewHolder(view, listener);
        }

        @Override
        public void onBindViewHolder(ShowSummaryViewHolder holder, int position) {
            Show show = showsInGenre.get(position);
            holder.update(show);
        }

        @Override
        public int getItemCount() {
            return showsInGenre.size();
        }

    }

    private static class ShowSummaryViewHolder extends RecyclerView.ViewHolder {

        private final ShowSummaryView showSummaryView;
        private final OnShowClickListener listener;

        ShowSummaryViewHolder(ShowSummaryView itemView, OnShowClickListener listener) {
            super(itemView);
            this.showSummaryView = itemView;
            this.listener = listener;
        }

        void update(final Show show) {
            showSummaryView.setPoster(show.getPosterUri().toString());
            showSummaryView.setTitle(show.getName());
            showSummaryView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    listener.onClick(show);
                }

            });
        }

    }

}

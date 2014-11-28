package com.ataulm.wutson;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

class TrendingShowsViewHolder extends RecyclerView.ViewHolder {

    private final TrendingShowsItemView trendingShowsItemView;

    public TrendingShowsViewHolder(TrendingShowsItemView itemView) {
        super(itemView);
        this.trendingShowsItemView = itemView;
    }

    public void present(final Show show) {
        trendingShowsItemView.setContentDescription(show.getName());
        trendingShowsItemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), show.getName(), Toast.LENGTH_SHORT).show();
                Activity activity = (Activity) v.getContext();
                activity.startActivity(new Intent(activity, ShowActivity.class));
            }

        });
    }

}

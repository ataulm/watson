package com.ataulm.wutson.myshows;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ataulm.wutson.R;
import com.ataulm.wutson.model.Episode;
import com.ataulm.wutson.view.UpcomingEpisodeWidget;

class UpcomingEpisodeViewHolder extends EpisodesByDateItemViewHolder {

    static UpcomingEpisodeViewHolder inflate(ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.view_upcoming_episode_item, parent, false);
        return new UpcomingEpisodeViewHolder(view);
    }

    private UpcomingEpisodeViewHolder(View itemView) {
        super(itemView);
    }

    void bind(Episode episode) {
        ((UpcomingEpisodeWidget) itemView).setShowName(episode.getShowName());
        ((UpcomingEpisodeWidget) itemView).setEpisodeNumber("Season " + episode.getSeasonNumber() + " Episode " + episode.getEpisodeNumber());
        ((UpcomingEpisodeWidget) itemView).setAirDate(episode.getAirDate().toString());
        ((UpcomingEpisodeWidget) itemView).setPoster(episode.getStillPath());
    }

}
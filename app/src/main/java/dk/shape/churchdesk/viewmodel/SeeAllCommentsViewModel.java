package dk.shape.churchdesk.viewmodel;

import android.view.View;

import dk.shape.churchdesk.R;
import dk.shape.churchdesk.view.SeeAllCommentsView;
import dk.shape.library.viewmodel.ViewModel;

//
// SeeAllCommentsViewModel
// churchdesk-android
//
// Created by davidjorgensen on 12-06-2015.
// Copyright (c) 2015 SHAPE A/S. All rights reserved.
// 
public class SeeAllCommentsViewModel extends ViewModel<SeeAllCommentsView> {

    public interface SeeAllCommentsListener {
        void onSeeAllCommentsClicked();
    }

    private final SeeAllCommentsListener _listener;
    private final int _count;

    public SeeAllCommentsViewModel(SeeAllCommentsListener listener, int count) {
        _listener = listener;
        _count = count;
    }

    @Override
    public void bind(SeeAllCommentsView seeAllCommentsView) {
        seeAllCommentsView.text.setText(seeAllCommentsView.getContext().getString(R.string.message_read_more, _count));

        seeAllCommentsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _listener.onSeeAllCommentsClicked();
            }
        });
    }

}
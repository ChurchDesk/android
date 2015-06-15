package dk.shape.churchdesk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import butterknife.InjectView;
import dk.shape.churchdesk.R;

//
// SeeAllCommentsView
// churchdesk-android
//
// Created by davidjorgensen on 12-06-2015.
// Copyright (c) 2015 SHAPE A/S. All rights reserved.
// 
public class SeeAllCommentsView extends BaseFrameLayout {

    @InjectView(R.id.text)
    public TextView text;

    public SeeAllCommentsView(Context context) {
        super(context);
    }

    public SeeAllCommentsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.view_see_all_comments;
    }

}
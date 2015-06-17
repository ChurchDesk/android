package dk.shape.churchdesk.request;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.RequestBody;

import dk.shape.churchdesk.entity.Comment;
import dk.shape.churchdesk.network.ParserException;
import dk.shape.churchdesk.network.PutRequest;

import static dk.shape.churchdesk.network.RequestUtils.parse;

//
// UpdateCommentRequest
// churchdesk-android
//
// Created by davidjorgensen on 12-06-2015.
// Copyright (c) 2015 SHAPE A/S. All rights reserved.
// 
public class UpdateCommentRequest extends PutRequest<Boolean> {

    private UpdateComment _updateComment;

    public UpdateCommentRequest(Comment comment, String site) {
        super(URLUtils.getUpdateCommentUrl(comment.id, site));

        _updateComment = new UpdateComment(comment);
    }

    @NonNull
    @Override
    protected RequestBody getData() {
        String data = parse(_updateComment);
        return RequestBody.create(json, data);
    }

    @Override
    protected Boolean parseHttpResponseBody(String body) throws ParserException {
        return true;
    }

    public class UpdateComment {
        @SerializedName("body")
        private String _body;

        public UpdateComment(Comment comment) {
            _body = comment.mBody;
        }
    }

}
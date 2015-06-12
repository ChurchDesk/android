package dk.shape.churchdesk.request;

import dk.shape.churchdesk.entity.Comment;
import dk.shape.churchdesk.network.DeleteRequest;
import dk.shape.churchdesk.network.ParserException;

//
// DeleteCommentRequest
// churchdesk-android
//
// Created by davidjorgensen on 12-06-2015.
// Copyright (c) 2015 SHAPE A/S. All rights reserved.
// 
public class DeleteCommentRequest extends DeleteRequest<Boolean> {

    public DeleteCommentRequest(String siteId, Comment comment) {
        super(URLUtils.getDeleteCommentUrl(siteId, comment.id));
    }

    @Override
    protected Boolean parseHttpResponseBody(String body) throws ParserException {
        return true;
    }

}
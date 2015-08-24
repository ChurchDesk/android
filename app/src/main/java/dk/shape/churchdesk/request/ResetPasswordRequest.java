package dk.shape.churchdesk.request;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.RequestBody;

import dk.shape.churchdesk.network.ParserException;
import dk.shape.churchdesk.network.PostRequest;

import static dk.shape.churchdesk.network.RequestUtils.parse;

//
// ResetPasswordRequest
// churchdesk-android
//
// Created by davidjorgensen on 16-06-2015.
// Copyright (c) 2015 SHAPE A/S. All rights reserved.
// 
    public class ResetPasswordRequest extends PostRequest<Boolean> {

    private Data _data;

    public ResetPasswordRequest(String email) {
        super(URLUtils.getResetPasswordUrl());

        _data = new Data(email);
    }

    @NonNull
    @Override
    protected RequestBody getData() {
        String data = parse(_data);
        return RequestBody.create(json, data);
    }

    @Override
    protected Boolean parseHttpResponseBody(String body) throws ParserException {
        Boolean[] res = parse(Boolean[].class, body);
        return res[0];
    }

    public class Data {
        @SerializedName("email")
        public String _email;

        public Data(String email) {
            _email = email;
        }
    }

}
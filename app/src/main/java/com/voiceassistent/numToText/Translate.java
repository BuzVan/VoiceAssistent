package com.voiceassistent.numToText;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Translate implements Serializable {

        @SerializedName("text")
        @Expose
        public List<String> text;

    }


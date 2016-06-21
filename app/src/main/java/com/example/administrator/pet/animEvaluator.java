package com.example.administrator.pet;

import android.animation.TypeEvaluator;

public class animEvaluator implements TypeEvaluator {
    @Override
    public Object evaluate(float fraction, Object startValue, Object endValue) {
        float start = (float)startValue;
        float end = (float)endValue;
        float now ;
        now = (int)(start + fraction * (end - start));

        return now;


    }
}
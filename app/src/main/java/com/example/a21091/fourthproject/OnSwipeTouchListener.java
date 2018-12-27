package com.example.a21091.fourthproject;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class OnSwipeTouchListener implements View.OnTouchListener {

    private GestureDetector gestureDetector;

    OnSwipeTouchListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener() {

            @Override
            protected void onSwipeTop() {
                OnSwipeTouchListener.this.onSwipeTop();
            }

            @Override
            protected void onSwipeBottom() {
                OnSwipeTouchListener.this.onSwipeBottom();
            }

            @Override
            protected void onSwipeLeft() {
                OnSwipeTouchListener.this.onSwipeLeft();
            }

            @Override
            protected void onSwipeRight() {
                OnSwipeTouchListener.this.onSwipeRight();
            }

            @Override
            public void onLongPress(MotionEvent e) {
                OnSwipeTouchListener.this.onLongPress();
            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }

    protected abstract void onLongPress();
    public abstract void onSwipeRight();
    public abstract void onSwipeLeft();
    public abstract void onSwipeTop();
    public abstract void onSwipeBottom();
}
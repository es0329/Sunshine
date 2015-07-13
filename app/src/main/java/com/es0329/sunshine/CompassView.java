package com.es0329.sunshine;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Custom view to render wind direction.
 * Created by Eric Sepulvado on 7/12/15.
 */
public class CompassView extends View {

    /**
     * Code-based creation.
     *
     * @param context
     */
    public CompassView(Context context) {
        super(context);
    }

    /**
     * Resource-based creation.
     *
     * @param context
     * @param attrs
     */
    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Inflation-based creation.
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public CompassView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int DESIRED_WIDTH = 100;
        final int DESIRED_HEIGHT = 100;

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        int viewWidth;
        int viewHeight;

        if (widthSpecMode == MeasureSpec.EXACTLY) {
            viewWidth = widthSpecSize;
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            viewWidth = Math.min(DESIRED_WIDTH, widthSpecSize);
        } else {
            viewWidth = DESIRED_WIDTH;
        }

        if (heightSpecMode == MeasureSpec.EXACTLY) {
            viewHeight = heightSpecSize;
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            viewHeight = Math.min(DESIRED_HEIGHT, heightSpecSize);
        } else {
            viewHeight = DESIRED_HEIGHT;
        }

        setMeasuredDimension(viewWidth, viewHeight);
    }
}

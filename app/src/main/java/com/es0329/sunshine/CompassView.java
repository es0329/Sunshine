package com.es0329.sunshine;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

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
        initialize(context);
    }

    /**
     * Resource-based creation.
     *
     * @param context
     * @param attrs
     */
    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
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
        initialize(context);
    }

    private void initialize(Context context) {
        AccessibilityManager accessibility
                = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);

        if (accessibility.isEnabled()) {
            sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
        }
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

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        event.getText().add("Wind speed/direction.");
        return true;
    }
}

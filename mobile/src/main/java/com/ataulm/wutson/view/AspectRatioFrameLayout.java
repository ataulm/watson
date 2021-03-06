package com.ataulm.wutson.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.ataulm.wutson.R;

public class AspectRatioFrameLayout extends FrameLayout {

    private final AspectRatio aspectRatio;

    public AspectRatioFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        aspectRatio = readAspectRatioFrom(context, attrs);
    }

    private AspectRatio readAspectRatioFrom(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AspectRatioImageView);
        String aspectRatio;
        try {
            aspectRatio = typedArray.getString(R.styleable.AspectRatioImageView_aspectRatio);
        } finally {
            typedArray.recycle();
        }
        return AspectRatio.parse(aspectRatio);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        double ratio = 1f * aspectRatio.getAspectHeight() / aspectRatio.getAspectWidth();

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int desiredHeight = (int) (width * ratio + 0.5f);

        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(desiredHeight, MeasureSpec.EXACTLY));
    }

}

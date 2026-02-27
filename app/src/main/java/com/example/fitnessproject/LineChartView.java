package com.example.fitnessproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class LineChartView extends View {

    private float[] data = {};
    private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public LineChartView(Context ctx) {
        super(ctx);
        init();
    }

    public LineChartView(Context ctx, AttributeSet a) {
        super(ctx, a);
        init();
    }

    public LineChartView(Context ctx, AttributeSet a, int d) {
        super(ctx, a, d);
        init();
    }

    private void init() {
        linePaint.setColor(0xFF4CAF50);
        linePaint.setStrokeWidth(5f);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        dotPaint.setColor(0xFFFFFFFF);
        gridPaint.setColor(0x332A2A5A);
        gridPaint.setStrokeWidth(1f);
    }

    public void setData(float[] data) {
        this.data = data;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (data == null || data.length < 2) return;
        int w = getWidth(), h = getHeight();
        float maxVal = 1;
        for (float v : data) if (v > maxVal) maxVal = v;

        float padL = 20f, padR = 20f, padT = 16f, padB = 20f;
        float chartW = w - padL - padR;
        float chartH = h - padT - padB;

        for (int i = 1; i <= 4; i++) {
            float y = padT + chartH * (1 - i / 4f);
            canvas.drawLine(padL, y, w - padR, y, gridPaint);
        }

        Path path = new Path();
        for (int i = 0; i < data.length; i++) {
            float x = padL + (i / (float) (data.length - 1)) * chartW;
            float y = padT + chartH * (1 - data[i] / maxVal);
            if (i == 0) path.moveTo(x, y);
            else path.lineTo(x, y);
        }
        canvas.drawPath(path, linePaint);

        for (int i = 0; i < data.length; i++) {
            float x = padL + (i / (float) (data.length - 1)) * chartW;
            float y = padT + chartH * (1 - data[i] / maxVal);
            canvas.drawCircle(x, y, 5f, dotPaint);
        }
    }
}
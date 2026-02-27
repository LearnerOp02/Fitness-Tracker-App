package com.example.fitnessproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class BarChartView extends View {

    private float[] values = {};
    private String[] labels = {};
    private final Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint valuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public BarChartView(Context ctx) {
        super(ctx);
        init();
    }

    public BarChartView(Context ctx, AttributeSet a) {
        super(ctx, a);
        init();
    }

    public BarChartView(Context ctx, AttributeSet a, int d) {
        super(ctx, a, d);
        init();
    }

    private void init() {
        barPaint.setColor(0xFFE94560);
        labelPaint.setColor(0xFFAAAAAA);
        labelPaint.setTextSize(28f);
        labelPaint.setTextAlign(Paint.Align.CENTER);
        valuePaint.setColor(0xFFFFFFFF);
        valuePaint.setTextSize(26f);
        valuePaint.setTextAlign(Paint.Align.CENTER);
        valuePaint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
    }

    public void setData(float[] values, String[] labels) {
        this.values = values;
        this.labels = labels;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (values == null || values.length == 0) return;
        int w = getWidth(), h = getHeight();
        float maxVal = 1;
        for (float v : values) if (v > maxVal) maxVal = v;

        int n = values.length;
        float barWidth = (w * 0.6f) / n;
        float gap = (w * 0.4f) / (n + 1);
        float bottomY = h - 40f;
        float topPad = 30f;

        for (int i = 0; i < n; i++) {
            float left = gap * (i + 1) + barWidth * i;
            float right = left + barWidth;
            float barH = ((values[i] / maxVal) * (bottomY - topPad));
            float top = bottomY - barH;

            RectF rect = new RectF(left, top, right, bottomY);
            canvas.drawRoundRect(rect, 10, 10, barPaint);

            if (values[i] > 0)
                canvas.drawText((int) values[i] + "m",
                        left + barWidth / 2, top - 6, valuePaint);

            String lbl = (labels != null && i < labels.length) ? labels[i] : "";
            canvas.drawText(lbl, left + barWidth / 2, bottomY + 28, labelPaint);
        }
    }
}
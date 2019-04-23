package ygang.com.widgetlinechart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * author : ygang
 * email : 1334045135@qq.com
 * date : 2019/4/22 13:48
 **/
public class LineChart extends View {

    //坐标轴的颜色
    private int xyColor = Color.parseColor("#BCBCBC");

    //坐标轴的宽度
    private int xyWidth = 3;

    //坐标轴文字颜色
    private int xyTextColor = Color.parseColor("#336DB0");

    //坐标轴文字大小
    private int xyTextSize = 32;

    //坐标轴之间的间距
    private int interval = 30;

    //折线的颜色
    private int lineColor = Color.parseColor("#5454FF");

    //背景颜色
    private int bgColor = Color.parseColor("#ffffff");

    //原点坐标x
    private float ori_x = 80;

    //原点坐标y
    private float ori_y;

    //y轴距离底部空隙间距
    private float bottom_y = 80;

    //x的刻度值长度  默认 40
    private int xScale = 80;

    //y的刻度值
    private int yScale = 80;

    //x刻度
    private String[] xLables = {"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月", "时间",};

    //y刻度
    private String[] yLables = {"0", "20", "40", "60", "80", "100", "120", "140", "160", "180", "200", "水位"};

    //每个x轴对应的y轴值
    private String[] yValues = {"60", "40", "70", "20", "90", "120", "100", "70", "20", "50", "30", "80",};

    //x坐标轴中最远的坐标值
    private int maxX_X, maxX_Y;

    //y坐标的最远坐标
    private int minY_X, minY_Y;

    //滑动时候，上次手指的x坐标
    private float starX;

    //初始化相应的坐标值  主要进行原点，第一个坐标，以及x最大值的相关计算
    private int width = 400;

    private int height = 400;

    public LineChart(Context context) {
        this(context, null);
    }


    public LineChart(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public LineChart(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        //读取文件上面的值
       /* TypedArray array = context.obtainStyledAttributes(attributeSet, R.styleable.linchart);
        int count = array.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = array.getIndex(i);
            switch (attr) {
                case R.styleable.linchart_xylinecolor:
                    xyColor = array.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.linchart_xylinewidth:
                    xyWidth = (int) array.getDimension(attr, 5);
                    break;
                case R.styleable.linchart_xytextcolor:
                    xyTextColor = array.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.linchart_xytextsize:
                    xyTextSize = (int) array.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.linchart_linecolor:
                    lineColor = array.getColor(attr, Color.GRAY);
                    break;
                case R.styleable.linchart_bgcolor:
                    bgColor = array.getColor(attr, Color.WHITE);
                    break;
                case R.styleable.linchart_interval:
                    interval = (int) array.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 100, getResources().getDisplayMetrics()));
                    break;
            }
        }
        array.recycle();*/
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        Log.i("ygang", "lineview===onMeasure widthMeasureSpec = " + widthMeasureSpec + " heightMeasureSpec = " + heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Log.v("lineview===onMeasure", "ondraw");
        width = canvas.getWidth();
        height = canvas.getHeight();

        //原点坐标y
        ori_y = height - 80;

        //x坐标轴中最远的坐标值
        maxX_X = width;
        minY_Y = 80;

        Log.i("ygang", "lineview===onMeasure width = " + width + " height = " + height);

        drawXYLine(canvas);
        drawDataLine(canvas);
    }

    //绘制坐标轴
    private void drawXYLine(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(xyColor);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(xyWidth);
        paint.setTextSize(xyTextSize);
        //绘制x轴
        float max = ori_x + (xLables.length - 1) * xScale + 80;//80是 多出来的距离
        if (max > maxX_X) {
            max = width;
        }
        canvas.drawLine(ori_x, ori_y, max, ori_y, paint);

        //绘制y轴
        float min = ori_y - (yLables.length - 1) * yScale - 80;//80是 多出来的距离
        if (min < minY_Y) {
            min = minY_Y;
        }
        canvas.drawLine(ori_x, ori_y, ori_x, min, paint);
        //绘画x轴的刻度
        drawXLablePoints(canvas, paint);
        //绘画y轴的刻度
        drawYLablePoints(canvas, paint);
    }

    //绘画x轴的刻度
    private void drawXLablePoints(Canvas canvas, Paint paint) {
        for (int i = 0; i < xLables.length; i++) {
            canvas.drawText(xLables[i], ori_x + i * xScale, ori_y + 30, paint);
        }
    }


    //绘画y轴的刻度
    private void drawYLablePoints(Canvas canvas, Paint paint) {
        for (int i = 0; i < yLables.length; i++) {
            Rect rect = new Rect();
            paint.getTextBounds(yLables[i], 0, yLables[i].length(), rect);
            if (i < 3 || (i > 4 && i < 7)) {
                paint.setColor(Color.parseColor("#D81B60"));
            } else {
                paint.setColor(Color.parseColor("#BCBCBC"));
            }
            canvas.drawText(yLables[i], ori_x - 7 - rect.width(), height - (bottom_y + i * yScale), paint);
        }
    }

    //绘制折线图
    private void drawDataLine(Canvas canvas) {
        if (yValues != null && yValues.length > 0) {
            Paint paint = new Paint();
            paint.setColor(lineColor);
            paint.setAntiAlias(true);
            paint.setStrokeWidth(xyWidth);
            paint.setTextSize(xyTextSize);
            //80/20=4(y轴)
            for (int i = 0; i < yValues.length; i++) {
                if (i == 0) {
                    float xI = Float.parseFloat(yValues[i]);
                    canvas.drawLine(ori_x, (float) (height - (bottom_y + xI * 4)), ori_x, (float) (height - (bottom_y + xI * 4)), paint);
                } else {
                    float x1 = Float.parseFloat(yValues[i - 1]);

                    float x2 = Float.parseFloat(yValues[i]);

                    Rect rect = new Rect();
                    paint.getTextBounds(xLables[i], 0, xLables[i].length(), rect);

                    Rect rect1 = new Rect();
                    paint.getTextBounds(xLables[i - 1], 0, xLables[i - 1].length(), rect1);
                    float y1 = (float) (height - (bottom_y + x1 * 4));
                    float y2 = (float) (height - (bottom_y + x2 * 4));
                    if (y1 != 0) {
                        // -8为了y坐标的点在文字中间
                        y1 = y1 - 8;
                    }
                    if (y2 != 0) {
                        // -8为了y坐标的点在文字中间
                        y2 = y2 - 8;
                    }
                    if (i == 1) {
                        canvas.drawLine(ori_x + (i - 1) * xScale, y1, ori_x + i * xScale + rect.width() / 2, y2, paint);
                    } else {
                        canvas.drawLine(ori_x + (i - 1) * xScale + rect1.width() / 2, y1, ori_x + i * xScale + rect.width() / 2, y2, paint);
                    }

                }

            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        if (((yValues.length - 1) * xScale) < (maxX_X - ori_x)) {
//            return false;
//        }
        //事件的拖动
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                starX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float distance = event.getX() - starX;
                starX = event.getX();
//                if (ori_x + distance > width) {
//                    Log.v("tagtag", "111");
//                    ori_x = bottom_y;
//                } else if (ori_x + distance < bottom_y) {
//                    Log.v("tagtag", "222");
//                    ori_x = bottom_y;
//                } else {
                    Log.i("lineview===onMeasure", "333");
                    ori_x = (int) (ori_x + distance);
//                }
                invalidate();
                break;

        }
        return true;

    }

    public int getXyColor() {
        return xyColor;
    }

    public void setXyColor(int xyColor) {
        this.xyColor = xyColor;
    }

    public int getXyWidth() {
        return xyWidth;
    }

    public void setXyWidth(int xyWidth) {
        this.xyWidth = xyWidth;
    }

    public int getXyTextColor() {
        return xyTextColor;
    }

    public void setXyTextColor(int xyTextColor) {
        this.xyTextColor = xyTextColor;
    }

    public int getXyTextSize() {
        return xyTextSize;
    }

    public void setXyTextSize(int xyTextSize) {
        this.xyTextSize = xyTextSize;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public float getOri_x() {
        return ori_x;
    }

    public void setOri_x(int ori_x) {
        this.ori_x = ori_x;
    }

    public float getOri_y() {
        return ori_y;
    }

    public void setOri_y(int ori_y) {
        this.ori_y = ori_y;
    }

    public int getxScale() {
        return xScale;
    }

    public void setxScale(int xScale) {
        this.xScale = xScale;
    }

    public int getyScale() {
        return yScale;
    }

    public void setyScale(int yScale) {
        this.yScale = yScale;
    }

    public String[] getxLables() {
        return xLables;
    }

    public void setxLables(String[] xLables) {
        this.xLables = xLables;
    }

    public String[] getyLables() {
        return yLables;
    }

    public void setyLables(String[] yLables) {
        this.yLables = yLables;
    }

    public int getMaxX_X() {
        return maxX_X;
    }

    public void setMaxX_X(int maxX_X) {
        this.maxX_X = maxX_X;
    }

    public int getMaxX_Y() {
        return maxX_Y;
    }

    public void setMaxX_Y(int maxX_Y) {
        this.maxX_Y = maxX_Y;
    }

    public int getMinY_X() {
        return minY_X;
    }

    public void setMinY_X(int minY_X) {
        this.minY_X = minY_X;
    }

    public int getMinY_Y() {
        return minY_Y;
    }

    public void setMinY_Y(int minY_Y) {
        this.minY_Y = minY_Y;
    }

    public float getStarX() {
        return starX;
    }

    public void setStarX(float starX) {
        this.starX = starX;
    }

    public int getmWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getmHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String[] getyValues() {
        return yValues;
    }

    public void setyValues(String[] yValues) {
        this.yValues = yValues;
    }

    public float getBottom_y() {
        return bottom_y;
    }

    public void setBottom_y(float bottom_y) {
        this.bottom_y = bottom_y;
    }
}

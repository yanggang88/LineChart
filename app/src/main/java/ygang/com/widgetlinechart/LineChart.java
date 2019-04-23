package ygang.com.widgetlinechart;

import android.content.Context;
import android.content.res.TypedArray;
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

    //原点坐标最大x
    private float ori_x;

    //第一个点的坐标
    private float first_x = 50;

    //第一个点的坐标最小x，和最大x坐标
    private float ori_min_x, ori_max_x;

    //原点坐标y
    private float ori_y;

    //x的刻度值长度  默认 40
    private int xScale = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 80, getResources().getDisplayMetrics());

    //y的刻度值
    private int yScale = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 55, getResources().getDisplayMetrics());

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

    //x轴最远的坐标值
    private float x_last_x, x_last_y;

    //y轴最远的坐标值
    private float y_last_x, y_last_y;

    private double[] dataValues = {40, 80, 120, 160, 200, 140};

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
        Log.i("ygang", "lineview===onMeasure widthMeasureSpec = " + widthMeasureSpec + " heightMeasureSpec = " + heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        width = canvas.getWidth();
        height = canvas.getHeight();

        //原点坐标最大x
        ori_x = 80;
        //原点坐标y
        ori_y = height - 80;

        //x坐标轴中最远的坐标值
        maxX_X = width - 50;
        minY_Y = 50;

        //第一个点的坐标最小x，和最大x坐标
        ori_min_x = width - 50 - 40 - dataValues.length * xScale;
        first_x = ori_x;
        ori_max_x = first_x;
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
        float max = first_x + (xLables.length - 1) * xScale + 50;
        if (max > maxX_X) {
            max = getMeasuredWidth();
        }
        x_last_x = max;
        x_last_y = ori_y;
        canvas.drawLine(first_x, ori_y, max, ori_y, paint);

        //绘制y轴
        float min = ori_y - (yLables.length - 1) * yScale - 50;
        if (min < minY_Y) {
            min = minY_Y;
        }
        y_last_x = first_x;
        y_last_y = min;
        canvas.drawLine(first_x, ori_y, first_x, min, paint);
        //绘画x轴的刻度
        drawXLablePoints(canvas, paint);
        //绘画y轴的刻度
        drawYLablePoints(canvas, paint);
    }

    //绘画x轴的刻度
    private void drawXLablePoints(Canvas canvas, Paint paint) {
        for (int i = 0; i < xLables.length; i++) {
            canvas.drawText(xLables[i], first_x + i * 75, ori_y + 30, paint);
        }
    }


    //绘画y轴的刻度s
    private void drawYLablePoints(Canvas canvas, Paint paint) {
        for (int i = 0; i < yLables.length; i++) {
            Rect rect = new Rect();
            paint.getTextBounds(yLables[i], 0, yLables[i].length(), rect);
            canvas.drawText(yLables[i], first_x - 7 - rect.width(), height - (first_x + i * 75), paint);
        }
    }

    //绘制折线图
    private void drawDataLine(Canvas canvas) {
        if (yValues != null && yValues.length > 0) {
            Paint paint = new Paint();
            paint.setColor(lineColor);
            paint.setAntiAlias(true);
            paint.setStrokeWidth(xyWidth);
            //75/20=3.75(y轴)
            for (int i = 0; i < yValues.length; i++) {
                if (i == 0) {
                    float xI = Float.parseFloat(yValues[i]);
                    canvas.drawLine(first_x, (float) (height - (first_x + xI * 3.75)), first_x, (float) (height - (first_x + xI * 3.75)), paint);
                } else {
                    float x1 = Float.parseFloat(yValues[i - 1]);

                    float x2 = Float.parseFloat(yValues[i]);

                    Rect rect = new Rect();
                    paint.getTextBounds(xLables[i], 0, xLables[i].length(), rect);

                    Rect rect1 = new Rect();
                    paint.getTextBounds(xLables[i - 1], 0, xLables[i - 1].length(), rect1);
                    float y1 = (float) (height - (first_x + x1 * 3.75));
                    float y2 = (float) (height - (first_x + x2 * 3.75));
                    if (y1 != 0) {
                        // -8为了y坐标的点在文字中间
                        y1 = y1 - 8;
                    }
                    if (y2 != 0) {
                        // -8为了y坐标的点在文字中间
                        y2 = y2 - 8;
                    }
                    if (i == 1) {
                        canvas.drawLine(first_x + (i - 1) * 75, y1, first_x + i * 75 + rect.width(), y2, paint);
                        Log.i("ygang", "lineview===onMeasure 坐标 = " + first_x + (i - 1) * 75 + " ， " + first_x + i * 75 + rect.width());
                    } else {
                        canvas.drawLine(first_x + (i - 1) * 75 + rect1.width(), y1, first_x + i * 75 + rect.width(), y2, paint);
                        Log.i("ygang", "lineview===onMeasure 坐标 = " + first_x + (i - 1) * 75 + rect1.width() + " ， " + first_x + i * 75 + rect.width());
                    }

                }

            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
        //事件的拖动
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

    public float getFirst_x() {
        return first_x;
    }

    public void setFirst_x(int first_x) {
        this.first_x = first_x;
    }

    public float getOri_min_x() {
        return ori_min_x;
    }

    public void setOri_min_x(int ori_min_x) {
        this.ori_min_x = ori_min_x;
    }

    public float getOri_max_x() {
        return ori_max_x;
    }

    public void setOri_max_x(int ori_max_x) {
        this.ori_max_x = ori_max_x;
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

    public float getX_last_x() {
        return x_last_x;
    }

    public void setX_last_x(int x_last_x) {
        this.x_last_x = x_last_x;
    }

    public float getX_last_y() {
        return x_last_y;
    }

    public void setX_last_y(int x_last_y) {
        this.x_last_y = x_last_y;
    }

    public float getY_last_x() {
        return y_last_x;
    }

    public void setY_last_x(int y_last_x) {
        this.y_last_x = y_last_x;
    }

    public float getY_last_y() {
        return y_last_y;
    }

    public void setY_last_y(int y_last_y) {
        this.y_last_y = y_last_y;
    }

    public double[] getDataValues() {
        return dataValues;
    }

    public void setDataValues(double[] dataValues) {
        this.dataValues = dataValues;
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
}

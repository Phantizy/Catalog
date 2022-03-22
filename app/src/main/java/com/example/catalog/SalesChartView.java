package com.example.catalog;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class SalesChartView extends View {
    int widthMargin;
    int heightMargin;
    int topSalesNumber = 0;
    int numColumns;

    Paint plotPaint = new Paint();
    Paint gridPaint = new Paint();
    Paint borderPaint = new Paint();
    Paint labelPaint = new Paint();

    //  Sales values for the last 6 months ( would normally come from a server) *dynamic*
    int[] salesNumbers   = {15000, 22000, 3000, 33000, 5000, 45000, 20000, 70000};
    String[] salesMonths = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG"};
    int YGridTicks       = 10000;
    int POINT_RADIUS     = 12;

    public SalesChartView(Context context) {
        super(context);
    }

    public SalesChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SalesChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void onDraw(Canvas canvas){
        super.onDraw(canvas); // super is self processing

        // Prepare a draw line view to draw the chart
        this.setBackgroundColor(Color.BLACK);
        // set plot color
        plotPaint.setColor(Color.BLUE);
        plotPaint.setStrokeWidth(5);
        plotPaint.setStrokeJoin(Paint.Join.ROUND);
        plotPaint.setStrokeCap(Paint.Cap.ROUND);

        // Set the grid color
        gridPaint.setColor(Color.GREEN);

        // Set border color
        borderPaint.setColor(Color.GREEN);
        borderPaint.setStrokeJoin(Paint.Join.ROUND);
        borderPaint.setStrokeCap(Paint.Cap.ROUND);
        borderPaint.setStrokeWidth(5);

        // Set Label Colors
        labelPaint.setColor(Color.GREEN);
        labelPaint.setTextSize(30);

        // Calculate amount to use for padding -- 1/20th of the screen width and height
        widthMargin = this.getWidth() / 10;
        heightMargin = this.getHeight() / 10;

        // Calculate position of chart sides to account for padding
        float chartTop = heightMargin;
        float chartLeft = widthMargin;
        float chartRight = this.getWidth() - widthMargin;
        float chartBottom = this.getHeight() - (2* heightMargin);

        // Draw chart borders
        canvas.drawLine(chartLeft, chartBottom, chartRight, chartBottom, borderPaint);
        canvas.drawLine(chartLeft, chartTop, chartRight, chartTop, borderPaint);
        canvas.drawLine(chartLeft, chartBottom, chartLeft, chartTop, borderPaint);
        canvas.drawLine(chartRight, chartBottom, chartRight, chartTop, borderPaint);

        // How many numbers are to be shown on the chart? numbers = entries
        numColumns = salesNumbers.length;

        // Determine the top sales number
        for (int i = 0; i < numColumns; i++){
            if(topSalesNumber < salesNumbers[i]){
                topSalesNumber = salesNumbers[i];
            }
        }

        // Calculate chart dimensions and plotting increments
        float chartWidth = chartRight - chartLeft;

        // Draw horizontal grid lines for the Y axis (dollars)
        for(int yIndex = 0; yIndex < topSalesNumber; yIndex += YGridTicks){
            float gridY = getYValue(yIndex, topSalesNumber, chartTop, chartBottom );
            canvas.drawLine(chartLeft, gridY, chartRight, gridY, gridPaint);
        }

        // Draw vertical grid lines
        for (int monthIndex = 0; monthIndex < numColumns; monthIndex++){
            float colX = getXValue(monthIndex, chartLeft, chartWidth, numColumns);
            canvas.drawLine(colX, chartBottom, colX, chartTop, gridPaint);
        }

        // Draw dollar labels along the y axis
        for(int yIndex = 0; yIndex < topSalesNumber; yIndex += YGridTicks){
            float gridY = getYValue(yIndex, topSalesNumber, chartTop, chartBottom);
            String gridLabel = "$" + yIndex;
            canvas.drawText(gridLabel, 0, gridY, labelPaint);
        }

        // Plot line segments
        for(int monthIndex = 0; monthIndex < numColumns-1; monthIndex++){
            //Calculate segment X,Y values
            float startMonthX = getXValue(monthIndex, chartLeft,
                    chartWidth, numColumns);
            float startMonthY = getYValue(salesNumbers[monthIndex], topSalesNumber,
                    chartTop, chartBottom);
            float endMonthX = getXValue(monthIndex+1, chartLeft,
                    chartWidth, numColumns);
            float endMonthY = getYValue(salesNumbers[monthIndex+1],
                    topSalesNumber, chartTop, chartBottom);

            // Draw plot line from one month's value to the next month's value
            canvas.drawLine(startMonthX, startMonthY, endMonthX,endMonthY, plotPaint);

        }

        // Plot data points
        for(int monthIndex = 0; monthIndex < numColumns; monthIndex++){
            float pointX = getXValue(monthIndex, chartLeft, chartWidth, numColumns);
            float pointY = getYValue(salesNumbers[monthIndex], topSalesNumber,
                    chartTop,chartBottom);
            canvas.drawCircle(pointX, pointY, POINT_RADIUS, plotPaint);

        }

        // Draw month labels along the x axis
        for(int monthIndex = 0; monthIndex < numColumns; monthIndex++){
            String textToWrite = salesMonths[monthIndex];
            float textWidth = labelPaint.measureText(textToWrite);
            float textX = getXValue(monthIndex, chartLeft,
                    chartWidth, numColumns) - textWidth/2;
            canvas.drawText(textToWrite, textX, chartBottom + heightMargin, labelPaint);

        }
    }

    private float getYValue(int yIndex, int topSalesNumber, float chartTop, float chartBottom) {
        float chartHeight = chartBottom - chartTop;
        float scaler = chartHeight/ topSalesNumber;
        float result = chartBottom - yIndex * scaler;
        return result;
    }


    private float getXValue(int monthIndex, float chartLeft, float chartWidth, int numColumns) {
        float plotIncrementX = chartWidth / (numColumns - 1);
        float result = plotIncrementX * monthIndex + chartLeft;
        return result;
    }
}
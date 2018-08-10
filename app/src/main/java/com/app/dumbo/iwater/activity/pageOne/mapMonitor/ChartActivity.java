package com.app.dumbo.iwater.activity.pageOne.mapMonitor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.activity.superClass.WithBackActivity;
import com.app.dumbo.iwater.retrofit2.ApiService;
import com.app.dumbo.iwater.retrofit2.Retrofit2;
import com.app.dumbo.iwater.retrofit2.entity.DataReception;
import com.app.dumbo.iwater.view.MyMarkerView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dumbo on 2018/5/2
 **/

public class ChartActivity extends WithBackActivity{
    private ApiService service;

    private RelativeLayout rlDate;//日期选择
    private TextView tvDate;
    private RelativeLayout chartTypeShow, chartTypeDismiss;//图表类型
    private PopupWindow popupWindow;//图表类型选择弹窗
    private View chartTypeWindow;//图表类型选择弹窗内容
    private ScrollView svLineChart, svBarChart, svRadarChart;

    private RelativeLayout lineChart;//显示折线图
    private RelativeLayout barChart;//显示柱状图
    private RelativeLayout radarChart;//显示雷达图

    private LineChart temLineChart;//温度折线图
    private LineChart dioLineChart;//溶氧折线图
    private LineChart phLineChart;//PH折线图
    private LineChart conLineChart;//电导率折线图
    private LineChart turLineChart;//浊度折线图

    private float[] dio;//溶氧
    private float[] tur;//浊度
    private float[] ph;//PH
    private float[] con;//电导率
    private float[] tem;//温度
    private char[] gad;//水质等级

    private RecyclerView myRecyclerView;//横向24小时时间轴
    private List<String > timeList;
    private String date;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_chart);
        super.onCreate(savedInstanceState);

        //获取MonitorActivity传过来的site值
        final int site=getIntent().getIntExtra("site",0);

        //控件初始化
        initView();

        //控件监听
        listenWidget(site);

        //网络请求
        service = Retrofit2.getService();

        //RecyclerView数据源
        timeList =new ArrayList<>();
        for(int i=0;i<24;i++){
            timeList.add(i+":00");
        }

        //添加雷达图
        addRadarChart();

        //请求数据
        callMonitorData(service, site, date);

    }

    /**控件初始化*/
    public void initView(){
        //获取今年今月今日
        Calendar calendar = Calendar.getInstance();
        int thisYear = calendar.get(Calendar.YEAR);
        int thisMonth = (calendar.get(Calendar.MONTH) + 1);
        int thisDay = calendar.get(Calendar.DAY_OF_MONTH);

        rlDate=findViewById(R.id.rl_date);
        tvDate=findViewById(R.id.tv_date);
        //tvDate显示日期为当前日期
        date=thisYear +"-"+ thisMonth +"-"+ thisDay;
        tvDate.setText(date);

        chartTypeShow =findViewById(R.id.chart_type_show);
        chartTypeDismiss =findViewById(R.id.chart_type_dismiss);

        chartTypeWindow = LayoutInflater.from(ChartActivity.this).
                inflate(R.layout.chart_popup_window,null);
        lineChart = chartTypeWindow.findViewById(R.id.line_chart);
        barChart = chartTypeWindow.findViewById(R.id.bar_chart);
        radarChart = chartTypeWindow.findViewById(R.id.radar_chart);

        temLineChart=findViewById(R.id.tem_line_chart);
        dioLineChart=findViewById(R.id.dio_line_chart);
        phLineChart=findViewById(R.id.ph_line_chart);
        conLineChart=findViewById(R.id.con_line_chart);
        turLineChart=findViewById(R.id.tur_line_chart);

        svLineChart =findViewById(R.id.sv_line_chart);
        svBarChart =findViewById(R.id.sv_bar_chart);
        svRadarChart =findViewById(R.id.sv_radar_chart);

        myRecyclerView=findViewById(R.id.my_recycler_view);
    }

    /**控件监听*/
    private void listenWidget(final int site) {
        LinearLayoutManager layoutManager=new LinearLayoutManager(ChartActivity.this);
        //设置布局管理器
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);//设置横向
        myRecyclerView.setLayoutManager(layoutManager);
        //设置适配器
        MyRecyclerAdapter myAdapter=new MyRecyclerAdapter(timeList);
        myRecyclerView.setAdapter(myAdapter);
        myAdapter.setOnItemClickListener(new MyRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position){
                    case 0:
                        break;
                }
            }
        });

        rlDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(service,site);
            }
        });

        //图表类型切换按钮监听
        chartTypeShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取屏幕尺寸
                DisplayMetrics dm = getResources().getDisplayMetrics();
                int screenHeight = dm.heightPixels;
                int screenWidth = dm.widthPixels;

                popupWindow=new PopupWindow(chartTypeWindow,screenWidth/3,screenHeight/5);
                popupWindow.showAsDropDown(chartTypeShow,0,0);
                chartTypeShow.setVisibility(View.INVISIBLE);
                chartTypeDismiss.setVisibility(View.VISIBLE);
            }
        });

        //隐藏弹窗
        chartTypeDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                chartTypeShow.setVisibility(View.VISIBLE);
                chartTypeDismiss.setVisibility(View.INVISIBLE);
            }
        });

        //折线图按钮监听
        lineChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svLineChart.setVisibility(View.VISIBLE);
                svBarChart.setVisibility(View.GONE);
                svRadarChart.setVisibility(View.GONE);
                chartTypeShow.setVisibility(View.VISIBLE);
                chartTypeDismiss.setVisibility(View.INVISIBLE);
                popupWindow.dismiss();
            }
        });

        //直方图按钮监听
        barChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svLineChart.setVisibility(View.GONE);
                svBarChart.setVisibility(View.VISIBLE);
                svRadarChart.setVisibility(View.GONE);
                chartTypeShow.setVisibility(View.VISIBLE);
                chartTypeDismiss.setVisibility(View.INVISIBLE);
                popupWindow.dismiss();
            }
        });

        //雷达图按钮监听
        radarChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svLineChart.setVisibility(View.GONE);
                svBarChart.setVisibility(View.GONE);
                svRadarChart.setVisibility(View.VISIBLE);
                chartTypeShow.setVisibility(View.VISIBLE);
                chartTypeDismiss.setVisibility(View.INVISIBLE);
                popupWindow.dismiss();
            }
        });
    }

    /**请求监测数据*/
    private void callMonitorData(ApiService service, int site, String date) {
        Call<DataReception> monitorDataCall = service.getPerHourData(site,date);
        monitorDataCall.enqueue(new Callback<DataReception>() {
            @Override
            public void onResponse(@NonNull Call<DataReception> call,
                                   @NonNull Response<DataReception> response) {
                System.out.println("请求数据成功！");
                if(response.body()!=null){
                    int dataCount= Objects.requireNonNull(response.body()).getData().size();

                    if(dataCount!=0){
                        dio=new float[dataCount];
                        tur=new float[dataCount];
                        ph=new float[dataCount];
                        con=new float[dataCount];
                        tem=new float[dataCount];
                        gad=new char[dataCount];

                        for(short i=0;i<dataCount;i++){
                            //获取具体数据
                            dio[i]= Objects.requireNonNull(response.body()).getData().get(i).getDissolvedOxygen();
                            tur[i]= Objects.requireNonNull(response.body()).getData().get(i).getTurbidity();
                            ph[i]= Objects.requireNonNull(response.body()).getData().get(i).getPh();
                            con[i]= Objects.requireNonNull(response.body()).getData().get(i).getConductivity();
                            tem[i]= Objects.requireNonNull(response.body()).getData().get(i).getTemperature();
                            gad[i]= Objects.requireNonNull(response.body()).getData().get(i).getGrade();
                        }

                        //温度折线图
                        addLineChart(temLineChart,dataCount,tem,"温度（℃）",0xFFFF8286);
                        //溶氧折线图
                        addLineChart(dioLineChart,dataCount,dio,"溶氧（mg/L）",0xFF00FFFF);
                        //PH折线图
                        addLineChart(phLineChart,dataCount,ph,"PH",0xFF7BFF6B);
                        //电导率折线图
                        addLineChart(conLineChart,dataCount,con,"电导率（μS/cm）",0xFF91A3FD);
                        //浊度折线图
                        addLineChart(turLineChart,dataCount,tur,"浊度（NTU）",0xffffc06d);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<DataReception> call, @NonNull Throwable t) {
                System.out.println("请求数据失败！");
                System.out.println(t.getMessage());
            }
        });
    }

    /**添加雷达图表*/
    private void addRadarChart() {
        RadarChart myRadarChart = findViewById(R.id.my_radar_chart);

        //雷达图基本设置
        myRadarChart.getDescription().setEnabled(false);//无描述

        //X轴设置
        XAxis xAxis= myRadarChart.getXAxis();
        xAxis.setTextSize(14f);
        xAxis.setTextColor(Color.GRAY);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            String[] labels=new String[]{"温度","溶氧","PH","电导率","浊度"};  //角标
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return labels[(int) (value % labels.length)];
            }
        });

        //Y轴设置
        YAxis yAxis= myRadarChart.getYAxis();
        yAxis.setLabelCount(6,false);
        yAxis.setDrawLabels(false);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(60f);

        //图例设置
        Legend legend= myRadarChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        //数据添加
        int mult = 100;
        int min = 50;
        int cnt = 5;//几边形

        ArrayList<RadarEntry> entries1 = new ArrayList<>();
        ArrayList<RadarEntry> entries2 = new ArrayList<>();

        int[] yestodayData=new int[]{58,41,39,59,22};
        int[] todayData=new int[]{26,48,43,25,37};

        for (int i = 0; i < cnt; i++) {
            entries1.add(new RadarEntry(yestodayData[i]));
            entries2.add(new RadarEntry(todayData[i]));
        }

        RadarDataSet set1 = new RadarDataSet(entries1, "昨天");
        set1.setColor(Color.rgb(117, 255, 119));
        set1.setFillColor(Color.rgb(164, 253, 164));
        set1.setDrawFilled(true);
        set1.setFillAlpha(180);
        set1.setLineWidth(2f);
        set1.setDrawHighlightCircleEnabled(true);
        set1.setDrawHighlightIndicators(false);

        RadarDataSet set2 = new RadarDataSet(entries2, "今天");
        set2.setDrawFilled(true);
        set2.setFillAlpha(180);
        set2.setLineWidth(2f);
        set2.setDrawHighlightCircleEnabled(true);
        set2.setDrawHighlightIndicators(false);
        set2.setColor(Color.rgb(110, 255, 245));
        set2.setFillColor(Color.rgb(167, 255, 251));
        ArrayList<IRadarDataSet> sets = new ArrayList<>();
        sets.add(set1);
        sets.add(set2);

        RadarData data = new RadarData(sets);
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.WHITE);

        myRadarChart.setData(data);
        myRadarChart.invalidate();
    }

    /**添加折线图表*/
    private void addLineChart(LineChart lineChart, int dataCount, float[] value, String label, int color) {
        //图表基本属性设置
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.getDescription().setEnabled(false);  // no description text
        lineChart.setTouchEnabled(true);   // enable touch gestures
        lineChart.setDragDecelerationFrictionCoef(0.9f);
        lineChart.setDragEnabled(true);//可拖动
        lineChart.setScaleEnabled(false);
        lineChart.setScaleXEnabled(true);
        lineChart.setScaleYEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setHighlightPerDragEnabled(true);
        //lineChart.setPinchZoom(true);//X Y同时缩放
        lineChart.animateX(2500); // set an alternative background color
        MyMarkerView mv = new MyMarkerView(this, R.layout.chart_line_marker_view);
        mv.setChartView(lineChart); // For bounds control
        lineChart.setMarker(mv); // Set the marker to the chart

        //底部X轴设置
        XAxis xAxis= lineChart.getXAxis();//获得X轴对象实例
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(Color.GRAY);
        xAxis.setLabelCount(12);
        xAxis.setAxisMaximum(23f);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularityEnabled(true);//最小间隔
        xAxis.setGranularity(1f);

        //左边Y轴设置
        YAxis leftYAxis = lineChart.getAxisLeft();//获得左边Y轴对象实例
        leftYAxis.setTextColor(Color.GRAY);
        leftYAxis.setDrawGridLines(false);//是否绘制网格轴线
        leftYAxis.setGridColor(Color.LTGRAY);
        leftYAxis.setGranularityEnabled(true);

        //右边Y轴设置
        YAxis rightYAxis = lineChart.getAxisRight();//获得右边Y轴对象实例
        rightYAxis.setTextColor(Color.GRAY);
        rightYAxis.setDrawGridLines(true);
        rightYAxis.setDrawZeroLine(false);
        rightYAxis.setGranularityEnabled(true);
        rightYAxis .setGranularity(0.01f);//最小间隔

        // 图例设置
        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(14f);
        legend.setTextColor(Color.BLACK);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setDrawInside(false);

        //数据添加
        ArrayList<Entry> yVals = new ArrayList<>();
        for (int i = 0; i < dataCount; i++) {
            yVals.add(new Entry(i, value[i]));
        }

        LineDataSet lineDataSet;
        if (lineChart.getData() != null && lineChart.getData().getDataSetCount() > 0) {
            lineDataSet = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            lineDataSet.setValues(yVals);
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            lineDataSet = new LineDataSet(yVals, label);
            lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            lineDataSet.setColor(color);
            lineDataSet.setCircleColor(color);
            //lineDataSet.setForm(Legend.LegendForm.CIRCLE);//图例形状
            lineDataSet.setDrawValues(false);//显示数值
            lineDataSet.setLineWidth(2f);
            lineDataSet.setCircleRadius(3.5f);
            lineDataSet.setDrawCircleHole(true);
            lineDataSet.setFillAlpha(65);
            lineDataSet.setFillColor(Color.WHITE);
            lineDataSet.setHighLightColor(Color.rgb(244, 117, 117));
            lineDataSet.enableDashedHighlightLine(10f, 5f, 0f);

            // create a data object with the datasets
            LineData data = new LineData(lineDataSet);
            data.setValueTextColor(Color.RED);
            //data.setDrawValues(true);
            data.setValueTextSize(9f);

            // set data
            lineChart.setData(data);
        }
    }

    /**显示日期选择对话框*/
    @SuppressLint("SetTextI18n")
    private void showDatePickerDialog(final ApiService service, final int site) {
        LayoutInflater inflater=LayoutInflater.from(ChartActivity.this);
        View datePickerWindow=inflater.inflate(R.layout.window_date_picker,null);
        final DatePicker datePicker=datePickerWindow.findViewById(R.id.date_picker);
        //设置最大最小日期
        Calendar cal=Calendar.getInstance();
        cal.set(2018,5-1,1);
        datePicker.setMinDate(cal.getTimeInMillis());
        datePicker.setMaxDate(new Date().getTime());
        //日期选择对话框
        AlertDialog datePickerDialog=new AlertDialog.Builder
                (ChartActivity.this,AlertDialog.THEME_HOLO_LIGHT)
                .setView(datePickerWindow)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        date=datePicker.getYear()+"-"
                                +(datePicker.getMonth()+1)+"-"+datePicker.getDayOfMonth();
                        tvDate.setText(date);
                        callMonitorData(service, site,date);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();
        datePickerDialog.show();
    }
}

class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyHolder> {
    private List<String> dataList;
    private OnItemClickListener myItemClickListener;

    public MyRecyclerAdapter(List<String> dataList){
        this.dataList=dataList;
    }

    //item回调接口
    public interface OnItemClickListener{
        void onItemClick(View view,int position);
    }

    //定义一个设置点击监听的方法
    public void setOnItemClickListener(OnItemClickListener itemClickListener){
        this.myItemClickListener=itemClickListener;
    }

    @NonNull
    @Override
    /*重写onCreateViewHolder方法，返回一个自定义的ViewHolder*/
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_view,null);
        return new MyHolder(view);
    }

    /**填充onCreateViewHolder方法返回的holder中的控件*/
    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {
        holder.tv_time.setText(dataList.get(position));
        //如果设置了回调，则设置点击事件
        if(myItemClickListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myItemClickListener.onItemClick(holder.itemView,position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList==null ? 0 : dataList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        private TextView tv_time;

        public MyHolder(View itemView) {
            super(itemView);
            tv_time=itemView.findViewById(R.id.tv_time);
        }
    }
}



package com.android_arna;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.common.base.Preconditions;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import org.apache.commons.lang.BooleanUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.ros.address.InetAddressFactory;
import org.ros.android.BitmapFromCompressedImage;
import org.ros.android.BitmapFromImage;
import org.ros.android.MessageCallable;
import org.ros.android.view.RosImageView;
import org.ros.message.MessageListener;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import org.ros.android.RosActivity;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;
import org.apache.commons.logging.Log;
import org.ros.android.view.RosTextView;

import sensor_msgs.CameraInfo;
import sensor_msgs.CompressedImage;
import sensor_msgs.Image;
import std_msgs.String;

import static com.android_arna.R.id.btn_sensor;
import static com.android_arna.R.id.image;


public class MainActivity extends RosActivity {

    java.lang.String inp_txt;

    java.lang.String name;
    java.lang.String ros_sensor_topic;
    java.lang.String topic;

    public MainActivity() {
        super("ARNA pub_sub", "ARNA pub_sub");
    }

    private armDataPub arm_info_pub;
    private armSelector_pub arm_selector;

    public java.lang.String x,y,z;
    public java.lang.String vals;
    double a,b,c;
    int RT_graph_len=-1, x_axis_min_disp=0, x_axis_max_disp=10, vis_lim=0; //there are 2 junk values

    int i=0, arr_len=10;
    double arr1[] = new double[arr_len];
    double arr2[] = new double[arr_len];
    double arr3[] = new double[arr_len];
    // have to mention array size or give the exact data for the array ie., int arr[] = {1,2,6,4}


    private RosTextView<geometry_msgs.Vector3> sensorTxt;
    private RosImageView<sensor_msgs.CompressedImage> cam2;
    private RosImageView<sensor_msgs.Image>camImage;

    public ConnectedNode connectedNode;

    public java.lang.String node_name;
    public boolean arm_select_flag = false;

    Button button_refresh;
    Button left_arm_select_btn;
    Button right_arm_select_btn;
    Button btn_sensor;
    Button btn_stop;

    TextView sensor_name_box;
    TextView disp_box;
    TextView graph_data_box;
    TextView android_sensor_txt;

    GraphView graph_disp_1;
    GraphView graph_disp_2;
    GraphView graph_disp_3;
    GraphView RT_graph; //real-time graph

    final LineGraphSeries<DataPoint> RT_series = new LineGraphSeries<>(new DataPoint[]{});


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.baxter_arm);

        arm_info_pub = new armDataPub();
        arm_selector = new armSelector_pub();

        //Buttons
        button_refresh = (Button) findViewById(R.id.button_refresh);
        btn_sensor = (Button)findViewById(R.id.btn_sensor);
        btn_stop =(Button)findViewById(R.id.btn_stop);

        left_arm_select_btn = (Button)findViewById(R.id.left_arm_select_btn);
        right_arm_select_btn = (Button)findViewById(R.id.right_arm_select_btn);


        //Text View Boxes
        sensor_name_box = (TextView) findViewById(R.id.sensor_name_box);
        graph_data_box=(TextView)findViewById(R.id.graph_data_box);
        android_sensor_txt = (TextView)findViewById(R.id.android_sensor_txt);

        graph_disp_1 = (GraphView) findViewById(R.id.graph_disp_1);
        graph_disp_2 = (GraphView) findViewById(R.id.graph_disp_2);
        graph_disp_3 = (GraphView) findViewById(R.id.graph_disp_3);
        RT_graph = (GraphView) findViewById(R.id.RT_graph);

        //btn_popup =(Button)findViewById(R.id.btn_popup);
        /*// ~~~~~~~ pop up with Button ~~~~~~~
        btn_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, pop.class));
            }
        });
        // ~~~~~~~ pop upwith Button ~~~~~~~*/

        //final LineGraphSeries<DataPoint> RT_series = new LineGraphSeries<>(new DataPoint[]{});


        //RT_graph.addSeries(RT_series);

        //~~~~~~~~~~~~~ Baxter Arm Sensor Feed ~~~~~~~~~~~~~
        //Baxter Sensor Feed
        sensorTxt = (RosTextView<geometry_msgs.Vector3>) findViewById(R.id.sensor_txt_box);
        sensorTxt.setTopicName("sensor_info");     //default camera topic needs to be set or app crashes
        sensorTxt.setMessageType(geometry_msgs.Vector3._TYPE);

        sensorTxt.setMessageToStringCallable(new MessageCallable<java.lang.String, geometry_msgs.Vector3>() {
            @Override
            public java.lang.String call(geometry_msgs.Vector3 message) {
                x = java.lang.String.valueOf(message.getX());
                y = java.lang.String.valueOf(message.getY());
                z = java.lang.String.valueOf(message.getZ());

                //send data to graph fn
                graph_data_collection(x,y,z,RT_series);
                //return message from sensor;
                return ("X: "+x +"\n"+"Y: "+y +"\n"+"Z: "+z);

            }
        });


        //~~~~~~~~~~~~~~~~~ camera Feed ~~~~~~~~~~~~~~~~~
        cam2 = (RosImageView<sensor_msgs.CompressedImage>)findViewById(R.id.camera_image);
        cam2.setTopicName("/camera/rgb/image_color/compressed");     //default camera topic needs to be set or app crashes
        cam2.setMessageType(CompressedImage._TYPE);
        cam2.setMessageToBitmapCallable(new BitmapFromCompressedImage());


        RT_series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series RT_series, DataPointInterface dataPoint) {
                Toast.makeText(
                        MainActivity.this,
                        //getApplicationContext(),
                        //RT_graph.getContext(),
                        "RT_series: On Data Point clicked: " + dataPoint,
                        Toast.LENGTH_SHORT
                ).show();
            }
        });




        //~~~~~~~~~~~~~~~~~ Android Sensor Data ~~~~~~~~~~~~~~~~~~~~~~
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                float sx = event.values[0]; // Sensor in x-direction
                float sy = event.values[1]; // sensor in y-direction
                float sz = event.values[2]; // sensor in z-direction
                double total = Math.sqrt(sx * sx + sy * sy + sz * sz);
                java.lang.String snsr_txt = "x: "+sx+"\ny: "+sy+"\nz: "+sz;
                android_sensor_txt.setText(java.lang.String.valueOf(snsr_txt));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }

        }, sensor, SensorManager.SENSOR_DELAY_NORMAL);

    }





    public void graph_data_collection(java.lang.String inp1, java.lang.String inp2, java.lang.String inp3, LineGraphSeries<DataPoint> RT_series_1){
        a = Double.valueOf(inp1);
        b = Double.valueOf(inp2);
        c = Double.valueOf(inp3);

        RT_graph_len++;
        vis_lim++;

        RT_series_1.appendData(new DataPoint(RT_graph_len, a), true, RT_graph_len+1);
        RT_graph.addSeries(RT_series_1);



        //RT_graph.getViewport().setMaxX(x_axis_vis_len);
        RT_graph.getViewport().setMinX(x_axis_min_disp);
        RT_graph.getViewport().setMaxX(x_axis_max_disp);

        RT_graph.getViewport().setXAxisBoundsManual(true);
        RT_graph.getViewport().setScrollable(true);
        RT_graph.getViewport().setScalable(true);

        if(vis_lim > 9){
            x_axis_min_disp += 1;
            x_axis_max_disp += 1;
        }

        if(i < arr_len){
            arr1[i] = a;
            arr2[i] = b;
            arr3[i] = c;

            graph_data_box.setText(java.lang.String.valueOf(i));
            i++;
        }

        if(i==arr_len)
        {
            graph_data_assign(arr1,arr2,arr3);
            i=0;
        }
    }

    public void graph_data_assign(double dat_1[], double dat_2[], double dat_3[]){
        graph_disp_1.removeAllSeries();// clear graph_1
        graph_disp_2.removeAllSeries();// clear graph_2
        graph_disp_3.removeAllSeries();// clear graph_3
        i=0;

        LineGraphSeries<DataPoint> series1, series2, series3;

        series1 = new LineGraphSeries<DataPoint>();
        series1.setColor(Color.RED);

        series2 = new LineGraphSeries<DataPoint>();
        series2.setColor(Color.BLUE);

        series3 = new LineGraphSeries<DataPoint>();
        series3.setColor(Color.GREEN);

        vals ="";
        for(i=0; i<arr_len; i++)
        {
            series1.appendData(new DataPoint(i,dat_1[i]), true, arr_len);
            series2.appendData(new DataPoint(i,dat_2[i]), true, arr_len);
            series3.appendData(new DataPoint(i,dat_3[i]), true, arr_len);
        }


        graph_data_box.setText("Plotted !");
        graph_disp_1.setTitle("Left Accelerometer: X");
        graph_disp_2.setTitle("Left Accelerometer: Y");
        graph_disp_3.setTitle("Left Accelerometer: Z");

        graph_disp_1.addSeries(series1);
        graph_disp_2.addSeries(series2);
        graph_disp_3.addSeries(series3);

        series1.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series RT_series, DataPointInterface dataPoint) {
                Toast.makeText(
                        MainActivity.this,
                        "series1: On Data Point clicked: " + dataPoint,
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
        //graph_disp.getViewport().setScrollableY(true); // scroll through graph
    }




    public void onSensorBtnClick(View view){
        PopupMenu sensor_popup = new PopupMenu(MainActivity.this, btn_sensor);
        sensor_popup.getMenuInflater()
                .inflate(R.menu.sensor_info_pop, sensor_popup.getMenu());

        sensor_popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(
                        MainActivity.this,
                        "You Selected : " + item.getTitle(),
                        Toast.LENGTH_SHORT
                ).show();

                inp_txt = item.getTitle().toString();
                sensor_name_box.setText("Sensor Selected: \n"+inp_txt+"\nlinear_acceleration");
                arm_info_pub.itemRequest(inp_txt);
                return true;
            }
        });
        sensor_popup.show();
    }

    public void onLeftArmBtnClick(View view){
        arm_select_flag =true;
        Toast.makeText(
                MainActivity.this,
                "Left Arm Selected" , Toast.LENGTH_SHORT
        ).show();
        arm_selector.armRequest("left");
    }

    public void onRightArmBtnClick(View view){
        arm_select_flag = true;
        Toast.makeText(
                MainActivity.this,
                "Right Arm Selected" , Toast.LENGTH_SHORT
        ).show();
        arm_selector.armRequest("right");
    }

    public void onUpButtonClick(View view){
        if(arm_select_flag==true){
            Toast.makeText(
                    MainActivity.this,
                    "Move Up" , Toast.LENGTH_SHORT
            ).show();
            arm_info_pub.itemRequest("UP");
        }
        else{
            startActivity(new Intent(MainActivity.this, pop.class));
        }
    }


    public void onDownButtonClick(View view){
        if(arm_select_flag==true){
            Toast.makeText(
                    MainActivity.this,
                    "Move Down" , Toast.LENGTH_SHORT
            ).show();
            arm_info_pub.itemRequest("DOWN");
        }
        else{
            startActivity(new Intent(MainActivity.this, pop.class));
        }
    }


    public void onLeftButtonClick(View view){
        if(arm_select_flag==true){
            Toast.makeText(
                    MainActivity.this,
                    "Move Left" , Toast.LENGTH_SHORT
            ).show();
            arm_info_pub.itemRequest("LEFT");
        }
        else{
            startActivity(new Intent(MainActivity.this, pop.class));
        }
    }


    public void onRightButtonClick(View view){
        if(arm_select_flag==true){
            Toast.makeText(
                    MainActivity.this,
                    "Move Right" , Toast.LENGTH_SHORT
            ).show();
            arm_info_pub.itemRequest("RIGHT");
        }
        else{
            startActivity(new Intent(MainActivity.this, pop.class));
        }
    }

    public void onArmRestBtnClick(View view){
        if(arm_select_flag==true){
            Toast.makeText(
                    MainActivity.this,
                    "move to rest position" , Toast.LENGTH_SHORT
            ).show();
            arm_info_pub.itemRequest("REST");
        }
        else{
            startActivity(new Intent(MainActivity.this, pop.class));
        }
    }
    @Override
    public void onStart(){
        super.onStart();
        //displayMessage();

    }

    public void onStopBtnClick(View v) {

        arm_info_pub.itemRequest("STOP");
        Toast.makeText(
                MainActivity.this,
                "Stop!!", Toast.LENGTH_SHORT).show();
    }

    public void onRefreshButtonClick(View v){
        //Restart activity
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }




    @Override //Like Main Function
    protected void init(NodeMainExecutor nodeMainExecutor) {

        NodeConfiguration nodeConfiguration =
                NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress());
        nodeConfiguration.setMasterUri(getMasterUri());
        nodeMainExecutor.execute(sensorTxt, nodeConfiguration.setNodeName("sensor123")); // read Baxter Joint sensor data
        nodeMainExecutor.execute(cam2, nodeConfiguration.setNodeName("camera123")); // read Baxter Joint sensor data
        nodeMainExecutor.execute(arm_info_pub, nodeConfiguration.setNodeName("arnaApp"));
        nodeMainExecutor.execute(arm_selector, nodeConfiguration.setNodeName("arm_selector123"));
    }
}





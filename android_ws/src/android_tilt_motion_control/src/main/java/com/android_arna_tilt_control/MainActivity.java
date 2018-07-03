package com.android_arna_tilt_control;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.provider.ContactsContract;
import android.support.animation.DynamicAnimation;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.ros.address.InetAddressFactory;
import org.ros.android.MessageCallable;
import org.ros.android.RosActivity;
import org.ros.android.view.RosTextView;
import org.ros.android.view.VirtualJoystickView;

import org.ros.internal.message.RawMessage;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import java.util.Timer;
import java.util.TimerTask;

import geometry_msgs.PoseWithCovariance;
import geometry_msgs.TwistWithCovariance;
import nav_msgs.Odometry;
import sensor_msgs.JointState;
import std_msgs.Header;
import std_msgs.String;

import static android.support.animation.SpringForce.STIFFNESS_LOW;
import static android.support.animation.SpringForce.STIFFNESS_MEDIUM;
import static com.android_arna_tilt_control.R.styleable.View;

public class MainActivity extends RosActivity {

    public MainActivity() {
        super("ARNA_tilt_control", "ARNA_tilt_control");
    }

    private arm_direction_publisher arm_dir_pub;
    private arm_joint_selection_publisher arm_joint_number_pub;

    private RosTextView<std_msgs.String> arna_joint_info;
    private VirtualJoystickView vir_joystick;
    private Timer publisherTimer;
    private FrameLayout joyLayout=null; // Joystick Layout


    private FrameLayout circle = null; //test joystick area


    // Object Specification
    Button joy_btn;
    Button btn_arm_stand;
    TextView android_sensor_txt;
    TextView joy_data_box;
    TextView joint_selection_box;
    ImageView img_view_box;
    ImageView tracker;
    Button arm_joint_sel_btn;


    //variables
    java.lang.String var1_str, var2_str, a,b,c,d,e,f;
    int i=-1;
    float joy_x, joy_y, pos_x, pos_y;
    boolean joy_btn_flag;
    java.lang.String inp_txt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arm_dir_pub = new arm_direction_publisher();
        arm_joint_number_pub = new arm_joint_selection_publisher();

        android_sensor_txt = (TextView)findViewById(R.id.android_sensor_txt);
        joy_data_box = (TextView)findViewById(R.id.joy_data_box);
        joint_selection_box = (TextView)findViewById(R.id.joint_selection_box);
        img_view_box = (ImageView)findViewById(R.id.img_view_box);

        btn_arm_stand =(Button)findViewById((R.id.btn_arm_stand));
        arm_joint_sel_btn =(Button)findViewById(R.id.arm_joint_sel_btn);




        //~~~~~ Virtual Joystick ~~~~~~
        tracker = (ImageView)findViewById(R.id.tracker);
        circle = (FrameLayout)findViewById(R.id.circle);
        circle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int px =0;
                int py =0;
                pos_x =0;
                pos_y =0;
                joy_data_box.setText(java.lang.String.valueOf("X: "+pos_x+"\nY: "+pos_y));


                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    //for touch highlighter the circle
                    px = (int) event.getX()-60;
                    py = (int) event.getY()-60;
                    tracker.setX(px);
                    tracker.setY(py);

                    // Obtaining the joystick position
                    // positions modified for user comfort
                    joy_x = event.getX();
                    joy_y = event.getY();

                    pos_x = (joy_x-250)/3;
                    pos_y = (-joy_y+250)/3;
                    joy_data_box.setText(java.lang.String.valueOf("X: "+pos_x+"\nY: "+pos_y));

                    if(pos_x > 30){
                        arm_dir_pub.itemRequest("RIGHT");
                    }

                    if(pos_x < -30){
                        arm_dir_pub.itemRequest("LEFT");
                    }

                    if(pos_y > 30){
                        arm_dir_pub.itemRequest("FWD");
                    }

                    if(pos_y < -30){
                        arm_dir_pub.itemRequest("BKWD");
                    }
                }

                if(event.getAction() == MotionEvent.ACTION_UP){
                    //~~~~~~ Spring Action
                    final SpringAnimation springAnim_X = new SpringAnimation(tracker, DynamicAnimation.TRANSLATION_X, 0);
                    final SpringAnimation springAnim_Y = new SpringAnimation(tracker, DynamicAnimation.TRANSLATION_Y, 0);
                    springAnim_X.getSpring().setStiffness(STIFFNESS_MEDIUM);
                    springAnim_Y.getSpring().setStiffness(STIFFNESS_MEDIUM);
                    springAnim_X.start();
                    springAnim_Y.start();

                }
                return true;
            }
        });



        //~~~~~~~~~~~~~~~~~ ros Text Display ~~~~~~~~~~~~~~~~~~~~~~
        arna_joint_info = (RosTextView<std_msgs.String>)findViewById(R.id.arna_joint_info);
        arna_joint_info.setTopicName("data_to_tablet");
        arna_joint_info.setMessageType(std_msgs.String._TYPE);
        arna_joint_info.setMessageToStringCallable(new MessageCallable<java.lang.String, std_msgs.String>() {
            @Override
            public java.lang.String call(std_msgs.String message) {
                var1_str= message.getData();
                return var1_str;
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
                java.lang.String snsr_txt = "x: "+sx+"\ny: "+sy+"\nz: "+sz;
                android_sensor_txt.setText(java.lang.String.valueOf(snsr_txt));

                //move left (or) right
                if(sx > 3.5){
                    dir_callback("LEFT");
                }

                if(sx < -3.5){
                    dir_callback("RIGHT");
                }

                // move up (or) down
                if(sy > 9.4){
                    dir_callback("BKWD");
                }

                if(sy < -3.5){
                    dir_callback("FWD");
                }

                if(sy>0.0 && sy<9.4 && sx<3 && sx > -3 && joy_btn_flag==false){
                    // -3< sx< 3 , 0< sy< 9
                    dir_callback("TAB_HOLD");
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }

        }, sensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void dir_callback(java.lang.String direction){
        switch (direction) {

            case "LEFT":
                arm_dir_pub.itemRequest("LEFT");
                img_view_box.setImageResource(R.drawable.arrow_left);
                break;

            case "RIGHT":
                arm_dir_pub.itemRequest("RIGHT");
                img_view_box.setImageResource(R.drawable.arrow_right);
                break;

            case "FWD":
                arm_dir_pub.itemRequest("FWD");
                img_view_box.setImageResource(R.drawable.arrow_forward);
                break;

            case "BKWD":
                arm_dir_pub.itemRequest("BKWD");
                img_view_box.setImageResource(R.drawable.arrow_backward);
                break;

            case "TAB_HOLD":
                arm_dir_pub.itemRequest("TAB_HOLD");
                img_view_box.setImageResource(R.drawable.compass);
                break;

            default:
                break;
        }
    }


    public void onStandBtnClick(View view){
        arm_dir_pub.itemRequest("STAND_POS");
    }


    public  void onArmJointSelectBtnClick(View view){
        PopupMenu join_btn_popup = new PopupMenu(MainActivity.this, arm_joint_sel_btn);
        join_btn_popup.getMenuInflater()
                .inflate(R.menu.joint_selection, join_btn_popup.getMenu());

        join_btn_popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(
                        MainActivity.this,
                        "You Selected : " + item.getTitle(),
                        Toast.LENGTH_SHORT
                ).show();

                inp_txt = item.getTitle().toString();
                joint_selection_box.setText("Joint Selected: \n"+inp_txt);
                arm_joint_number_pub.jointRequest(inp_txt);
                return true;
            }
        });
        join_btn_popup.show();
    }


    @Override
    public void onStart(){
        super.onStart();
        //displayMessage();
    }


    @Override //Like Main Function
    protected void init(NodeMainExecutor nodeMainExecutor) {

        NodeConfiguration nodeConfiguration =
                NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress());
        nodeConfiguration.setMasterUri(getMasterUri());
        nodeMainExecutor.execute(arm_dir_pub, nodeConfiguration.setNodeName("arna_dir_publisher")); // execute arm_dir_pub publisher node
        nodeMainExecutor.execute(arna_joint_info, nodeConfiguration.setNodeName("arna_joint_disp_node"));
        nodeMainExecutor.execute(arm_joint_number_pub, nodeConfiguration.setNodeName("arna_joint_number_publisher")); // execute the arm_joint_Number_pub publisher node
    }
}

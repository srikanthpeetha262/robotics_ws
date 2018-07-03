package com.android_arna_tilt_control;

import org.ros.android.view.visualization.layer.DefaultLayer;
import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

import java.util.Timer;

import std_msgs.String;

/**
 * Created by ngs-baxter on 3/12/18.
 */

public class arm_joint_selection_publisher extends DefaultLayer implements NodeMain {
    private rosjava_test_msgs.AddTwoIntsRequest item_service_client;
    private Timer publisherTimer;
    private volatile boolean makeServiceCall;
    int j0_count=0, j1_count=0,j2_count=0, j_def_count=0;
    int j0_num_check=0, j1_num_check=0,j2_num_check=0, j_def_num_check=0;

    java.lang.String msg;

    public arm_joint_selection_publisher() {
        init();
    }

    void init() {

    }
    // function for SERVICE

    public void jointRequest(java.lang.String itemName) {
        switch (itemName) {
            case "J0": // Joint-0
                msg = "0";
                j0_count++;
                break;

            case "J1": // Joint-1
                msg = "1";
                j1_count++;
                break;

            case "J2": // Joint-2
                msg = "2";
                j2_count++;
                break;

            case "Default_Joints":  // Default Joints
                msg = "101";
                j_def_count++;
                break;

            default:
                break;
        }
    }

    private Thread thread;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("android_15/JointNumberPublisher");
    }

    public void onStart(final ConnectedNode connectedNode) {
        final Publisher<std_msgs.String> publisher =
                connectedNode.newPublisher("/arna_joint_selection", std_msgs.String._TYPE);

        connectedNode.executeCancellableLoop(new CancellableLoop() {

            @Override
            protected void loop() throws InterruptedException {

                if (msg== null){
                    msg  = "999";
                }
                std_msgs.String str = publisher.newMessage();
                str.setData(msg); // setting the msg

                // Publishing the Message
                if (j0_count==j0_num_check){
                    publisher.publish(str);
                    j0_num_check++;
                }

                if (j1_count==j1_num_check){
                    publisher.publish(str);
                    j1_num_check++;
                }

                if (j2_count==j2_num_check){
                    publisher.publish(str);
                    j2_num_check++;
                }


                if (j_def_count==j_def_num_check){
                    publisher.publish(str);
                    j_def_num_check++;
                }
                // Publishing the Message
                Thread.sleep(1000);
            }

        });
    }


    @Override
    public void onShutdown(Node node) {

    }

    @Override
    public void onShutdownComplete(Node node) {
        publisherTimer.cancel();
        publisherTimer.purge();
    }

    @Override
    public void onError(Node node, Throwable throwable) {

    }
}

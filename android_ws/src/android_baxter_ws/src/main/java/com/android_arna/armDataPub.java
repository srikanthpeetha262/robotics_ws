package com.android_arna;

import org.ros.android.view.visualization.layer.DefaultLayer;
import org.ros.concurrent.CancellableLoop;
import org.ros.exception.RemoteException;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.internal.message.topic.TopicDescription;
import org.ros.internal.node.server.NodeIdentifier;
import org.ros.internal.node.topic.TopicDeclaration;
import org.ros.master.client.TopicType;
import org.ros.message.MessageFactory;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;
import org.ros.node.topic.Publisher;

import java.util.Timer;
import java.util.TimerTask;

import rosjava_test_msgs.AddTwoIntsRequest;
import rosjava_test_msgs.AddTwoIntsResponse;
import std_msgs.String;


public class armDataPub extends DefaultLayer implements NodeMain {

    private rosjava_test_msgs.AddTwoIntsRequest item_service_client;
    private Timer publisherTimer;
    private volatile boolean makeServiceCall;
    int up_count=0, down_count=0,left_count=0, right_count=0,rest_count=0, left_acc_count=0, right_acc_count=0, stop_count=0;
    int up_num_check=0, down_num_check=0,left_num_check=0, right_num_check=0, rest_num_check=0,left_acc_num_check=0, right_acc_num_check=0, stop_count_num_check=0;

    java.lang.String msg;
    java.lang.String topicName ="chatter";
    int sequenceNumber=0;

    public armDataPub() {
        init();
    }

    void init() {

    }
    // function for SERVICE

    public void itemRequest(java.lang.String itemName) {
        switch (itemName) {
            case "UP":
                msg ="UP";
                up_count++;
                break;

            case "DOWN":
                msg = "DOWN";
                down_count++;
                break;

            case "LEFT":
                msg="LEFT";
                left_count++;
                break;

            case "RIGHT":
                msg="RIGHT";
                right_count++;
                break;

            case "REST":
                msg="REST";
                rest_count++;
                break;

            case "left_Accelerometer":
                msg = "left_Accelerometer";
                left_acc_count++;
                break;

            case "right_Accelerometer":
                msg ="right_Accelerometer";
                right_acc_count++;
                break;

            case "STOP":
                msg ="STOP";
                stop_count++;
                break;

            default:
                break;
        }
        makeServiceCall = true;
    }

    private boolean ranOnce;

    private Thread thread;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("android_15/NodePublisher");
    }

    public void onStart(final ConnectedNode connectedNode) {
        final Publisher<std_msgs.String> publisher =
                connectedNode.newPublisher("/chatter", std_msgs.String._TYPE);

        connectedNode.executeCancellableLoop(new CancellableLoop() {

            @Override
            protected void loop() throws InterruptedException {

                if (msg==null){
                    msg  = "Default msg";
                }
                std_msgs.String str = publisher.newMessage();
                str.setData(msg); // setting the msg

                // Publishing the Message
                if (up_count==up_num_check){
                    publisher.publish(str);
                    up_num_check++;
                }

                if (down_count==down_num_check){
                    publisher.publish(str);
                    down_num_check++;
                }

                if (left_count==left_num_check){
                    publisher.publish(str);
                    left_num_check++;
                }

                if (right_count==right_num_check){
                    publisher.publish(str);
                    right_num_check++;
                }

                if (rest_count==rest_num_check){
                    publisher.publish(str);
                    rest_num_check++;
                }


                if (left_acc_count==left_acc_num_check){
                    publisher.publish(str);
                    left_acc_num_check++;
                }

                if (right_acc_count==right_acc_num_check){
                    publisher.publish(str);
                    right_acc_num_check++;
                }

                if (stop_count==stop_count_num_check){
                    publisher.publish(str);
                    stop_count_num_check++;
                }
                // Publishing the Message

                sequenceNumber++;
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


/*

package com.android_arna;

import org.ros.android.view.visualization.layer.DefaultLayer;
import org.ros.concurrent.CancellableLoop;
import org.ros.exception.RemoteException;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;
import org.ros.node.topic.Publisher;

import java.util.Timer;
import java.util.TimerTask;

import rosjava_test_msgs.AddTwoIntsRequest;
import rosjava_test_msgs.AddTwoIntsResponse;
import std_msgs.String;



public class armDataPub extends DefaultLayer implements NodeMain {

    private rosjava_test_msgs.AddTwoIntsRequest item_service_client;
    private Timer publisherTimer;
    private volatile boolean makeServiceCall;

    java.lang.String msg;

    public armDataPub() {
        init();
    }

    void init() {

    }
    // function for SERVICE

    public void itemRequest(java.lang.String itemName) {
        switch (itemName) {
            case "UP":
                item_service_client.setItemRequest("UP");
                msg ="UP";
                break;
            case "DOWN":
                item_service_client.setItemRequest("DOWN");
                msg = "DOWN";
                break;
            case "LEFT":
                item_service_client.setItemRequest("LEFT");
                msg="LEFT";
                break;
            case "RIGHT":
                item_service_client.setItemRequest("RIGHT");
                msg="RIGHT";
                break;
            default:
                break;
        }
        makeServiceCall = true;
    }

    private boolean ranOnce;

    private Thread thread;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("android_15/NodePublisher");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        // SERVICE
        final ServiceClient<AddTwoIntsRequest, AddTwoIntsResponse> serviceClient;
        try {
            serviceClient = connectedNode.newServiceClient("add_two_ints", rosjava_test_msgs.AddTwoInts._TYPE); //service created
        } catch (org.ros.exception.ServiceNotFoundException e) {
            throw new RosRuntimeException(e);
        }

        item_service_client = serviceClient.newMessage();//Service

        publisherTimer = new Timer();
        publisherTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (makeServiceCall){
                    serviceClient.call(item_service_client, new ServiceResponseListener<AddTwoIntsResponse>(){//here i added rosjava_test_msgs
                        @Override
                        public void onSuccess(rosjava_test_msgs.AddTwoIntsResponse response) {
                        }

                        @Override
                        public void onFailure(RemoteException e) {
                        }
                    });

                }
                makeServiceCall=false;
            }
        }, 0, 15);
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


}*/
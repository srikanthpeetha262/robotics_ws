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


public class armSelector_pub extends DefaultLayer implements NodeMain {

    private Timer publisherTimer;
    private volatile boolean makeServiceCall;
    int left_count=0, right_count=0;
    int left_num_check=0, right_num_check=0;

    java.lang.String msg;
    int sequenceNumber=0;

    public armSelector_pub() {
        init();
    }

    void init() {

    }
    // function for SERVICE

    public void armRequest(java.lang.String itemName) {
        switch (itemName) {
            case "left":
                msg ="left";//+java.lang.String.valueOf(up_count);
                left_count++;
                break;

            case "right":
                msg = "right";
                right_count++;
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
        return GraphName.of("android_15/armSelector_pub");
    }

    public void onStart(final ConnectedNode connectedNode) {
        final Publisher<std_msgs.String> publisher =
                connectedNode.newPublisher("/select_baxter_arm", std_msgs.String._TYPE);

        connectedNode.executeCancellableLoop(new CancellableLoop() {

            @Override
            protected void loop() throws InterruptedException {

                if (msg==null){
                    msg  = "Default msg"; // and do not publish
                }
                std_msgs.String str = publisher.newMessage();
                str.setData(msg); // set the msg and do not publish


                if (left_count==left_num_check){
                    publisher.publish(str); // publish msg
                    left_num_check++;
                }

                if (right_count==right_num_check){
                    publisher.publish(str); //publish msg
                    right_num_check++;
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

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

public class arm_direction_publisher extends DefaultLayer implements NodeMain {

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("ARNA_tilt_control/NodePublisher");
    }

    private Timer publisherTimer;
    public arm_direction_publisher() {
        init();
    }
    void init() {}

    java.lang.String msg, ang_txt;
    int up_count=0;
    int up_num_check=0;

    public void itemRequest(java.lang.String itemName) {
        switch (itemName) {
            case "UP":
                msg = "FWD";
                break;

            case "BKWD":
                msg = "BKWD";
                break;

            case "LEFT":
                msg="LEFT";
                break;

            case "RIGHT":
                msg="RIGHT";
                break;

            case "TAB_HOLD":
                msg = "TAB_HOLD";
                break;

            case  "STAND_POS":
                msg ="STAND_POS";
                break;

            default:
                ang_txt = itemName;
                msg = "ANGLES";
                break;
        }
    }

    public void onStart(final ConnectedNode connectedNode) {
        final Publisher<String> publisher =
                connectedNode.newPublisher("/arna_arm_direction", std_msgs.String._TYPE);

        connectedNode.executeCancellableLoop(new CancellableLoop() {

            @Override
            protected void loop() throws InterruptedException {

                std_msgs.String str = publisher.newMessage();

                if (msg == null) {
                    msg = "Default msg";
                    str.setData(msg); // set the msg but do not publish
                }


                // set & publish the message
                switch (msg){
                    case "FWD":
                        str.setData(msg);
                        publisher.publish(str);
                        break;

                    case "BKWD":
                        str.setData(msg);
                        publisher.publish(str);
                        break;

                    case "LEFT":
                        str.setData(msg);
                        publisher.publish(str);
                        break;

                    case "RIGHT":
                        str.setData(msg);
                        publisher.publish(str);
                        break;

                    case "TAB_HOLD":
                        str.setData(msg);
                        publisher.publish(str);
                        break;

                    case "STAND_POS":
                        str.setData(msg);
                        publisher.publish(str);
                        break;

                    case "ANGLES":
                        str.setData(ang_txt);
                        publisher.publish(str);
                        break;

                    default:
                        break;
                }

                Thread.sleep(65);
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
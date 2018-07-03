#!/usr/bin/env python

import argparse
import struct
import sys
import baxter_interface
	
import rospy
from std_msgs.msg import String
from std_msgs.msg import Header
from sensor_msgs.msg import Imu

from geometry_msgs.msg import (
    PoseStamped,
    Pose,
    Point,
    Quaternion,
    Vector3,
)


def get_sensorData(msg):
	global sub_once		
	sensor_data = msg.linear_acceleration
	pub = rospy.Publisher("sensor_info", Vector3, queue_size=100)

	pub.publish(sensor_data)
	print sensor_data,"\n"
	rospy.sleep(1)
	
	
	#sub_once.unregister() # Unregister from the sensor topic
	return 0
	


def get_input(info):
	# Read Accelerometer selected
	global sub_once
	
	new_msg = info.data
	
	if(new_msg == "left_Accelerometer"):
		
		print "left Acc selected"
		sub_once = rospy.Subscriber("robot/accelerometer/left_accelerometer/state", Imu, get_sensorData)
		#rospy.sleep(10.0)
		return 0
	
	elif(new_msg == "right_Accelerometer"):
		print "right Acc selected"
		sub_once = rospy.Subscriber("robot/accelerometer/right_accelerometer/state", Imu, get_sensorData)
		return 0
	
	if(new_msg=="STOP"):
		print "Stopping Sensor Data Retrieval"
		rospy.signal_shutdown("kill code issued")
	
	return 0;
	
	

if __name__ == '__main__':
	rospy.init_node("bax_sensor_pub")
	rospy.Subscriber("chatter", String, get_input)
	r = rospy.Rate(100000) #
	t = rospy.Time(10) #10 seconds
	r.sleep()
	rospy.spin()
	#ik_test("left")



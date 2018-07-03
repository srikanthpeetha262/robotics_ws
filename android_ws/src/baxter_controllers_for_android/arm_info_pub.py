#!/usr/bin/env python

import rospy

from std_msgs.msg import String
from std_msgs.msg import Float64MultiArray
from sensor_msgs.msg import JointState

def get_input(info):
	msg = ""
	
	new_msg = info.position
	pub = rospy.Publisher("data_to_tablet", String, queue_size=100)
	
	lim = len(new_msg);
	for i in range(0,lim):
		msg = msg+str(new_msg[i])+", "
	
	pub.publish(msg)
	print "sent.."
	rospy.sleep(1)
	return 0;
	
	

if __name__ == '__main__':
	rospy.init_node("publish_to_tab")
	rospy.Subscriber("info_from_arna", JointState, get_input)
	r = rospy.Rate(100000) #
	t = rospy.Time(10) #10 seconds
	r.sleep()
	rospy.spin()
	#ik_test("left")


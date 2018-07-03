#!/usr/bin/env python
import rospy
import copy
from geometry_msgs.msg import Twist
from sensor_msgs.msg import Joy

pub = rospy.Publisher('phy_joy_topic', Joy, queue_size=10)
zeros_pub = rospy.Publisher('phy_joy_zeros', Joy, queue_size=10)

pub_msg = Joy()
zeros_msg = Joy()

joy_msgs = []
joy_btns = []

zeros_msg.axes = [0.0,0.0,0.0,0.0,0.0,0.0]
zeros_msg.buttons = [0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0]


def zeros_callback(z_data):
	global pub_msg
	pub_msg.axes = copy.deepcopy(z_data.axes)
	pub_msg.buttons = copy.deepcopy(z_data.buttons)


# Receives joystick messages (subscribed to Joy topic)
def joy_callback(data):
	global pub_msg, joy_msgs, joy_btns
	
	# delete previous messages
	del joy_msgs[:]
	del joy_btns[:]
	
	# save new msgs in the list
	for i in data.axes:
		joy_msgs.append(i)
	
	for j in data.buttons:
		joy_btns.append(j)
	
	#joy_msgs[0],[1],[2] -> x,y,Theta, range: [-1,1]
	joy_msgs[0] = (-1)*joy_msgs[0] # set info as per coordinate axis
	joy_msgs[2] = (-1)*joy_msgs[2] # set info as per coordinate axis
	
	# Modifying range as per motor control requirements
	joy_msgs[0] = joy_msgs[0] * 127
	joy_msgs[1] = joy_msgs[1] * 127
	joy_msgs[2] = joy_msgs[2] * 127

	
	pub_msg.axes = copy.deepcopy(joy_msgs)
	pub_msg.buttons = copy.deepcopy(joy_btns)


def msg_sender():
	global pub
	pub.publish(pub_msg)
	print pub_msg



# Intializes everything
if __name__ == '__main__':
	global pub_msg, pub, zeros_pub, zeros_msg
	rospy.init_node('phy_joy_node')
	rate = rospy.Rate(10)

	while not rospy.is_shutdown():
		zeros_pub.publish(zeros_msg)
		if not (rospy.Subscriber("joy", Joy, joy_callback) ):
			rospy.Subscriber("phy_joy_zeros", Joy, zeros_callback)
		
		msg_sender()
		rate.sleep()
	
	

	
	
	

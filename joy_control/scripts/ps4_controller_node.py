#!/usr/bin/env python
import rospy
from geometry_msgs.msg import Twist
from sensor_msgs.msg import Joy

# Receives joystick messages (subscribed to Joy topic)
def callback(data):
	joy_msgs = data.axes
	joy_btns = data.buttons
	
	print "\nJoy stick and left button controller msgs:\n", joy_msgs
	# These messages are from 2 joysticks(L,R) and left top controller buttons

	print "Messages from buttons:\n", joy_btns
	# These messages are from right top buttons and L1,L2,R1,R2



# Intializes everything
def start():
	# publishing to "turtle1/cmd_vel" to control turtle1
	global pub
	pub = rospy.Publisher('turtle1/cmd_vel', Joy, queue_size=10)
	# subscribed to joystick inputs on topic "joy"
	rospy.Subscriber("joy", Joy, callback)
	# starts the node
	rospy.init_node('Joy2Turtle')
	rospy.spin()

if __name__ == '__main__':
	start()

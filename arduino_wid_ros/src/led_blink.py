#!/usr/bin/env python

''' * Author: Srikanth Peetha
    * Created on: July 10, 2018 '''

import pyfirmata
import time

import rospy
from std_msgs.msg import String

print "Starting.."

board = pyfirmata.Arduino('/dev/ttyUSB0')
led_in = board.get_pin('d:5:o')
led_out = board.get_pin('d:7:o')
print board

def callback(msg):
	global led_in, led_out
	print msg.data
	
	if(msg.data.lower() == "led_on"):
		led_in.write(1)
		led_out.write(0)

	elif(msg.data.lower() == "led_off"):
		led_in.write(0)
		led_out.write(0)
		
	else:
		print "invalid input"


def blinker():
	rospy.init_node('led_blinker', anonymous=True)
	rospy.Subscriber("led_blink_input", String, callback)
	rospy.spin()

if __name__ == '__main__':
	try:
		blinker()
	except rospy.ROSInterruptException:
		pass






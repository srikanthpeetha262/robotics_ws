#!/usr/bin/env python
import rospy
import cv2
import random
from cv_bridge import CvBridge
from sensor_msgs.msg import Image
from std_msgs.msg import String

img_folder_path = '/home/sri/robo_faces/src/face_expressions/scripts/faces/'
pub = rospy.Publisher("/face_expression", Image, queue_size=10)

def display_pictures(msg):
	inp_msg = str(msg.data)
	global img_folder_path
	global pub
	check = 10
	new_input = True
	cmd_flag = False
	bridge = CvBridge()

	
	if(inp_msg == "neutral"):
		cmd_flag = True
		img_path = img_folder_path + 'expressions/neutral.JPG'
		img = cv2.imread(img_path)
		
	if(inp_msg == "happy"):
		cmd_flag = True
		img_path = img_folder_path + 'expressions/happy.JPG'
		img = cv2.imread(img_path)
	
	if(inp_msg == "sad"):
		cmd_flag = True
		img_path = img_folder_path +'expressions/sad.JPG'
		img = cv2.imread(img_path)
	
	if(inp_msg == "afraid"):
		cmd_flag = True
		img_path = img_folder_path +'expressions/afraid.JPG'
		img = cv2.imread(img_path)
	
	if(inp_msg == "angry"):
		cmd_flag = True
		img_path = img_folder_path + 'expressions/angry.JPG'
		img = cv2.imread(img_path)
		
	if(inp_msg == "disgusted"):
		cmd_flag = True
		img_path = img_folder_path + 'expressions/disgusted.JPG'
		img = cv2.imread(img_path)
	
	if(inp_msg == "surprise"):
		cmd_flag = True
		img_path = img_folder_path + 'expressions/surprise.JPG'
		img = cv2.imread(img_path)

	if(inp_msg == "blink"):
		print inp_msg
		for i in range(1,12):
			path = img_folder_path + 'blink/Slide' + str(i)+'.JPG'
			img = cv2.imread(path)
			msg = bridge.cv2_to_imgmsg(img, encoding="bgr8")
			pub.publish(msg)
			rospy.sleep(0.2)

	
	if (cmd_flag == True):
		print inp_msg
		msg = bridge.cv2_to_imgmsg(img, encoding="bgr8")
		pub.publish(msg)
		new_input = False
		check = 0


		

	return 0

if __name__ =='__main__':
	rospy.init_node('expression_pub')
	#display_pictures()
	rospy.Subscriber("chatter", String, display_pictures)
	print "Starting..."
	rospy.spin()
	
# Viewing Image command
# rosrun image_view image_view image:=</topic_name>

'''
import numpy as np
import cv2

# Load an color image in grayscale
#img = cv2.imread('eye_blink.png',0)
img = cv2.imread('robot.jpg')
cv2.imshow('image',img)
cv2.waitKey(0)
# TKINTER -- GUI for python
'''

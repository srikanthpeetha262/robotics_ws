#!/usr/bin/env python
import rospy
import cv2
import random
import pygame

from cv_bridge import CvBridge
from sensor_msgs.msg import Image
from std_msgs.msg import String

# declaring the publisher
#pub = rospy.Publisher("/face_expression", Image, queue_size=10)

# setting image resources
img_folder_path = '/home/sri/arna_faces/src/face_expressions/scripts/faces/'
disp_height = 3200
disp_width = 1800
msg_count = 0

pygame.init()
screen = pygame.display.set_mode((disp_height, disp_width))
screen.fill((255,255,255))

def display_pictures(msg):
	inp_msg = str(msg.data)
	global pub, img_folder_path, msg_count
	global disp_height, disp_width
	crashed = False

	
	cmd_flag = False
	bridge = CvBridge()

	
	if(inp_msg == "neutral"):
		cmd_flag = True
		img_path = img_folder_path + 'expressions/neutral.JPG'

	if(inp_msg == "happy"):
		cmd_flag = True
		img_path = img_folder_path + 'expressions/happy.JPG'
	
	if(inp_msg == "sad"):
		cmd_flag = True
		img_path = img_folder_path +'expressions/sad.JPG'
	
	if(inp_msg == "afraid"):
		cmd_flag = True
		img_path = img_folder_path +'expressions/afraid.JPG'
	
	if(inp_msg == "angry"):
		cmd_flag = True
		img_path = img_folder_path + 'expressions/angry.JPG'
		
	if(inp_msg == "disgusted"):
		cmd_flag = True
		img_path = img_folder_path + 'expressions/disgusted.JPG'
	
	if(inp_msg == "surprise"):
		cmd_flag = True
		img_path = img_folder_path + 'expressions/surprise.JPG'

	if(inp_msg == "blink"):
		print inp_msg
		for i in range(1,12):
			path = img_folder_path + 'blink/Slide' + str(i)+'.JPG'

			rospy.sleep(0.2)

	
	if (cmd_flag == True):
		print inp_msg
		while not crashed:
			img = pygame.image.load(img_path)
			img = pygame.transform.scale(img, (disp_height, disp_width))
			screen.blit(img, (0,0))
			pygame.display.update()
			#pygame.display.flip()
			cmd_flag = False
			del img_path
			for event in pygame.event.get():
				if event.type == pygame.QUIT:
					crashed = True



	return 0

if __name__ =='__main__':
	rospy.init_node('expression_pub')
	rospy.Subscriber("chatter", String, display_pictures)
	print "Starting..."
	rospy.spin()
	if rospy.is_shutdown():
		pygame.quit()
	

# Viewing Image command
# rosrun image_view image_view image:=</topic_name>













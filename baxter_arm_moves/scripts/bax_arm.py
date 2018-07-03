#!/usr/bin/env python

import argparse
import struct
import sys
import baxter_interface

import rospy
from std_msgs.msg import String
from std_msgs.msg import Header

from geometry_msgs.msg import (
    PoseStamped,
    Pose,
    Point,
    Quaternion,
)

from baxter_core_msgs.srv import (
    SolvePositionIK,
    SolvePositionIKRequest,
)
count=0;
wing=""


def move_to_rest_position(limb,count):
	arm_1 = baxter_interface.Limb(limb)	
	#set the return postion of right arm
	if(limb == 'right'):
		rest_pos_names = ['right_w0', 'right_w1', 'right_w2', 'right_e0', 'right_e1', 'right_s0', 'right_s1']
		
		rest_pos_angles = [0.15339807878854136, -0.008820389530341128, -0.007669903939427069,-0.014956312681882784, 1.4254516471425207, -0.05062136600021865, 0.05790777474267437]
		
		rest_pos = dict(zip(rest_pos_names, rest_pos_angles))


	#set the return postion of left arm		
	if(limb == 'left'):
		rest_pos_names = ['left_w0', 'left_w1', 'left_w2', 'left_e0', 'left_e1', 'left_s0', 'left_s1']
		rest_pos_angles = [-0.04256796686382023, 0.029529130166794215, 0.09817477042466648, 0.07401457301547121, 1.505985638506505,  -0.07401457301547121, 0.05483981316690354]
		rest_pos = dict(zip(rest_pos_names, rest_pos_angles))

	# Move the arm to resting position
	print ("\nmoving the Arm to resting position")
	arm_1.move_to_joint_positions(rest_pos)
	#count += 1;
	return 0;





'''
	>>>>>> MOVE UP <<<<<<
'''
def move_up_fn(limb, dict1, e1_up_max, s1_up_max, inc, upFlag, arm):
	# ~~~~~~~~~ Left Arm ~~~~~~~~~~~
	if (limb=="left"):
	
		if(dict1['left_e1'] != e1_up_max): # e1 not at max pos upwards
			dict1['left_e1'] -= inc

		else:
			print "Max Upward Limit Reached !"
			upFlag = True;
			
		if(upFlag == False and limb=="left"):
			arm.move_to_joint_positions(dict1)


	upFlag = False # Transfer from left -> right
	
	# ~~~~~~~~~ Right Arm ~~~~~~~~~~~
	if(limb=="right"):
		
		if(dict1['right_e1'] != e1_up_max): # e1 not at max pos upwards
			dict1['right_e1'] -= inc

		else:
			print "Max Upward Limit Reached !"
			upFlag = True;
	
		if(upFlag == False and limb=="right"):
			arm.move_to_joint_positions(dict1)








'''
	>>>>>> MOVE DOWN <<<<<<
'''
def move_down_fn(limb, dict1, e1_up_max, e1_down_max, s1_down_max, inc, upFlag, arm):
	if (limb=="left"):
		# ~~~~~~~~~ Left Arm ~~~~~~~~~~~
		if(dict1['left_e1'] == e1_up_max):
			dict1['left_e1'] += inc
		else:
			if(dict1['left_s1'] != s1_down_max):
				dict1['left_s1'] += inc
			else:
				print "Max Downward Limit Reached !"
	
	if(upFlag == False):
		arm.move_to_joint_positions(dict1)


	upFlag = False # Transfer from left -> right
	
	if(limb=="right"):
	# ~~~~~~~~~ Right Arm ~~~~~~~~~~~
		if(dict1['right_e1'] == e1_up_max):
			dict1['right_e1'] += inc
		else:
			if(dict1['right_s1'] != s1_down_max):
				dict1['right_s1'] += inc
			else:
				print "Max Downward Limit Reached !"
	
	if(upFlag == False):
		arm.move_to_joint_positions(dict1)






'''
	>>>>>> MOVE LEFT <<<<<<
'''
def move_left_fn(limb, dict1, s0_left_max, inc, upFlag, arm):
	if (limb=="left"):
		# ~~~~~~~~~ Left Arm ~~~~~~~~~~~
		if(dict1['left_s0'] != s0_left_max):
			dict1['left_s0'] -= inc
		else:
			print "Max Left limit Reached"
			upFlag = True

	if(upFlag == False):
		arm.move_to_joint_positions(dict1)

	upFlag = False # Transfer from left -> right
	
	if(limb=="right"):
	# ~~~~~~~~~ Right Arm ~~~~~~~~~~~
		if(dict1['right_s0'] != s0_left_max):
			dict1['right_s0'] -= inc
		else:
			print "Max Left limit Reached"
			upFlag = True

	if(upFlag == False):
		arm.move_to_joint_positions(dict1)






'''
	>>>>>> MOVE RIGHT <<<<<<
'''
def move_right_fn(limb, dict1, s0_right_max, inc, upFlag, arm):
	if (limb=="left"):
		# ~~~~~~~~~ Left Arm ~~~~~~~~~~~
		if(dict1['left_s0'] != s0_right_max):
			dict1['left_s0'] += inc
		else:
			print "Max Right limit Reached"
			upFlag = True

	if(upFlag == False):
		arm.move_to_joint_positions(dict1)

	upFlag = False # Transfer from left -> right
	
	if(limb=="right"):
	# ~~~~~~~~~ Right Arm ~~~~~~~~~~~
		if(dict1['right_s0'] != s0_right_max):
			dict1['right_s0'] += inc
		else:
			print "Max Right limit Reached"
			upFlag = True

	if(upFlag == False):
		arm.move_to_joint_positions(dict1)



	








def ik_test(msg2):
	global wing
	global count
	#limb = wing.data # check the data coming here
	
	if(wing == "left"):
		limb="left"
	elif(wing == "right"):
		limb="right"
		
	
	print limb
	msg=msg2.data
	print msg
	
	if(count==0):
		move_to_rest_position(limb,count) # Only on Start of this script, move the arm to resting position
		count += 1
	
	
	if(limb == 'right'):
			names = ['right_s0', 'right_s1','right_w0', 'right_w1', 'right_w2', 'right_e0', 'right_e1']
	
	if(limb == 'left'):
			names = ['left_w0', 'left_w1', 'left_w2', 'left_e0', 'left_e1', 'left_s0', 'left_s1']


	arm = baxter_interface.Limb(limb) # arm = specified arm	
	d = arm.joint_angles() # get the current joint angle dict
	angles = d.values() # list of the arm angles
	
	current_pos = dict(zip(names, angles))
	
	# Joint angle limits
	e1_up_max = 1.47
	e1_down_max = -0.052
	
	s1_up_max = 1.047
	s1_down_max = -2.417
	
	s0_left_max = 0.890
	s0_right_max = -2.461
	
	#increment/decrement value
	inc = 0.3
	
	if(msg == "STOP"):
		print "Stopping Arm Control Node.."
		rospy.signal_shutdown("kill code issued")
		
	elif(msg == "REST"):
		move_to_rest_position(limb,count)
	
	elif(msg == "UP"):
		upFlag = False
		return move_up_fn(limb, current_pos, e1_up_max, s1_up_max, inc, upFlag, arm)

	elif(msg == "DOWN"):
		upFlag = False
		return move_down_fn(limb, current_pos, e1_up_max, e1_down_max, s1_down_max, inc, upFlag, arm)
	

	elif(msg == "LEFT"):
		upFlag = False
		return move_left_fn(limb, current_pos, s0_left_max, inc, upFlag, arm)
	

	elif(msg == "RIGHT"):
		upFlag = False
		return move_right_fn(limb, current_pos, s0_right_max, inc, upFlag, arm)
	
	else:
		return 0;#get out of the function

	return 0




def get_input(info):
	# Get arm 
	global wing

	#wing = rospy.wait_for_message("select_baxter_arm", String)
	wing = info.data
	#rospy.Subscriber("chatter", String, ik_test)

	
	
if __name__ == '__main__':
	rospy.init_node("bax_arm_movement")
	count=0;
	
	rospy.Subscriber("select_baxter_arm", String, get_input)
	#wing = rospy.wait_for_message("select_baxter_arm", String)
	#print "wing change: ", wing
	rospy.Subscriber("chatter", String, ik_test)

	r = rospy.Rate(1000) #10hz
	r.sleep()
	rospy.spin()
	
	#ik_test("left")


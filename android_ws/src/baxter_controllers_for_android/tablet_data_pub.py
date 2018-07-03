#!/usr/bin/env python
import copy
import rospy

from std_msgs.msg import String
from sensor_msgs.msg import JointState

arnaJoint = JointState()
current_angles = JointState()

#Set Default Joint Angles
def get_current_angles(msg):
	current_angles.position = msg.position
	#print arnaJoint.position
	#print "~~~~~~"



def get_input(info):
	count = 0		
	global arnaJoint	
	data_pub = rospy.Publisher("/arna_arm_joint", JointState, queue_size=10)
	
	#~~~ get the current angles from the arm
	del arnaJoint.position[:]
	arnaJoint.position.append(current_angles.position[0]) 			#[0]
	arnaJoint.position.append(current_angles.position[1]) 			#[1]
	arnaJoint.position.append(current_angles.position[2])			#[2]
	
	
	#~~~
	#for left & right commands
	# Left  = -ve axis
	# right = +ve axis

	#~~~
	#for up & down commands
	# up   = +ve axis
	# down = -ve axis

	if (info.data == "TAB_HOLD"):
		msg = "Tablet in holding position"	
	
	elif (info.data == "RIGHT"):
		msg = "RIGHT"
		diff = -0.1
		arnaJoint.position[0] += diff # change the joint[0] angle
		data_pub.publish(arnaJoint) # publish the joint data
		print arnaJoint

	
	elif (info.data == "LEFT"):
		msg = "LEFT"
		diff = +0.1 # set the change in angle value
		arnaJoint.position[0] += diff # change the joint[0] angle
		data_pub.publish(arnaJoint) # publish the joint data
		print arnaJoint


	elif (info.data == "BKWD"):
		msg = "BKWD"
		diff = -0.05 # set the change in angle value
		
		if(arnaJoint.position[1] > 0.6):
			arnaJoint.position[1] += diff # change the joint[1] angle
			data_pub.publish(arnaJoint) # publish the joint data
			print arnaJoint
		else:
			print "max Backward limit reached"


	elif (info.data == "FWD"):
		msg = "FWD"
		diff = +0.05 # set the change in angle value
		
		if(arnaJoint.position[1] < 2.5):
			arnaJoint.position[1] += diff # change the joint[1] angle
			data_pub.publish(arnaJoint) # publish the joint data 
			print arnaJoint
		else:
			print "max Forward limit reached"

	
	elif(info.data == "STAND_POS"):
		del arnaJoint.position[:]
		arnaJoint.position.append(0) 			#[0]
		arnaJoint.position.append(1.6) 			#[1]
		arnaJoint.position.append(-0.0201942)	#[2]

		msg = "Moving the arm to Standing position"
		print arnaJoint
	

	else:
		msg = "not a valid input"
		
	print "\n"+ "Direction: " + msg 
	print "\n"

	return 0



if __name__ == '__main__':
	rospy.init_node('arna_angle_node',anonymous=False)
	r = rospy.Rate(20) #20 Hz

	group_name = "arna_manipulator" # Make sure the value of "group_name" varible here is equal to the value of variable "group_name" in "arna_effor_example_node.cpp" in src 
	
	rospy.Subscriber("/hebiros/" + group_name + "/feedback/joint_state", JointState, get_current_angles)	
			
	rospy.Subscriber("arna_arm_direction", String, get_input)
	r.sleep()
	rospy.spin()


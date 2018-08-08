#!/usr/bin/env python
import rospy
import glob
import yaml
import numpy as np
from sensor_msgs.msg import Image
import cv2
from camera_calibration.calibrator import ChessboardInfo
from cv_bridge import CvBridge


print "Started Node..."
rospy.init_node('image_rect_node')
img_rect_pub = rospy.Publisher('/sensors/camera/image_rect', Image, queue_size=100)
pattern_size = (5, 7)

obj_default_val = np.zeros((5*7,3), np.float32)
obj_default_val[:,:2] = np.mgrid[0:5,0:7].T.reshape(-1,2)

obj_points = []
img_points = []
bridge = CvBridge()

	
def img_callback(img_msg):
	global img_rect_pub, pattern_size, obj_default_val, obj_points, img_points, bridge
	del obj_points[:]
	del img_points[:]

	img = bridge.imgmsg_to_cv2(img_msg, desired_encoding="bgr8")

	cv2.imshow('Original',img)
	cv2.waitKey(100)
	
	h,  w = img.shape[:2]
	img_gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
	found, corners = cv2.findChessboardCorners(img_gray, pattern_size)
		
	if not found:
		print ">>Object not found<<\n"
		disp_img = img
	else:
		print "calibrating object...\n"
		obj_points.append(obj_default_val)
		img_points.append(corners)

		rms, cam_matrix, dist_coeff, rvecs, tvecs = cv2.calibrateCamera(obj_points, img_points, img_gray.shape[::-1], None, None)
		newcameramtx, roi=cv2.getOptimalNewCameraMatrix(cam_matrix,dist_coeff,(w,h),1,(w,h))

		img_rect = cv2.undistort(img, cam_matrix, dist_coeff, None, newcameramtx)		
		x,y,w,h = roi
		disp_img = img_rect
		
		pub_msg = bridge.cv2_to_imgmsg(img_rect, encoding="bgr8")
		img_rect_pub.publish(pub_msg)

	cv2.imshow('calibrated',disp_img)
	cv2.waitKey(100)


if __name__ == '__main__':
	rospy.Subscriber('/sensors/camera/image_color', Image, img_callback)
	rospy.spin()








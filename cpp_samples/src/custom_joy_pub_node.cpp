/*
 * This is a custom node for receiving data from virtual joysticks.
 * one angular(theta), and one linear(x,y). This node reads the data
 * and publishes them as per the motor requirements.
*/


#include "ros/ros.h"
#include "std_msgs/String.h"
#include "sensor_msgs/Joy.h"

class CustomJoyPub
{
	public:
		CustomJoyPub()
		{
			ros::NodeHandle n_;
			jc_pub_ = n_.advertise<sensor_msgs::Joy>
						("custom_joy", 1000, false);
			
			joy_sub_ = n_.subscribe("tab_joy", 10,
						 &CustomJoyPub::joy_callback, this);
			
		}
		
		void joy_callback(sensor_msgs::Joy msg_in_)
		{
			int i;
			j_msg_ = msg_in_;
			
			j_msg_.axes.resize(3);// Three data points- x,y,Theta.
			j_msg_.buttons.resize(12);// buttons if required

			//Modifying range as per motor control requirements
			j_msg_.axes[0] = (-127) * j_msg_.axes[0];
			j_msg_.axes[1] = ( 127) * j_msg_.axes[1];
			j_msg_.axes[2] = (-127) * j_msg_.axes[2];
			
			for(i=0; i<12; i++)
				j_msg_.buttons[i] = 0;

			jc_pub_.publish(j_msg_);

		}
		
	protected:
		ros::Publisher jc_pub_;
		ros::Subscriber joy_sub_;
		
		sensor_msgs::Joy j_msg_;
};



int main(int argc, char **argv)
{
	ros::init(argc, argv, "custom_joy_pub_node");
	ROS_INFO("the custom joystick publisher node has been started");
	CustomJoyPub cjp;
	ros::spin();	
}





#include "ros/ros.h"
#include "/home/andromeda-2-1-2/bax-myo_ws/devel/include/baxter_core_msgs/JointCommand.h"


int main(int argc, char **argv)
{
	ros::init(argc, argv, "moveBaxLeft");
	ros::NodeHandle n;
	
	ros::Publisher moveBaxLeft_pub = n.advertise<baxter_core_msgs::JointCommand>("/robot/limb/left/joint_command", 1);
	ros::Rate loop_rate(100);

	baxter_core_msgs::JointCommand msg;
	msg.mode = baxter_core_msgs::JointCommand::POSITION_MODE;
	
	msg.names.push_back("left_s0");
	msg.names.push_back("left_e1");
	msg.names.push_back("left_s0");
	msg.names.push_back("left_s1");
	msg.names.push_back("left_w0");
	msg.names.push_back("left_w1");
	msg.names.push_back("left_w2");
	
	// Memory allocation to eliminate "segmenation fault" error
	
	msg.command.resize(msg.names.size());
	
  	for(size_t i = 0; i < msg.names.size(); i++)
		msg.command[i] = 0.0;
	
	//end of the memory allocation process
	
		
	int i = 1;
	while (ros::ok())
	{
		moveBaxLeft_pub.publish(msg);
		ros::spinOnce();
		loop_rate.sleep();
		i = 2;
	}
	
	ros::shutdown();
		
	return 0;
}	




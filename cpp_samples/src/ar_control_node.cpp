#include "ros/ros.h"
#include "sensor_msgs/JointState.h"
#include "std_msgs/Float64MultiArray.h"
#include "/home/andromeda-2-1-2/turtle_ws/src/cpp_samples/srv/AddGroupFromNamesSrv.srv"

#define NUMBER_OF_JOINTS    3

sensor_msgs::JointState jointFeedback;
sensor_msgs::JointState ar_angles;

void ar_angles_callback(sensor_msgs::JointState msg)
{
	ar_angles.position = msg.position;
	ar_angles.velocity = msg.velocity;
}


// Callback when we get joint state feedback
void joint_callback(sensor_msgs::JointState data )
{
	jointFeedback = data;

	ros::NodeHandle NN;
	ros::Rate r(20); // Hz

	ros::Publisher info_pub = NN.advertise<sensor_msgs::JointState>(
						   "/ar_info", 10); // Buffer 10 messages
	   
	info_pub.publish(jointFeedback);

	/*ROS_INFO(
	   "Joint Position [0]:%f [1]:%f [2]:%f",
	   jointFeedback.position[0],
	   jointFeedback.position[1],
	   jointFeedback.position[2] );*/

}


// Callback for gripper position feedback
void eef_callback(sensor_msgs::JointState msg )
{
    ROS_INFO("Gripper data access is restricted");
}


int main( int argc, char **argv )
{
	ros::init( argc, argv, "ar_control_node" );
	ros::NodeHandle n;
	ros::Rate loop_rate(20); // Hz

	ROS_WARN("arm_control sleeping for 3sec allowing all nodes to start up");
	ros::Duration(3.0).sleep();

	ros::Publisher eef_command_publisher =
			n.advertise<sensor_msgs::JointState>(
			"/LeftArm/joint_pos_cmd", 10);//modified info
	  
	ros::Subscriber eef_subscriber =
		n.subscribe("/LeftArm/joint_pos_state", 10, eef_callback);

	ros::Subscriber get_angles = n.subscribe("/ar_joint", 10,
							ar_angles_callback );



	std::string group_name = "ar_manipulator"; // Make sure the value of "group_name" varible here is equal to the value of variable "group_name" in "tablet_data_pub.py" file in scripts


	ros::ServiceClient add_group_client =
	n.serviceClient<AddGroupFromNamesSrv>("/ar/add_group_from_names");


	bool groupCreated = false;
	AddGroupFromNamesSrv add_group_srv;
	add_group_srv.request.group_name = group_name;


	add_group_srv.request.families = {"AR"};
	add_group_srv.request.names = {
	"joint_0",
	"joint_1",
	"joint_3"  };
	groupCreated = add_group_client.call( add_group_srv );

	if ( true == groupCreated )
	{
		ROS_INFO( "Created group: %s", group_name.c_str() );
	}
	else
	{
		ROS_FATAL( "Failed to create group: %s", group_name.c_str() );
		return -1;
	}

	ros::Subscriber feedback_subscriber = n.subscribe(
	"/ar/" + group_name + "/feedback/joint_state",
	10, joint_callback );


	jointFeedback.position.reserve( NUMBER_OF_JOINTS );
	jointFeedback.velocity.reserve( NUMBER_OF_JOINTS );
	jointFeedback.effort.reserve( NUMBER_OF_JOINTS );


	ros::Publisher joint_command_publisher = 
	n.advertise<sensor_msgs::JointState>(
	"/AR/" + group_name + "/command/joint_state",
	10 ); // Buffer up to 10 messages


	ros::Publisher info_pub = n.advertise<sensor_msgs::JointState>(
	"/info_from_ar",
	10 ); // Buffer up to 10 messages



	sensor_msgs::JointState joint_command_msg;

	// These names are absolutely necessary
	joint_command_msg.name.push_back( "AR/Shoulder_Yaw" );   // [0]
	joint_command_msg.name.push_back( "AR/Shoulder_Pitch" ); // [1]
	joint_command_msg.name.push_back( "AR/Arm_Roll" ); // [2]

	joint_command_msg.position.resize( NUMBER_OF_JOINTS );


	// Create the command message for the gripper
	sensor_msgs::JointState eef_cmd_msg;
	eef_cmd_msg.position.push_back( 0.698131 ); // Closed position


	while( ros::ok() )
	{	
		joint_command_msg.position = ar_angles.position;
		//joint_command_msg.velocity = ar_angles.velocity;
		
		std::cout << "\n~~~";

		joint_command_publisher.publish( joint_command_msg );
		info_pub.publish(jointFeedback);

		ros::spinOnce();
		loop_rate.sleep();
	}

	return 0;
}




EV_robot
========

Framework of EV_robot created:

EV_robot_main.java          : handle the multiple threads 
Motion_tracking.java        : receiving pedometer readings including step event(length,heading,time)
Pedometer.java              : receive raw inertial data and detect step events
RSSI_sensing.java           : sense RSSI and return distance information, right now use it to judge (>2m)
Vision_tracking.java        : if available, fuse motion information to perform constrainted particle filter, otherwise do regular PF
WC_control.java             : communicate with lower level hardware
WC_follow_strategy.java     : based on all sensing returns, what motion is required for the mobile robot?  Motion decision part.


TODO list:
1. RSSI sensing is written in C, need to integrate it in.
2. WC_control part is written in C++ and communicate with serial port, need to integrate this part in.

3. WC_follow_strategy needs to be more specific and contains all possible scenarios.
4. Vision_tracking part is empty and always returns FALSE, need to merge Ying's part.
5. EV_robot_main part need proper threads' management.

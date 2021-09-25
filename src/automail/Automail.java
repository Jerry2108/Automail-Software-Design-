package automail;

import simulation.Clock;
import simulation.IMailDelivery;

public class Automail {

    private Robot[] robots;
    private MailPool mailPool;
    
    private int numRegRobots;
    private int numFastRobots; 
    private int numBulkRobots;
    
    
    public Automail(MailPool mailPool, IMailDelivery delivery, int numRegRobots, int numFastRobots, int numBulkRobots) {  	
    	/** Initialize the MailPool */
    	
    	this.mailPool = mailPool;
    	this.numRegRobots = numRegRobots;
    	this.numFastRobots = numFastRobots;
    	this.numBulkRobots = numBulkRobots;
    	
    	/** Initialize robots, currently only regular robots */
    	int totalRobots = numRegRobots + numFastRobots + numBulkRobots;
    	robots = new Robot[totalRobots];
    	for (int i = 0; i < numRegRobots; i++) robots[i] = new RegularRobot(delivery, mailPool, i);
    	for (int i = numRegRobots; i < (numRegRobots+ numBulkRobots); i++) robots[i] = new BulkRobot(delivery, mailPool, i);
    	for (int i =  (numRegRobots+ numBulkRobots); i < totalRobots; i++) robots[i] = new FastRobot(delivery, mailPool, i);
       
    }

    public Robot[] getRobots() {
        return robots;
    }

    public MailPool getMailPool() {
        return mailPool;
    }
    
    /**
     * Depending on type of robot returns average operating time of that group. 
     * @param robot
     * @return
     */
    
    public double getOpTime(Robot robot) {
    	int time = 0;
    	if (robot instanceof RegularRobot) {
    		for(int i = 0 ; i <robots.length ; i++) {
        		if(robots[i] instanceof RegularRobot) {
        			time += robots[i].getOperatingTime();
        		}
        	}
    		return (double)time/numRegRobots;
    	} else if(robot instanceof BulkRobot) {
        	for(int i = 0 ; i <robots.length ; i++) {
        		if(robots[i] instanceof BulkRobot) {
        			time += robots[i].getOperatingTime();
        		}
        	}
        	return (double)time/numBulkRobots;
    	} else if(robot instanceof FastRobot) {
    		for(int i = 0 ; i <robots.length ; i++) {
        		if(robots[i] instanceof FastRobot) {
        			time += robots[i].getOperatingTime();
        		}
        	}
        	return (double)time/numFastRobots;
    	}
    	return 0;
    }
    
   
}

package automail;


import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;
import simulation.Clock;
import simulation.IMailDelivery;
import java.util.ArrayList;

public class BulkRobot extends Robot {
    
	public static final String TYPE = "BULK_ROBOT";
	public static final double RATE = 0.01;
	private static final int speed = 1;
	private ArrayList<MailItem> tube = null;
	private final int tubeSize = 5;
	
	
	public BulkRobot(IMailDelivery delivery, MailPool mailPool, int number) {
		// TODO Auto-generated constructor stub
		super(delivery, mailPool);
		this.id = "B" + number;
		tube = new ArrayList<MailItem>(tubeSize);
		waitTime = 0;
	}
	/**
	 * Returns number of mailItems in the tube.
	 */
	public int getTubeSize() {return tube.size();}

	public void operate() throws ExcessiveDeliveryException { 
	
    	switch(current_state) {
    		/** This state is triggered when the robot is returning to the mailroom after a delivery */
    		case RETURNING:
    			/** If its current position is at the mailroom, then the robot should change state */
                if(current_floor == Building.getInstance().getMailroomLocationFloor()){
        			/** Tell the sorter the robot is ready */
        			mailPool.registerWaiting(this);
                	changeState(RobotState.WAITING);
                } else {
                	/** If the robot is not at the mailroom floor yet, then move towards it! */
                    moveTowards(Building.getInstance().getMailroomLocationFloor());
                	break;
                }
    		case WAITING:
    			/** If the StorageTube is ready and the Robot is waiting in the mailroom then start the delivery */
                if(!isEmpty() && receivedDispatch){
                	receivedDispatch = false;
                	deliveryCounter = 0; // reset delivery counter
                	setDestination();
                	changeState(RobotState.DELIVERING);
                } else {
                  waitTime++;
                }
                break;
    		case DELIVERING:
    			if(current_floor == destination_floor){ // If already here drop off either way
                    /** Delivery complete, report this to the simulator! */
                    delivery.deliver(this,tube.get(0), "");
                    tube.remove(tube.get(0));
                    deliveryCounter++;
                    if(deliveryCounter > 5){  // Implies a simulation bug
                    	throw new ExcessiveDeliveryException();
                    }
             
                    /** Check if want to return, i.e. if there is no item in the tube*/
                    if(tube.size() == 0){
                    	changeState(RobotState.RETURNING);
                    }
                    else{
                        /** If there is another item, set the robot's route to the location to deliver the item */
                        setDestination();
                        changeState(RobotState.DELIVERING);
                    }
    			} else {
	        		/** The robot is not at the destination yet, move towards it! */
	                moveTowards(destination_floor);
    			}
                break;
    	}
    }
	/**
     * Sets the route for the robot
     */
    private void setDestination() {
        /** Set the destination floor */
        destination_floor = tube.get(0).getDestFloor();
    }
    
    private void moveTowards(int destination) {
        if(current_floor < destination){
            current_floor+=speed;
        } else {
            current_floor-=speed;
        }
    }
    
    public String getIdTube() {
    	return String.format("%s(%1d)", this.id, tube.size());
    }
    
    /**
     * Prints out the change in state
     * @param nextState the state to which the robot is transitioning
     */
    private void changeState(RobotState nextState){
    	assert(tube == null);
    	if (current_state != nextState) {
            System.out.printf("T: %3d > %7s changed from %s to %s%n", Clock.Time(), getIdTube(), current_state, nextState);
    	}
    	current_state = nextState;
    	if(nextState == RobotState.DELIVERING){
            System.out.printf("T: %3d > %7s-> [%s]%n", Clock.Time(), getIdTube(), tube.get(0).toString());
    	}
    }
     
    /**Returns top mailItem in tube*/
	public ArrayList<MailItem> getTube() {
		return tube;
		
	}

	public boolean isEmpty() {
		return (tube.size() == 0);
	}

	public void addToTube(MailItem mailItem) throws ItemTooHeavyException {
		tube.add(mailItem);
		if(mailItem.weight > INDIVIDUAL_MAX_WEIGHT)throw new ItemTooHeavyException();
	}
	
	public double getMaintainenceCost() {
		return RATE * avgOperatingTime;
	}
	

}

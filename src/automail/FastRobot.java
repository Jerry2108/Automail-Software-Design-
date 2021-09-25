package automail;


import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;
import simulation.Clock;
import simulation.IMailDelivery;

public class FastRobot extends Robot {
    
	public static final String TYPE = "FAST_ROBOT";
	public static final double RATE = 0.05;
	private static final int TUBELOAD = 0;
	private static final int speed = 3;
	private MailItem deliveryItem = null;

	
	
	public FastRobot(IMailDelivery delivery, MailPool mailPool, int number) {
		// TODO Auto-generated constructor stub
		super(delivery, mailPool);
		this.id = "F" + number;
		waitTime = 0;
	}
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
                    delivery.deliver(this, deliveryItem, "");
                    deliveryItem = null;
                    deliveryCounter++;
                    if(deliveryCounter > 2){  // Implies a simulation bug
                    	throw new ExcessiveDeliveryException();
                    }
                    changeState(RobotState.RETURNING);
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
        destination_floor = deliveryItem.getDestFloor();
    }
    
    private void moveTowards(int destination) {
    	if(current_floor < destination){
        	if(current_floor+speed < destination) {
               current_floor+=speed;
        	} else {
        	   current_floor += (destination-current_floor);
        	}
        } else {
        	if(current_floor-speed > Building.getInstance().getMailroomLocationFloor()) {
               current_floor-=speed;
        	} else {
        		current_floor = Building.getInstance().getMailroomLocationFloor();
        	}
        }
    }
    public String getIdTube() {
    	return String.format("%s(%1d)", this.id, TUBELOAD);
    }
    
    /**
     * Prints out the change in state
     * @param nextState the state to which the robot is transitioning
     */
    private void changeState(RobotState nextState){
    	assert(!(deliveryItem == null));
    	if (current_state != nextState) {
            System.out.printf("T: %3d > %7s changed from %s to %s%n", Clock.Time(), getIdTube(), current_state, nextState);
    	}
    	current_state = nextState;
    	if(nextState == RobotState.DELIVERING){
            System.out.printf("T: %3d > %7s-> [%s]%n", Clock.Time(), getIdTube(), deliveryItem.toString());
    	}
    }

	public boolean isEmpty() {
		return (deliveryItem == null);
	}

	public void addToHand(MailItem mailItem) throws ItemTooHeavyException {
		assert(deliveryItem == null);
		deliveryItem = mailItem;
		if (deliveryItem.weight > INDIVIDUAL_MAX_WEIGHT) throw new ItemTooHeavyException();
	}
	public double getMaintainenceCost() {
		return RATE * avgOperatingTime;
	}
	
	
}

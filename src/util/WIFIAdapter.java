package util;

import com.unimelb.swen30006.wifimodem.WifiModem;

import automail.Building;

public class WIFIAdapter {
	WifiModem wifiModem;

	public WIFIAdapter() {
		// TODO Auto-generated constructor stub
	     try {
		 wifiModem = WifiModem.getInstance(Building.getInstance().getMailroomLocationFloor());
	     } catch (Exception e) {
	    	 System.out.println("Problem in setting up WIFI");
	    	 System.exit(0);
	     }
		
	}
	/**
	 * Returns service fee of requested destination.s
	 * @param destFloor
	 * @return
	 */
	
	public double getServiceFee(int destFloor) {
		double price = wifiModem.forwardCallToAPI_LookupPrice(destFloor);
		if(price>0) {
			return price;
		}
		return 0;
		
	}

}

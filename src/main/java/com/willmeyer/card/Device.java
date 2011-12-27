package com.willmeyer.card;

import com.willmeyer.util.*;
import com.willmeyer.card.exception.*;

/**
 * A Device.  A Device is basically a class that exposes standard start and stop methods, then 
 * reports an interface that it exposes.  In this way, other modules can depend on specific device 
 * interfaces without depending on specific implementation classes.  This means mock and real 
 * devices can be replaced transparently, for example.
 */
public interface Device {

	/**
	 * Initialize and connect the device.  Throw an exception if everything wasn't totally 
	 * successful and the device isn't ready to communicate with, otherwise just return.  In the 
	 * case of an exception, the exception itself indicates whether the failure is temporary or
	 * permanent, which will determine whether the system trues to make it available for restart
	 * later.
	 */
	public void start(PropertiesPlusPlus props) throws ComponentInitException;
	
	/**
	 * Returns the logical name of the interface this device exposes (returned via 
	 * getInterfaceImpl).  This should be the fully-qualified name of the Java language interface, 
	 * really.
	 * 
	 * @return The interface name, never null
	 */
	public String getInterfaceName();
	
	/**
	 * Returns the actual instance of the interface referenced in getInterfaceName.
	 * 
	 * @return The interface, never null
	 */
	public Object getInterfaceImpl();
	
	/**
	 * Do whatever shutdown is required such that the Device could have start called again.  This 
	 * may in theory be called even if the start wasn't successful, so devices should handle that 
	 * case.
	 */
	public void stop();
}

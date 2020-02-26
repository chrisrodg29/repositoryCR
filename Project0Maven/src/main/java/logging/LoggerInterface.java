package logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nbb1.NBBDriver;

public interface LoggerInterface {
	
	Logger log = LogManager.getLogger(NBBDriver.class);

}

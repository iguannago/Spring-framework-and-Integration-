/**
 * 
 */
package rewards.batch;

import org.apache.log4j.Logger;
import org.springframework.batch.core.listener.SkipListenerSupport;

/**
 * Skip listener for bad dining records
 * 
 * @author djnorth
 *
 */
@SuppressWarnings("rawtypes")
public class DiningSkipListener extends SkipListenerSupport {

	@Override
	public void onSkipInRead(Throwable t) {
		Logger.getLogger(this.getClass()).warn("skipping due to exception", t);
	}
	
}

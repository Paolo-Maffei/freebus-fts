package example;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.freebus.fts.common.HexString;
import org.freebus.knxcomm.event.CloseEvent;
import org.freebus.knxcomm.event.FrameEvent;
import org.freebus.knxcomm.link.Link;
import org.freebus.knxcomm.link.LinkListener;
import org.freebus.knxcomm.link.serial.Ft12SerialLink;
import org.freebus.knxcomm.link.serial.SerialPortUtil;
import org.freebus.knxcomm.types.LinkMode;

/**
 * Open a serial link, switch to bus monitor mode,
 * and log the received frames.
 */
public final class SerialLinkExample
{
   private final static Logger logger = Logger.getLogger(SerialLinkExample.class);

   public static void main(String[] args) throws InterruptedException, IOException
   {
      final Link link = new Ft12SerialLink(SerialPortUtil.getPortNames()[0]);

      link.addListener(new LinkListener()
      {
         @Override
         public void linkClosed(CloseEvent e)
         {
            logger.info("*** exit ***");
            System.exit(0);
         }
         
         @Override
         public void frameReceived(FrameEvent e)
         {
            logger.debug("received: " + HexString.toString(e.getData()));
         }
      });

      link.open(LinkMode.BusMonitor);

      while (true)
      {
         Thread.sleep(10000);
      }
   }
}

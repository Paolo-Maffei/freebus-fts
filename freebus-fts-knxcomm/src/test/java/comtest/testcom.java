package comtest;


import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.freebus.knxcomm.BusInterface;
import org.freebus.knxcomm.BusInterfaceFactory;
import org.freebus.knxcomm.telegram.Application;
import org.freebus.knxcomm.telegram.PhysicalAddress;
import org.freebus.knxcomm.telegram.Priority;
import org.freebus.knxcomm.telegram.Telegram;
import org.freebus.knxcomm.telegram.Transport;

public class testcom {
   private static int sequence = 0;
   private static BusInterface busInterface;
   private static Logger logger = Logger.getRootLogger();

   public static void main(String[] args)  {
	   try {
		      SimpleLayout layout = new SimpleLayout();
		      ConsoleAppender consoleAppender = new ConsoleAppender( layout );
		      logger.addAppender( consoleAppender );

		      // ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
		      logger.setLevel( Level.DEBUG );
		     
		    } catch( Exception ex ) {
		      System.out.println( ex );
		    }

	try {
		busInterface = BusInterfaceFactory.newSerialInterface("COM5");
		logger.info("Start test com");
		busInterface.open();
		
       Telegram telegram =  new Telegram();
         telegram.setFrom(new PhysicalAddress(1, 1, 255));
         telegram.setDest(new PhysicalAddress(1, 1, 6));
         telegram.setPriority(Priority.SYSTEM);
         telegram.setTransport(Transport.Connect);
         sequence = 0;
         busInterface.send(telegram);
         telegram.setFrom(new PhysicalAddress(1, 1, 255));
         telegram.setDest(new PhysicalAddress(1, 1, 6));
         telegram.setPriority(Priority.SYSTEM);
         telegram.setTransport(Transport.Connected);
         telegram.setSequence(sequence++);
         telegram.setApplication(Application.Memory_Read);
         telegram.setData(new int[] { 0, 0, 0 });
    
			busInterface.send(telegram);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }
}

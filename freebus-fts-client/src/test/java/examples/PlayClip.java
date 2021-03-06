package examples;

import org.freebus.fts.client.core.Config;
import org.freebus.fts.common.Environment;
import org.freebus.fts.elements.audio.AudioClip;
import org.freebus.fts.elements.services.LookAndFeelManager;

/**
 * Play a sound clip.
 */
public class PlayClip
{
   /**
    * The main.
    */
   public static void main(String[] args) throws InterruptedException
   {
      Environment.init();
      @SuppressWarnings("unused")
      final Config globalConfig = new Config();
      LookAndFeelManager.setDefaultLookAndFeel();

      final AudioClip clip = new AudioClip("fake_monkey_chatter_short");
      clip.play();

      Thread.sleep(5000);
   }
}

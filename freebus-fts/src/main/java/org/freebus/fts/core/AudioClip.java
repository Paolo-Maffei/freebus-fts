package org.freebus.fts.core;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import org.apache.log4j.Logger;

/**
 * Class for playing an audio clip.
 */
public class AudioClip
{
   private static final int EXTERNAL_BUFFER_SIZE = 8192;
   private final java.net.URL clipUrl;
   private SourceDataLine line;
   private Thread playbackThread;

   /**
    * Create an audio clip object.
    *
    * @param clipName - the name of the audio clip, without path or file
    *           extension.
    */
   public AudioClip(final String clipName)
   {
      final String clipFileName = "sounds/" + clipName + ".wav";
      Logger.getLogger(AudioClip.class).debug("Playing " + clipFileName);

      final ClassLoader classLoader = ImageCache.class.getClassLoader();
      clipUrl = classLoader.getResource(clipFileName);
   }

   /**
    * Play the audio clip.
    */
   public void play()
   {
      try
      {
         final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(clipUrl);
         final AudioFormat format = audioInputStream.getFormat();

         final DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
         line = (SourceDataLine) AudioSystem.getLine(info);

         line.open();
         line.start();

         playbackThread = new Thread()
         {
            @Override
            public void run()
            {
               try
               {
                  int rlen = 0;
                  final byte[] buffer = new byte[EXTERNAL_BUFFER_SIZE];
                  while (rlen != -1)
                  {
                     rlen = audioInputStream.read(buffer, 0, buffer.length);
                     if (rlen >= 0)
                        line.write(buffer, 0, rlen);
                  }
               }
               catch (IOException e)
               {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               }
               finally
               {
                  if (line != null && line.isOpen())
                  {
                     line.drain();
                     line.close();
                  }
               }
            }
         };

         playbackThread.start();
      }
      catch (Exception e)
      {
         Logger.getLogger(getClass()).debug("Failed to play audio clip " + clipUrl, e);
      }
   }
}

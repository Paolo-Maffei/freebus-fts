package org.freebus.fts;

import org.freebus.fts.comm.BusInterface;
import org.freebus.fts.gui.MainWindow;

public final class Main
{
   /**
    * @param args
    * @throws Exception 
    */
   public static void main(String[] args) throws Exception
   {
      final MainWindow mainWin = new MainWindow();
      mainWin.open();
      mainWin.run();

      mainWin.dispose();
      BusInterface.disposeInstance();

//      javax.swing.SwingUtilities.invokeLater(new Runnable()
//      {
//         public void run()
//         {
//            try
//            {
////               UIManager.setLookAndFeel(new MetalLookAndFeel());
//               UIManager.setLookAndFeel(new GTKLookAndFeel());
//            }
//            catch (UnsupportedLookAndFeelException e)
//            {
//               System.out.println("Cannot load custom look-and-feel, using default");
//            }
//
//            final MainWindow mainWin = new MainWindow();
//            mainWin.setVisible(true);
//         }
//      });
   }
}

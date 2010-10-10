package org.freebus.fts.filter;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.freebus.fts.I18n;
import org.freebus.fts.elements.utils.FileUtils;

/**
 * A {@link JFileChooser} file-filter for *.trx files.
 */
public final class TrxFileFilter extends FileFilter
{
   @Override
   public boolean accept(File file)
   {
      if (file.isDirectory()) return true;

      final String ext = FileUtils.getExtension(file);
      return "trx".equalsIgnoreCase(ext);
   }

   @Override
   public String getDescription()
   {
      return I18n.getMessage("TrxFileFilter.description");
   }
}

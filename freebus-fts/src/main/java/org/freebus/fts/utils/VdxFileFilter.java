package org.freebus.fts.utils;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.freebus.fts.core.I18n;

/**
 * A {@link JFileChooser} file-filter for *.vd_ files.
 */
public final class VdxFileFilter extends FileFilter
{
   @Override
   public boolean accept(File file)
   {
      if (file.isDirectory()) return true;

      final String ext = FileUtils.getExtension(file);
      return "vd_".equals(ext);
   }

   @Override
   public String getDescription()
   {
      return I18n.getMessage("VdxFileFilter.description");
   }
}

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
   private final boolean showVdx, showProjects;

   public VdxFileFilter(boolean showVdx, boolean showProjects)
   {
      this.showVdx = showVdx;
      this.showProjects = showProjects;
   }

   @Override
   public boolean accept(File file)
   {
      if (file.isDirectory()) return true;

      final String ext = FileUtils.getExtension(file);
      return (showVdx && "vd_".equals(ext)) || (showProjects && "pr_".equals(ext));
   }

   @Override
   public String getDescription()
   {
      return I18n.getMessage("VdxFileFilter.description");
   }
}

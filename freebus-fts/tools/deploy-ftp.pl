#!/usr/bin/perl
#
# Tool for uploading a file to a ftp server.
#
use strict;

use Getopt::Long;
use Net::FTP;

my $appName = $0;
$appName =~ s/^.*[\\\/]//;

my $verbose = 0;


sub usage #()
{
   print STDERR <<END
Usage: $appName [options] file

Upload a file to a ftp server. T

Valid options are:
--config=x     read the configuration from file x. Commandline options override
               the settings of the config file.
--server=x     the name of the ftp server.
--user=x       the login user.
--pass=x       the login password.
--dir=x        the target directory on the ftp server.
--wipe         delete the contents of the target directory after successful upload.
--index        create an index.html page for the target directory that contains
               the files of the target directory.
--verbose      be more verbose.
--help         this help.
END
   ;
   exit 1;
}


sub createIndex #(files)
{
   # -rw-r--r--   1 user   group  10486441 Oct 20 18:54 freebus-fts-0.1-SNAPSHOT-r2212.zip
   my @files = @_;

   my $tempFile = '/tmp/deploy-ftp-'.$$.'.html';
   open(OUT, '>', $tempFile) || die "$appName: cannot write $tempFile: $!";

   print OUT "<html><body style=\"background-color:#2C4056; text-color:#fff; margin:40px;\">\n";
   print OUT "<h2 style=\"background-color:#507AAA; color:#fff; padding:10px 8px 10px 8px;\">";
   print OUT "Download Area</h2>\n";
   print OUT "<div style=\"background-color:#fff; text-color:#000; width:100%\">\n";
   print OUT "<table style=\"border:0; cell-spacing:8px; padding:10px;\">\n";

   for my $line (@files)
   {
      my @a = split(/\s+/, $line, 9);
      next if ($a[8] =~ /^\./ || $a[8] eq 'index.html' || $a[8] =~ /\.part$/);

      my $size = $a[4];
      if ($size >= 1048576)
      {
         $size = sprintf("%.1f MiB", $size / 1048576.0);
      }
      elsif ($size >= 1024)
      {
         $size = sprintf("%.1f KiB", $size / 1024.0);
      }

      print OUT "<tr><td><a href=".$a[8].">".$a[8]."</a></td><td>&nbsp;&nbsp;".$a[5].' '.$a[6].', '.$a[7]."</td><td style=\"text-align:right;\">&nbsp;&nbsp;$size</td></tr>\n";
   }

   print OUT "</table>\n</div>\n</body></html>\n";
   close(OUT);

   return $tempFile;
}


sub main #()
{
   my ($server, $user, $pass, $dir, $name, $configFile, $wipe, $index);
   
   GetOptions('server=s' => \$server,
              'user=s' => \$user,
              'pass=s' => \$pass,
              'dir=s' => \$dir,
              'name=s' => \$name,
              'config=s' => \$configFile,
              'verbose+' => \$verbose,
              'wipe' => \$wipe,
              'index' => \$index,
              'help' => sub { usage(); })
              || exit 1;
   my $file = $ARGV[0];

   if ($configFile)
   {
      $configFile =~ s/^~/$ENV{HOME}/;

      open(IN, '<:utf8', $configFile) || die "$appName: cannot read $configFile: $!\n";
      while (<IN>)
      {
         chomp $_;
         $_ =~ s/^\s+//;
         $_ =~ s/\s+$//;
         next if ($_ =~ /^\#/ || $_ eq '');
         my ($nm, $val) = split(/\s*=\s*/, $_, 2);
         if ($nm eq 'server' && !$server) { $server = $val; }
         elsif ($nm eq 'user' && !$user) { $user = $val; }
         elsif ($nm eq 'pass' && !$pass) { $pass = $val; }
         elsif ($nm eq 'dir' && !$dir) { $dir = $val; }
         elsif ($nm eq 'name' && !$name) { $name = $val; }
      }
      close(IN);
   }

   usage() if ($server eq '' || $user eq '' || $file eq '');
   
   if ($name eq '')
   {
      $name = $file;
      $name =~ s/^.*[\\\/]//;
   }
   print "$appName: no target filename given\n" if ($name eq '');

   print "$appName: connecting to $server\n" if ($verbose);
   my $ftp = Net::FTP->new($server, Debug => $verbose>1);
   die "$appName: failed to connect to $server: $!\n" unless ($ftp);

   print "$appName: logging in as $user\n" if ($verbose);
   $ftp->login($user, $pass) || die "$appName: cannot login to $server: $!\n";
   $ftp->binary();

   if ($dir ne '')
   {
      print "$appName: changing directory into $dir\n" if ($verbose);
      $ftp->cwd($dir) || die "$appName: cannot change into directory $dir: $!\n";
   }

   my @wipeFiles;
   if ($wipe)
   {
      for my $file ($ftp->dir())
      {
         $file =~ s/^.*\s//;
         next if ($file =~ /^\./ || $file eq $name);

         push(@wipeFiles, $file);
      }
   }

   my $tmpFile = $name.'.part';
   print "$appName: uploading $file as $tmpFile\n" if ($verbose);
   $ftp->delete($tmpFile);
   $ftp->put($file, $tmpFile) || die "$appName: cannot upload $file: $!\n";

   print "$appName: renaming uploaded file to $file\n" if ($verbose);
   $ftp->delete($name);
   $ftp->rename($tmpFile, $name) || die "$appName: cannot rename $tmpFile to $name: $!\n";

   if ($wipe && $#wipeFiles >= 0)
   {
      print "Deleting old files: ".join(' ', @wipeFiles)."\n";
      for my $file (@wipeFiles)
      {
         $ftp->delete($file) || warn "$appName: cannot delete $file: $!\n";
      }
   }

   if ($index)
   {
      my @files = $ftp->dir();
      my $tmpIndexFile = createIndex(@files);

      $ftp->put($tmpIndexFile, "index.html.part") || die "$appName: cannot upload $tmpIndexFile: $!\n";
      $ftp->delete("index.html");
      $ftp->rename("index.html.part", "index.html") || die "$appName: failed to rename index.html.part to index.html: $!\n";

      unlink $tmpIndexFile;
   }

   print "$appName: done\n" if ($verbose);
   $ftp->quit();
}

&main;
exit 0;

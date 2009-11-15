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
               the settings in the config file.
--server=x     the name of the ftp server
--user=x       the login user
--pass=x       the login password
--dir=x        the target director on the ftp server
--verbose      be more verbose
--help         this help
END
   ;
   exit 1;
}


sub main #()
{
   my ($server, $user, $pass, $dir, $name, $configFile);
   
   GetOptions('server=s' => \$server,
              'user=s' => \$user,
              'pass=s' => \$pass,
              'dir=s' => \$dir,
              'name=s' => \$name,
              'config=s' => \$configFile,
              'verbose+' => \$verbose,
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
   $name = $file unless ($name);

   print "$appName: connecting to $server\n" if ($verbose);
   my $ftp = Net::FTP->new($server, Debug => $verbose>1);
   die "$appName: failed to connect to $server: $!\n" unless ($ftp);

   print "$appName: logging in as $user\n" if ($verbose);
   $ftp->login($user, $pass) || die "$appName: cannot login to $server: $!\n";
   $ftp->binary();

   if ($dir ne '')
   {
      print "$appName: changing directory into $dir\n" if ($verbose);
      $ftp->cwd($dir) || die "$appName: cannot change director to $dir: $!\n";
   }

   my $tmpFile = $name.'.part';
   print "$appName: uploading $file as $tmpFile\n" if ($verbose);
   $ftp->delete($tmpFile);
   $ftp->put($file, $tmpFile) || die "$appName: cannot upload $file: $!\n";

   print "$appName: renaming uploaded file to $file\n" if ($verbose);
   my $oldFile = $name.'.old';
   $ftp->delete($oldFile);
   $ftp->rename($name, $oldFile) || $ftp->delete($name);
   $ftp->rename($tmpFile, $name) || die "$appName: cannot rename $tmpFile to $name: $!\n";

   print "$appName: done\n" if ($verbose);
   $ftp->quit();
}

&main;

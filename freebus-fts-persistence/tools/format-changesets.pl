#!/usr/bin/perl
#
#  Format the Liquibase changeset entries
#
use strict;
use Getopt::Long;

my $author = $ENV{USER};
my $id;


sub usage()
{
   print <<END
Usage: $0 [options] <infile >outfile

Update the Liquibase changeset attributes according to the specified
options. The changelog file is read from standard input and written
to standard output.

Valid options are:
 --autor=x   Set the author to x. Default: $author.
 --id=x      The id of the changelog. The changesets are named "<id>-<num>".
 --help      This help

END
   ;
   exit 1;
}


GetOptions('author=s' => \$author,
           'id=s' => \$id,
           'help' => sub { usage(); })
   || die;

die "$0: no --author given" if ($author eq '');
die "$0: no --id given" if ($id eq '');


my $num = 0;
while (<STDIN>)
{
   chomp $_;
   if ($_ =~ /<changeSet/i)
   {
      ++$num;
      $_ =~ s/[^ ].*$//;
      print "$_<changeSet author=\"$author\" id=\"$id-$num\">\n";
   }
   else
   {
      print "$_\n";
   }
}

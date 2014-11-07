#!/usr/bin/perl
# created by Mong-ju Jung for Pivotal
# mjung@pivotal.io

use strict;

my $file_to_output = shift;
my $lines_to_generate = shift;
my $rand_key_len = shift;
my $rand_val_len = shift;

my $hostname = `hostname`;
chomp ($hostname);

if (!$file_to_output || !$lines_to_generate || !$rand_key_len || !$rand_val_len) {
	print STDERR "Correct Usage: $0 file_to_output lines_to_generate key_length value_length\n";

	exit 1;
}

if ($rand_key_len < length(time.key_suffix())*1.5) {
	print STDERR "key_length should be at least ".(length(time.key_suffix())*1.5)." !!\n";

	exit 2;
}

if (-f $file_to_output) {
	`rm -f $file_to_output`;
}

$lines_to_generate =~ /(\d+)/;
$lines_to_generate = $1;

my $fh;
open ($fh, ">$file_to_output") or die "Could not create $file_to_output!!\n";

my $alphas = "abcdefghijklmnopqrstuvwxyz";

my @arr_alphas = split("|", $alphas);

my $alphas_cnt = length($alphas);

sub key_suffix {
	return $hostname.$file_to_output;	
}

sub getRandKey {
	my ($len) = @_;

	$len -= length(time.key_suffix());

	my $str = "";
	if ($len > 0) {
		my $idx;
		for (1..$len) {
			$idx = rand($alphas_cnt);
			$str .= $arr_alphas[int($idx)];
		}	
	}

	return $str.(time).(key_suffix());
}

sub getRandVal {
	my ($len) = @_;

	my $str = "";
	my $idx;
	for (1..$len) {
		$idx = rand($alphas_cnt);
		$str .= $arr_alphas[int($idx)];
	}	

	return $str;
}

for (1..$lines_to_generate) {
	print $fh getRandKey($rand_key_len).",".getRandVal($rand_val_len)."\n";
}

close ($fh);

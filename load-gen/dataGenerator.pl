#*******************************************************************************
# The MIT License (MIT)
#
# Copyright (c) 2014 MongJu Jung <mjung@pivotal.io>
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.
#*******************************************************************************
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

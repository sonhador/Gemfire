#!/usr/bin/perl

use strict;

my $prev_cnt = 0;
while (1) {
	my $cur_cnt = `./perf_check.sh`;
	chomp ($cur_cnt);

	print ($cur_cnt - $prev_cnt);
	print "\n";

	$prev_cnt = $cur_cnt;

	sleep 1;		
}

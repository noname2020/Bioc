#!/usr/bin/python
import os
import sys

goldtask1 = sys.argv[1]
outputFolder = sys.argv[2] + '/'

if not os.path.exists(outputFolder):
	os.makedirs(outputFolder)

pmidToLines = {}
f = open(goldtask1)
for line in f:
	pmid, geneid, sent = line.strip().split('\t')
	lines = pmidToLines.setdefault(pmid, [])
	lines.append(line.strip())
f.close()

for pmid in pmidToLines:
	lines = pmidToLines[pmid]
	fname = outputFolder + pmid + '.txt'
	f = open(fname, 'w')
	f.write('\n'.join(lines))
	f.close()


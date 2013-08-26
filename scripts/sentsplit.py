#/usr/bin/python
"""
Usage: python scripts/sentsplit.py data/articles/ data/articles_sent/
"""

import sys
import os

infolder = sys.argv[1]
outfolder = sys.argv[2]
files = os.listdir(infolder)
cmd = ' '.join(['cp', 'data/BioC.dtd', outfolder])
os.system(cmd)

for fname in files:
	print 'processing', fname
	if fname[-3:] != 'xml':
		print 'skipping', fname
		continue
	infile = infolder + '/' + fname
	outfile = outfolder + '/' + fname
	cmd = ' '.join(['java', '-cp', 'external/BioC_Java_1.0/lib/bioc.jar:external/BioC_Java_1.0/lib/woodstox-core-asl-4.1.5.jar:external/BioC_Java_1.0/lib/stax2-api-3.1.1.jar', 'bioc.test.SentenceSplit', infile, outfile])
	os.system(cmd)
	#sys.exit()

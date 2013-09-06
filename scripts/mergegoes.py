#!/usr/bin/python
"""
Usage example: python mergegoes.py ~/Dropbox/Bioc/data/evaluation/finalTriples.1.10 4 > ~/Dropbox/Bioc/data/evaluation/finalTriples.1.10.4
"""
import os
import sys

threshold = int(sys.argv[2])

pmidToResults = {}
f = open(sys.argv[1])
for line in f:
	pmid, gene, go = line.strip().split()
	geneNgo = pmidToResults.setdefault(pmid, [])
	geneNgo.append((gene, go))
f.close()

for pmid in pmidToResults:
	geneNgo = pmidToResults[pmid]
	geneToGoes = {}
	for gene, go in geneNgo:
		goes = geneToGoes.setdefault(gene, [])
		goes.append(go)
	for gene in 	geneToGoes:
		goes = geneToGoes[gene]
		goToCount = {}
		for go in goes:
			if go in goToCount:
				goToCount[go] = goToCount[go] + 1
			else:
				goToCount[go] = 1
		countGo = []
		for go in goToCount:
			countGo.append([goToCount[go], go])
		countGo.sort(reverse=True)
		#print pmid, gene, countGo
		#continue
		printed = False
		for count, go in countGo:
			if count < threshold and printed:
				#print pmid, gene, go, count, 'break'
				print pmid, gene, go
				#continue
				break
			print pmid, gene, go
			printed = True

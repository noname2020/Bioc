Bioc = $(shell pwd)

JAVAC = javac
JAVA = java

#JAVAC = ~/java/jdk1.7.0_11/bin/javac
#JAVA = ~/java/jdk1.7.0_11/bin/java
JAVA_FLAGS = -classpath "$(Bioc)/class:lib/commons-lang3-3.1.jar:lib/commons-compress-1.4.1.jar:lib/json-simple-1.1.jar:lib/commons-io-2.4.jar:lib/lingpipe-4.1.0.jar:lib/jython-2.2-beta1.jar:external/BioC_Java_1.0/lib/bioc.jar:external/BioC_Java_1.0/lib/biolemmatizer-core-1.1-jar-with-dependencies.jar:external/BioC_Java_1.0/lib/junit-4.1.1.jar:external/BioC_Java_1.0/lib/hamcrest-core-1.3.jar:external/BioC_Java_1.0/lib/stax-utils.jar:external/BioC_Java_1.0/lib/stax2-api-3.1.1.jar:external/BioC_Java_1.0/lib/woodstox-core-asl-4.15.jar:external/BioC_Java_1.0/lib/xmlunit-1.4.jar"
JAVAC_FLAGS = \
$(JAVA_FLAGS) \
-sourcepath "$(Bioc)/src:$(Bioc)/autosrc" \
-g:lines,vars,source \
-d $(Bioc)/class \
#-J-Xmx30g -source 1.7

JAVADOC = javadoc
JAVADOC_FLAGS = -J-Xmx3g
JAVADOCS=html

MEDTAGGER_VERSION=20080618
ifeq ($(BUILDING_GRMM),yes)
  DISTNAME=grmm-$(VERSION)
else
  VERSION=$(MEDTAGGER_VERSION)
  DISTNAME=MedTagger-$(VERSION)
endif

### note: the model file name is changed to MedTagger_model_third
all: class link-resources
	$(JAVAC) $(JAVAC_FLAGS) `find src -name '*.java'`
	$(JAVA) -Xmx2g -Xms2g $(JAVA_FLAGS) GoTaskMain_DC test	data/bc4go_test_v090313/ MayoTask4_2_task1b_0.1.corrected.run2
#	$(JAVA) -Xmx20g -Xms20g $(JAVA_FLAGS) GoTaskMain_DC annot	data/bc4go_test_v090313/ MayoTask4_2_task1b_0.1.corrected.run3 7 10 1
#	$(JAVA) -Xmx20g -Xms20g $(JAVA_FLAGS) GoTaskMain_DC annot	data/bc4go_test_v090313/ goldtask1_0.01/ 7 10 1
#	$(JAVA) -Xmx2g -Xms2g $(JAVA_FLAGS) GoTaskMain_DC test	data/
#	$(JAVA) -Xmx2g -Xms2g $(JAVA_FLAGS) GoTaskMain_DC annot	data/
#	$(JAVA) -Xmx2g -Xms2g $(JAVA_FLAGS) GoTaskMain train
#	 
#///Users/m048100/Documents/i2b2/i2b2Challenge2010/Data/release3_merged/ldaModels/i2b22010_txt.uniNorm_eventStr-final_alpha=0.25_beta=0.1_k=100.phi
#///Users/m048100/Documents/i2b2/i2b2Challenge2010/Data/release3_merged/ldaModels/aspects.json.i2b22010_txt.uniNorm_eventStr-final_6.phi.global /Users/m048100/Documents/i2b2/i2b2Challenge2010/Data/release3_merged/GoldStdsvm_features uniNorm+eventStr train

javadoc: html class
	$(JAVADOC) $(JAVADOC_FLAGS) -classpath $(JAVAC_FLAGS) -d $(Bioc)/html -sourcepath $(Bioc)/src -source 1.7 -subpackages edu

grmmdoc: html class
	$(JAVADOC) $(JAVADOC_FLAGS) -classpath $(JAVAC_FLAGS) -d $(Bioc)/html -sourcepath $(Bioc)/src -source 1.7 

copy-resources: class
	cd src ; gtar --exclude CVS -cf - `find . -type d -name resources` | (cd ../class ; gtar -xf -)

# Soft link the resources directories in MedTagger/src into MedTagger/class
link-resources: class
	cd src ; for d in `find . -type d -name resources` ; do \
	  echo $$d ; \
	  mkdir -p `dirname ../class/$$d` ; \
	  rm -rf ../class/$$d ; \
          (cd ../class ; ln -s `echo $$d | sed 's,/[^/]*,/\.\.,g'`/src/$$d $$d ) ; \
	done

jar:	class
	jar -cvf lib/MedTagger.jar -C class cc/

srcjar:	class
	jar -cvf lib/MedTagger.jar src Makefile -C class cc/ 

class:
	mkdir -p class

html:
	mkdir -p html

clean:
	rm -rf class/* lib/unpack

echo-classpath:
	export CLASSPATH=$(Bioc)/class

# removed javadoc
.distfiles: FORCE jar 
	rm -rf $@
	echo HACKING >> $@
	echo LICENSE >> $@
	echo Makefile >> $@
	echo OTHER-SIMILAR-SOFTWARE.html >> $@
	echo README.html >> $@
	echo TODO >> $@
	echo README.ant >> $@
	echo build.xml >> $@
	find src -name '*.java' -not -path 'src/com/*' >> $@
	find src -path '*/resources/*' -type f  -not -path '*/CVS/*' >> $@   # include resource dirs -cas
	echo lib/*.jar lib/Makefile >> $@
	#find lib/jython -type f -not -path '*/CVS/*' >> $@
	#find scripts -type f -not -path '*/CVS/*' >> $@
	#echo doc/*.html >> $@
	# Include built jars.  Wildcards cannot be used below, for these files don't exist yet. -cas
#	echo dist/mallet.jar dist/mallet-deps.jar >> $@
#	if [ ! -z "$$BUILDING_GRMM"]; then echo dist/grmm-deps.jar >> $@; fi
	# include the javadocs
	#find $(JAVADOCS) -type f >> $@
 	# find the executables in bin/ directory to be included
	find bin -type f -maxdepth 1 -perm -a+x -not \( -path '*/CVS/*' -or -name 'prepend-license.sh' \) >> $@
#	if [ -z "$$BUILDING_GRMM" ]; then \
#	  grep -v mallet/grmm $@ > /tmp/$@ ; rm $@ ; mv /tmp/$@ $@ ; \
#	fi

dist/$(DISTNAME).tar.gz: .distfiles
	-mkdir dist
	# remove extant build directory
	rm -rf $(DISTNAME)
	# create temp build directory
	mkdir $(DISTNAME)
	# add other important files to dist dir for convenience
	cp lib/MedTagger-deps.jar lib/MedTagger.jar dist
	# copying files to build directory
	#cat .distfiles | xargs -n256 cp --preserve --link --parents --target-directory $(DISTNAME)
	tar --files-from .distfiles -cf - | (cd $(DISTNAME) ; tar -xpvf -)
	# tar build directory
	tar -chvf dist/$(DISTNAME).tar $(DISTNAME)
	# remove extant *.tar.gz file
	rm -f $(TARBALL)
	# gzip tar file
	gzip -9 dist/$(DISTNAME).tar
	# remove temp build directory
	rm -rf $(DISTNAME)

FORCE:


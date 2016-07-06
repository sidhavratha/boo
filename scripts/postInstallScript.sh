#!/bin/bash


for DEST in /usr/local/bin/dfstouch
do
  if [[ -f $DEST && ! -L $DEST ]] ; then
    mv $DEST $DEST.old
  fi

  # Install this version of dfstouch as one of alternative
  alternatives --install $DEST `basename $DEST` $DEST.${rpmversion} `echo ${rpmversion}| sed 's/\.//g'`
done

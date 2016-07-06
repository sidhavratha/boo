#!/bin/bash


for DEST in /usr/local/bin/boo
do
  if [[ -f $DEST && ! -L $DEST ]] ; then
    mv $DEST $DEST.old
  fi

  # Install this version of boo as one of alternative
  alternatives --install $DEST `basename $DEST` $DEST.${rpmversion} `echo ${rpmversion}| sed 's/\.//g'`
done

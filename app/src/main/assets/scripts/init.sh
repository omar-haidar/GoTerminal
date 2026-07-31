#set -e  # Exit immediately on Failure

#export PATH=/bin:/sbin:/usr/bin:/usr/sbin:/usr/share/bin:/usr/share/sbin:/usr/local/bin:/usr/local/sbin:/system/bin:/system/xbin
#export HOME=$ROOTFS/root

#if [ ! -s /etc/resolv.conf ]; then
#    echo "nameserver 8.8.8.8" > $ROOTFS/etc/resolv.conf
#fi


export PS1='\[\033[01;32m\]\u@reterm\[\033[00m\]:\[\033[01;34m\]\w\[\033[00m\]\$ '
# shellcheck disable=SC2034
export PIP_BREAK_SYSTEM_PACKAGES=1

#fix linker warning
if [[ ! -f /linkerconfig/ld.config.txt ]];then
    mkdir -p /linkerconfig
    touch /linkerconfig/ld.config.txt
fi

if [ "$#" -eq 0 ]; then
    #source $ROOTFS/etc/profile
    export PS1='\[\033[01;32m\]\u@reterm\[\033[00m\]:\[\033[01;34m\]\w\[\033[00m\]\$ '
    cd $HOME
else
    exec "$@"
fi
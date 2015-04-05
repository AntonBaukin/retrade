..\dev\rsync\sshpass.exe -p "%1" ..\dev\rsync\rsync.exe -e "..\dev\rsync\ssh.exe -oStrictHostKeyChecking=no" -rtz "--chmod=u=rwxs,g=rxs" "target/web/" "omicron@%2:/home/tomee/webapps/retrade/"
@rem "--chown=omicron:www"
@exit

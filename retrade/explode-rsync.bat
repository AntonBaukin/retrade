..\dev\rsync\sshpass.exe -p "%1" ..\dev\rsync\rsync.exe -rtz "--chown=omicron:www" "--chmod=g=rwx" -e "..\dev\rsync\ssh.exe -oStrictHostKeyChecking=no" "target/web/" "omicron@%2:/home/tomee/webapps/retrade/"
@exit

#!/bin/bash

version_key="version := \""

# TODO DRY these two
function get-version {
    version_key_len=`echo $version_key | wc -c`
    cat build.sbt | grep "$version_key" | cut -c ${version_key_len}- | rev | cut -c 2- | rev
}

function get-value-from-file {
    key="${1}"
    file="${2}"
    key_len=`echo ${key} | wc -c`
    cat "${file}" | grep "${key}" | cut -c ${key_len}-
}


# function to run a script remotely and not fall over if the pipe breaks and
# still recover the log
function run-script-remotely {
    user=`whoami`
    poll_time=5
    local OPTIND
    while getopts "h?s:S:l:u:p:H:w:" opt; do
        case "$opt" in
        h|\?)
            show_help
            exit 0
            ;;
        s)  script_path=$OPTARG
            script_name=`basename $script_path`
            ;;
        l)  script_log_path=$OPTARG
            ;;
        S)  ssh_args=$OPTARG
            ;;
        u)  echo "Setting user to $OPTARG"
            user=$OPTARG
            ;;
        p)  poll_time=$OPTARG
            ;;
        H)  host=$OPTARG
            ;;
        w)  remote_work_dir=$OPTARG
            ;;
        esac
    done

    if [ "$remote_work_dir" = "" ]; then
        remote_work_dir=/home/${user}
    fi

    job_ended_file=job-ended-`date +%s`

    tmp_script=/tmp/tmp-script-for-run-script-remotely.sh
    tmp_script_name=`basename $tmp_script`

    echo "#!/bin/bash" > $tmp_script
    echo "chmod +x $script_name" >> $tmp_script
    echo "$remote_work_dir/$script_name" >> $tmp_script
    echo "echo \$? > /tmp/$job_ended_file" >> $tmp_script

    echo "INFO (run-script-remotely): scp-ing scripts"
    scp ${ssh_args} ${script_path} ${user}@${host}:${remote_work_dir}/
    scp ${ssh_args} ${tmp_script} ${user}@${host}:${remote_work_dir}/
    ssh ${ssh_args} ${user}@${host} chmod +x ${remote_work_dir}/${tmp_script_name}

    echo "INFO (run-script-remotely): running script remotely"
    ssh ${ssh_args} ${user}@${host} "screen -dm bash -c \"${remote_work_dir}/${tmp_script_name}\""

    function grab-job-ended-file {
        scp ${ssh_args} ${user}@${host}:/tmp/${job_ended_file} /tmp/
        echo $?
    }

    echo "INFO (run-script-remotely): Will try to grab job ended file"
    grabbed_exit_code=`grab-job-ended-file`
    while [ "${grabbed_exit_code}" != 0 ]; do
        echo "INFO (run-script-remotely): Job still not ended"
        sleep ${poll_time}
        grabbed_exit_code=`grab-job-ended-file`
    done

    scp ${ssh_args} ${user}@${host}:${script_log_path} /tmp/
    echo "INFO (run-script-remotely): Remote logs:"
    cat /tmp/`basename ${script_log_path}`
    
    return `cat /tmp/${job_ended_file}`
}


import sys
import subprocess as sub

def main():
    strAppName = sys.argv[1]
    strPathToFile = sys.argv[2]
    execute(strAppName, strPathToFile)

def execute(appName, file):
    print "Execute file %s on %s" %(file,appName)

    if ".sh" not in file[len(file)-3:]:
        print("only able to execute .sh files")
        exit(1)

    print("Enable ssh for %s" %(appName))
    isEnabled = sub.check_output(["cf", "enable-ssh", appName])
    if "fail" in isEnabled.lower():
        print("Failed to enable ssh to %s. \nError: %s" %(appName, isEnabled))
        exit(1)

    ssh = sub.Popen(["cf", "ssh", appName,"-c" ,"bash %s" %file],
                    shell=False,
                    stdout=sub.PIPE,
                    stderr=sub.PIPE)

    error = ssh.stdout.readlines()

    if not error == []:
            print >>sys.stderr, "ERROR: %s" % error
            exit(1)
    else:
        print "Execute file %s successfully" %file

if __name__ == "__main__":
    main()

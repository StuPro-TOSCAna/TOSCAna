import subprocess as sub
import sys
import json
import ast

database_uri = ""
database_username = ""
database_port = ""
database_password = ""
database_name = ""
database_host = ""
strServiceInstanceName = ""

def main():
    print("\nStart readCredentials python script")
    strAppName = sys.argv[1]
    strService = sys.argv[2]
    strServiceType = str(sys.argv[3])
    global strServiceInstanceName
    strServiceInstanceName = str(sys.argv[4])
    
    # steps for a mysql service
    if strServiceType == "mysql":
        print("Read credentials from mysql service " + strService)
        readEnvironmentConfigFile(strAppName)
        read_mysql_credentials(strAppName, strService)
        createConfigureFile()
        
    return

def read_mysql_credentials(appName, serviceName):
    # required environment variable names for the database connection.
    # database_uri we set for safety's sake
    strDatabaseUri = "database_uri"
    strEnvDatabaseUser = env["cf_database_user_placeholder_" + strServiceInstanceName]
    strEnvDatabaseName = env["cf_database_name_placeholder_" + strServiceInstanceName]
    
    # convention that there is a environment database_host for the host
    strEnvDatabaseHost = "database_host"
    strEnvDatabasePort = env["3306"]
    strEnvDatabasePassword =  env["cf_database_password_placeholder_" + strServiceInstanceName]
    
    # find the VCAP_SERVICES block
    serviceBlock = get_Service_Env_Block_MySql(appName)

    # convert it to json and set environment variables
    print("Read service credentials from {}".format(serviceName))
    jsonEnv = json.loads(serviceBlock)
    global database_uri
    global database_username
    global database_port
    global database_password
    global database_name
    global database_host

    database_uri = jsonEnv["VCAP_SERVICES"][serviceName][0]["credentials"]["uri"]
    database_username = jsonEnv["VCAP_SERVICES"][serviceName][0]["credentials"]["username"]
    database_port = jsonEnv["VCAP_SERVICES"][serviceName][0]["credentials"]["port"]
    database_password = jsonEnv["VCAP_SERVICES"][serviceName][0]["credentials"]["password"]
    database_name = jsonEnv["VCAP_SERVICES"][serviceName][0]["credentials"]["name"]
    database_host = jsonEnv["VCAP_SERVICES"][serviceName][0]["credentials"]["hostname"]

    # set environment variables in the CF container
    sub.call(["cf" ,"set-env",appName, strDatabaseUri, database_uri])
    sub.call(["cf" ,"set-env",appName, strEnvDatabaseUser, database_username])
    sub.call(["cf" ,"set-env",appName, strEnvDatabaseName, database_name])
    sub.call(["cf" ,"set-env",appName, strEnvDatabaseHost, database_host])
    sub.call(["cf" ,"set-env",appName, strEnvDatabasePort, database_port])
    sub.call(["cf" ,"set-env",appName, strEnvDatabasePassword, database_password])

    # ensure the env variables changes take effect
    # sub.call(["cf", "restage", appName])
    return

def get_Service_Env_Block_MySql(appName):
    print("Request application '{}' for environment variables".format(appName))
    strBegin = "System-Provided:"
    strEnd = "VCAP_APPLICATION"
    env = sub.check_output(["cf","env", appName])
    begin = env.find(strBegin) + len(strBegin) + 1
    end = env.find(strEnd) - 5
    return env[begin:end]

def createConfigureFile():
    print("Create a mysql configuration file 'mysqlConfig.txt' for later connections")
    configFile = open("mysqlConfig.txt", "w")
    configFile.write("""{'user': '%s',
    'password': '%s',
    'host': '%s',
    'database': '%s',
    'raise_on_warnings': True,}""" %(database_username, database_password,
    database_host, database_name))

def readEnvironmentConfigFile(appName):
    print("Read " + appName + "_environment_config.txt")
    with open (appName + "_environment_config.txt", "r") as configFile:
        envConfig = configFile.read()
        global env
        env = ast.literal_eval(envConfig)
    
    
if __name__ == "__main__":
    main()

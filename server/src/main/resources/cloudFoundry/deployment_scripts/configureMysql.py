import ast
import sys

# needs a special python mysql connector installation
def main():
    print("\nStart configureMysql python script")
    try:
        print("Try to import mysql.connector")
        import mysql.connector
    except ImportError:
        print("Failed to load the Package mysql.connector. Please install it")
        exit(1)

    mysqlConfigFile = 'mysqlConfig.txt'
    # should be a sql file
    strConfigureFile = str(sys.argv[1])

    if not strConfigureFile.lower().endswith(".sql"):
        print("needs an .sql File for db init")
        exit(1)
    
    print("Try to execute SQL-File {} in database".format(strConfigureFile))

    try:
        with open(strConfigureFile, 'r') as dbinit_file:
            print("Read the sql configure file '{}'".format(strConfigureFile))
            dbinit_command = dbinit_file.read()
    except IOError as err:
        print("Failed to read dbinit File named {}. Where it is?".format(strConfigureFile))
        print("Error: {}".format(err))
        exit(1)

    try:
        with open(mysqlConfigFile, 'r') as content_file:
            print("Read mysql config file '{}'".format(mysqlConfigFile))
            mysql_config = content_file.read()
    except IOError as err:
        print("Failed to read mysqlConfigFile. Standard is {}. Where it is?".format(mysqlConfigFile))
        exit(1)

    config = ast.literal_eval(mysql_config)
    print("Connect to database")
    cnx = mysql.connector.connect(**config)
    cursor = cnx.cursor()
    create_schema(cursor, dbinit_command)
    cnx.close()
    return

def create_schema(cursor, creationCommand):
    import mysql.connector
    try:
        print("Try to execute your database init file")
        cursor.execute(creationCommand)
        cursor.close()
        print("Create schema successfully")
        return
    except mysql.connector.Error as err:
        print("Failed creating db schema: {}".format(err))
        print("Used instruction: {}".format(creationCommand))
        exit(1)
    return

if __name__ == "__main__":
    main()

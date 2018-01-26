<<<<<<< HEAD
import mysql.connector
=======
>>>>>>> master
import ast
import sys

# needs a special python mysql connector installation
def main():
<<<<<<< HEAD
=======
    try:
        import mysql.connector
    except ImportError:
        print("Failed to load the Package mysql.connector. Please install it")
        exit(1)

>>>>>>> master
    mysqlConfigFile = 'mysqlConfig.txt'
    # should be a sql file
    strConfigureFile = str(sys.argv[1])

<<<<<<< HEAD
    if ".sql" not in strConfigureFile[len(strConfigureFile)-4:]:
        print("needs an .sql File for db init")
        exit(1)
=======
    if not strConfigureFile.lower().endswith(".sql"):
        print("needs an .sql File for db init")
        exit(1)
    
     print("Try to execute SQL-File {} in database".format(strConfigureFile))
>>>>>>> master

    try:
        with open(strConfigureFile, 'r') as dbinit_file:
            dbinit_command = dbinit_file.read()
    except IOError as err:
<<<<<<< HEAD
        print("Failed to read dbinit File named %s. Where it is?" %(strConfigureFile))
=======
        print("Failed to read dbinit File named {}. Where it is?".format(strConfigureFile))
>>>>>>> master
        exit(1)

    try:
        with open(mysqlConfigFile, 'r') as content_file:
            mysql_config = content_file.read()
    except IOError as err:
<<<<<<< HEAD
        print("""Failed to read mysqlConfigFile. Standard is %s. Where it is?""" %(mysqlConfigFile))
=======
        print("Failed to read mysqlConfigFile. Standard is {}. Where it is?".format(mysqlConfigFile))
>>>>>>> master
        exit(1)

    config = ast.literal_eval(mysql_config)
    cnx = mysql.connector.connect(**config)
    cursor = cnx.cursor()
    create_schema(cursor, dbinit_command)
    cnx.close()
    return

def create_schema(cursor, creationCommand):
    try:
        cursor.execute(creationCommand)
        print("Create schema successfully")
        return
    except mysql.connector.Error as err:
        print("Failed creating db schema: {}".format(err))
        exit(1)
    return

if __name__ == "__main__":
    main()

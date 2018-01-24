import sys
import fileinput

def main():
    strFileName = sys.argv[1]
    strFind = sys.argv[2]
    strReplace = sys.argv[3]
    replaceInFile(strFileName, strFind, strReplace)

def replaceInFile(fileName, strFind, strReplace):
    sourceFile = open(fileName, "r")
    content_file = sourceFile.readlines()

    cleaned_content_file = []

    for line in content_file:
        cleaned_content_file.append(line.replace(strFind, strReplace))

    cleaned_content = ''.join(cleaned_content_file)

    sourceFile = open(fileName, "w")
    sourceFile.write(cleaned_content)
    print('Replace all occurring "%s" in File "%s" to "%s"'%(strFind, fileName, strReplace))

if __name__ == "__main__":
    main()

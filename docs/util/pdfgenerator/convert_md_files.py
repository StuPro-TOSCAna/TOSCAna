import os
import glob
import subprocess
import re
import sys

source_folder = os.path.abspath('source/')
dest_folder = sys.argv[1] + '/'

def transform_to_rst(path):
    filename = os.path.splitext(os.path.basename(path))[0]
    dirname = os.path.dirname(path)
    target = os.path.join(dirname, filename+'.rst')
    process = subprocess.call(['pandoc', '-f', 'markdown+auto_identifiers', '-t', 'rst', '-o', target, path],stdout=subprocess.PIPE)
    process = subprocess.call(['rm', '-r', path])

def setup_folder():
    if(os.path.exists(source_folder)):
        subprocess.call(['rm', '-r', source_folder],stdout=subprocess.PIPE)
    process = subprocess.call(['rsync', '-r', dest_folder, source_folder],stdout=subprocess.PIPE)

def convert_md_files():
    md_file_paths = glob.glob(os.path.join(source_folder, '**/*.md'), recursive=True)
    for path in md_file_paths:
        transform_to_rst(path)

def main():
    setup_folder()
    convert_md_files()



if __name__ == '__main__':
    main()

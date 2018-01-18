# checks if given program is available in path
# $1: name of the bucket 
# $2: objectKey for the file
# $3: filepath
function uploadFile () {
# TODO catch exception?
  echo "Uploading file "$2"."
  aws s3api put-object --bucket $1 --key $2 --body $3
}

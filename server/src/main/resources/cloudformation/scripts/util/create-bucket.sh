# creates an S3Bucket with the given name with the AWS CLI
# $1: name of the bucket
# $2: region where the bucket should be created
function createBucket () {
# TODO catch exception?
  echo "Creating the bucket "$1"."
  aws s3api create-bucket --bucket $1 --region $2 --create-bucket-configuration LocationConstraint=$2
}

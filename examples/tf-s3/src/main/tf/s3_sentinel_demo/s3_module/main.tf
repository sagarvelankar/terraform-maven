resource "aws_s3_bucket" "bucket2" {
  bucket = "tftest-bucket2"

  versioning {
    enabled = true
  }

  tags = {
    application_id = "cna"
    stack_name = "stacked"
    created_by = "rhutto@deliveredtech.com"
  }
}

module "bucket3" {
  source = "../s3_submodule"
}

output "bucket_names" {
  value = [ aws_s3_bucket.bucket2.bucket , module.bucket3.bucket_name ]
}

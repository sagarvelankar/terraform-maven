provider "aws" {
  region = "us-east-1"
}

resource "aws_s3_bucket" "bucket1" {
  bucket = "tftest-bucket1"

  versioning {
    enabled = true
  }

  tags = {
    application_id = "cna"
    stack_name = "stacked"
    created_by = "rhutto@deliveredtech.com"
  }
}

module "bucket2" {
  source = "./s3_module"
}

output "bucket_names" {
  value = concat( [ aws_s3_bucket.bucket1.bucket ], module.bucket2.bucket_names )
}

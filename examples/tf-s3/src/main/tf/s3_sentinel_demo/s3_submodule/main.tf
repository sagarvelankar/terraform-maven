resource "aws_s3_bucket" "bucket3" {
  bucket = "tftest-bucket3"

  versioning {
    enabled = true
  }

  tags = {
    application_id = "cna"
    stack_name = "stacked"
    created_by = "rhutto@deliveredtech.com"
  }
}

output "bucket_name" {
  value = aws_s3_bucket.bucket3.bucket
}

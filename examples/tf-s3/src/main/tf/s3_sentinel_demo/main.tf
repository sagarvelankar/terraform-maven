provider "aws" {
  region = "us-east-1"
}

resource "aws_s3_bucket" "bucket1" {
  bucket = "bucket1"

  versioning {
    enabled = true
  }

  tags = {
    application_id = "cna"
    stack_name = "stacked"
    termination_date = "NA"
    name = "dejavu"
    created_by = "codeposse@dtcc.com"
    data_class = "magenta"
    environment = "test"
    description = "who let dogs out ruf ruf"
  }

}

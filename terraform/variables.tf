variable "aws_region" {
  description = "AWS region for resources"
  default     = "us-east-1"
}

variable "instance_type" {
  description = "EC2 instance type"
  default     = "t3.micro"
}

variable "key_name" {
  description = "EC2 key pair name"
  default     = "textbook-library-key"
}

variable "app_port" {
  description = "Application port"
  default     = 3001
}

variable "aws_access_key" {
    type        = string
    description = "Aws acmpca access key"
    sensitive   = true
}

variable "aws_secret_key" {
    type        = string
    description = "Aws acmpca secret key"
    sensitive   = true
}

variable "certificate_authority_arn" {
    type        = string
    description = "certificate authority arn"
    sensitive   = true
}
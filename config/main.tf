terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.16"
    }
    local = {
      source = "hashicorp/local"
    }
    kubernetes = {
      source = "hashicorp/kubernetes"
      version = "2.16.1"
    }
  }
}
#allow access to aws acmpca
provider "aws" {
  region  = "eu-west-2"
  access_key = var.aws_access_key
  secret_key = var.aws_secret_key
}
#Create private key
resource "tls_private_key" "key" {
  algorithm = "RSA"
}
#Create csr
resource "tls_cert_request" "csr" {
  private_key_pem = tls_private_key.key.private_key_pem

  subject {
    common_name = "callisto-timecard"
  }
}

#Create certificate with csr
resource "aws_acmpca_certificate" "cert_sign_request" {
  certificate_authority_arn   = var.certificate_authority_arn
  certificate_signing_request = tls_cert_request.csr.cert_request_pem
  signing_algorithm           = "SHA256WITHRSA"
  validity {
    type  = "MONTHS"
    value = 1
  }
}
#Create private key as local file
resource "local_file" "private_key" {
  content = tls_private_key.key.private_key_pem
  filename = "timecard-key.pem"
}
#Create certificate as local file
resource "local_file" "certificate" {
  content = join("\n", [aws_acmpca_certificate.cert_sign_request.certificate, aws_acmpca_certificate.cert_sign_request.certificate_chain])
  filename = "timecard-certificate.pem"
}


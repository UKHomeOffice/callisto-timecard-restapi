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

provider "aws" {
  region  = "eu-west-2"
  access_key = var.aws_access_key
  secret_key = var.aws_secret_key
}

resource "tls_private_key" "key" {
  algorithm = "RSA"
}

resource "tls_cert_request" "csr" {
  private_key_pem = tls_private_key.key.private_key_pem

  subject {
    common_name = "callisto-timecard"
  }
}

resource "aws_acmpca_certificate" "cert_sign_request" {
  certificate_authority_arn   = var.certificate_authority_arn
  certificate_signing_request = tls_cert_request.csr.cert_request_pem
  signing_algorithm           = "SHA256WITHRSA"
  validity {
    type  = "MONTHS"
    value = 1
  }
}

resource "local_file" "private_key" {
  content = tls_private_key.key.private_key_pem
  filename = "timecard-key.pem"
}

resource "local_file" "certificate" {
  content = join("\n", [aws_acmpca_certificate.cert_sign_request.certificate, aws_acmpca_certificate.cert_sign_request.certificate_chain])
  filename = "timecard-certificate.pem"
}

#provider "kubernetes" {
#  config_path    = "~/.kube/config"
#  config_context   = "minikube"
#}

#data "kubernetes_namespace" "callisto-namespace" {
#  metadata {
#    name = "callisto-dev"
#  }
#}
#
#resource "kubernetes_secret" "some-secret" {
#  metadata {
#    name      = "callisto-timecard-kafkaCert"
#  }
#  data = {
#    "cert-arn" = "secret"
#  }
#}


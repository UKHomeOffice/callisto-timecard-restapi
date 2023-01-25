set -e

dir=/timecard-restapi-keystore
alias=$1
days=100
ca_arn=$2

cd $dir

export AWS_ACCESS_KEY_ID=$3
export AWS_SECRET_ACCESS_KEY=$4
export AWS_DEFAULT_REGION=eu-west-2

if test -f "$alias-certificate.pem";
then
    echo "Certificate already created exiting"
    exit 0
fi

echo "Creating new certificate"

# Create cert signed by CA
if
  ARN=$(aws acm-pca issue-certificate --certificate-authority-arn $ca_arn --csr fileb://$alias.csr --signing-algorithm "SHA256WITHRSA" --validity Value=$days,Type="DAYS" --output text)
then
  echo "Arn Stored as env variable"
else echo "Arn not stored"
exit 1
fi

aws acm-pca wait certificate-issued --certificate-authority-arn $ca_arn --certificate-arn $ARN
if [ $? -eq 255 ]
then
  echo "Certificate not issued"
  exit 1
else
  echo "Certificate issued" >&2
fi

# Get Certificate from arn
if
  aws acm-pca get-certificate --certificate-authority-arn $ca_arn --certificate-arn $ARN | tr -d \" > $alias-certificate.pem
  sed '1d;s/\    Certificate: //g;s/\    CertificateChain: //g;s/,//g;$d;s/\\n/\n/g' $alias-certificate.pem > $alias-certificate-temp.pem && mv $alias-certificate-temp.pem $alias-certificate.pem
then
  echo "Certificate retrieved"
else echo "Retrieving certificate failed"
exit 1
fi

set -e

dir=/timecard-restapi-keystore
alias=$1
days=100
password=$2
ca_arn=$3
root_dir=$(pwd)

cd $dir

export AWS_ACCESS_KEY_ID=$4
export AWS_SECRET_ACCESS_KEY=$5
export AWS_DEFAULT_REGION=eu-west-2

if test -f "$dir/$alias.keystore.jks";
then
    echo "Certififcarte already created, checking validity..."
  if openssl x509 -checkend 86400 -noout -in ${alias}_certificate.pem
    then
      echo "Certificate is valid, exiting"
      exit
    fi
fi

echo "Creating new certificate"

# Create a private key
if
  openssl req -newkey rsa:2048 -nodes -keyout $alias-key.pem -subj "/CN=Accruals-Key" -out $alias.csr
then
  echo "Created private key"
else echo "Creating private key failed"
exit
fi

# Create cert signed by CA
if
  ARN=$(aws acm-pca issue-certificate --certificate-authority-arn $ca_arn --csr fileb://$alias.csr --signing-algorithm "SHA256WITHRSA" --validity Value=$days,Type="DAYS" --output text)
then
  echo "Arn Stored as env variable"
else echo "Arn not stored"
exit
fi

aws acm-pca wait certificate-issued --certificate-authority-arn $ca_arn --certificate-arn $ARN
if [ $? -eq 255 ]
then
  echo "Certificate not issued"
  exit
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
exit
fi
# Import cert into keystore
#if
#  keytool -keystore $alias.keystore.jks -alias Callisto -import -noprompt -file $alias-certificate.pem -storepass $password -keypass $password
#then
#  echo "stored certificate in keystore"
#else echo "Failed to store certificate in keystore"
#exit
#fi
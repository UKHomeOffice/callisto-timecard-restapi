set -e

dir=/timecard-restapi-keystore
alias=$1
days=100
password=$2
ca_arn=$3
root_dir=$(pwd)

#aws configure set aws_access_key_id $4
#aws configure set aws_secret_access_key $5
#aws configure set aws_region eu-west-2

cd $dir
ls -ltr

export AWS_ACCESS_KEY_ID=$4
export AWS_SECRET_ACCESS_KEY=$5
export AWS_DEFAULT_REGION=eu-west-2

if test -f "${alias}_certificate.pem";
then
    echo Certififcarte created, checking validity...
  if openssl x509 -checkend 86400 -noout -in ${alias}_certificate.pem
    then
      echo "Certificate is valid, exiting"
      exit
    fi
fi
#Check for keystore?? //truststore?


echo "Certificate has expired"

echo "Creating new certificate"

# Create a private key
if
  keytool -genkey -validity $days -alias $alias -dname "C=GB, O=UK Home Office, CN=Callisto $alias" -keystore $alias.keystore.jks -keyalg RSA -storepass $password -keypass $password
then
  echo "Created private key"
else echo "Creating private key failed"
exit
fi

ls -la
# Create CSR
if
  keytool -keystore $alias.keystore.jks -alias $alias -certreq -file $alias.csr -storepass $password -keypass $password
  sed -e 's/\ NEW//g' $alias.csr > $alias_temp.csr && mv $alias_temp.csr $alias.csr
then
  echo "Created CSR"
else echo "Creating CSR failed"
exit
fi

ls -la
# Create cert signed by CA
if
  ARN=$(aws acm-pca issue-certificate --certificate-authority-arn $ca_arn --csr fileb://$alias.csr --signing-algorithm "SHA256WITHRSA" --validity Value=$days,Type="DAYS" --output text)
  echo $ARN
then
  echo "Arn Stored as env variable"
else echo "Arn not stored"
exit
fi

aws acm-pca wait certificate-issued --certificate-authority-arn $ca_arn --certificate-arn $ARN
if [ $? -eq 255 ]
then
  echo "Certificate not issue"
  exit
else
  echo "Certificate issued" >&2
fi

# Get Certificate from arn
if
  aws acm-pca get-certificate --certificate-authority-arn $ca_arn --certificate-arn $ARN | jq '.Certificate, .CertificateChain' | sed 's/\\n/\n/g' | tr -d \" > $alias-certificate.pem
then
  echo "Certificate retrieved"
else echo "Retrieving certificate failed"
exit
fi

# Import cert into keystore
if
  keytool -keystore $alias.keystore.jks -alias Callisto -import -noprompt -file $alias.pem -storepass $password -keypass $password
then
  echo "stored certificate in keystore"
else echo "Failed to store certificate in keystore"
exit
fi

# cp truststore into desired location
if
  cp /opt/openjdk-17/lib/security/cacerts $alias.truststore.jks
then
  echo "Truststore copied"
fi


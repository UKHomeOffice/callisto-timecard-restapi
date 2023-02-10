set -e

service_alias=$1
keystore_dir=$2
days=100
ca_arn=$3
bootstrap_server_url=$4
password=$5

export AWS_ACCESS_KEY_ID=$6
export AWS_SECRET_ACCESS_KEY=$7
export AWS_DEFAULT_REGION=eu-west-2

cd $keystore_dir

#Create private key & csr
echo "Creating private key & csr"
if openssl req -newkey rsa:2048 -nodes -keyout $service_alias-key.pem -subj "/CN=$service_alias-producer" -out $service_alias.csr
 then
  echo "Created private key & CSR file"
   else
  echo "Creating private key & CSR file failed"
   exit 1
fi

# issue certificate
if
  CERTIFICATE_ARN=$(aws acm-pca issue-certificate --certificate-authority-arn $ca_arn --csr fileb://$service_alias.csr --signing-algorithm "SHA256WITHRSA" --validity Value=$days,Type="DAYS" --output text)
then
  echo "Arn Stored as env variable"
else
  echo "Arn not stored"
  exit 1
fi

# wait for certificate to be issued
aws acm-pca wait certificate-issued --certificate-authority-arn $ca_arn --certificate-arn $CERTIFICATE_ARN
if [ $? -eq 255 ]
then
  echo "Certificate not issued"
  exit 1
else
  echo "Certificate issued" >&2
fi

# Get Certificate from arn
if
  aws acm-pca get-certificate --certificate-authority-arn $ca_arn --certificate-arn $CERTIFICATE_ARN | jq '.Certificate, .CertificateChain' | sed 's/\\n/\n/g' | tr -d \" > $service_alias-certificate.pem
then
  echo "Certificate retrieved"
else
  echo "Retrieving certificate failed"
  exit 1
fi

# Test Connection
if
  openssl s_client -connect $bootstrap_server_url -key $service_alias-key.pem -cert $service_alias-certificate.pem -brief
then
  echo "Connection to msk succesful"
else
  echo "Connection to msk failed"
  exit 1
fi

#Create p12 file
if openssl pkcs12 -export -in $service_alias-certificate.pem -inkey $service_alias-key.pem  -passout pass:$password -name shared > $service_alias-key-pair.p12
then
echo "P12 file created succesfully"
else
  echo "P12 file creation failed"
  exit 1
fi

# Import cert into keystore
if
  keytool -importkeystore -srckeystore $service_alias-key-pair.p12 -srcstorepass $password -destkeystore $service_alias.keystore.jks -srcstoretype pkcs12 -alias shared -storepass $password -keypass $password
then
  echo "stored certificate in keystore"
  echo "Ready for kafka operations"
else
  echo "Failed to store certificate in keystore"
  exit 1
fi

echo "Success!!"
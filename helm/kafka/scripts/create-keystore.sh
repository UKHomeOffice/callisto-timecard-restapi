set -e

service_alias=$1
keystore_dir=/timecard-restapi-keystore
days=100
ca_arn=$2
bootstrap_server_url=$3
password=$4

export AWS_ACCESS_KEY_ID=$5
export AWS_SECRET_ACCESS_KEY=$6
export AWS_DEFAULT_REGION=eu-west-2

cd $keystore_dir

if test -f "$service_alias-certificate.pem";
then
  echo "Certificate already created, checking validity..."
    if openssl x509 -checkend 86400 -noout -in $service_alias-certificate.pem
    then
      echo "Certificate is valid, exiting"; exit 0;
    fi
fi

if test -f "$service_alias-key.pem"
then
  echo "removing private key"
  rm $service_alias-key.pem
fi

if test -f "$service_alias.csr"
then
  echo "removing csr"
  rm $service_alias.csr
fi

if test -f "$service_alias-certificate.pem"
then
  echo "removing certificate"
  rm $service_alias-certificate.pem
fi

if test -f "$service_alias.keystore.jks"
 then
  echo "removing keystore"
  rm $service_alias.keystore.jks
fi

#Create private key & csr
echo "Creating private key & csr"
if openssl req -newkey rsa:2048 -nodes -keyout $service_alias-key.pem -subj "/CN=timecard-Key" -out $service_alias.csr
 then
  echo "Created private key & CSR file"
   else
  echo "Creating private key & CSR file failed"
   exit 1
fi

# issue certificate
if
  CERTIFICATE_ARN=$(aws acm-pca issue-certificate --certificate-authority-arn $ca_arn --csr fileb://$service_alias.csr --signing-algorithm "SHA256WITHRSA" --validity Value=100,Type="DAYS" --output text)
  echo $CERTIFICATE_ARN
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
  aws acm-pca get-certificate --certificate-authority-arn $ca_arn --certificate-arn $CERTIFICATE_ARN | tr -d \" > $service_alias-certificate.pem
  sed '1d;s/\    Certificate: //g;s/\    CertificateChain: //g;s/,//g;$d;s/\\n/\n/g' $service_alias-certificate.pem > $service_alias-certificate-temp.pem && mv $service_alias-certificate-temp.pem $service_alias-certificate.pem
  openssl x509 -checkend 86400 -in $service_alias-certificate.pem ; then
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
  keytool -importkeystore -srckeystore $service_alias-key-pair.p12 -srcstorepass $password -destkeystore $service_alias.keystore.jks -srcstoretype pkcs12 -service_alias shared -storepass $password -keypass $password
then
  echo "stored certificate in keystore"
  echo "Ready for kafka operations"
else
  echo "Failed to store certificate in keystore"
  exit 1
fi

echo "Success!!"
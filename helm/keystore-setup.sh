set -e

dir=keystore
alias=$1
days=100
password=$2
ca_arn=$3
aws_access_key_id=$4
aws_secret_access_key=$5
aws_region=eu-west-2

chmod 777 .aws
cd .aws
which aws
ls -ltr

aws configure set aws_access_key_id $4
aws configure set aws_secret_access_key $5
aws configure set aws_region eu-west-2

if test -f "$dir/${alias}_certificate.pem";
then
    echo Certififcarte created, checking validity...
  if openssl x509 -checkend 86400 -noout -in ${dir}/${alias}_certificate.pem
    then
      echo "Certificate is valid, exiting"
      exit
    fi
fi

#Check for keystore??

echo "Certificate has expired"

rm -r ${dir}/${alias}_certificate.pem

echo "Creating new certificate"

rm -rf $dir
mkdir -p $dir

# Create a private key
if
  keytool -genkey -validity $days -alias $alias -dname "C=GB, O=UK Home Office, CN=Callisto $alias" -keystore $dir/$alias.keystore.jks -keyalg RSA -storepass $password -keypass $password
then
  echo "Created private key"
else echo "Creating private key failed"
exit
fi

# Create CSR
if
  keytool -keystore $dir/$alias.keystore.jks -alias $alias -certreq -file $dir/$alias.csr -storepass $password -keypass $password
  sed -e 's/\ NEW//g' $dir/$alias.csr > $dir/$alias_temp.csr && mv $dir/$alias_temp.csr $dir/$alias.csr
then
  echo "Created CSR"
else echo "Creating CSR failed"
exit
fi

# Create cert signed by CA
if
  ARN=$(aws acm-pca issue-certificate --certificate-authority-arn $ca_arn --csr fileb://$dir/$alias.csr --signing-algorithm "SHA256WITHRSA" --validity Value=$days,Type="DAYS" --profile pca --output text)
  kubectl create secret generic callisto-timecard-acmpca --from-literal=certificate_arn=$ARN
then
  echo "Arn Stored"
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
  aws acm-pca get-certificate --certificate-authority-arn $ca_arn --certificate-arn $ARN | jq '.Certificate, .CertificateChain' | sed 's/\\n/\n/g' | tr -d \" > $dir/$alias-certificate.pem
then
  echo "Certificate retrieved"
else echo "Retrieving certificate failed"
exit
fi

# Import cert into keystore
if
  keytool -keystore $dir/$alias.keystore.jks -alias Callisto -import -noprompt -file $dir/$alias.pem -storepass $password -keypass $password
then
  echo "stored certificate in keystore"
else echo "Failed to store certificate in keystore"
exit
fi

# cp truststore into desired location
if
  cp /opt/openjdk-17/lib/security/cacerts $dir/$alias.truststore.jks
then
  echo "Truststore copied"
fi


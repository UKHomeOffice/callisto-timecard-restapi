set -e


dir=/timecard-restapi-keystore
alias=$1
days=100
password=$2
bootstrap=$3

cd $dir

if test -f "$alias.keystore.jks";
then
    echo "Keystore already created, exiting ..."
    exit 0
fi

# Test Connection
if
  openssl s_client -connect $3 -key $alias-key.pem -cert $alias-certificate.pem -brief
then
  echo "Connection to msk succesful"
else echo "Connection to msk failed"
exit 1
fi

#Create p12 file
if
  openssl pkcs12 -export -in $alias-certificate.pem -inkey $alias-key.pem  -passout pass:$password -name shared > $alias-key-pair.p12
then
  echo "P12 file created succesfully"
else echo "P12 file creation failed"
exit 1
fi

# Import cert into keystore
if
  keytool -importkeystore -srckeystore $alias-key-pair.p12 -srcstorepass $password -destkeystore $alias.keystore.jks -srcstoretype pkcs12 -alias shared -storepass $password -keypass $password
then
  echo "stored certificate in keystore"
  echo "Ready for kafka operations"
else echo "Failed to store certificate in keystore"
exit
fi
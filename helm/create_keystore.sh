#!/usr/bin/env sh
set -e

dir=/keystore/$1
alias=$1
days=14600
password=changeit

if test -f "$dir/${alias}_creds"; then
    echo Keystore already created
    exit
fi

rm -rf $dir
mkdir -p $dir

# Create a private key
keytool -genkey -validity $days -alias $alias -dname "C=GB, O=UK Home Office, CN=Callisto $alias" -keystore $dir/$alias.keystore.jks -keyalg RSA  -storepass $password  -keypass $password -storetype pkcs12

# Create CSR
keytool -keystore $dir/$alias.keystore.jks -alias $alias -certreq -file $dir/$alias.csr -storepass $password -keypass $password

#TODO - will need to find the args to set the details automatically.
aws configure --profile pca

# Create cert signed by CA
aws acm-pca issue-certificate --certificate-authority-arn $cert_arn --csr fileb://$dir/$alias.csr --signing-algorithm "SHA256WITHRSA" --validity Value=300,Type="DAYS" --profile pca
# TODO - this command print a arn to the command line. We will need to extract that

# Get Certificate from arn
aws acm-pca get-certificate --certificate-authority-arn $cert_arn --certificate-arn <Insert CertificateArn value from previous step> --profile pca | jq '.Certificate, .CertificateChain' | sed 's/\\n/\n/g' | tr -d \" > certificate.pem

# Import cert into keystore
keytool -keystore $dir/$alias.keystore.jks -alias Callisto -import -noprompt -file ./certificate.pem-storepass $password -keypass $password

# cp truststore into desired location
cp /opt/openjdk-17/lib/security/cacerts kafka.client.truststore.jks

echo -n $password > $dir/${alias}_creds

# Inspect keystore contents
# keytool -list -v -keystore $dir/$alias.keystore.jks -storepass $password
# keytool -list -v -keystore $dir/$alias.truststore.jks -storepass $password
set -e

dir=/timecard-restapi-keystore
alias=$1

cd $dir

if test -f "$dir/${alias}-certificate.pem";
then
    echo "Certificate already created, checking validity..."
  if openssl x509 -checkend 86400 -noout -in ${alias}_certificate.pem
    then
      echo "Certificate is valid, exiting"
      exit
    fi
fi

rm $alias-key.pem
rm $alias.csr
rm $alias-certificate.pem
rm $alias.keystore.jks

echo "Creating private key & csr"
if
  openssl req -newkey rsa:2048 -nodes -keyout $alias-key.pem -subj "/CN=timecard-Key" -out $alias.csr
then
  echo "Created private key & CSR file"
else echo "Creating private key & CSR file failed"
exit
fi

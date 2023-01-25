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

if test -f "$dir/$alias-key.pem";
then
  echo "removing private key"
  rm $alias-key.pem
fi

if test -f "$dir/$alias.csr";
then
  echo "removing csr"
  rm $alias.csr
fi

if test -f "$dir/$alias-certificate.pem";
then
  echo "removing certificate"
  rm $alias-certificate.pem
fi

if test -f "$dir/$alias.keystore.jks";
then
  echo "removing keystore"
  rm $alias.keystore.jks
fi

echo "Creating private key & csr"
if
  openssl req -newkey rsa:2048 -nodes -keyout $alias-key.pem -subj "/CN=timecard-Key" -out $alias.csr
then
  echo "Created private key & CSR file"
else echo "Creating private key & CSR file failed"
exit
fi

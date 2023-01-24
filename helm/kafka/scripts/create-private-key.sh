set -e

dir=/timecard-restapi-keystore
alias=$1

cd $dir

if test -f "$dir/$alias-key.pem";
then
    echo "Private key already created, exiting ..."
    exit
fi

echo "Creating private key & csr"
if
  openssl req -newkey rsa:2048 -nodes -keyout $alias-key.pem -subj "/CN=timecard-Key" -out $alias.csr
then
  echo "Created private key & CSR file"
else echo "Creating private key & CSR file failed"
exit
fi

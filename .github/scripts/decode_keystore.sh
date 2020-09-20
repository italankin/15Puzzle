#!/usr/bin/env sh

SECRET=$1

OUTPUT='app/keystore-release'
mkdir -p $OUTPUT

echo "$SECRET" | base64 -d > secret_plain || exit 1
gpg --quiet --batch --yes --decrypt --passphrase="$KEYSTORE_ENC_KEY" \
--output "$OUTPUT/release.keystore" secret_plain || exit 1
rm secret_plain

PROPS="$OUTPUT/release.properties"
echo "keystore=keystore-release/release.keystore" > $PROPS
echo "keystore_password=$KEYSTORE_PASSWORD" >> $PROPS
echo "alias=$KEYSTORE_ALIAS" >> $PROPS
echo "alias_password=$KEYSTORE_ALIAS_PASSWORD" >> $PROPS

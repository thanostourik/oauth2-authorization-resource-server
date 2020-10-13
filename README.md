Simple starter project with Authorization and Resource servers setup.

# Prerequisites
- JDK 11

# Preparation
### Setting up the RSA key
There is already a private/public key pair provided in `resources/keystore.jks`

To create a new one do:
```bash
keytool -genkeypair -alias <key_alias> -keyalg RSA -keypass <key_password> -keystore <key_store>.jks -storepass <key_password>
```
And update the properties in application.yml
```yaml
oauth2jwt:
  authorization-server:
    rsa:
      alias: <key_alias>
      password: <key_password>
      keystore: <key_store>.jks
```

### Setting up the auth user
Default user credentials:
```yaml
username: thanos.tourik@gmail.com
password: thanos.tourik
```

To add more users edit `resources/data.sql`
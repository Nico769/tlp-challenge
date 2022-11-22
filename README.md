# tlp-challenge

This project uses Quarkus. If you want to run the application in [Quarkus dev mode](https://quarkus.io/guides/getting-started#development-mode), you have to:

1. Clone the repository

```sh
git clone https://github.com/Nico769/tlp-challenge.git
```

2. Go to the cloned repository and run

```sh
./mvnw compile quarkus:dev
```

3. You can now reach the application at `http://localhost:8080`. Please read the [CustomerResource](src/main/java/io/landolfi/customer/CustomerResource.java) and [DeviceResource](src/main/java/io/landolfi/device/DeviceResource.java) classes to see the available REST operations.
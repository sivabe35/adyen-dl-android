# AdyenDL for Android
This repository contains Adyen's Directory Lookup (DL) library for Android. With DL you can dramatically improve payment experience for your shoppers by providing payments methods that are popular in their country. 

Adyen allows you to dynamically fetch the list of currently available methods based on shoppers country, currency and amount. After a shopper selects the preferred payment method, the SDK provides a redirect URL. Then, if a payment method's app is installed on a shopper's mobile device, this app should be opened to complete the payment; or if the app isn't present - the payment is processed using the mobile-optimized web flow of the selected payment method.

This library is suited to accept [250+ local payment methods](https://www.adyen.com/payment-methods) with Adyen. For accepting card payments, please make use of the [Client-Side Encryption library](https://github.com/Adyen/adyen-cse-android), which enables you to capture card details fully in-app.


## Requirements
The AdyenDL-Android library is written in Java and is compatible with apps supporting Android 4.4 and up. 

Although most of the integration complexity is wrapped in this library, you also need to set up a merchant server to validate the integrity of each payment request/response. Please find an example of the API for the merchants server [here](https://github.com/Adyen/adyen-checkout-ios/tree/master/ServerSideExample/Parse).

## Example

For your convenience this repository contains an example app, which can be used as a reference while integrating.

To run the example project, clone the repo and run it from your IDE.

## Installation

AdyenDL is available through a gradle task. To install it, follow the steps below:

1. In your `build.gradle` of the root directory add the following line:
    
    ```json
    buildscript {
        repositories {
            ...
        }
        dependencies {
            ...
            classpath 'de.undercouch:gradle-download-task:2.1.0'
        }
    }
    ```
    This plugin will allow Gradle to download the AdyenDL library.
    
2. In your `build.gradle` of the app module add the following task:

    ```json
    import de.undercouch.gradle.tasks.download.Download
    
    ...
    
    task downloadAdyenLibrary(type: Download) {
        src 'https://raw.githubusercontent.com/Adyen/adyen-dl-android/master/adyendl/adyendl-1.0.0.aar'
        dest('libs');
    }
    ```
   Once you run this task using `./gradlew downloadAdyenLibrary` command, the `adyendl` .aar file will be downloaded in your `libs` folder.
   
3. Next, add the following snippet in your `build.gradle` of the app module:

    ```json
    repositories {
        flatDir {
            dirs 'libs'
        }
    }
    ```

## Usage

Create environment configuration and set up a payment processor.

```java
configuration = new Configuration(Environment.LIVE, "http://www.{YourServer}.com/api.php", null, null);
```

Create a payment object.

```java
payment = new Payment();

payment.setMerchantReference("Reference");
payment.setCountryCode("NL");
payment.setCurrency("EUR");
payment.setAmount(199);
```

Fetch a list of available payment methods.

```java
JSONObject paymentMethodsJSON = PaymentsProcessor.getInstance().fetchPaymentMethods(configuration, payment);

//  Present a received list of methods on a screen.
//  ...
````

Fetch a payment URL for the desired payment method.

```java
String redirectUrlStr = PaymentsProcessor.getInstance().fetchRedirectUrl(configuration, payment, paymentMethod.getBrandCode(), null);

//  Open a received payment URL in a browser to continue payment flow.
//  ...
```


## License

AdyenDL is available under the MIT license. See the LICENSE file for more info.


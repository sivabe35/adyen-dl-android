# AdyenDL merchant server example
In the client side AdyenDL SDK a reference is made to three endpoints that should be implemented on the merchant server. This document aims to explain how these endpoints can be created, and contains PHP examples (referred to as ```api.php```) of the actual implementation. Please note that this sample code relies on the fact that you use your own authentication mechanism in order to verify the identity of the device and user while giving access to the endpoints.

## HMAC signature calculation (endpoint 1 & 2)

For the first two endpoints, a signature needs to be calculated that is used by Adyen to verify the integrity of the payment request. In the PHP example below, ```api.php?method=paymentSignatureURL&action=paymentInitiation``` represents the ```paymentSignatureURL``` and ```api.php?method=paymentSignatureURL``` represents the ```paymentResultSignatureURL```. The request data that is send to the endpoint, with the HMAC signature in ```merchantSig```, will be returned for both endpoints. Please note that you'll have to set the ```hmacKey```, ```merchantAccount``` and ```skinCode``` -- which can be obtained from our Customer Area.

```php
if($_GET['method'] == 'calculateSignature'){

  $hmacKey = ''; // Provide your HMAC key
  $request = $_GET;

  if($_GET['action'] == 'paymentInitiation'){
    $request['merchantAccount'] = ''; // Provide your merchant account code
    $request['skinCode'] = ''; // Provide your skin code
    $request['sessionValidity'] = date("c",strtotime("+1 days"));
  }

  unset($request['method']);
  unset($request['action']);
  unset($request['environment']);
  unset($request['merchantSig']);

  $escapeval = function($val){return str_replace(':','\\:',str_replace('\\','\\\\',$val));};

  ksort($request, SORT_STRING);

  $signData = implode(":",array_map($escapeval,array_merge(array_keys($request), array_values($request))));
  $request['merchantSig'] = base64_encode(hash_hmac('sha256',$signData,pack('H*',$hmacKey),true));

  echo json_encode($request);
}
```

## Check payment status (endpoint 3)

The third endpoint ```paymentStatusURL``` is aimed to let the client side SDK verify the notification ([read more](https://docs.adyen.com/support/integration#notifications)) that was sent asynchronously to your server. Before you create the function to check the notification, you need to add an endpoint to accept (and store) notifications that we send to you:

```php
elseif($_GET['method'] == 'registerNotification'){
  // Include action to register notification in your database environment
  echo "[accepted]";
}
```

After notifications are successfully accepted by your server, it is time to add an endpoint for ```paymentStatusURL```. The client side SDK will call this endpoint and adds as identifier the ```merchantReference```, so you know for which notification you should return the ```eventCode``` and ```success```.

```php
elseif($_GET['method'] == 'validateNotification'){
  // Include action to fetch eventCode and success for notification that was stored in your from database environment, by using identifier $_GET['merchantReference']
  echo json_encode(array('eventCode'=>$data['eventCode'],'success'=>$data['success']));
}
```


## License

AdyenDL is available under the MIT license. See the LICENSE file for more info.

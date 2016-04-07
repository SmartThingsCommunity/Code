# twitter_demo

This is a demonstration of how to use the SmartThings developer platform to create a simple twitter integration.

When someone tweets to the configured twitter handle with either '#red', or #'blue', a SmartThings controlled color changing lightbulb will change either more red or blue depending on the red to blue vote ratio.

The node.js app is the middleware that sits between Twitter and SmartThings. It handles authentication with both, sets up a Twitter stream to get tweets from, and sends requests to a SmartThings Web Service SmartApp. For convenience, the SmartApp groovy file has been included here.

The node.js app is meant to be run on Heroku. The following Heroku config vars are necessary to get the node app working:

* CALLBACK_URL - The URL that SmartThings OAuth will call back to after authenticating.
* SMARTAPP_CLIENT_ID - The client id of your web services SmartApp.
* SMARTAPP_SECRET - The client secret of your web services SmartApp.
* TWITTER_ACCESS_TOKEN_KEY - your twitter developers access token key.
* TWITTER_ACCESS_TOKEN_SECRET - your twitter developers access token secret.
* TWITTER_CONSUMER_KEY - your twitter developers consumer key.
* TWITTER_CONSUMER_SECRET - your twitter developers consumer secret.
* TWITTER_HANDLE - the Twitter handle to get tweets for from the Twitter stream. (without the '@' character)
* DEBUG - if set to true, will turn on more verbose logging in the node.js application.

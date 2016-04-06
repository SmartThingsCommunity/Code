var config = {};

config.twitter = {};
config.oauth = {};
config.app = {};

config.twitter.consumer_key = process.env.TWITTER_CONSUMER_KEY;
config.twitter.consumer_secret = process.env.TWITTER_CONSUMER_SECRET;
config.twitter.access_token_key = process.env.TWITTER_ACCESS_TOKEN_KEY;
config.twitter.access_token_secret = process.env.TWITTER_ACCESS_TOKEN_SECRET;
config.twitter.handle = process.env.TWITTER_HANDLE;

config.oauth.client_id = process.env.SMARTAPP_CLIENT_ID;
config.oauth.client_secret = process.env.SMARTAPP_SECRET;
config.oauth.callback_url = process.env.CALLBACK_URL;

config.app.port = process.env.PORT || 5000;

module.exports = config;

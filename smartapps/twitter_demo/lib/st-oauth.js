
var STError = require('./st-error');
require('date-utils');

var querystring = require('querystring');
var request = require('request');

var AUTH_HOST = "https://graph-na01t-useast1.smartthingsgdev.com";
var AUTH_PATH = "/oauth/authorize";
var TOKEN_PATH = "/oauth/token";
var ENDPOINT_PATH = "/api/smartapps/endpoints";
var REFRESH_TOKEN_BUFFER_SECONDS = 60;

/**
 * Creates an instance of the STOAuthClient
 * @constructor
 * @param {string} clientId - The OAuth Client ID
 * @param {string} clientSecret - The OAuth Client secret
 * @param {string} redirectUri - The URI that will receive the access token
 */
function STOAuthClient(clientId, clientSecret, redirectUri) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.redirectUri = redirectUri;
}

/**
 * Gets the SmartThings authorization URL
 * @param {string} action - additonal URL params to pass along to callback.
 */
STOAuthClient.prototype.getAuthUrl = function(action) {
    action = action || "";
    var redirect = this.redirectUri + action;
    logMessage('redirect_uri: ' + redirect);
    var qs = querystring.stringify({
        response_type: 'code',
        client_id: this.clientId,
        scope: 'app',
        redirect_uri: this.redirectUri // + action
    });
    var uri = AUTH_HOST + AUTH_PATH + '?' + qs;
    logMessage('generated auth url: ' + uri);
    return uri;
};

/**
 * Gets an access token
 * @example
 * st.getAccessToken('some-code', function(error, tokenInfo, smartAppUri) {
 *    if (!error) {
 *       // store token and uri somewhere to call later
 *       // here is what is on the tokenInfo object:
 *       var accessToken = tokenInfo.access_token;
 *       var refreshToken = tokenInfo.refresh_token;
 *       var expiresIn = tokenInfo.expires_in;
 *       var expiresAt = tokenInfo.expires_at;
 *    }
 * });
 * @param {string} code - The authorization code used to request the token
 * @param {Function} callback - The function to call on completion
 */
STOAuthClient.prototype.getAccessToken = function(code, callback){
    logMessage("In STOAuth getAccessToken");
    var tokenConfig = {
        uri: AUTH_HOST + TOKEN_PATH,
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        method: 'POST',
        params: {
            grant_type: 'authorization_code',
            code: code,
            client_id: this.clientId,
            client_secret: this.clientSecret,
            redirect_uri: this.redirectUri
        }
    };
    var that = this;
    this._request(tokenConfig, function(error, response, body) {
        if (error || response.statusCode != 200) {
            console.error('got error on GET request: ' + response.statusCode);
            console.error('error on GET request: ' + error);
            callback(error, response, body);
        } else {
            logMessage('got response body: ' + JSON.stringify(body));
            logMessage('got response:' + response.statusCode);
            var tokenInfo;
            try {
                tokenInfo = JSON.parse(body);
            } catch (e) {
                console.error('got error parsing token response: ' + e);
                callback(e, null, null);
            }
            tokenInfo.expires_at = (new Date).addSeconds(tokenInfo.expires_in);
            logMessage('token expires_at: ' + tokenInfo.expires_at);

            var endpointConfig = {
                uri: AUTH_HOST + ENDPOINT_PATH,
                method: 'GET',
                headers: {'Authorization': 'Bearer ' + tokenInfo.access_token}
            };
            that._request(endpointConfig, function(error, response, body) {
                if (error) {
                    console.error('got error getting endpoint: ' + error);
                    callback(error, null, null);
                } else {
                    var apiJson;
                    try {
                        apiJson = JSON.parse(body);
                    } catch(e) {
                        console.error('error getting endpoint: ' + e);
                        callback(e, null, null);
                    }
                    logMessage('apiJson: ' + apiJson);
                    logMessage('apiJson length: ' + apiJson.length);
                    var smartAppUri = apiJson[apiJson.length - 1].uri;
                    callback(null, tokenInfo, smartAppUri);
                }
            });
        }
    });
};

/**
* Executes a GET request to a SmartApp endpoint.
* @example
* st.get({
*    uri: 'https://smart-app-endpoint/some-path',
*    token: 'access-token',
*    params: {queryParam: 'queryValue', queryParam2: 'queryValue2'}
* }, function(error, response, body) {
*       if (!error) {
*          // proccess response
*       }
*    }
* });
* @param {Object} opts - The options for the request. Required properties are
*   uri and token. Request parameters may be specified using the params option,
*   which is an object like {param1: 'val'}. These will be sent as query parameters
*   on the URL.
* @param {Function} callback - The function to call on completion.
*/
STOAuthClient.prototype.get = function(opts, callback) {
    var config = {
        uri: opts.uri,
        method: 'GET',
        headers: {'Authorization': 'Bearer ' + opts.token},
        params: opts.params
    };
    this._request(config, function(error, response, body) {
        if (error) {
            console.error('got error on GET request: ' + response.statusCode);
            console.error('error on GET request: ' + error);
            callback(error, response, body);
        } else {
            callback(null, response, body);
        }
    });
};

/**
* Executes a DELETE request to a SmartApp endpoint
* @param {Object} opts - The options for the request. Required properties are
*   uri and token. Request parameters may be specified using the params option,
*   which is an object like {param1: 'val'}. These will be sent as query parameters
*   on the URL.
* @param {Function} callback - The function to call on completion.
* @example
* st.delete({
*    uri: 'https://smart-app-endpoint/some-path',
*    token: 'access-token',
*    params: {queryParam: 'queryValue', queryParam2: 'queryValue2'}
* }, function(error, response, body) {
*       if (!error) {
*          // proccess response
*       }
*    }
* });
*/
STOAuthClient.prototype.delete = function(opts, callback) {
    var config = {
        uri: opts.uri,
        method: 'DELETE',
        headers: {'Authorization': 'Bearer ' + opts.token},
        params: opts.params
    };
    this._request(config, function(error, response, body) {
        if (error) {
            console.error('got error on GET request: ' + response.statusCode);
            console.error('error on GET request: ' + error);
            callback(error, response, body);
        } else {
            callback(null, response, body);
        }
    });
};

/**
* Executes a POST request to a SmartApp endpoint
* @param {Object} opts - The options for the request. Required properties are
*   uri and token. Request parameters may be specified using the params option,
*   which is an object like {param1: 'val'}. These will be sent as request body
*   parameters on the form.
* @param {Function} callback - The function to call on completion.
* @example
* st.post({
*    uri: 'https://smart-app-endpoint/some-path',
*    token: 'access-token',
*    params: {queryParam: 'queryValue', queryParam2: 'queryValue2'}
* }, function(error, response, body) {
*       if (!error) {
*          // proccess response
*       }
*    }
* });
*/
STOAuthClient.prototype.post = function(opts, callback) {
    logMessage("OPTS: " + JSON.stringify(opts));
    if (opts.headers) {
        opts.headers['Authorization'] = 'Bearer ' + opts.token;
    } else {
        opts.headers = {'Authorization': 'Bearer ' + opts.token};
    }
    logMessage("HEADERS: " + JSON.stringify(opts.headers));
    var config = {
        uri: opts.uri,
        method: 'POST',
        headers: opts.headers,
        params: opts.params
    };
    logMessage("CONFIG: " + JSON.stringify(config));
    this._request(config, function(error, response, body) {
        if (error) {
            console.error('got error on POST request: ' + response.statusCode);
            console.error('error on POST request: ' + error);
            callback(error, response, body);
        } else {
            callback(null, response, body);
        }
    });
};

/**
* Executes a PUT request to a SmartApp endpoint
* @param {Object} opts - The options for the request. Required properties are
*   uri and token. Request parameters may be specified using the params option,
*   which is an object like {param1: 'val'}. These will be sent as request body
*   parameters on the form.
* @param {Function} callback - The function to call on completion.
* @example
* st.put({
*    uri: 'https://smart-app-endpoint/some-path',
*    token: 'access-token',
*    params: {queryParam: 'queryValue', queryParam2: 'queryValue2'}
* }, function(error, response, body) {
*       if (!error) {
*          // proccess response
*       }
*    }
* });
*/
STOAuthClient.prototype.put = function(opts, callback) {
    var config = {
        uri: opts.uri,
        method: 'PUT',
        headers: {'Authorization': 'Bearer ' + opts.token},
        params: opts.params
    };
    this._request(config, function(error, response, body) {
        if (error) {
            console.error('got error on POST request: ' + response.statusCode);
            console.error('error on POST request: ' + error);
            callback(error, response, body);
        } else {
            callback(null, response, body);
        }
    });
};

/**
 * Refreshes the access token.
 * @param {string} refreshToken - The refresh token.
 * @param {Function} callback - The function to call on completion.
 * @example
 * st.refreshToken('SOME-REFRESH-TOKEN', function(error, tokenInfo) {
 *   if (!error) {
 *      var accessToken = tokenInfo.access_token;
 *      var refreshToken = tokenInfo.refresh_token;
 *      var expiresIn = tokenInfo.expires_in;
 *      var expiresAt = tokenInfo.expires_at;
 *   }
 * });
 */
STOAuthClient.prototype.refreshToken = function(refreshToken, callback) {
    var refreshConfig = {
        uri: AUTH_HOST + TOKEN_PATH,
        method: 'POST',
        params: {
            grant_type: 'refresh_token',
            refresh_token: refreshToken,
            client_id: this.clientId,
            client_secret: this.clientSecret,
            scope: "app"
        }
    }
    this._request(refreshConfig, function(error, response, body) {
        if (error) {
            console.error('Error refreshing token: ' + error);
            console.error('Error status code: ' + response.statusCode);
            callback(error, null);
        } else {
            var tokenInfo;
            try {
                tokenInfo = JSON.parse(body);
            } catch (e) {
                console.error('Error parsing token response: ' + e);
                var err = new STError('Error parsing token response', e);
                callback(err, null);
            }
            tokenInfo.expires_at = (new Date).addSeconds(tokenInfo.expires_in);
            logMessage('token expires_at: ' + tokenInfo.expires_at);
            callback(null, tokenInfo);
        }
    });
};

/**
 * Checks if the token needs to be refreshed.
 *
 * A token is considered as needing to be refresh if it will expire in 60 seconds
 * or less from now, unless otherwise specified.
 *
 * @param {Object} token The token object as returned by getAccessToken
 * @param {Number} buffer the amount of time in seconds to buffer the refresh
 *                 token required. Defaults to 60 seconds if not specified.
 * @return {Boolean} true if the token needs to be refreshed, false if it does not.
 * @example
 * var expiresWithinOneDay = st.tokenNeedsRefresh(tokenObject, 60 * 60 * 24);
 *
 * // one minute check is the default:
 * var expiresWithinOneMinute = st.tokenNeedsRefresh(tokenObject);
 */
STOAuthClient.prototype.tokenNeedsRefresh = function(token, buffer) {
    // JavaScript troothiness sucks. Can't just check if (buffer) because zero
    // evaluates to false.
    var refreshBuffer = (buffer === null || buffer === undefined) ?
        REFRESH_TOKEN_BUFFER_SECONDS : buffer;

    logMessage('using refresh buffer of: ' + refreshBuffer);

    var expiresAt = new Date(token.expires_at);
    var now = new Date();
    var expiresAtPadded = expiresAt.removeSeconds(refreshBuffer);

    logMessage('token expires at: ' + expiresAt);
    logMessage('time right now: ' + now);
    logMessage('token expires at (padded)' + expiresAtPadded)

    var needsRefresh = (Date.compare(expiresAtPadded, now) == -1) ? true : false
    logMessage('token needs refresh? ' + needsRefresh);
    return needsRefresh;
}

/**
 * Executes an HTTP request
 * @private
 */
STOAuthClient.prototype._request = function(opts, callback) {
    var params = opts.params ? opts.params : {};

    var requestConfig = {
        uri: opts.uri,
        method: opts.method,
        headers: opts.headers ? opts.headers : {}
    };
    // Send query params if GET or DELETE, otherwise send payload as body for POST and PUT
    if (opts.method == 'GET' || opts.method == 'DELETE') {
        requestConfig.qs = params;
    } else {
        // By default the content-type is 'application/json', unless header is explicitly passed
        if(requestConfig.headers['Content-Type']) {
            requestConfig.form = params;
        } else {
            requestConfig.json = true;
            requestConfig.body = params;
        }
    }

    logMessage('will make request with options: ' + JSON.stringify(requestConfig));

    request(requestConfig, function(error, response, body) {
        logMessage('in request handler, error: ' + JSON.stringify(error));
        logMessage('in request handler, response: ' + JSON.stringify(response));
        logMessage('in request handler, body: ' + JSON.stringify(body));

        var status = response.statusCode;
        logMessage('in request handler, statusCode: ' + status);

        if (error || !(status >= 200 || status < 300 || status === 304)) {
            console.error('got error on request: ' + error);
            console.error('error code: ' + response.statusCode);
            callback(new STError('Error on request: ' + JSON.stringify(requestConfig),
                response.statusCode), response, body);
        } else {
            logMessage('no error on request, statusCode: ' + response.statusCode);
            callback(null, response, body);
        }
    });
};

var logMessage = function(message) {
    if(process.env.DEBUG) {
        console.log(message);
    }
}

module.exports = STOAuthClient;

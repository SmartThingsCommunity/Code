require('date-utils');

var REFRESH_TOKEN_BUFFER_SECONDS = 60;

/**
 * Creates a simple object that represents an access token from SmartThings.
 *
 * @constructor
 * @param {Object} tokenObj - An object that represents a token. The object must
 *        have the following properties: access_token, refresh_token,
 *        expires_in.
 * @example
 * var token = new STToken({
 *   access_token: 'SOME-ACCESS-TOKEN',
 *   refresh_token: 'SOME-REFRESH-TOKEN',
 *   expires_in: 90 // time the token expires in secondds
 * });
 * console.log('access token: ' + token.access_token);
 * console.log('refresh token: ' + token.refresh_token);
 * console.log('expires in: ' + token.expires_in);
 * console.log('expires at: ' + token.expires_at);
 */
function STToken(tokenObj) {
    this.access_token = tokenObj.access_token;
    this.refresh_token = tokenObj.refresh_token;
    this.expires_in = tokenObj.expires_in;
    this.expires_at = (new Date).addSeconds(tokenObj.expires_in);
    console.log('Created new STToken: ' + JSON.stringify(this));
}

STToken.prototype.refreshNeeded = function(buffer) {
    // JavaScript troothiness sucks. Can't just check if (buffer) because zero
    // evaluates to false.
    var refreshBuffer = (buffer === null || buffer === undefined) ?
        REFRESH_TOKEN_BUFFER_SECONDS : buffer;

    console.log('using refresh buffer of: ' + refreshBuffer);

    var expiresAt = new Date(this.expires_at);
    var now = new Date();
    var expiresAtPadded = expiresAt.removeSeconds(refreshBuffer);

    console.log('token expires at: ' + expiresAt);
    console.log('time right now: ' + now);
    console.log('token expires at (padded)' + expiresAtPadded)

    var needsRefresh = (Date.compare(expiresAtPadded, now) == -1) ? true : false
    console.log('token needs refresh? ' + needsRefresh);
    return needsRefresh;
};

module.exports = STToken;

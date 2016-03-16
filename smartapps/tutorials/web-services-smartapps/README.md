# Web Services SmartApps Tutorial

ABOUT
=====

This example illustrates how a SmartApp can expose RESTful endpoints, and how a third party application can integrate with the SmartApp using OAuth.

There are two components to this example:

Web Services SmartApp
---------------------

A Web Services SmartApp is simply a SmartApp that can be called through REST requests.

Sinatra App
-----------

This example uses a simple [Sinatra](http://www.sinatrarb.com/) app to illustrate the process of obtaining an access token to make REST calls to the SmartApp.

TRY IT OUT
==========

Create SmartApp
---------------

1. In the SmartThings IDE, create a new SmartApp with the contents of web-services-smartapp-groovy. Make sure to enable OAuth from the App Settings, and note the Client ID and Client Secret.
2. Publish the SmartApp.

Simulator and cURL
------------------

1. Install the SmartApp in the simulator, and pick some switches.
2. Copy the API token and API URL from the simulator.
3. Open a terminal command prompt.
4. Using curl, issue a GET request to the `/switches` endpoint, using the API URL and token: `curl -H "Authorization: Bearer <API-TOKEN>" -X GET "<API-ENDPOINT>/switches"`
5. Send the on or off command to the configured switches, using the `/switches/<command>` endpoint: `curl -H "Authorization: Bearer <API-TOKEN>" -X PUT "<API-ENDPOINT>/switches/on"`
6. When you are done experimenting, uninstall the SmartApp from the simulator.

Sinatra App
-----------

1. Make sure you have [Ruby](https://www.ruby-lang.org/en/documentation/installation/) and [Bundler](http://bundler.io/) installed. This example has been tested with Ruby 2.2.1 and Bundler 1.10.4.
2. In a terminal, set the environment variables `ST_CLIENT_ID` and `ST_CLIENT_SECRET` with the ID and secret for the SmartApp. Alternatively, replace the `CLIENT_ID` and `CLIENT_SECRET` variables in the application with your actual client ID and secret.
3. Run bundler: `bundle install`.
4. Start the app: `ruby server.rb`
5. Open http://localhost:4567 in your web browser.
6. Click on the "Authorize with SmartThings" link. Enter your SmartThings username and password if not already logged in.
7. Select a Location from the dropdown, select one or more devices, and click Authorize.
8. The app will simply redirect to a page showing the result of the web request to the SmartApp endpoint.

IMPORTANT NOTES
===============

* When you publish a SmartApp for yourself, it is published only to the server that you are publishing from. It cannot be installed by users associated with a different server, without being published there as well.
* Additional servers will be added as needed (don't count on just two, or three, or any fixed number of servers).
* For SmartApps to be globally available, and replicated on additional servers as they are created (with the same Client ID and secret), they should be submitted to SmartThings for publication.
* Regardless of where the SmartApp is installed, `https://graph.api.smartthings.com/` should be used to retrieve the access code, token, and endpoints for the SmartApp. The JSON returned from getting the SmartApp endpoints will contain the `uri` and `base_url`, which will be specific to the installation location of the SmartApp. Refer to `server.rb` to see this in practice.

----

For more information, see the [Web Services Docs](http://docs.smartthings.com/en/latest/smartapp-web-services-developers-guide/index.html).

/**
 *  Example: Control a switch with a contact sensor
 *
 *  Copyright 2015 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Example: Control a device with an API call",
    namespace: "com.smarthings.developers",
    author: "Andrew Mager & Kris Schaller",
    description: "Make an HTTP request to a SmartApp to control devices.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    section(title: "Select Devices") {
        input "light", "capability.switch", title: "Select a light or outlet", required: true, multiple:false
    }
}

// Since the SmartApp doesn't have any dependencies when it's installed or updated,
// we don't need to worry about those states.
def installed() {}
def updated() {}


// This block defines an endpoint, and which functions will fire depending on which type
// of HTTP request you send
mappings {
    // The path is appended to the endpoint to make requests
    path("/switch") {
        // These actions link HTTP verbs to specific callback functions in your SmartApp
        action: [
            GET: "getSwitch", // "When an HTTP GET request is received, run getSwitch()"
            PUT: "setSwitch"
        ]
    }
}


// Callback functions
def getSwitch() {
    // This returns the current state of the switch in JSON
    return light.currentState("switch")
}

def setSwitch() {
    switch(request.JSON.value) {
        case "on":
            light.on();
            break;
        case "off":
            light.off();
            break;
        default:
            break;
    }
}

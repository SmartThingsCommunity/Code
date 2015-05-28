/**
 *  Example: Control a switch with a contact sensor
 *
 *  Copyright 2014 Andrew Mager
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
    name: "Example: Control a switch and lock with a contact sensor",
    namespace: "com.smarthings.developers",
    author: "Andrew Mager & Kris Schaller",
    description: "Using a contact sensor, control a switch and a lock.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


// This is where a user will select devices to be used by this SmartApp
preferences {
    // You can create multiple sections to organize the configuration fields of your SmartApp
    section(title: "Select Devices") {
        // Inputs assign variables to a group of physical devices
        input "contact", "capability.contactSensor", title: "Select a contact sensor", multiple: false
        input "light", "capability.switch", title: "Select a light or outlet", required: true
    }
}

// This function runs when the SmartApp is installed
def installed() {
    // This is a standard debug statement in Groovy
    log.debug "Installed with settings: ${settings}"
    initialize()
}

// This function runs when the SmartApp has been updated
def updated() {
    log.debug "Updated with settings: ${settings}"
    // Notice that all event subscriptions are removed when a SmartApp is updated
    unsubscribe()
    initialize()
}

// This function is where you initialize callbacks for event listeners
def initialize() {
    // The subscribe function takes a input, a state, and a callback method
    subscribe(contact, "contact.open", openHandler)
    subscribe(contact, "contact.closed", closedHandler)
}

// These are our callback methods
def openHandler(evt) {
    log.debug "$evt.name: $evt.value"
    // Turn the light on
    light.on()
}

def closedHandler(evt) {
    log.debug "$evt.name: $evt.value"
    // Turn the light off and lock the lock
    light.off()
}

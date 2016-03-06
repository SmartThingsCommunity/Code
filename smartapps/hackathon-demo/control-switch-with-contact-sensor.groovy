/**
 *  Example: Control a switch with a contact sensor
 *
 *  Copyright 2015 Andrew Mager
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
    name: "Example: Control a switch with a contact sensor",
    namespace: "com.smarthings.dev",
    author: "Andrew Mager",
    description: "Using a contact sensor, control a switch.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    section("Select Devices") {
        input "contact", "capability.contactSensor", title: "Select a contact sensor", multiple: false
        input "light", "capability.switch", title: "Select a light or outlet"
    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    unsubscribe()
    initialize()
}

def initialize() {
    subscribe contact, "contact.open", openHandler
    subscribe contact, "contact.closed", closedHandler
}

def openHandler(evt) {
    log.debug "$evt.name: $evt.value"
    light.on()
}

def closedHandler(evt) {
    log.debug "$evt.name: $evt.value"
    light.off()
}

// TODO: implement event handlers

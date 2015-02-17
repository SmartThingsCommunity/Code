/**
 *  Control a Switch with an API call
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
    name: "Control a Switch with an API call",
    namespace: "co.mager",
    author: "Andrew Mager",
    description: "V2 of 'RESTful Switch' example. Trying to make OAuth work properly.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    oauth: true)

preferences {
  section("Allow External Service to Control These Things...") {
    input "light1", "capability.switch", title: "Pick switch #1", required: false
  }
}
 

/* This block defines which functions will fire when you hit certain endpoints. */

mappings {
  path("/switch") {
    action: [
      GET: "getSwitch",
      PUT: "setSwitch"
    ]
  }
}

def installed() {
    log.debug "Installed with settings: ${settings}"
}

def updated() {
    log.debug "Updated with settings: ${settings}"
}

def getSwitch() {
    light1.currentState("switch")
}

def setSwitch($evt) {
    log.debug "The event: " + request.JSON.value
    
    if (request.JSON.value == "on") {
        light1.on()
    }
    else if (request.JSON.value == "off") {
        light1.off()
    }
    else {
        log.error "Invalid value: $request.JSON.value"
    }
}

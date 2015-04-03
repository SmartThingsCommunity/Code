/**
 *  My Living Room Lighting
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
    name: "My Living Room Lighting",
    namespace: "smartthings",
    author: "smartthings",
    description: "allow a button controller to control my hue lamps, tv lights, and floor lamp",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


 preferences {
    section("Control which hue lamps?") {
        input "hueLamps", "capability.colorControl", required: true, multiple: true
    }
    section("Control which TV lights?") {
        input "tvLights", "capability.colorControl", required: false
    }
    section("Control other (non-hue) lamps?") {
        input "otherLights", "capability.switch", required: false, multiple: true
    }
    section("Which button controller?") {
        input "buttonDevice", "capability.button", required: true
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
    subscribe(buttonDevice, "button", buttonEvent)
    hueLamps.each {
        log.debug "lamp ${it.displayName} hue: ${it.currentHue}"
        log.debug "lamp ${it.displayName} saturation: ${it.currentSaturation}"
        log.debug "lamp ${it.displayName} color ${it.currentColor}"
        log.debug "lamp ${it.displayName} level ${it.currentLevel}"
    }

    log.debug "tv hue: ${tvLights?.currentHue}"
    log.debug "tv saturation: ${tvLights?.currentSaturation}"
    log.debug "tv color ${tvLights?.currentColor}"
    log.debug "tv level ${tvLights?.currentLevel}"
}

def buttonEvent(evt) {
    def buttonState = evt.value // "pushed" or "held"
    def buttonNumber = parseJson(evt.data)?.buttonNumber 
    
    log.debug "buttonState:  $buttonState"
    log.debug "buttonNumber: $buttonNumber"
    
    if (!(1..4).contains(buttonNumber)) {
        log.error "This app only supports four buttons. Invalid buttonNumber: $buttonNumber"
        } else if (!(buttonState == "pushed" || buttonState == "held")) {
            log.error "This app only supports button pushed and held values. Invalid button state: $buttonState"
            } else { 
                def meth = "handleButton" + buttonNumber + buttonState.capitalize()
                log.debug "meth: $meth"
                "$meth"()
            }
        }

// normal "white" lighting
def handleButton1Pushed() {
    log.debug "handle1Pushed"
    
    hueLamps.setColor(level: 100, hue: 20, saturation: 80)
    
    // notice the "?." operator - tvLights may not be set (required: false).
    tvLights?.setColor(level: 100, hue: 100, saturation: 100)
    otherLights?.on()
}

// turn everything off
def handleButton1Held() {
    log.debug "handleButton1Held"
    
    hueLamps.off()
    tvLights?.off()
    otherLights?.off()
}

// soft, dim white light
def handleButton2Pushed() {
    log.debug "handleButton2Pushed"

    hueLamps.setColor(level: 50, hue: 20, saturation: 80)
    tvLights.setColor(level: 30, hue: 70, saturation: 70)
    otherLights?.on()
}

// set to what you want!
def handleButton2Held() {
    log.debug "handleButton2Held"
}

// dim red light
def handleButton3Pushed() {
    hueLamps.setColor(level: 40, hue: 100, saturation: 100)
    tvLights?.setColor(level: 30, hue: 100, saturation: 100)
    otherLights?.off()
}

// set to what you want!
def handleButton3Held() {
    log.debug "handleButton3Held"
}

// dim blue light 
def handleButton4Pushed() {
    log.debug "handleButton4Pushed"
    
    hueLamps.setColor(level: 10, hue: 70, saturation: 100)
    tvLights?.setColor(level: 10, hue: 70, saturation: 100)
    otherLights?.off()
}

// debug information
def handleButton4Held() {
    hueLamps.each {
        log.debug "lamp ${it.displayName} hue: ${it.currentHue}"
        log.debug "lamp ${it.displayName} saturation: ${it.currentSaturation}"
        log.debug "lamp ${it.displayName} level ${it.currentLevel}"
    }

    log.debug "tv hue: ${tvLights?.currentHue}"
    log.debug "tv saturation: ${tvLights?.currentSaturation}"
    log.debug "tv color ${tvLights?.currentColor}"
    log.debug "tv level ${tvLights?.currentLevel}"
}
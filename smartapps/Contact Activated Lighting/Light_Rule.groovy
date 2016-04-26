/**
 *  Light Rule
 *
 *  Copyright 2016 Tim Slagle
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
    name: "Light Rule",
    namespace: "tslagle13",
    author: "Tim Slagle",
    description: "Light rule child app for \"Motion Activated Light\"",
    category: "Convenience",
    parent: "tslagle13:Contact Activated Lighting",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	page name: "mainPage", title: "Automate Lights & Switches", install: false, uninstall: true, nextPage: "namePage"
    page name: "namePage", title: "Automate Lights & Switches", install: true, uninstall: true
}

def mainPage() {
	dynamicPage(name: "mainPage") {
        section("Which contact sensor?") {
            input "contactSensor", "capability.contactSensor", title: "Which contact sensor(s)?", multiple: true
        }
        section("Which light would you like to turn on?") {
            input "light", "capability.switch", title: "Which light?", multiple: true
        }
        section("Extras") {
            input "contactInactive", "bool", title: "Turn off if contacts closes?", submitOnChange: true
            if (contactInactive) {
            	input "delay", "number", title: "After how many seconds?", required: false
            }
        }
    }
}

def namePage() {
	if (!overrideLabel) {
        // if the user selects to not change the label, give a default label
        def l = "$contactSensor turns on $light"
        log.debug "will set default label of $l"
        app.updateLabel(l)
    }
    dynamicPage(name: "namePage") {
        if (overrideLabel) {
            section("Automation name") {
                label title: "Enter custom name", defaultValue: app.label, required: false
            }
        } else {
            section("Automation name") {
                paragraph app.label
            }
        }
        section {
            input "overrideLabel", "bool", title: "Edit automation name", defaultValue: "false", required: "false", submitOnChange: true
        }
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
	subscribe (contactSensor, "contact" , contactHandler)
}

def contactHandler(evt){
	log.debug evt.name
    log.debug evt.value
    log.debug evt.date
    log.debug evt.isStateChange()
    
    if (evt.value == "active") {
    	turnLightOn()
    }
    else if (contactInactive) {
    	runIn(delay , turnLightOff)
    }
}

def turnLightOn() {
	light.on()
}

def turnLightOff() {
	light.off()
}

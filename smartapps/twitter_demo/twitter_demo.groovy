/**
 *  twitter_demo
 *
 *  Copyright 2016 SmartThings
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
    name: "twitter_demo",
    namespace: "smartthings",
    author: "SmartThings",
    description: "web services smartapp to demonstrate twitter integration",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    oauth: [displayName: "twitterDemo", displayLink: ""])

// Choose a color control bulb
preferences {
	section("Pick a color changing bulb") {
    	input "bulb", "capability.colorControl", title: "Which Bulb?", multiple: false, required: true
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
    subscribe(bulb, "color", handleColor)
    subscribe(bulb, "switch", handleSwitch)
}

def handleSwitch(evt) {
	log.debug "$evt"
}

def handleColor(evt) {
	log.debug "$evt"
}

//URL Mappings
mappings {
    // blue endpoint
	path("/setColor/blue") {
    	action: [
        	GET: "setBulbBlue"
        ]
    }
    // red endpoint
    path("/setColor/red") {
        action: [
            GET: "setBulbRed"
        ]
    }
    // endpoint for setting color to parameter specified in request body
    path("/setColor") {
    	action: [
        	POST: "setColor"
        ]
    }
}

// blue endpoint handler method
def setBulbBlue() {
	bulb.setColor([hex: "0000FF"])
}

// red endpoint handler method
def setBulbRed() {
	bulb.setColor([hex: "FF0000"])
}

// endpoint handler method to set color to the value of the 'color' parameter specified in request body
def setColor() {
	def color = request.JSON?.color
    bulb.setColor([hex: color])
}

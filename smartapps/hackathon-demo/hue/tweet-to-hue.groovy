/**
 *  Twitter Hue
 *
 *  Copyright 2015 Andrew Mager (mager@smartthings.com) / Jack Chi (jack@smartthings.com)
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
    name: "Twitter Hue",
    namespace: "mager",
    author: "Andrew Mager, Jack Chi",
    description: "Use Twitter to Chnage Hue Color",
    category: "SmartThings Internal",
    iconUrl: "http://www.newsroom.immi.gov.au/assets/images/icons/logos/icon-twitterLogo.png",
    iconX2Url: "http://www.amstevenson.net/images/icons/twitterblue.png",
    iconX3Url: "https://g.twimg.com/Twitter_logo_blue.png",
    oauth: true)

preferences {
  section("Control these hue bulbs...") {
    input "hues", "capability.colorControl", title: "Which Hue Bulbs?", required:false, multiple:true
  }
}
 

/* This block defines which functions will fire when you hit certain endpoints. */

mappings {
  path("/hue") {
  	action: [
  		PUT: "postHue"
  	]
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

}

/**
* Body: { color=[yourcolor] } to change color
* Example:
*     {
*          "value" : " #smartthings is so color=blue"
*     }
*/

def postHue() {
	def tweetText = request.JSON.value
	log.info "POST: $tweetText"
    
    try {
    	def tweetColor = (tweetText =~ /color=(\w+)/)[0][1].toLowerCase()
    	log.debug (tweetText =~ /color=(\w+)/)
        setHueColor(tweetColor)     
    }
	catch (any) {
    	log.trace "POST: Check Body (e.g: @RT: #smartthings color=red)"
     }    
}

private setHueColor(color) {

	def hueColor = 0
	def saturation = 100

	switch(color) {
		case "white":
			hueColor = 52
			saturation = 19
			break;
		case "blue":
			hueColor = 70
			break;
		case "green":
			hueColor = 39
			break;
		case "yellow":
			hueColor = 25
			break;
		case "orange":
			hueColor = 10
			break;
		case "purple":
			hueColor = 75
			break;
		case "pink":
			hueColor = 83
			break;
		case "red":
			hueColor = 100
			break;
	}

	state.previous = [:]

	hues.each {
		state.previous[it.id] = [
			"switch": it.currentValue("switch"),
			"level" : it.currentValue("level"),
			"hue": it.currentValue("hue"),
			"saturation": it.currentValue("saturation")
		]
	}

	log.debug "current values = $state.previous"

	def newValue = [hue: hueColor, saturation: saturation, level: 100]
	log.debug "new value = $newValue"

	hues*.setColor(newValue)
}
/**
 *  SMS to Hue
 *
 *  Copyright 2015 Andrew Mager & Kris Schaller
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
    name: "[Workshop Demo] SMS to Hue",
    namespace: "com.smartthings.dev",
    author: "Andrew Mager & Jim Anderson",
    description: "Update a Hue bulb's color based on a SMS.",
    category: "Fun & Social",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    oauth: true)


preferences {
  section("Control these hue bulbs...") {
    input "hues", "capability.colorControl", title: "Which Hue Bulbs?", required:false, multiple:true
  }
}
 
 
// This block defines an endpoint, and which functions will fire depending on which type
// of HTTP request you send
mappings {
    // The path is appended to the endpoint to make request
    path("/hue") {
        action: [
            PUT: "postHue" 
        ]
    }
}


def installed() {}
def updated() {}



/*
  This function receives a JSON payload and parses out the color from a tweet. 
  For example, someone tweets, "@SmartThingsDev #IoTWorld2015 color=blue". Then it sends the 
  correct color as a string to setHueColor().
*/
def postHue() {
    /*
      "request.JSON?" checks to make sure that the object exists. And ".text" is the
      key for the value that we're looking for. It's the body of the tweet.
    */
    def tweetText = request.JSON?.text
    
    try {
        // Finds the text "color=[colorname]" and parses out the color name
        def tweetColor = (tweetText =~ /color=(\w+)/)[0][1].toLowerCase()
        setHueColor(tweetColor)     
    }
    catch (any) {
        log.trace "POST: Check Body (e.g: @RT: #smartthings color=red)"
     }    
}


// This function takes a String of text and associates it with an Integer value for the color.
private setHueColor(color) {

    // Initaliaze values for hue, saturation, and level
    def hueColor = 0
    def saturation = 100
    def level = 100

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

    // Set the new value of hue, saturation, and level
    def newValue = [hue: hueColor, saturation: saturation, level: level]

    // Update each Hue bulb with the new values
    hues*.setColor(newValue)
}

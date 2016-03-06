/**
 *  Weather Underground PWS Connect
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
 
import java.text.DecimalFormat
 
definition(
    name: "Weather Underground PWS Connect",
    namespace: "co.mager",
    author: "Andrew Mager",
    description: "Connect your SmartSense Temp/Humidity sensor to your Weather Underground Personal Weather Station.",
    category: "Green Living",
    iconUrl: "http://i.imgur.com/HU0ANBp.png",
    iconX2Url: "http://i.imgur.com/HU0ANBp.png",
    iconX3Url: "http://i.imgur.com/HU0ANBp.png",
    oauth: true)


preferences {
    section("Select a sensor") {
        input "temp", "capability.temperatureMeasurement", title: "Temperature", required: true
        input "humidity", "capability.relativeHumidityMeasurement", title: "Humidity", required: true
    }
    section("Configure your Weather Underground credentials") {
        input "weatherID", "text", title: "Weather Station ID", required: true
        input "password", "password", title: "Weather Underground password", required: true

    }
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
}


def updated() {
    log.debug "Updated with settings: ${settings}"
    initialize()
}


def initialize() {
    
    runEvery10Minutes(updateCurrentWeather)
    
    log.trace "Temp: " + temp.currentTemperature
    log.trace "Humidity: " + humidity.currentHumidity
    log.trace "Dew Point: " + calculateDewPoint(temp.currentTemperature, humidity.currentHumidity)

}


def updateCurrentWeather() {
    
    def params = [
        uri: "http://weatherstation.wunderground.com",
        path: "/weatherstation/updateweatherstation.php",
        query: [
            "ID": weatherID,
            "PASSWORD": password,
            "dateutc": "now",
            "tempf": temp.currentTemperature,
            "humidity": humidity.currentHumidity,
            "dewptf": calculateDewPoint(temp.currentTemperature, humidity.currentHumidity),
            "action": "updateraw",
            "softwaretype": "SmartThings"
        ]
    ]
    
    if (temp.currentTemperature) {
        try {
            httpGet(params) { resp ->   
                log.debug "response data: ${resp.data}"
            }
        } catch (e) {
            log.error "something went wrong: $e"
        }
    }

}


def calculateDewPoint(t, rh) {
    def dp = 243.04 * ( Math.log(rh / 100) + ( (17.625 * t) / (243.04 + t) ) ) / (17.625 - Math.log(rh / 100) - ( (17.625 * t) / (243.04 + t) ) ) 
    return new DecimalFormat("##.##").format(dp)
}

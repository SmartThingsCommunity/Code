/**
 *  Granny's Faucet
 *  Notifies you when Granny is up and running
 *  Let you know when she hasn't been around so you can check in on her
 *
 *  Copyright 2015 SmartThings Hack
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
    name: "Granny's Faucet",
    namespace: "com.firstbuild",
    author: "SmartThings Hack",
    description: "Check to see if Granny used the faucet in a 24 hour period and send a notification if she does.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    section("Which Faucets?") {
        input "faucet", "capability.accelerationSensor", title: "Which faucet?", required:true
     }
     section("Who to text") {
        input "phone", "phone", title: "Phone number?", required: true  
     }
     section("How often do you want grandma to check in?") {
        input "minutes", "number", title: "Delay in minutes before we notify you", defaultValue: 1
     }
}

def installed() {
    log.trace "Installed with settings: ${settings}"
    initialize()
}

def updated() {
    log.trace "Updated with settings: ${settings}"
    state.lastOpened= [date:now()] // now() is ms time built into SmartApps
     log.debug "Last Updated Date: $state.lastOpened.date"

    unsubscribe()
    initialize()
}

def initialize() {
     subscribe(faucet, "acceleration.active", faucetActiveHandler)
     subscribe(faucet, "acceleration.inactive", faucetInactiveHandler)
}

def faucetInactiveHandler(evt) {
    log.trace "#faucetClosedHandler#"
    def inputSeconds = 60*minutes.toInteger()
    log.debug "waiting...$inputSeconds"
    runIn(inputSeconds, alertMe)
}

def faucetActiveHandler(evt) {
    // Don't send a continuous stream of text messages
     def inputSeconds = 60*minutes.toInteger()
    def deltaSeconds = inputSeconds
    def timeAgo = new Date(now() - (1000 * deltaSeconds)) // 61 seconds ago
    def recentEvents = faucet.eventsSince(timeAgo)
    log.trace "Found ${recentEvents?.size() ?: 0} events in the last $deltaSeconds seconds"
     log.debug "Recent Events $recentEvents.value"
    def alreadySentSms = recentEvents.count { 
        it.value && it.value == "active"
        } > 1
    
    if (alreadySentSms) {
        log.debug "SMS already sent to $phone1 within the last $minutes minute"
    } else {
        //  
        sendSms(phone, "Grandma opened faucet")
        state.lastOpened.date = now()
        log.debug "Grandma Opened Faucet: $state.lastOpened"

    }   
}

def alertMe() {
    log.trace "#alerting...#"
     def targetTime = state.lastOpened.date + minutes.toInteger()*60*1000
     log.debug "#alertMe: last: ${state.lastOpened.date} , now: ${now()}, targetTime: ${targetTime}} "
    if ( now() > targetTime ){
            log.debug "Grandma needs water badly"
            sendSms(phone, "Grandma needs water badly")     
    } else {
        log.debug "Grandma's aight!"
    }
}
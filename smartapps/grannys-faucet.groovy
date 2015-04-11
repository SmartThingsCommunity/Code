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
    description: "Check to see if Granny used the faucet in a 24 hour period and send a notification if she does. Built at Hack The Home in Louisville, KY (4/11/15)",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    section("Which Faucets?") {
        input "faucet", "capability.contactSensor", title: "Which faucet?", required:true
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
    subscribe(faucet, "contact.open", faucetHandler)
}

def faucetHandler(evt) {
    // Don't send a continuous stream of text messages
     def inputSeconds = 60*minutes.toInteger()
    def deltaSeconds = inputSeconds + 1
    def timeAgo = new Date(now() - (1000 * deltaSeconds)) // 61 seconds ago
    def recentEvents = faucet.eventsSince(timeAgo)
    log.trace "Found ${recentEvents?.size() ?: 0} events in the last $deltaSeconds seconds"
     log.debug "Recent Events $recentEvents.value"
    def alreadySentSms = recentEvents.count { 
        it.value.contains("open")
        } > 1
    
    if (alreadySentSms) {
        log.debug "SMS already sent to $phone1 within the last $minutes minute"
    } else {
        //  
        sendSms(phone, "Grandma opened faucet")
        state.lastOpened.date = new Date().getTime()
        log.debug "Grandma Opened Faucet: $state.lastOpened"
        runIn(60*inputSeconds, alertMe)
    }   
}

def alertMe() {
     log.debug "#alertMe: last: ${state.lastOpened.date} , now: ${now()} "
    if ( now() > state.lastOpened.date ) {
            sendSms(phone, "Grandma needs water badly")     
    } 
}

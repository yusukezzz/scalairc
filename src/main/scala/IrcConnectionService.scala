package net.yusukezzz.android.helloscala

import android.app.Service
import android.content.Intent
import android.os.{Binder, IBinder}

/**
 * Created with IntelliJ IDEA.
 * User: yusukezzz
 * Date: 2013/02/02
 * Time: 14:49
 * To change this template use File | Settings | File Templates.
 */
class IrcConnectionService extends Service {
  class IrcConnectionBinder extends Binder {
    def getService = IrcConnectionService.this
  }
  val mBinder: IBinder = new IrcConnectionBinder
  override def onBind(intent: Intent): IBinder = mBinder
}

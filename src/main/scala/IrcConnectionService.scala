package net.yusukezzz.android.helloscala

import android.app.Service
import android.content.Intent
import android.os.{Binder, IBinder}
import collection.immutable.HashMap
import android.util.Log

/**
 * Created with IntelliJ IDEA.
 * User: yusukezzz
 * Date: 2013/02/02
 * Time: 14:49
 * To change this template use File | Settings | File Templates.
 */
class IrcConnectionService extends Service {
  var conns = new HashMap[String, IrcConnection]
  class IrcConnectionBinder extends Binder {
    def getService = IrcConnectionService.this
  }
  lazy val mBinder: IBinder = new IrcConnectionBinder
  override def onBind(intent: Intent): IBinder = mBinder

  def addHost(host: IrcHost): IrcConnection = {
    val conn = new IrcConnection(host)
    conn.connect()
    conns += (host.hostname -> conn)
    conn
  }
}

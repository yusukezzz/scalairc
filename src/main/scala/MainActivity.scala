package net.yusukezzz.android.helloscala

import _root_.android.app.Activity
import android.os.{StrictMode, Handler, IBinder, Bundle}
import android.content.{Context, Intent, ComponentName, ServiceConnection}
import android.util.Log

class MainActivity extends Activity with TypedActivity {
  val host: IrcHost = new IrcHost("chat.freenode.net", 6667, "", "androzzz", "androzzz", "yusukezzz", "UTF-8")
  val handler: Handler = new Handler()

  var mIsBind: Boolean = false
  val mServConn: ServiceConnection = new ServiceConnection {
    def onServiceDisconnected(name: ComponentName) {
      MainActivity.localService = None
      mIsBind = false
      Log.d("IRC", "service disconnected")
    }
    def onServiceConnected(name: ComponentName, binder: IBinder) {
      MainActivity.localService = Some(binder.asInstanceOf[IrcConnectionService#IrcConnectionBinder].getService)
      mIsBind = true
      Log.d("IRC", "service connected")
    }
  }

  override def onCreate(bundle: Bundle) {
    // android 3.0 以降でデフォルト有効になっているStrictModeを無効化する
    // StrictMode は本来、メインスレッド内でのネットワーク接続等パフォーマンスに悪影響がある行為を叱ってくれるもの
    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
    super.onCreate(bundle)
    setContentView(R.layout.main)

    val str = "irc server connecting..."
    findView(TR.received_text).setText(str)
    bindService(new Intent(this, classOf[IrcConnectionService]), mServConn, Context.BIND_AUTO_CREATE)
  }

  override def onStart() {
    super.onStart()
    try{
      val looper = new Runnable {
        override def run() {
          val text = host.connection.receive
          appendText(text)
          host.connection.receive = ""
          handler.postDelayed(this, 1000)
        }
      }
      handler.postDelayed(looper, 5000)
      Log.d("IRC", "looper first posted")
    } catch {
      case e => findView(TR.received_text).setText(e.getMessage)
    }
  }

  def appendText(text: String) {
    // FIXME
    val tmp = findView(TR.received_text).getText()
    findView(TR.received_text).setText(tmp + "\r" + text)
  }
}

object MainActivity {
  var localService:Option[IrcConnectionService] = None
}

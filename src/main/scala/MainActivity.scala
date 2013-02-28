package net.yusukezzz.android.helloscala

import _root_.android.app.Activity
import android.os.{Handler, IBinder, Bundle}
import android.content.{Context, Intent, ComponentName, ServiceConnection}

class MainActivity extends Activity with TypedActivity {
  var mIsBind: Boolean = false
  var localService:Option[IrcConnectionService] = None
  val host: IrcHost = new IrcHost("freenode", 6667, "", "androzzz", "androzzz", "yusukezzz", "UTF-8")
  val handler: Handler = new Handler()
  val mServConn: ServiceConnection = new ServiceConnection {
    def onServiceDisconnected(name: ComponentName) {
      localService = null
      mIsBind = false
    }
    def onServiceConnected(name: ComponentName, binder: IBinder) {
      localService = Some(binder.asInstanceOf[IrcConnectionService#IrcConnectionBinder].getService)
      mIsBind = true
    }
  }

  override def onCreate(bundle: Bundle) {
    super.onCreate(bundle)
    setContentView(R.layout.main)

    findView(TR.textview).setText("hello, world!")
    if (mIsBind == false) {
      bindService(new Intent(this, classOf[IrcConnectionService]), mServConn, Context.BIND_AUTO_CREATE)
    }

    val looper: Runnable = new Runnable {
      def run() {
        val text = host.connection.receive
        appendText(text)
      }
    }
  }

  def appendText(text: String) {
    findView(TR.textview).setText(text)
  }
}

package net.yusukezzz.android.helloscala

import java.io.{InputStreamReader, OutputStreamWriter, BufferedReader, BufferedWriter}
import java.net.Socket

/**
 * Created with IntelliJ IDEA.
 * User: yusukezzz
 * Date: 2013/02/02
 * Time: 15:55
 * To change this template use File | Settings | File Templates.
 */
class IrcConnection(val host:IrcHost) extends Thread {
  var receive:String = ""
  var running:Boolean = false
  var socket:Socket = null
  var bw:BufferedWriter
  var br:BufferedReader

  def connect {
    socket = new Socket(host.hostname, host.port)
    bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
    br = new BufferedReader(new InputStreamReader(socket.getInputStream(),host.charset))
    running = true
    this.start()
    this.nick
  }

  override def run {
    var current: String = null
    while(!isInterrupted) {
      current = br.readLine()
      if (current != null) {
        this.dispatch(current)
      } else {
        this.close
      }
    }
  }

  def close = synchronized {
    if (isInterrupted == false) {
      interrupt()
    }
    br.close()
    bw.close()
    socket.close()
    this.join()
    br = null
    bw = null
    socket = null
  }

  def dispatch(msg: String) {
    val m: IrcMessage = IrcMessage.parse(msg)
    if (m == null) {
      return
    }
    val cmd = m.command
    if (cmd.equalsIgnoreCase("PRIVMSG")) {
      val user = m.getUser
      val text = user.nick + " " + m.trailing
      // addMsg
    } else if (cmd.equalsIgnoreCase("PING")) {
      val ping = m.trailing
      pong(ping)
      if (!running) {
        running = true
      }
    }
  }

  def write(cmd: String) {
    bw.write(cmd + "\n")
    bw.flush()
  }

  def joinCh {
    this.write("JOIN " + host.channel)
  }

  def nick {
    this.write("NICK " + host.nick)
  }

  def pong(daemon: String) {
    this.write("PONG " + daemon)
  }
}

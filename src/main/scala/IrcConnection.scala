package net.yusukezzz.android.helloscala

import java.io.{InputStreamReader, OutputStreamWriter, BufferedReader, BufferedWriter}
import java.net.{InetAddress, Socket}
import android.util.Log
import collection.immutable.HashMap

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
  var bw:BufferedWriter = null
  var br:BufferedReader = null
  var channels = new HashMap[String, IrcChannel]

  def connect() {
    // FIXME: use another process (like AsyncTask)
    Log.d("IRC", "socket connecting")
    socket = new Socket(host.hostname, host.port)
    Log.d("IRC", "socket connected")
    bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream))
    br = new BufferedReader(new InputStreamReader(socket.getInputStream,host.charset))
    Log.d("IRC", "reader/writer initialized")
    running = true
    this.start()
    Log.d("IRC", "thread started")
    this.nick(host.nick)
    Log.d("IRC", "irc nick sended")
    this.user()
    Log.d("IRC", "irc user registered")
    this.joinCh(host.channel)
    Log.d("IRC", "irc channel joined: " + host.channel)
  }

  override def run() {
    var current: String = null
    while(!isInterrupted) {
      current = br.readLine()
      if (current != null) {
        this.dispatch(current)
      } else {
        this.close()
      }
    }
  }

  def close() = synchronized {
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
      val text = user.nick + " " + m.trailing + "\n"
      receive += text
    } else if (cmd.equalsIgnoreCase("PING")) {
      val ping = m.trailing
      pong(ping)
      if (!running) {
        running = true
      }
    } else {
      //receive += m.trailing + "\n"
    }
  }

  def write(cmd: String) {
    bw.write(cmd + "\n")
    bw.flush()
    Log.d("IRC", "send cmd: " + cmd)
  }

  def joinCh(ch_name: String):IrcChannel = {
    channels.get(ch_name) match {
      case Some(ch) => ch
      case None => {
        this.write("JOIN " + ch_name)
        val c = new IrcChannel(ch_name)
        channels += (ch_name -> c)
        c
      }
    }
  }

  def nick(name: String) {
    this.write("NICK " + name)
  }

  def pong(daemon: String) {
    this.write("PONG " + daemon)
  }

  def user() {
    var hostname = ""
    try {
      hostname = InetAddress.getLocalHost.getHostName
    } catch {
      case _ => Log.d("IRC", "hostname unresolved")
    }
    this.write("USER " + host.login + " " + hostname + " " + host.hostname + " :" + host.real)
  }
}

package net.yusukezzz.android.helloscala

import java.util.regex.{Pattern, Matcher}

/**
 * Created with IntelliJ IDEA.
 * User: yusukezzz
 * Date: 2013/02/02
 * Time: 19:17
 * To change this template use File | Settings | File Templates.
 */
class IrcMessage(val prefix: String, val command: String, val middle: String, val trailing: String) {
  def getNick: String = {
    var i = prefix.indexOf("!")
    if (i != -1 || (i = prefix.indexOf("@")) != -1) {
      prefix.substring(0, i)
    } else {
      if (prefix.length() > 0) {
        prefix
      } else {
        null
      }
    }
  }

  def getUsername: String = {
    val i = prefix.indexOf("!") + 1
    if (i != 0) {
      var j = prefix.indexOf("@", i)
      if (j == -1) {
        j = prefix.length()
      }
      prefix.substring(i, j)
    } else {
      null
    }
  }

  def getHost: String = {
    val i = prefix.indexOf("@") + 1
    if (i != 0) {
      prefix.substring(i, prefix.length())
    } else {
      null
    }
  }

  def getUser: IrcUser = {
    new IrcUser(getNick, getUsername, getHost)
  }
}

object IrcMessage {
  val MES_REGEXP: String = "^(:([\\w!@~\\.]+?)\\x20)?(\\w+|\\d{3})((\\x20([^\\x00\\x20\\n:]+))*)?(\\x20:([^\\x00\\n]+))?"

  def parse(str: String): IrcMessage = {
    var msg: IrcMessage = null
    val m: Matcher = Pattern.compile(MES_REGEXP).matcher(str)
    if (m.find()) {
      try {
        msg = new IrcMessage(m.group(2), m.group(3), m.group(4), m.group(8))
      } catch (IllegalStateException e) {
        msg = new IrcMessage("", "", "", "")
      }
    }
    msg
  }
}